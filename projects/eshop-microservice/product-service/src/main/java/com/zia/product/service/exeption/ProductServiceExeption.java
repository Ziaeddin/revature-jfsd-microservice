package com.zia.product.service.exeption;

public class ProductServiceExeption extends RuntimeException {

    private String statusCode;

    public ProductServiceExeption(String message, String statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
