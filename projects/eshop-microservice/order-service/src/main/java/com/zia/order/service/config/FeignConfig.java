package com.zia.order.service.config;

import com.zia.order.service.external.decoder.CustomErrorDecoder;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder feignDecoder() {
        return new CustomErrorDecoder();
    }
}
