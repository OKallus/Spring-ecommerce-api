package com.ecommerce.ecommerce_api.service.impl;

import com.ecommerce.ecommerce_api.dto.response.OrderResponse;
import com.ecommerce.ecommerce_api.entity.*;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.repository.OrderRepository;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Transactional
    public OrderResponse createFromCart(String email) {
        Cart cart = cartService.findCartByEmail(email);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Carrinho está vazio");
        }

        User user = cart.getUser();

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para: " + product.getName());
            }
            // Deduz estoque
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

            return OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
        }).collect(Collectors.toList());

        BigDecimal total = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);

        Order saved = orderRepository.save(order);
        cartService.clearCart(cart);

        return toResponse(saved);
    }

    public Page<OrderResponse> listUserOrders(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::toResponse);
    }

    public OrderResponse findById(Long id, String email) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + id));

        if (!order.getUser().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Pedido não encontrado: " + id);
        }
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(item ->
                        OrderResponse.OrderItemResponse.builder()
                                .id(item.getId())
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .subtotal(item.getSubtotal())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }
}
