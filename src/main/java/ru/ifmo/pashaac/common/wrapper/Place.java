package ru.ifmo.pashaac.common.wrapper;

import org.springframework.data.annotation.Id;

/**
 * One place description
 * Need to parse data on client JSP side
 * <p>
 * Created by Pavel Asadchiy
 * 19.04.16 22:36.
 */
public class Place {

    @Id
    private final String id;        // place_id in Google API
    private final String name;      // place name
    private final String type;      // should be substr of table
    private final String address;   // formatted_address
    private final String phone;     // international_phone_number
    private final String rating;    // place rating

    private final String region;    // should be substr of table
    private final String country;   // should be substr of table

    private final Searcher searcher;    // coordinates / icon / radius

    public Place(String id,
                 String name,
                 String type,
                 String address,
                 String phone,
                 String rating,
                 String region,
                 String country,
                 Searcher searcher) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.phone = phone;
        this.rating = rating;
        this.region = region;
        this.country = country;
        this.searcher = searcher;
    }

    public Place(String id, String placeType, String region, String country, Searcher searcher) {
        this(id, null, placeType, null, null, null, region, country, searcher);
    }

    public Place() {
        this(null, null, null, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        return id != null && id.equals(place.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getRating() {
        return rating;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public Searcher getSearcher() {
        return searcher;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", rating='" + rating + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", searcher=" + searcher +
                '}';
    }
}
