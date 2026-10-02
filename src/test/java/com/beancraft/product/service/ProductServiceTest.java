package com.beancraft.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beancraft.product.domain.Product;
import com.beancraft.product.domain.RoastLevel;
import com.beancraft.product.dto.ProductCreateRequest;
import com.beancraft.product.dto.ProductUpdateRequest;
import com.beancraft.product.exception.ProductAlreadyExistsException;
import com.beancraft.product.exception.ProductNotFoundException;
import com.beancraft.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private UUID id;
    private Product product;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        product = product(id, "Serra do Caparaó", "Brasil", RoastLevel.MEDIUM, 250, "42.90", 12);
    }

    @Test
    void findAllReturnsProductResponses() {
        when(repository.findAll()).thenReturn(List.of(product));

        var result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(id, result.getFirst().id());
        assertEquals("Serra do Caparaó", result.getFirst().name());
    }

    @Test
    void findByIdReturnsProductWhenItExists() {
        when(repository.findById(id)).thenReturn(Optional.of(product));

        var result = service.findById(id);

        assertEquals(id, result.id());
        assertEquals(new BigDecimal("42.90"), result.price());
    }

    @Test
    void findByIdThrowsWhenProductDoesNotExist() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.findById(id));
    }

    @Test
    void createPersistsProductWhenNameIsAvailable() {
        var request = new ProductCreateRequest("Serra do Caparaó", "Brasil", RoastLevel.MEDIUM, 250,
                new BigDecimal("42.90"), 12);
        when(repository.existsByNameIgnoreCase(request.name())).thenReturn(false);
        when(repository.save(any(Product.class))).thenReturn(product);

        var result = service.create(request);

        assertEquals(id, result.id());
        verify(repository).save(any(Product.class));
    }

    @Test
    void createRejectsDuplicateName() {
        var request = new ProductCreateRequest(" Serra do Caparaó ", "Brasil", RoastLevel.MEDIUM, 250,
                new BigDecimal("42.90"), 12);
        when(repository.existsByNameIgnoreCase("Serra do Caparaó")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> service.create(request));
        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void updateChangesProductWhenItExistsAndNameIsAvailable() {
        var request = new ProductUpdateRequest("Mogiana", "Brasil", RoastLevel.DARK, 500,
                new BigDecimal("58.00"), 7);
        when(repository.findById(id)).thenReturn(Optional.of(product));
        when(repository.existsByNameIgnoreCaseAndIdNot(request.name(), id)).thenReturn(false);
        when(repository.save(product)).thenReturn(product);

        var result = service.update(id, request);

        assertEquals("Mogiana", result.name());
        assertEquals(RoastLevel.DARK, result.roast());
        assertEquals(new BigDecimal("58.00"), result.price());
    }

    @Test
    void updateThrowsWhenProductDoesNotExist() {
        var request = new ProductUpdateRequest("Mogiana", "Brasil", RoastLevel.DARK, 500,
                new BigDecimal("58.00"), 7);
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.update(id, request));
    }

    @Test
    void updateRejectsNameAlreadyUsedByAnotherProduct() {
        var request = new ProductUpdateRequest("Mogiana", "Brasil", RoastLevel.DARK, 500,
                new BigDecimal("58.00"), 7);
        when(repository.findById(id)).thenReturn(Optional.of(product));
        when(repository.existsByNameIgnoreCaseAndIdNot(request.name(), id)).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> service.update(id, request));
        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void deleteRemovesExistingProduct() {
        when(repository.findById(id)).thenReturn(Optional.of(product));

        service.delete(id);

        verify(repository).delete(product);
    }

    @Test
    void deleteThrowsWhenProductDoesNotExist() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.delete(id));
        verify(repository, never()).delete(any(Product.class));
    }

    private static Product product(UUID id, String name, String origin, RoastLevel roast, int weight,
                                   String price, int stock) {
        Product product = new Product(name, origin, roast, weight, new BigDecimal(price), stock);
        Instant timestamp = Instant.parse("2025-01-01T00:00:00Z");
        ReflectionTestUtils.setField(product, "id", id);
        ReflectionTestUtils.setField(product, "createdAt", timestamp);
        ReflectionTestUtils.setField(product, "updatedAt", timestamp);
        return product;
    }
}