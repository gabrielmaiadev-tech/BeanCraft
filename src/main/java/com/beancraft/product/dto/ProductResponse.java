package com.beancraft.product.dto;

import com.beancraft.product.domain.RoastLevel;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String origin,
        RoastLevel roast,
        Integer weightGrams,
        BigDecimal price,
        Integer stock,
        Instant createdAt,
        Instant updatedAt) {
}