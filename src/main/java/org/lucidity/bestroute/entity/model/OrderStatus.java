package org.lucidity.bestroute.entity.model;

import lombok.ToString;

@ToString
public enum OrderStatus {

    PLACED,
    PROCESSING,
    DISPATCHED,
    DELIVERED,
    CANCELLED

}
