package org.lucidity.bestroute.service;

import lombok.extern.slf4j.Slf4j;
import org.lucidity.bestroute.constants.Constants;
import org.lucidity.bestroute.entity.model.*;
import org.lucidity.bestroute.entity.response.ShortestDeliveryTimeResponse;
import org.lucidity.bestroute.utils.HaverSineUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PathFindByTime implements PathFinding {

    Map<String, RestaurantDetails> restaurantInfo;

    public Optional<ShortestDeliveryTimeResponse> findBestRoute(DeliveryCaptain deliveryCaptain, List<OrderInfo> orders,
                                                                Map<String, RestaurantDetails> restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
        return findPath(deliveryCaptain, orders);
    }

    @Override
    public Optional<ShortestDeliveryTimeResponse> findPath(DeliveryCaptain deliveryCaptain, List<OrderInfo> orders) {
        try {

            // Node, GraphEdgeDetails -> Destination,Weight
            Map<String, List<GraphEdgeDetails>> adjList = new HashMap<>();
            buildDestinationGraph(deliveryCaptain, orders, adjList);

            log.info("Adj list :: \n{}", adjList.entrySet());

            ShortestDeliveryTimeResponse response = shortestTimeDeliveryPath(adjList, deliveryCaptain.getCaptainName(), Arrays.asList("R1", "C1", "C2", "R2"));
            log.info("Response :: \n{}", response);

            return Optional.of(response);
        } catch (Exception e) {
            log.error("unable to process findPath for deliveryCaptain, id =  {}, due to : ",
                    deliveryCaptain.getDeliveryCaptainId(), e);
        }
        return Optional.empty();
    }

    private Map<String, List<GraphEdgeDetails>> buildDestinationGraph(DeliveryCaptain deliveryCaptain,
                                                                      List<OrderInfo> orders, Map<String, List<GraphEdgeDetails>> adjList) {
        try {
            // for appending the initial delivery captain location to all restaurant where oder has to pick
            int i = 1;
            for (RestaurantDetails restaurant : restaurantInfo.values()) {

                GeoLocation captainLiveLocation = deliveryCaptain.getCaptainLiveLocation();
                GeoLocation restaurantLocation = restaurant.getAddress().getLocation();

                double avgTimeReq = HaverSineUtils.distanceTo(captainLiveLocation, restaurantLocation)
                        / HaverSineUtils.averageSpeed;

                // appending for delivery captain
                List<GraphEdgeDetails> graphEdgeDetailsList = adjList.getOrDefault(deliveryCaptain.getCaptainName(), new ArrayList<>());
                GraphEdgeDetails graphEdgeDetails = GraphEdgeDetails.builder().destination(Constants.RESTAURANT + i).weight(avgTimeReq).build();
                graphEdgeDetailsList.add(graphEdgeDetails);
                adjList.put(deliveryCaptain.getCaptainName(), graphEdgeDetailsList);

                // appending same for reverse for R1 to delivery captain
                List<GraphEdgeDetails> graphEdgeDetailsResList = adjList.getOrDefault(Constants.RESTAURANT + i, new ArrayList<>());
                GraphEdgeDetails graphEdgeDetailsRes = GraphEdgeDetails.builder().destination(deliveryCaptain.getCaptainName()).weight(avgTimeReq).build();
                graphEdgeDetailsResList.add(graphEdgeDetailsRes);
                adjList.put(Constants.RESTAURANT + i, graphEdgeDetailsResList);

                i++;

            }

            // for appending c1, c2 -> r1 , r2
            i = 1;
            for (OrderInfo order : orders) {
                int k = 1;
                for (Map.Entry<String, RestaurantDetails> entry : restaurantInfo.entrySet()) {
                    RestaurantDetails restaurantDetails = entry.getValue();

                    GeoLocation restaurant = restaurantDetails.getAddress().getLocation();
                    GeoLocation deliveryLocation = order.getDestinationAddress().getLocation();

                    double avgTimeReq = HaverSineUtils.distanceTo(restaurant, deliveryLocation) / HaverSineUtils.averageSpeed
                            + restaurantDetails.getAvgPreparationTime();

                    // appending for C1
                    List<GraphEdgeDetails> graphEdgeDetailsList = adjList.getOrDefault(Constants.CUSTOMER + i, new ArrayList<>());
                    GraphEdgeDetails graphEdgeDetails = GraphEdgeDetails.builder().destination(Constants.RESTAURANT + k).weight(avgTimeReq).build();
                    graphEdgeDetailsList.add(graphEdgeDetails);
                    adjList.put(Constants.CUSTOMER + i, graphEdgeDetailsList);

                    // appending same for reverse R1
                    List<GraphEdgeDetails> graphEdgeDetailsResList = adjList.getOrDefault(Constants.RESTAURANT + k, new ArrayList<>());
                    GraphEdgeDetails graphEdgeDetailsRes = GraphEdgeDetails.builder().destination(Constants.CUSTOMER + i).weight(avgTimeReq).build();
                    graphEdgeDetailsResList.add(graphEdgeDetailsRes);
                    adjList.put(Constants.RESTAURANT + k, graphEdgeDetailsResList);

                    k++;
                }
                i++;
            }

            // C1 reachable from C2, vice versa
            for (int j = 0; j < orders.size(); j++) {
                for (int k = 0; k < orders.size(); k++) {
                    if (k != j) {


                        GeoLocation location1 = orders.get(j).getDestinationAddress().getLocation();
                        GeoLocation location2 = orders.get(k).getDestinationAddress().getLocation();

                        double avgTimeReq = HaverSineUtils.distanceTo(location1, location2) / HaverSineUtils.averageSpeed;

                        // appending for C1 - C2, C2 - C1
                        List<GraphEdgeDetails> graphEdgeDetailsList = adjList.getOrDefault(Constants.CUSTOMER + (j + 1), new ArrayList<>());
                        GraphEdgeDetails graphEdgeDetails = GraphEdgeDetails.builder().destination(Constants.CUSTOMER + (k + 1)).weight(avgTimeReq).build();
                        graphEdgeDetailsList.add(graphEdgeDetails);
                        adjList.put(Constants.CUSTOMER + (j + 1), graphEdgeDetailsList);

                    }
                }
            }

            // for R1 reachable to R2 and vice versa
            i = 1;
            for (String restaurantKey1 : restaurantInfo.keySet()) {
                int k = 1;
                for (String restaurantKey2 : restaurantInfo.keySet()) {
                    if (!restaurantKey1.equals(restaurantKey2)) {
                        GeoLocation location1 = restaurantInfo.get(restaurantKey1).getAddress().getLocation();
                        GeoLocation location2 = restaurantInfo.get(restaurantKey2).getAddress().getLocation();

                        double avgTimeReq = HaverSineUtils.distanceTo(location1, location2) / HaverSineUtils.averageSpeed;

                        List<GraphEdgeDetails> graphEdgeDetailsList = adjList.getOrDefault(Constants.RESTAURANT + i, new ArrayList<>());
                        GraphEdgeDetails graphEdgeDetails = GraphEdgeDetails.builder().destination(Constants.RESTAURANT + k).weight(avgTimeReq).build();
                        graphEdgeDetailsList.add(graphEdgeDetails);
                        adjList.put(Constants.RESTAURANT + i, graphEdgeDetailsList);
                    }
                    k++;
                }
                i++;
            }
        } catch (Exception e) {
            log.error("unable to build the graph for incoming orders, due to : ", e);
        }
        return new HashMap<>();
    }


    public static Map<String, Double> dijkstra(Map<String, List<GraphEdgeDetails>> adjList, String start) {
        Map<String, Double> distances = new HashMap<>();
        PriorityQueue<Map.Entry<String, Double>> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

        // Initialize distances with infinity for all nodes except start node
        for (String node : adjList.keySet()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        priorityQueue.add(new AbstractMap.SimpleEntry<>(start, 0.0));

        while (!priorityQueue.isEmpty()) {
            Map.Entry<String, Double> entry = priorityQueue.poll();
            String currentNode = entry.getKey();
            double currentDistance = entry.getValue();

            // If current distance is greater than stored distance, skip
            if (currentDistance > distances.get(currentNode)) {
                continue;
            }

            // Explore neighbors of current node
            for (GraphEdgeDetails neighbor : adjList.get(currentNode)) {
                String nextNode = neighbor.getDestination();
                double edgeWeight = neighbor.getWeight();
                double distance = currentDistance + edgeWeight;

                // Update distance if new distance is shorter
                if (distance < distances.get(nextNode)) {
                    distances.put(nextNode, distance);
                    priorityQueue.add(new AbstractMap.SimpleEntry<>(nextNode, distance));
                }
            }
        }

        return distances;
    }

    public ShortestDeliveryTimeResponse shortestTimeDeliveryPath(Map<String, List<GraphEdgeDetails>> adjList, String start, List<String> deliveryLocations) {
        List<String> shortestPath = new ArrayList<>();
        double shortestTime = Double.POSITIVE_INFINITY;

        // Generate permutations of delivery locations
        List<List<String>> permutations = generatePermutations(deliveryLocations);

        // Iterate through each permutation and find the shortest path
        for (List<String> permutation : permutations) {
            double totalTime = calculateTotalTime(adjList, start, permutation);
            if (totalTime < shortestTime) {
                shortestTime = totalTime;
                shortestPath = permutation;
            }
        }

        return ShortestDeliveryTimeResponse.builder().shortestPath(String.join(",", shortestPath))
                .deliveryTime(String.valueOf(shortestTime)).build();

    }

    private static double calculateTotalTime(Map<String, List<GraphEdgeDetails>> adjList, String start, List<String> deliveryLocations) {
        double totalTime = 0.0;
        String currentLocation = start;

        for (String destination : deliveryLocations) {
            double shortestDistance = dijkstra(adjList, currentLocation).get(destination);
            totalTime += shortestDistance;
            currentLocation = destination;
        }

        return totalTime;
    }

    public static List<List<String>> generatePermutations(List<String> deliveryLocations) {
        List<List<String>> permutations = new ArrayList<>();
        generatePermutationsHelper(deliveryLocations, 0, permutations);
        return permutations;
    }

    private static void generatePermutationsHelper(List<String> deliveryLocations, int index, List<List<String>> permutations) {
        if (index == deliveryLocations.size()) {
            permutations.add(new ArrayList<>(deliveryLocations));
            return;
        }

        for (int i = index; i < deliveryLocations.size(); i++) {
            Collections.swap(deliveryLocations, i, index);
            generatePermutationsHelper(deliveryLocations, index + 1, permutations);
            Collections.swap(deliveryLocations, i, index);
        }
    }
}
