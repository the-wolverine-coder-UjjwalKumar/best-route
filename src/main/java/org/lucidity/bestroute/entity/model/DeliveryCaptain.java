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
public class DeliveryCaptain {

    @JsonProperty("delivery_captain_id")
    private long deliveryCaptainId;
    @JsonProperty("captain_name")
    private String captainName;
    @JsonProperty("contact_details")
    private ContactDetails contactDetails;
    @JsonProperty("address")
    private Address address;
    @JsonProperty("captain_live_location")
    private GeoLocation captainLiveLocation;
    @JsonProperty("available")
    private boolean available;

    public void updateLiveLocation(GeoLocation captainLiveLocation) {
        this.captainLiveLocation = captainLiveLocation;
    }

    public void updateAvailability(boolean available) {
        this.available = available;
    }
}
