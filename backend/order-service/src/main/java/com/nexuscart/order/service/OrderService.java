package com.nexuscart.order.service;

import com.nexuscart.kafka.event.OrderEvents;
import com.nexuscart.order.entity.Order;
import com.nexuscart.order.entity.OrderItem;
import com.nexuscart.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Page<Order> getUserOrders(UUID userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    public Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Transactional
    public Order createOrder(UUID userId, List<OrderItemRequest> items, Order.ShippingAddress address) {
        String orderNumber = generateOrderNumber();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .shippingAddress(address)
                .status(Order.OrderStatus.PENDING)
                .subtotal(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : items) {
            BigDecimal itemTotal = itemReq.unitPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .productId(itemReq.productId())
                    .productName(itemReq.productName())
                    .productSku(itemReq.productSku())
                    .productImage(itemReq.productImage())
                    .quantity(itemReq.quantity())
                    .unitPrice(itemReq.unitPrice())
                    .totalPrice(itemTotal)
                    .build();

            order.getItems().add(item);
            subtotal = subtotal.add(itemTotal);
        }

        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal.add(order.getShippingCost()).add(order.getTax()).subtract(order.getDiscount()));

        order = orderRepository.save(order);
        log.info("Created order: {}", orderNumber);

        // Publish order created event
        publishOrderCreatedEvent(order);

        return order;
    }

    @Transactional
    public Order confirmOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());

        log.info("Confirmed order: {}", order.getOrderNumber());
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(UUID orderId, String reason) {
        Order order = getOrder(orderId);

        if (order.getStatus() == Order.OrderStatus.SHIPPED ||
                order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel shipped/delivered orders");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setNotes(reason);

        // Publish cancellation event
        OrderEvents.OrderCancelledEvent event = OrderEvents.OrderCancelledEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(order.getId().toString())
                .userId(order.getUserId().toString())
                .reason(reason)
                .cancelledAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send("order.cancelled", event);

        log.info("Cancelled order: {}", order.getOrderNumber());
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(UUID orderId, Order.OrderStatus status) {
        Order order = getOrder(orderId);
        order.setStatus(status);

        switch (status) {
            case SHIPPED -> order.setShippedAt(LocalDateTime.now());
            case DELIVERED -> order.setDeliveredAt(LocalDateTime.now());
            default -> {
            }
        }

        log.info("Updated order {} status to {}", order.getOrderNumber(), status);
        return orderRepository.save(order);
    }

    private void publishOrderCreatedEvent(Order order) {
        OrderEvents.OrderCreatedEvent event = OrderEvents.OrderCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(order.getId().toString())
                .userId(order.getUserId().toString())
                .items(order.getItems().stream()
                        .map(item -> OrderEvents.OrderCreatedEvent.OrderItem.builder()
                                .productId(item.getProductId().toString())
                                .quantity(item.getQuantity())
                                .price(item.getUnitPrice())
                                .build())
                        .collect(Collectors.toList()))
                .totalAmount(order.getTotalAmount())
                .createdAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send("order.created", event);
        log.info("Published order.created event for: {}", order.getOrderNumber());
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    public record OrderItemRequest(
            UUID productId, String productName, String productSku,
            String productImage, Integer quantity, BigDecimal unitPrice) {
    }
}
