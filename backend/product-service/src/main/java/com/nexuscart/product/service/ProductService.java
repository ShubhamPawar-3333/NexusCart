package com.nexuscart.product.service;

import com.nexuscart.product.entity.Product;
import com.nexuscart.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByStatus(Product.ProductStatus.ACTIVE, pageable);
    }

    public Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Page<Product> getProductsByCategory(UUID categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Page<Product> getFeaturedProducts(Pageable pageable) {
        return productRepository.findByFeaturedTrue(pageable);
    }

    public Page<Product> getVendorProducts(UUID vendorId, Pageable pageable) {
        return productRepository.findByVendorId(vendorId, pageable);
    }

    public Page<Product> searchProducts(String query, Pageable pageable) {
        return productRepository.searchProducts(query, pageable);
    }

    public Page<Product> filterByPrice(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    public Page<Product> filterByCategoryAndPrice(UUID categoryId, BigDecimal minPrice,
            BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByCategoryAndPriceRange(categoryId, minPrice, maxPrice, pageable);
    }

    public List<Product> getProductsByIds(List<UUID> ids) {
        return productRepository.findByIdIn(ids);
    }

    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.existsBySku(product.getSku())) {
            throw new RuntimeException("Product SKU already exists");
        }
        product.setStatus(Product.ProductStatus.DRAFT);
        log.info("Creating product: {}", product.getName());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID id, Product updates) {
        Product existing = getProduct(id);

        if (updates.getName() != null)
            existing.setName(updates.getName());
        if (updates.getDescription() != null)
            existing.setDescription(updates.getDescription());
        if (updates.getPrice() != null)
            existing.setPrice(updates.getPrice());
        if (updates.getCompareAtPrice() != null)
            existing.setCompareAtPrice(updates.getCompareAtPrice());
        if (updates.getCostPrice() != null)
            existing.setCostPrice(updates.getCostPrice());
        if (updates.getBrand() != null)
            existing.setBrand(updates.getBrand());
        if (updates.getImages() != null)
            existing.setImages(updates.getImages());
        if (updates.getThumbnailUrl() != null)
            existing.setThumbnailUrl(updates.getThumbnailUrl());
        if (updates.getWeight() != null)
            existing.setWeight(updates.getWeight());
        if (updates.getDimensions() != null)
            existing.setDimensions(updates.getDimensions());
        if (updates.getStatus() != null)
            existing.setStatus(updates.getStatus());
        if (updates.getTags() != null)
            existing.setTags(updates.getTags());
        existing.setFeatured(updates.isFeatured());

        log.info("Updated product: {}", id);
        return productRepository.save(existing);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = getProduct(id);
        product.setStatus(Product.ProductStatus.DISCONTINUED);
        productRepository.save(product);
        log.info("Discontinued product: {}", id);
    }

    @Transactional
    public Product publishProduct(UUID id) {
        Product product = getProduct(id);
        product.setStatus(Product.ProductStatus.ACTIVE);
        log.info("Published product: {}", id);
        return productRepository.save(product);
    }
}
