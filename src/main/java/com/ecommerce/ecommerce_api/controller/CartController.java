package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.request.CartItemRequest;
import com.ecommerce.ecommerce_api.dto.response.CartResponse;
import com.ecommerce.ecommerce_api.service.impl.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Carrinho", description = "Gerenciamento do carrinho de compras")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Ver carrinho do usuário autenticado")
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(cartService.getCart(user.getUsername()));
    }

    @PostMapping("/items")
    @Operation(summary = "Adicionar item ao carrinho")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(user.getUsername(), request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Atualizar quantidade de um item")
    public ResponseEntity<CartResponse> updateItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateItem(user.getUsername(), itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remover item do carrinho")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(user.getUsername(), itemId));
    }
}
