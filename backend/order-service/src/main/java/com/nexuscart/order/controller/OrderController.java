package com.nexuscart.order.controller;

import com.nexuscart.dto.common.ApiResponse;
import com.nexuscart.dto.common.PageResponse;
import com.nexuscart.order.entity.Order;
import com.nexuscart.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable UUID orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by number")
    public ResponseEntity<ApiResponse<Order>> getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get current user's orders")
    public ResponseEntity<ApiResponse<PageResponse<Order>>> getUserOrders(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Order> orders = orderService.getUserOrders(UUID.fromString(userId), PageRequest.of(page, size));
        PageResponse<Order> response = PageResponse.of(orders.getContent(), page, size, orders.getTotalElements());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create new order")
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreateOrderRequest request) {

        List<OrderService.OrderItemRequest> items = request.items().stream()
                .map(i -> new OrderService.OrderItemRequest(
                        i.productId(), i.productName(), i.productSku(),
                        i.productImage(), i.quantity(), i.unitPrice()))
                .toList();

        Order order = orderService.createOrder(UUID.fromString(userId), items, request.shippingAddress());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Order created"));
    }

    @PatchMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @PathVariable UUID orderId,
            @RequestBody Map<String, String> body) {

        String reason = body.getOrDefault("reason", "Cancelled by user");
        Order order = orderService.cancelOrder(orderId, reason);

        return ResponseEntity.ok(ApiResponse.success(order, "Order cancelled"));
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable UUID orderId,
            @RequestBody Map<String, String> body) {

        Order.OrderStatus status = Order.OrderStatus.valueOf(body.get("status"));
        Order order = orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok(ApiResponse.success(order, "Status updated"));
    }

    public record CreateOrderRequest(
            List<OrderItemDto> items,
            Order.ShippingAddress shippingAddress) {
    }

    public record OrderItemDto(
            UUID productId, String productName, String productSku,
            String productImage, Integer quantity, java.math.BigDecimal unitPrice) {
    }
}
