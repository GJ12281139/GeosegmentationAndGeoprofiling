package ru.ifmo.pashaac.data.source;

import com.google.maps.model.PlacesSearchResult;
import fi.foyt.foursquare.api.entities.CompactVenue;
import org.springframework.data.annotation.Id;
import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.common.primitives.Icon;
import ru.ifmo.pashaac.common.primitives.Marker;
import ru.ifmo.pashaac.data.source.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.data.source.google.maps.GooglePlaceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Place abstract entity
 * <p>
 * Created by Pavel Asadchiy
 * on 30.05.16 21:02.
 */
public class Place extends Marker {

    public static final Icon GOOGLE_MAPS_ICON = Icon.VISTA_BALL_POISON_GREEN_32;
    public static final Icon FOURSQUARE_ICON = Icon.VISTA_BALL_PINK_32;

    public static final String GOOGLE_MAPS_SOURCE = "GoogleMaps";
    public static final String FOURSQUARE_SOURCE = "Foursquare";

    @Id
    @Nonnull
    private final String id;
    @Nonnull
    private final String name;
    @Nonnull
    private final String placeType;
    @Nullable
    private final String phone;
    @Nullable
    private final String address;
    @Nonnull
    private final String city;
    @Nonnull
    private final String country;
    @Nonnull
    private final String source;
    @Nullable
    private final String additionalInfo;
    @Nullable
    private final String url;

    private final double rating;


    public Place(double lat,
                 double lng,
                 double rad,
                 @Nullable String icon,
                 @Nonnull String id,
                 @Nonnull String name,
                 @Nonnull String placeType,
                 @Nullable String phone,
                 @Nullable String address,
                 @Nonnull String city,
                 @Nonnull String country,
                 @Nonnull String source,
                 @Nullable String additionalInfo,
                 @Nullable String url,
                 double rating) {
        super(lat, lng, rad, icon);
        this.id = id;
        this.name = name.replace('"', '\'').replace('\n', ' ');
        this.placeType = placeType;
        this.phone = phone;
        this.address = address == null ? null : address.replace('"', '\'');
        this.city = city;
        this.country = country;
        this.source = source;
        this.additionalInfo = additionalInfo;
        this.url = url;
        this.rating = rating;
    }

    public Place(PlacesSearchResult searchResult, BoundingBox boundingBox, GooglePlaceType placeType) {
        this(
                searchResult.geometry.location.lat,
                searchResult.geometry.location.lng,
                0,
                placeType.getIcon(),
                searchResult.placeId,
                searchResult.name,
                placeType.name(),
                null,
                searchResult.formattedAddress,
                boundingBox.getCity(),
                boundingBox.getCountry(),
                GOOGLE_MAPS_SOURCE,
                null,
                null,
                searchResult.rating
        );
    }

    public Place(CompactVenue venue, BoundingBox boundingBox, FoursquarePlaceType placeType) {
        this(
                venue.getLocation().getLat(),
                venue.getLocation().getLng(),
                0,
                placeType.getIcon(),
                venue.getId(),
                venue.getName(),
                placeType.name(),
                venue.getContact().getFormattedPhone(),
                venue.getLocation().getAddress(),
                boundingBox.getCity(),
                boundingBox.getCountry(),
                FOURSQUARE_SOURCE,
                "checkinsCoun=" + venue.getStats().getUsersCount(),
                venue.getUrl(),
                venue.getStats().getCheckinsCount()
        );
    }

    public Place() {
        this(0, 0, 0, null, "", "", "", null, null, "", "", "", null, null, 0);
    }

    public static boolean filter(Place place) {
//        return place.getAddress() != null && Character.isUpperCase(place.getName().charAt(0));
        return Character.isUpperCase(place.getName().charAt(0));
    }

    public static Collection<Place> filterAverageEmptyAddress(Collection<Place> places) {
        double avg = places.stream()
                .mapToDouble(Place::getRating)
                .average()
                .getAsDouble();
        return places.stream()
                .filter(place -> filter(place) || place.getRating() > avg / 2)
                .collect(Collectors.toList());
    }

    public static Collection<Place> filterTopRating(Collection<Place> places, int percents) {
        if (percents < 0 || percents > 100) {
            throw new IllegalStateException("percents should be between 0 and 100: percents=" + percents);
        }
        return places.stream()
                .filter(Place::filter)
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating())) // descend
                .limit((int) Math.ceil(places.size() * percents * 1.0 / 100))
                .collect(Collectors.toList());
    }

    @Nonnull
    public String getId() {
        return id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getPlaceType() {
        return placeType;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    @Nonnull
    public String getCity() {
        return city;
    }

    @Nonnull
    public String getCountry() {
        return country;
    }

    @Nonnull
    public String getSource() {
        return source;
    }

    @Nullable
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        return id.equals(place.id) && source.equals(place.source);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + source.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", placeType='" + placeType + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", source='" + source + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", rating=" + rating +
                '}';
    }
}
