package com.zia.order.service.exeption;

import com.zia.order.service.external.response.ProductErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExeptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomExeption.class)
    public ResponseEntity<ProductErrorDetail> handleOrderServiceExeption(CustomExeption ex) {
        ProductErrorDetail errorDetail = new ProductErrorDetail(ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(errorDetail, HttpStatus.valueOf(ex.getStatus()));
    }

}
