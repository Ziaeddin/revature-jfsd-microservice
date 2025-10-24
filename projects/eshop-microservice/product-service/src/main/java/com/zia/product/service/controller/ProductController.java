package com.zia.product.service.controller;

import com.zia.product.service.dto.ProductRequest;
import com.zia.product.service.dto.ProductResponse;
import com.zia.product.service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/products")
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.addProduct(productRequest);
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
        List<ProductResponse> productResponses = productService.getAllProducts();
        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Long productId){
        ProductResponse productResponse = productService.getProductById(productId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    // http://localhost:9002/api/products/1/reduce-quantrity?quantity=2
    @PutMapping("/{id}/reduce-quantity/")
    public ResponseEntity<Void> reduceQuantity(@PathVariable("id") Long productId,
                                               @RequestParam("quantity") Long quantity){
        productService.reduceQuantity(productId, quantity);
        return ResponseEntity.noContent().build();
    }

}
