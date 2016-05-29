package ru.ifmo.pashaac.google.maps;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import org.springframework.data.annotation.Id;
import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.common.primitives.Icon;
import ru.ifmo.pashaac.common.primitives.Marker;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Google place
 * <p>
 * Created by Pavel Asadchiy
 * 19.04.16 22:36.
 */
public class GooglePlace extends Marker {

    public static final Icon GOOGLE_ICON = Icon.VISTA_BALL_POISON_GREEN_32;

    @Id
    private final String id;        // place_id
    private final String name;
    private final String placeType;
    private final String address;   // formatted_address
    private final String phone;     // international_phone_number
    private final double googleRating;

    private final String city;
    private final String country;

    public GooglePlace(String id,
                       String name,
                       String placeType,
                       String address,
                       String phone,
                       double googleRating,
                       String city,
                       String country,
                       double lat,
                       double lng,
                       double rad,
                       String icon) {
        super(lat, lng, rad, googleRating, icon);
        this.id = id;
        this.name = name;
        this.placeType = placeType;
        this.address = address;
        this.phone = phone;
        this.googleRating = googleRating;
        this.city = city;
        this.country = country;
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
                details.geometry.location.lat,
                details.geometry.location.lng,
                0,
                icon);
    }

    public GooglePlace(String id, String placeType, BoundingBox bBox, LatLng location, String icon) {
        this(id, null, placeType, null, null, 0, bBox.getCity(), bBox.getCountry(), location.lat, location.lng, 0, icon);
    }

    public GooglePlace(PlacesSearchResult placesSearchResult, BoundingBox boundingBox, GooglePlaceType placeType) {
        this(placesSearchResult.placeId,
                placesSearchResult.name.replace("\"", "\\\"").replace("\n", ""),
                placeType.name(),
                placesSearchResult.formattedAddress == null ? "" : placesSearchResult.formattedAddress.replace("\"", "\\\""),
                "", // no phone
                placesSearchResult.rating,
                boundingBox.getCity(),
                boundingBox.getCountry(),
                placesSearchResult.geometry.location.lat,
                placesSearchResult.geometry.location.lng,
                placesSearchResult.rating,
                placeType.icon);
    }

    public GooglePlace() {
        this(null, null, null, null, null, 0, null, null, 0, 0, 0, null);
    }

    public GooglePlace(PlaceDetails details, GooglePlace place) {
        this(details, place.getPlaceType(), place.getCity(), place.getCountry(), place.getIcon());
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

    public double getGoogleRating() {
        return googleRating;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "GooglePlace{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", placeType='" + placeType + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", googleRating=" + googleRating +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public static Set<Marker> toMarkers(final Collection<GooglePlace> places) {
        return places.stream()
                .map(place -> (Marker) place)
                .collect(Collectors.toSet());
    }

    public static Set<GooglePlace> useSourceIcon(final Collection<GooglePlace> places) {
        return places.stream()
                .map(place -> new GooglePlace(
                        place.getId(),
                        place.getName(),
                        place.getPlaceType(),
                        place.getAddress(),
                        place.getPhone(),
                        place.getGoogleRating(),
                        place.getCity(),
                        place.getCountry(),
                        place.getLat(),
                        place.getLng(),
                        place.getRad(),
                        GOOGLE_ICON.getPath()))
                .collect(Collectors.toSet());
    }

    public static boolean filter(GooglePlace place) {
        return Character.isUpperCase(place.getName().charAt(0));
    }
    public static Set<GooglePlace> filterTopRating(Collection<GooglePlace> places, int percents) {
        if (percents < 0 || percents > 100) {
            throw new IllegalStateException("Percents should be between 0 and 100");
        }
        return places.stream()
                .filter(GooglePlace::filter)
                .sorted((p1, p2) -> Double.compare(p2.getGoogleRating(), p1.getGoogleRating())) // descend
                .limit((int) Math.ceil(places.size() * percents * 1.0 / 100))
                .collect(Collectors.toSet());
    }
}
