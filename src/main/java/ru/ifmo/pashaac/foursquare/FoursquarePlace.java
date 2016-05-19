package ru.ifmo.pashaac.foursquare;

import fi.foyt.foursquare.api.entities.CompactVenue;
import org.springframework.data.annotation.Id;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.primitives.Icon;
import ru.ifmo.pashaac.common.primitives.Marker;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * 08.05.16 14:46.
 */
public class FoursquarePlace extends Marker {

    public static final Icon FOURSQUARE_ICON = Icon.VISTA_BALL_PINK_32;

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
                           double lat,
                           double lng,
                           double rad,
                           String icon,
                           @Nullable String url,
                           int checkinsCount,
                           int userCount) {
        super(lat, lng, rad, icon);
        this.id = id;
        this.name = name;
        this.placeType = placeType;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
        this.url = url;
        this.checkinsCount = checkinsCount;
        this.userCount = userCount;
    }

    public FoursquarePlace(CompactVenue venue, String city, String country, String placeType, String icon) {
        this(venue.getId(),
                venue.getName().replace("\"", "\\\"").replace("\n", ""),
                placeType,
                venue.getContact().getFormattedPhone(),
                venue.getLocation().getAddress() == null ? null : venue.getLocation().getAddress().replace("\"", "\\\""),
                city,
                country,
                venue.getLocation().getLat(),
                venue.getLocation().getLng(),
                0,
                icon,
                venue.getUrl(),
                venue.getStats().getCheckinsCount(),
                venue.getStats().getUsersCount());
    }

    public FoursquarePlace() { // for mongodb
        this(null, null, null, null, null, null, null, 0, 0, 0, null, null, 0, 0);
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
                ", url='" + url + '\'' +
                ", checkinsCount=" + checkinsCount +
                ", userCount=" + userCount +
                '}';
    }

    public static boolean filter(FoursquarePlace place) {
        return place.getAddress() != null && !place.getAddress().trim().isEmpty() && Character.isUpperCase(place.getName().charAt(0));
    }

    public static Set<FoursquarePlace> filterAverage(final Collection<FoursquarePlace> places) {
        double sum = places.stream()
                .mapToLong(FoursquarePlace::getCheckinsCount)
                .sum();
        final double avg = sum * 1.0 / places.size();
        return places.stream()
                .filter(place -> place.getCheckinsCount() > avg)
                .collect(Collectors.toSet());
    }

    public static Set<FoursquarePlace> filterAverageEmptyAddress(final Collection<FoursquarePlace> places) {
        double sum = places.stream()
                .mapToLong(FoursquarePlace::getCheckinsCount)
                .sum();
        final double avg = sum * 1.0 / places.size();
        return places.stream()
                .filter(place -> filter(place) || place.getCheckinsCount() > avg)
                .collect(Collectors.toSet());
    }


    public static Set<FoursquarePlace> filterTopCheckinsPercent(final Collection<FoursquarePlace> places, int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalStateException("Percent should be between 0 and 100");
        }
        int count = (int) Math.ceil(places.size() * percent * 1.0 / 100);
        return places.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getCheckinsCount(), p1.getCheckinsCount()))
                .limit(count)
                .collect(Collectors.toSet());
    }

    public static Set<FoursquarePlace> filterLongDistancePlaces(final Collection<FoursquarePlace> places) {
        final HashSet<FoursquarePlace> goodPlaces = new HashSet<>();
        for (FoursquarePlace place : places) {
            int neighborsCount = 0;
            for (FoursquarePlace neighbor : places) {
                double distance = GeoMath.distance(place.getLat(), place.getLng(), neighbor.getLat(), neighbor.getLng());
                if (distance < Properties.getClusterMaxRadius()) {
                    ++neighborsCount;
                }
                if (neighborsCount > Properties.getClusterMinPlaces()) {
                    goodPlaces.add(place);
                    break;
                }
            }
        }
        return goodPlaces;
    }

}
