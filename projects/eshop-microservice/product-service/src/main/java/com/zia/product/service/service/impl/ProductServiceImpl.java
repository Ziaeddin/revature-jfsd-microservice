package com.zia.product.service.service.impl;

import com.zia.product.service.dto.ProductRequest;
import com.zia.product.service.dto.ProductResponse;
import com.zia.product.service.entity.Product;
import com.zia.product.service.repository.ProductRepository;
import com.zia.product.service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//
import static org.springframework.beans.BeanUtils.copyProperties;
//
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductResponse addProduct(ProductRequest productRequest) {
        Product product = new Product();
        copyProperties(productRequest, product);
        Product savedProduct = productRepository.save(product);
        ProductResponse productResponse = new ProductResponse();
        copyProperties(savedProduct, productResponse);
        return productResponse;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        List<ProductResponse> productResponses = productRepository.findAll().stream().map(product -> {
            ProductResponse productResponse = new ProductResponse();
            copyProperties(product, productResponse);
            return productResponse;
        }).toList();
        return productResponses;
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        ProductResponse productResponse = new ProductResponse();
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        copyProperties(product, productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(Long productId, Long quantity) {

    }
}
