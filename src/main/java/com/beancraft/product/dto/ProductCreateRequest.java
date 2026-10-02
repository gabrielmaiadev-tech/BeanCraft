package com.beancraft.product.dto;

import com.beancraft.product.domain.RoastLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 120) String origin,
        @NotNull RoastLevel roast,
        @NotNull @Min(1) Integer weightGrams,
        @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal price,
        @NotNull @Min(0) Integer stock) {
}