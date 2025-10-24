package com.zia.product.service.service;

import com.zia.product.service.dto.ProductRequest;
import com.zia.product.service.dto.ProductResponse;
import java.util.List;

public interface ProductService {

    ProductResponse addProduct(ProductRequest productRequest);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long productId);


    // most important
    void reduceQuantity(Long productId, Long quantity);

}