package org.lucidity.bestroute.entity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderInfo {

    @JsonProperty("order_id")
    private long orderId;
    @JsonProperty("restaurant_id")
    private long restaurantId;
    @JsonProperty("custom_id")
    private long customerId;
    @JsonProperty("status")
    private OrderStatus status;
    @JsonProperty("order_details")
    private Map<String, String> orderDetails;
    @JsonProperty("destination_address")
    private Address destinationAddress;

    public void updateOrderStatus(OrderStatus status) {
        this.status = status;
    }
}
