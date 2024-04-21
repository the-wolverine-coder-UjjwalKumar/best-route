package org.lucidity.bestroute.entity.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lucidity.bestroute.entity.model.Customer;
import org.lucidity.bestroute.entity.model.DeliveryCaptain;
import org.lucidity.bestroute.entity.model.RestaurantDetails;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliverOrderRequest {
    @JsonProperty("customer_data")
    private List<Customer> customers;
    @JsonProperty("restaurant_data")
    private List<RestaurantDetails> restaurantDetails;
    @JsonProperty("captain_data")
    private List<DeliveryCaptain> captainDetails;
}
