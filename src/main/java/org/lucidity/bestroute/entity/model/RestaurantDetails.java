package org.lucidity.bestroute.entity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantDetails {
    @JsonProperty("restaurant_id")
    private long restaurantId;
    @JsonProperty("restaurant_name")
    private String restaurantName;
    private Address address;
    @JsonProperty("contact_details")
    private ContactDetails contactDetails;
    @JsonProperty("avg_preparation_time")
    private double avgPreparationTime;
}
