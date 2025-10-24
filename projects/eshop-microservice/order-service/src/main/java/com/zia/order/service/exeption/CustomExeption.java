package com.zia.order.service.exeption;

import lombok.Data;

@Data
public class CustomExeption extends RuntimeException {

    private String errorMessage;
    private String errorCode;
    private int status;

    public CustomExeption(String errorMessage, String errorCode, int status) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.status = status;
    }


}
