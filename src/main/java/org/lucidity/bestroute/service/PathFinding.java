package org.lucidity.bestroute.service;

import org.lucidity.bestroute.entity.model.DeliveryCaptain;
import org.lucidity.bestroute.entity.model.OrderInfo;
import org.lucidity.bestroute.entity.response.ShortestDeliveryTimeResponse;

import java.util.List;
import java.util.Optional;

public interface PathFinding {
    Optional<ShortestDeliveryTimeResponse> findPath(DeliveryCaptain deliveryCaptain, List<OrderInfo> orders);
}
