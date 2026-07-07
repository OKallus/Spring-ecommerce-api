package com.ecommerce.ecommerce_api.service.impl;

import com.ecommerce.ecommerce_api.dto.request.ProductRequest;
import com.ecommerce.ecommerce_api.dto.response.ProductResponse;
import com.ecommerce.ecommerce_api.entity.Product;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> listAll(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable).map(this::toResponse);
    }

    public ProductResponse findById(Long id) {
        return toResponse(getProduct(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProduct(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(request.getCategory());
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = getProduct(id);
        product.setActive(false); // soft delete
        productRepository.save(product);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stockQuantity(p.getStockQuantity())
                .imageUrl(p.getImageUrl())
                .category(p.getCategory())
                .active(p.getActive())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
