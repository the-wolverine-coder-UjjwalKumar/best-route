package org.lucidity.bestroute.service;

import lombok.extern.slf4j.Slf4j;
import org.lucidity.bestroute.entity.model.*;
import org.lucidity.bestroute.entity.request.DeliverOrderRequest;
import org.lucidity.bestroute.entity.response.ApiResponse;
import org.lucidity.bestroute.entity.response.ShortestDeliveryTimeResponse;
import org.lucidity.bestroute.utils.InitializeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.lucidity.bestroute.constants.Constants.*;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private InitializeData initializeData;

    @Autowired
    private PathFindByTime pathFindByTime;

    public Map<String, RestaurantDetails> restaurantInfo;

    public List<OrderInfo> getOrders(OrderStatus status) {
        try {
            // can be fetch from DB or data files
            List<OrderInfo> orders = new ArrayList<OrderInfo>();
            if (!Objects.isNull(status)) {
                return orders.stream().filter(orderInfo ->
                        orderInfo.getStatus().equals(status)).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Unable to fetch orders, due to :: ", e);
        }
        return null;
    }

    public OrderInfo addOrders(OrderInfo orderInfo) {
        return orderInfo;
    }

    public DeliverOrderRequest loadLocalData() {
        return initializeData.getLocalData();
    }

    public ApiResponse<Object> resolveBestRoute() {
        return resolveBestRoute(loadLocalData());
    }

    public ApiResponse<Object> resolveBestRoute(DeliverOrderRequest deliverOrderRequest) {
        try {

            if (Objects.nonNull(deliverOrderRequest)) {

                List<OrderInfo> orders = buildOrderDetails(deliverOrderRequest);
                Optional<ShortestDeliveryTimeResponse> routeResponse = getBestRoute(
                        deliverOrderRequest.getCaptainDetails(), orders);

                return routeResponse.<ApiResponse<Object>>map(ApiResponse::new).orElseGet(ApiResponse::new);

            }

            return new ApiResponse<>(400, HttpStatus.BAD_REQUEST.name());

        } catch (Exception e) {
            log.error("unable to resolve best route ", e);
            return new ApiResponse<>(500, HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
    }

    public Optional<ShortestDeliveryTimeResponse> getBestRoute(List<DeliveryCaptain> captainDetails, List<OrderInfo> orders) {
        try {
            if (!CollectionUtils.isEmpty(captainDetails) && !CollectionUtils.isEmpty(orders)) {
                DeliveryCaptain deliveryCaptain = getDeliveryCaptain(captainDetails);

                return pathFindByTime.findBestRoute(deliveryCaptain, orders, restaurantInfo);
            }
        } catch (Exception e) {
            log.error("unable to process the getBestRoute, due to : ", e);
        }
        return Optional.empty();
    }

    private DeliveryCaptain getDeliveryCaptain(List<DeliveryCaptain> captainDetails) {
        // filtering out the available captains which are ready to take orders
        List<DeliveryCaptain> availableCaptains = captainDetails.stream().filter(DeliveryCaptain::isAvailable)
                .toList();

        Random random = new Random();
        int idx = random.nextInt(availableCaptains.size());
        return availableCaptains.get(idx);

    }

    public List<OrderInfo> buildOrderDetails(DeliverOrderRequest deliverOrderRequest) {
        try {
            restaurantInfo = getRestaurantInfo(deliverOrderRequest);

            List<OrderInfo> orderInfoList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(deliverOrderRequest.getCustomers())) {
                long orderId = 9988L;
                for (Customer customer : deliverOrderRequest.getCustomers()) {

                    OrderInfo orderInfo = OrderInfo.builder()
                            .orderId(orderId++)
                            .restaurantId(getRestaurantId(restaurantInfo))
                            .customerId(customer.getCustomerId())
                            .status(OrderStatus.PLACED)
                            .destinationAddress(customer.getAddress())
                            .build();

                    orderInfoList.add(orderInfo);
                }
            }

            return orderInfoList;

        } catch (Exception e) {
            log.error("unable to build order details, due to : ", e);
        }
        return new ArrayList<>();
    }

    private long getRestaurantId(Map<String, RestaurantDetails> restaurantInfo) {
        List<String> restaurantIds = new ArrayList<>(restaurantInfo.keySet());
        Random random = new Random();

        // providing equal chance for each restaurant to serve order
        int idx = random.nextInt(restaurantIds.size());
        return Long.parseLong(restaurantIds.get(idx));
    }

    private Map<String, RestaurantDetails> getRestaurantInfo(DeliverOrderRequest deliverOrderRequest) {
        try {
            if (!CollectionUtils.isEmpty(deliverOrderRequest.getRestaurantDetails())) {
                return deliverOrderRequest.getRestaurantDetails().stream().collect(Collectors.toMap(
                        restaurant -> String.valueOf(restaurant.getRestaurantId()),
                        restaurant -> restaurant
                ));
            }
        } catch (Exception e) {
            log.error("unable to add restaurant info, due to :  ", e);
        }
        return new HashMap<>();
    }

    private Map<String, Customer> getCustomerInfo(DeliverOrderRequest deliverOrderRequest) {
        try {
            if (!CollectionUtils.isEmpty(deliverOrderRequest.getCustomers())) {
                return deliverOrderRequest.getCustomers().stream()
                        .collect(Collectors.toMap(
                                customer -> String.valueOf(customer.getCustomerId()),
                                customer -> customer
                        ));
            }

        } catch (Exception e) {
            log.error("unable to add customer info, due to :  ", e);
        }
        return new HashMap<>();
    }

    private void assignAvailableDeliveryCaptain(Map<String, Map<String, GeoLocation>> geoLocationMap,
                                                List<DeliveryCaptain> captainDetails,
                                                Map<DeliveryCaptain, Map<String, Map<String, GeoLocation>>> captainBucket) {
        try {
            List<DeliveryCaptain> availableCaptains = captainDetails.stream().filter(DeliveryCaptain::isAvailable)
                    .toList();

            // assuming one delivery captain AMAN is available,
            // hence not adding a scheduler which will check after 5 min who all are available and do we have any orders to assign

            if (!CollectionUtils.isEmpty(availableCaptains)) {
                for (DeliveryCaptain availableCaptain : availableCaptains) {

                    // mark as busy
                    availableCaptain.updateAvailability(false);
                    captainBucket.put(availableCaptain, geoLocationMap);

                }
            }


        } catch (Exception e) {
            log.error("unable to assign available captains, due to ", e);
        }
    }

    private void addRecentOrdersServedLocation(Map<String, Map<String, GeoLocation>> geoLocationMap,
                                               List<RestaurantDetails> restaurantDetails) {
        try {
            if (!CollectionUtils.isEmpty(restaurantDetails)) {
                int i = 1;
                for (RestaurantDetails restaurant : restaurantDetails) {
                    Map<String, GeoLocation> locationMap = geoLocationMap.getOrDefault((ORDER + i),
                            new HashMap<>());

                    locationMap.put(RESTAURANT + i, restaurant.getAddress().getLocation());
                    geoLocationMap.put(ORDER + i, locationMap);
                    i++;
                }
            }
        } catch (Exception e) {
            log.error("unable to add recent orders customer location, due to : ", e);
        }
    }

    private void addRecentOrdersCustomerLocation(Map<String, Map<String, GeoLocation>> geoLocationMap,
                                                 List<Customer> customers) {
        try {
            if (!CollectionUtils.isEmpty(customers)) {
                int i = 1;
                for (Customer customer : customers) {
                    String key = ORDER + i;

                    Map<String, GeoLocation> map = geoLocationMap.getOrDefault(key, new HashMap<>());
                    map.put((CUSTOMER + 1), customer.getAddress().getLocation());
                    geoLocationMap.put(key, map);

                    i++;
                }
            }
        } catch (Exception e) {
            log.error("unable to add recent orders customer location, due to : ", e);
        }
    }
}
