package com.zia.order.service.service;

import com.zia.order.service.model.OrderRequest;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);
}
