package com.ecommerce.ecommerce_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String description;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal price;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Estoque não pode ser negativo")
    private Integer stockQuantity;

    private String imageUrl;
    private String category;
}
