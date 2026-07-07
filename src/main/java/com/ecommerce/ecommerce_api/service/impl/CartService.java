package com.ecommerce.ecommerce_api.service.impl;

import com.ecommerce.ecommerce_api.dto.request.CartItemRequest;
import com.ecommerce.ecommerce_api.dto.response.CartResponse;
import com.ecommerce.ecommerce_api.entity.Cart;
import com.ecommerce.ecommerce_api.entity.CartItem;
import com.ecommerce.ecommerce_api.entity.Product;
import com.ecommerce.ecommerce_api.entity.User;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.repository.CartRepository;
import com.ecommerce.ecommerce_api.repository.ProductRepository;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartResponse getCart(String email) {
        Cart cart = findCartByEmail(email);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String email, CartItemRequest request) {
        Cart cart = findCartByEmail(email);
        Product product = productRepository.findById(request.getProductId())
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Estoque insuficiente. Disponível: " + product.getStockQuantity());
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItem(String email, Long itemId, Integer quantity) {
        Cart cart = findCartByEmail(email);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado no carrinho"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(String email, Long itemId) {
        Cart cart = findCartByEmail(email);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Cart findCartByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado"));
    }

    private CartResponse toResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .total(cart.getTotal())
                .items(cart.getItems().stream().map(item ->
                        CartResponse.CartItemResponse.builder()
                                .id(item.getId())
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .unitPrice(item.getProduct().getPrice())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }
}
