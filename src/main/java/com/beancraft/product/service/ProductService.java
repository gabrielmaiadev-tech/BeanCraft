package com.beancraft.product.service;

import com.beancraft.product.domain.Product;
import com.beancraft.product.dto.ProductCreateRequest;
import com.beancraft.product.dto.ProductResponse;
import com.beancraft.product.dto.ProductUpdateRequest;
import com.beancraft.product.exception.ProductAlreadyExistsException;
import com.beancraft.product.exception.ProductNotFoundException;
import com.beancraft.product.repository.ProductRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(ProductService::toResponse).toList();
    }

    public ProductResponse findById(UUID id) {
        return toResponse(findProduct(id));
    }

    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCase(name)) {
            throw new ProductAlreadyExistsException(name);
        }
        Product product = new Product(name, request.origin().trim(), request.roast(),
                request.weightGrams(), request.price(), request.stock());
        return toResponse(repository.save(product));
    }

    @Transactional
    public ProductResponse update(UUID id, ProductUpdateRequest request) {
        Product product = findProduct(id);
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ProductAlreadyExistsException(name);
        }
        product.update(name, request.origin().trim(), request.roast(),
                request.weightGrams(), request.price(), request.stock());
        return toResponse(repository.save(product));
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(findProduct(id));
    }

    private Product findProduct(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    private static ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getOrigin(), product.getRoast(),
                product.getWeightGrams(), product.getPrice(), product.getStock(), product.getCreatedAt(), product.getUpdatedAt());
    }
}