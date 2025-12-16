package com.nexuscart.inventory.controller;

import com.nexuscart.dto.common.ApiResponse;
import com.nexuscart.inventory.entity.Inventory;
import com.nexuscart.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory for a product")
    public ResponseEntity<ApiResponse<List<Inventory>>> getProductInventory(@PathVariable UUID productId) {
        List<Inventory> inventory = inventoryService.getProductInventory(productId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @GetMapping("/product/{productId}/available")
    @Operation(summary = "Get available quantity for a product")
    public ResponseEntity<ApiResponse<Integer>> getAvailableQuantity(@PathVariable UUID productId) {
        Integer quantity = inventoryService.getAvailableQuantity(productId);
        return ResponseEntity.ok(ApiResponse.success(quantity));
    }

    @GetMapping("/product/{productId}/check")
    @Operation(summary = "Check if product is in stock")
    public ResponseEntity<ApiResponse<Boolean>> checkStock(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "1") int quantity) {
        boolean inStock = inventoryService.isInStock(productId, quantity);
        return ResponseEntity.ok(ApiResponse.success(inStock));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items")
    public ResponseEntity<ApiResponse<List<Inventory>>> getLowStockItems() {
        List<Inventory> items = inventoryService.getLowStockItems();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @PostMapping("/add")
    @Operation(summary = "Add stock to inventory")
    public ResponseEntity<ApiResponse<Inventory>> addStock(@RequestBody Map<String, Object> request) {
        UUID productId = UUID.fromString((String) request.get("productId"));
        UUID warehouseId = UUID.fromString((String) request.get("warehouseId"));
        int quantity = (Integer) request.get("quantity");

        Inventory inventory = inventoryService.addStock(productId, warehouseId, quantity);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Stock added"));
    }

    @PutMapping("/update")
    @Operation(summary = "Update stock quantity")
    public ResponseEntity<ApiResponse<Inventory>> updateStock(@RequestBody Map<String, Object> request) {
        UUID productId = UUID.fromString((String) request.get("productId"));
        UUID warehouseId = UUID.fromString((String) request.get("warehouseId"));
        int quantity = (Integer) request.get("quantity");

        Inventory inventory = inventoryService.updateStock(productId, warehouseId, quantity);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Stock updated"));
    }
}
