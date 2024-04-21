package org.lucidity.bestroute.utils;

import org.lucidity.bestroute.entity.model.GeoLocation;

public class HaverSineUtils {

    public static final double averageSpeed = 20; // 20Km/Hr

    // fetching distance between two geo location.
    public static double distanceTo(GeoLocation location1, GeoLocation location2) {
        // Radius of the Earth in kilometers
        double earthRadius = 6371;

        double lat1 = Math.toRadians(location1.getLatitude());
        double lon1 = Math.toRadians(location1.getLongitude());
        double lat2 = Math.toRadians(location2.getLatitude());
        double lon2 = Math.toRadians(location2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;

    }
}
