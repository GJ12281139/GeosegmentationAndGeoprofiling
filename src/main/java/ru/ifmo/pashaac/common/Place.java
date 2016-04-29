package ru.ifmo.pashaac.common;

import com.google.maps.model.LatLng;

/**
 * One place description
 * Need to parse data on client JSP side
 * <p>
 * Created by Pavel Asadchiy
 * 19.04.16 22:36.
 */
public class Place {

    private final double lat;
    private final double lng;
    private final double rad; // radius around marker
    private final String icon;

    private final String placeId;
    private final String placeName;
    private final String placeType;
    private final String address;

    public Place(double lat,
                 double lng,
                 double rad,
                 String icon,
                 String placeId,
                 String placeName,
                 String placeType,
                 String address) {
        this.lat = lat;
        this.lng = lng;
        this.rad = rad;
        this.icon = icon;
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeType = placeType;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public double getRad() {
        return rad;
    }

    public String getIcon() {
        return icon;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceType() {
        return placeType;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        return placeId != null && placeId.equals(place.placeId);
    }

    @Override
    public int hashCode() {
        return placeId != null ? placeId.hashCode() : 0;
    }

    public static class Builder {

        private double lat;
        private double lng;
        private double rad;
        private String icon;

        private String placeId;
        private String placeName;
        private String placeType;
        private String address;

        public Builder setLat(double lat) {
            this.lat = lat;
            return this;
        }

        public Builder setLng(double lng) {
            this.lng = lng;
            return this;
        }

        public Builder setRad(double rad) {
            this.rad = rad;
            return this;
        }

        public Builder setIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder setPlaceId(String placeId) {
            this.placeId = placeId;
            return this;
        }

        public Builder setPlaceName(String placeName) {
            this.placeName = placeName;
            return this;
        }

        public Builder setPlaceType(String placeType) {
            this.placeType = placeType;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setLatLng(LatLng latLng) {
            this.lat = latLng.lat;
            this.lng = latLng.lng;
            return this;
        }

        public Place build() {
            return new Place(lat, lng, rad, icon, placeId, placeName, placeType, address);
        }
    }

    @Override
    public String toString() {
        return "Place{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", rad=" + rad +
                ", icon='" + icon + '\'' +
                ", placeId='" + placeId + '\'' +
                ", placeName='" + placeName + '\'' +
                ", placeType='" + placeType + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
