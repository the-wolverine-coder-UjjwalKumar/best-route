package org.lucidity.bestroute.service;


import org.junit.jupiter.api.Test;
import org.lucidity.bestroute.entity.model.OrderInfo;
import org.lucidity.bestroute.entity.request.DeliverOrderRequest;
import org.lucidity.bestroute.entity.response.ShortestDeliveryTimeResponse;
import org.lucidity.bestroute.utils.InitializeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class OrderServiceTests {

    @Autowired
    InitializeData initializeData;

    @Autowired
    OrderService orderService;

    @Test
    void validateTest() {
        List<String> path = List.of("R1","R2","C2","C1");

        DeliverOrderRequest deliverRequest = initializeData.getLocalData();

        List<OrderInfo> orders = orderService.buildOrderDetails(deliverRequest);
        Optional<ShortestDeliveryTimeResponse> routeResponse = orderService.getBestRoute(
                deliverRequest.getCaptainDetails(), orders);

        if (routeResponse.isPresent()) {
            String predictedPath = routeResponse.get().getShortestPath();
            String inputPath = String.join(",", path);

            assert inputPath.equals(predictedPath);
        }

    }
}
