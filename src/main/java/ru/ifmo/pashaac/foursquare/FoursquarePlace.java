package ru.ifmo.pashaac.foursquare;

import fi.foyt.foursquare.api.entities.CompactVenue;
import org.springframework.data.annotation.Id;
import ru.ifmo.pashaac.common.wrapper.Searcher;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * 08.05.16 14:46.
 */
public class FoursquarePlace {

    @Id
    private final String id;
    private final String name;
    private final String placeType;
    @Nullable
    private final String phone; // formattedPhone
    @Nullable
    private final String address;
    private final String city;
    private final String country;

    private final Searcher searcher;

    @Nullable
    private final String url;
    private final int checkinsCount;
    private final int userCount;

    public FoursquarePlace(String id,
                           String name,
                           String placeType,
                           @Nullable String phone,
                           @Nullable String address,
                           String city,
                           String country,
                           Searcher searcher,
                           @Nullable String url,
                           int checkinsCount,
                           int userCount) {
        this.id = id;
        this.name = name;
        this.placeType = placeType;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
        this.searcher = searcher;
        this.url = url;
        this.checkinsCount = checkinsCount;
        this.userCount = userCount;
    }

    public FoursquarePlace(CompactVenue venue, String city, String country, String placeType, String icon) {
        this(venue.getId(),
                venue.getName().replace("\"", "\\\"").replace("\n", ""),
                placeType,
                venue.getContact().getFormattedPhone(),
                venue.getLocation().getAddress(),
                city,
                country,
                new Searcher(venue.getLocation().getLat(), venue.getLocation().getLng(), 0, icon),
                venue.getUrl(),
                venue.getStats().getCheckinsCount(),
                venue.getStats().getUsersCount());
    }

    public FoursquarePlace() { // for mongodb
        this(null, null, null, null, null, null, null, null, null, 0, 0);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public Searcher getSearcher() {
        return searcher;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    public int getCheckinsCount() {
        return checkinsCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public String getPlaceType() {
        return placeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoursquarePlace that = (FoursquarePlace) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "FoursquarePlace{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", placeType='" + placeType + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", searcher=" + searcher +
                ", url='" + url + '\'' +
                ", checkinsCount=" + checkinsCount +
                ", userCount=" + userCount +
                '}';
    }

    public static Set<FoursquarePlace> cleaner(Collection<FoursquarePlace> places) {
        return places.stream()
                .filter(place -> place.getAddress() != null && !place.getAddress().trim().isEmpty())
                .filter(place -> Character.isUpperCase(place.getName().charAt(0)))
                .filter(place -> !place.getName().equals(place.getName().toUpperCase()))
                .collect(Collectors.toSet());
    }
}
