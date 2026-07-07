package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.response.OrderResponse;
import com.ecommerce.ecommerce_api.service.impl.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Criação e histórico de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar pedido a partir do carrinho atual")
    public ResponseEntity<OrderResponse> create(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createFromCart(user.getUsername()));
    }

    @GetMapping
    @Operation(summary = "Listar histórico de pedidos do usuário")
    public ResponseEntity<Page<OrderResponse>> listOrders(
            @AuthenticationPrincipal UserDetails user,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.listUserOrders(user.getUsername(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar um pedido específico")
    public ResponseEntity<OrderResponse> getOrder(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id, user.getUsername()));
    }
}
