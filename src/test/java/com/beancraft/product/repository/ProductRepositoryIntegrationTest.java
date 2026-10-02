package com.beancraft.product.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beancraft.product.domain.Product;
import com.beancraft.product.domain.RoastLevel;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class ProductRepositoryIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("beancraft_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ProductRepository repository;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void savesAndFindsProductByNameIgnoringCase() {
        Product saved = repository.saveAndFlush(new Product("Mogiana", "Brasil", RoastLevel.LIGHT,
                250, new BigDecimal("39.90"), 8));

        assertTrue(repository.existsByNameIgnoreCase("mogiana"));
        assertTrue(repository.existsByNameIgnoreCaseAndIdNot("Mogiana", java.util.UUID.randomUUID()));
        assertEquals(saved.getId(), repository.findById(saved.getId()).orElseThrow().getId());
    }
}