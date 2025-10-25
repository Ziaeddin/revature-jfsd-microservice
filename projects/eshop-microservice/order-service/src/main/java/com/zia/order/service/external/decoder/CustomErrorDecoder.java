package com.zia.order.service.external.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zia.order.service.exeption.CustomExeption;
import com.zia.order.service.external.response.ProductErrorDetail;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {


    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            ProductErrorDetail productErrorDetail = mapper.readValue(response.body().asInputStream(), ProductErrorDetail.class);
            System.out.println(productErrorDetail.toString());
            return new CustomExeption(productErrorDetail.getMessage()
            , productErrorDetail.getStatusCode()
                    , response.status());
        } catch (Exception e) {
            return new CustomExeption(e.getMessage(),
                    "INTERNAL_SERVER_ERROR",
                    response.status());
        }

    }
}
