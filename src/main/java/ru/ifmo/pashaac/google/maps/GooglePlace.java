package ru.ifmo.pashaac.google.maps;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import org.springframework.data.annotation.Id;
import ru.ifmo.pashaac.common.wrapper.BoundingBox;
import ru.ifmo.pashaac.common.wrapper.Searcher;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Google place
 * <p>
 * Created by Pavel Asadchiy
 * 19.04.16 22:36.
 */
public class GooglePlace {

    @Id
    private final String id;        // place_id
    private final String name;
    private final String placeType;
    private final String address;   // formatted_address
    private final String phone;     // international_phone_number
    private final double rating;

    private final String city;
    private final String country;

    private final Searcher searcher;

    public GooglePlace(String id,
                       String name,
                       String placeType,
                       String address,
                       String phone,
                       double rating,
                       String city,
                       String country,
                       Searcher searcher) {
        this.id = id;
        this.name = name;
        this.placeType = placeType;
        this.address = address;
        this.phone = phone;
        this.rating = rating;
        this.city = city;
        this.country = country;
        this.searcher = searcher;
    }

    public GooglePlace(PlaceDetails details, String placeType, String city, String country, String icon) {
        this(details.placeId,
                details.name.replace("\"", "\\\"").replace("\n", ""),
                placeType,
                details.formattedAddress.replace("\"", "\\\""),
                details.internationalPhoneNumber,
                details.rating,
                city,
                country,
                new Searcher(details.geometry.location.lat, details.geometry.location.lng, 0, icon));
    }

    public GooglePlace(String id, String placeType, BoundingBox bBox, LatLng location, String icon) {
        this(id, null, placeType, null, null, 0, bBox.getCity(), bBox.getCountry(),
                new Searcher(location.lat, location.lng, 0, icon));
    }

    public GooglePlace() {
        this(null, null, null, null, null, 0, null, null, null);
    }

    public GooglePlace(PlaceDetails details, GooglePlace place) {
        this(details, place.getPlaceType(), place.getCity(), place.getCountry(), place.getSearcher().getIcon());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GooglePlace place = (GooglePlace) o;

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

    public String getPlaceType() {
        return placeType;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public double getRating() {
        return rating;
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

    @Override
    public String toString() {
        return "GooglePlace{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", placeType='" + placeType + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", rating='" + rating + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", searcher=" + searcher +
                '}';
    }

    public static Set<GooglePlace> cleaner(Collection<GooglePlace> places) {
        return places.stream()
                .filter(place -> place.getAddress() != null && !place.getAddress().trim().isEmpty())
                .filter(place -> Character.isUpperCase(place.getName().charAt(0)))
                .filter(place -> !place.getName().equals(place.getName().toUpperCase()))
                .filter(place -> !containsCamelCase(place.getName().split("[^\\w']+")))
                .collect(Collectors.toSet());
    }

    private static boolean containsCamelCase(String[] words) {
        for (String word : words) {
            int upperCaseChars = 0;
            int alphaCount = 0;
            for (int i = 0; i < word.length(); i++) {
                if (Character.isAlphabetic(word.charAt(i))) {
                    ++alphaCount;
                    if (Character.isUpperCase(word.charAt(i))) {
                        ++upperCaseChars;
                    }
                }
            }
            if (upperCaseChars > 1 && upperCaseChars < alphaCount) {
                return true;
            }
        }
        return false;
    }
}
