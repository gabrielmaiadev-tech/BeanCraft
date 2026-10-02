package com.beancraft.product.exception;

public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(String name) {
        super("Já existe um produto com o nome: " + name);
    }
}