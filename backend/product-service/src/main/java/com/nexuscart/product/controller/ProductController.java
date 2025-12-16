package com.nexuscart.product.controller;

import com.nexuscart.dto.common.ApiResponse;
import com.nexuscart.dto.common.PageResponse;
import com.nexuscart.product.entity.Product;
import com.nexuscart.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all active products with pagination")
    public ResponseEntity<ApiResponse<PageResponse<Product>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productService.getAllProducts(pageable);
        PageResponse<Product> response = PageResponse.of(
                products.getContent(), page, size, products.getTotalElements());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable UUID id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU")
    public ResponseEntity<ApiResponse<Product>> getProductBySku(@PathVariable String sku) {
        Product product = productService.getProductBySku(sku);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<ApiResponse<PageResponse<Product>>> getProductsByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByCategory(categoryId, pageable);
        PageResponse<Product> response = PageResponse.of(
                products.getContent(), page, size, products.getTotalElements());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured products")
    public ResponseEntity<ApiResponse<PageResponse<Product>>> getFeaturedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getFeaturedProducts(pageable);
        PageResponse<Product> response = PageResponse.of(
                products.getContent(), page, size, products.getTotalElements());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products")
    public ResponseEntity<ApiResponse<PageResponse<Product>>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.searchProducts(q, pageable);
        PageResponse<Product> response = PageResponse.of(
                products.getContent(), page, size, products.getTotalElements());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter products by price range")
    public ResponseEntity<ApiResponse<PageResponse<Product>>> filterProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        if (categoryId != null && minPrice != null && maxPrice != null) {
            products = productService.filterByCategoryAndPrice(categoryId, minPrice, maxPrice, pageable);
        } else if (minPrice != null && maxPrice != null) {
            products = productService.filterByPrice(minPrice, maxPrice, pageable);
        } else {
            products = productService.getAllProducts(pageable);
        }

        PageResponse<Product> response = PageResponse.of(
                products.getContent(), page, size, products.getTotalElements());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/batch")
    @Operation(summary = "Get multiple products by IDs")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByIds(@RequestBody List<UUID> ids) {
        List<Product> products = productService.getProductsByIds(ids);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PostMapping
    @Operation(summary = "Create product")
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Product created"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable UUID id, @RequestBody Product updates) {
        Product updated = productService.updateProduct(id, updates);
        return ResponseEntity.ok(ApiResponse.success(updated, "Product updated"));
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish product")
    public ResponseEntity<ApiResponse<Product>> publishProduct(@PathVariable UUID id) {
        Product published = productService.publishProduct(id);
        return ResponseEntity.ok(ApiResponse.success(published, "Product published"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted"));
    }
}
