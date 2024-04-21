package org.lucidity.bestroute;

import org.junit.jupiter.api.Test;
import org.lucidity.bestroute.entity.model.OrderInfo;
import org.lucidity.bestroute.entity.request.DeliverOrderRequest;
import org.lucidity.bestroute.entity.response.ShortestDeliveryTimeResponse;
import org.lucidity.bestroute.service.OrderService;
import org.lucidity.bestroute.utils.InitializeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = BestRouteApplication.class)
class BestRouteApplicationTests {

    @Test
    public void contextLoads() {}

    @Test
    public void myTest(){
        String s = "HELLO WORLD";
        assert s.equals("HELLO WORLD");
    }

}
