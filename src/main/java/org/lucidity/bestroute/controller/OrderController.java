package org.lucidity.bestroute.controller;

import org.lucidity.bestroute.entity.request.DeliverOrderRequest;
import org.lucidity.bestroute.entity.response.ApiResponse;
import org.lucidity.bestroute.entity.model.OrderInfo;
import org.lucidity.bestroute.entity.model.OrderStatus;
import org.lucidity.bestroute.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping()
    public ApiResponse<List<OrderInfo>> getOrders(@RequestParam("status") OrderStatus status) {
        return new ApiResponse<>(orderService.getOrders(status));
    }

    @PostMapping()
    public ApiResponse<OrderInfo> addOrder(@RequestBody OrderInfo orderInfo) {
        return new ApiResponse<>(orderService.addOrders(orderInfo));
    }

    @GetMapping("/load-data")
    public ApiResponse<DeliverOrderRequest> loadLocalData() {
        return new ApiResponse<>(orderService.loadLocalData());
    }

    @GetMapping("/resolve-route-local-data")
    public ApiResponse<Object> resolveRoute() {
        return orderService.resolveBestRoute();
    }

    @GetMapping("/best-route")
    public ApiResponse<Object> resolveBestRoute(@RequestBody DeliverOrderRequest deliverOrderRequest) {
        return orderService.resolveBestRoute(deliverOrderRequest);
    }


}
