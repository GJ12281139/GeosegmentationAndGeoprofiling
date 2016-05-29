<%@ page import="ru.ifmo.pashaac.common.Properties" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Geosegmentation and geoprofiling</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" charset="utf-8">

    <spring:url value="/resources/js/map-utils.js" var="mapUtilsJs"/>
    <spring:url value="/resources/js/auto.js" var="autoJs"/>
    <spring:url value="/resources/js/culture.js" var="cultureJs"/>
    <spring:url value="/resources/js/food.js" var="foodJs"/>
    <spring:url value="/resources/js/nightLife.js" var="nightLifeJs"/>
    <spring:url value="/resources/js/sport.js" var="sportJs"/>
    <spring:url value="/resources/js/userGUI.js" var="userGUIJs"/>

    <spring:url value="/resources/css/map.css" var="mapCss"/>
    <spring:url value="/resources/css/auto.css" var="autoCss"/>
    <spring:url value="/resources/css/culture.css" var="cultureCss"/>
    <spring:url value="/resources/css/food.css" var="foodCss"/>
    <spring:url value="/resources/css/nightLife.css" var="nightLifeCss"/>
    <spring:url value="/resources/css/sport.css" var="sportCss"/>
    <spring:url value="/resources/css/userGUI.css" var="userGUICss"/>

    <script src="${mapUtilsJs}"></script>
    <script src="${autoJs}"></script>
    <script src="${cultureJs}"></script>
    <script src="${foodJs}"></script>
    <script src="${nightLifeJs}"></script>
    <script src="${sportJs}"></script>
    <script src="${userGUIJs}"></script>

    <%-- TODO: what need from libs ??????? --%>
    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
    <script src="http://code.jquery.com/jquery-latest.js"></script>
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet"
          type="text/css"/>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
    <script src="http://code.jquery.com/jquery-1.10.2.js"></script>
    <script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
</head>
<body>

<%-- user buttons --%>
<input id="latitude" class="textbox" placeholder="latitude (ex. 59.957)">
<input id="longitude" class="textbox" placeholder="longitude (ex. 30.307)">
<input id="city" class="textbox" placeholder="city (ex. Saint-Petersburg)">
<input id="country" class="textbox" placeholder="country (ex. Russia)">
<input id="findme" class="button" value="Find me" type="button" onclick="geolocation()">
<input id="submit" class="button" value="Submit" type="button" onclick="submit()">
<input id="foursquareSource" class="button" value="Foursquare data source" type="button"
       onclick="foursquareSourceHandler()">
<input id="googleSource" class="button" value="Google data source" type="button" onclick="googleSourceHandler()">
<input id="segmentText" class="text" value="Segment radius range [from,to]:" style="border: none">
<input id="segmentMinRadius" class="textbox" value="275" onchange="segmentMinRadiusHandler()">
<input id="segmentMaxRadius" class="textbox" value="800" onchange="segmentMaxRadiusHandler()">


<%-- Categories --%>
<input id="culture" class="button" value="Cultural and leisure time" type="button" onclick="culture()">
<input id="food" class="button" value="Snack / lunch / dinner ..." type="button" onclick="food()">
<input id="nightLife" class="button" value="Parties / clubs / bars..." type="button" onclick="nightLife()">
<input id="sport" class="button" value="Sport places!" type="button" onclick="sport()">
<input id="auto" class="button" value="Auto-auto-auto" type="button" onclick="auto()">


<%-- Foursquare Culture --%>
<input id="fMuseumText" class="text" value="Museums: 30%" type="text">
<input id="fMuseum" type="range" value="30" oninput="fMuseumRangeChange()">
<input id="fParkText" class="text" value="Parks: 30%" type="text">
<input id="fPark" type="range" value="30" oninput="fParkRangeChange()">
<input id="fPlazaText" class="text" value="Plazas: 30%" type="text">
<input id="fPlaza" type="range" value="30" oninput="fPlazaRangeChange()">
<input id="fSculptureGardenText" class="text" value="Sculpture gardens: 30%" type="text">
<input id="fSculptureGarden" type="range" value="30" oninput="fSculptureGardenRangeChange()">
<input id="fSpirtualCenterText" class="text" value="Spirtual centers: 30%" type="text">
<input id="fSpirtualCenter" type="range" value="30" oninput="fSpirtualCenterRangeChange()">
<input id="fTheaterText" class="text" value="Theaters: 30%" type="text">
<input id="fTheater" type="range" value="30" oninput="fTheaterRangeChange()">
<input id="fFountainText" class="text" value="Fountains: 30%" type="text">
<input id="fFountain" type="range" value="30" oninput="fFountainRangeChange()">
<input id="fGardenText" class="text" value="Gardens: 30%" type="text">
<input id="fGarden" type="range" value="30" oninput="fGardenRangeChange()">
<input id="fPalaceText" class="text" value="Palaces: 30%" type="text">
<input id="fPalace" type="range" value="30" oninput="fPalaceRangeChange()">
<input id="fCastleText" class="text" value="Castles: 30%" type="text">
<input id="fCastle" type="range" value="30" oninput="fCastleRangeChange()">
<%-- Google culture --%>
<input id="gMuseumText" class="text" value="Museums: 30%" type="text">
<input id="gMuseum" type="range" value="30" oninput="gMuseumRangeChange()">
<input id="gParkText" class="text" value="Parks: 30%" type="text">
<input id="gPark" type="range" value="30" oninput="gParkRangeChange()">
<input id="gChurchText" class="text" value="Churches: 30%" type="text">
<input id="gChurch" type="range" value="30" oninput="gChurchRangeChange()">


<%-- Foursuqare Food --%>
<input id="fAsianRestaurantText" class="text" value="Asian restaurants: 30%" type="text">
<input id="fAsianRestaurant" type="range" value="30" oninput="fAsianRestaurantRangeChange()">
<input id="fJapaneseRestaurantText" class="text" value="Japan restaurants: 30%" type="text">
<input id="fJapaneseRestaurant" type="range" value="30" oninput="fJapaneseRestaurantRangeChange()">
<input id="fFrenchRestaurantText" class="text" value="French restaurants: 30%" type="text">
<input id="fFrenchRestaurant" type="range" value="30" oninput="fFrenchRestaurantRangeChange()">
<input id="fItalianRestaurantText" class="text" value="Italian restaurants: 30%" type="text">
<input id="fItalianRestaurant" type="range" value="30" oninput="fItalianRestaurantRangeChange()">
<input id="fBakeryText" class="text" value="Bakery: 30%" type="text">
<input id="fBakery" type="range" value="30" oninput="fBakeryRangeChange()">
<input id="fBistroText" class="text" value="Bistro: 30%" type="text">
<input id="fBistro" type="range" value="30" oninput="fBistroRangeChange()">
<input id="fFastFoodRestaurantText" class="text" value="Fast Food: 30%" type="text">
<input id="fFastFoodRestaurant" type="range" value="30" oninput="fFastFoodRestaurantRangeChange()">
<input id="fCafeText" class="text" value="Cafe: 30%" type="text">
<input id="fCafe" type="range" value="30" oninput="fCafeRangeChange()">
<%-- Google food --%>
<input id="gCafeText" class="text" value="Cafe: 30%" type="text">
<input id="gCafe" type="range" value="30" oninput="gCafeRangeChange()">
<input id="gRestaurantText" class="text" value="Restaurant: 30%" type="text">
<input id="gRestaurant" type="range" value="30" oninput="gRestaurantRangeChange()">


<%-- Foursquare Night Life --%>
<input id="fNightLifeSpotText" class="text" value="Bars/clubs/disco/...: 30%" type="text">
<input id="fNightLifeSpot" type="range" value="30" oninput="fNightLifeSpotRangeChange()">
<input id="fBowlingAlleyText" class="text" value="Bowling alleys: 30%" type="text">
<input id="fBowlingAlley" type="range" value="30" oninput="fBowlingAlleyRangeChange()">
<input id="fMovieTheaterText" class="text" value="Movie theaters: 30%" type="text">
<input id="fMovieTheater" type="range" value="30" oninput="fMovieTheaterRangeChange()">
<input id="fPoolHallText" class="text" value="Pool halls: 30%" type="text">
<input id="fPoolHall" type="range" value="30" oninput="fPoolHallRangeChange()">
<%-- Google Night Life --%>
<input id="gBowlingAlleyText" class="text" value="Bowling alleys: 30%" type="text">
<input id="gBowlingAlley" type="range" value="30" oninput="gBowlingAlleyRangeChange()">
<input id="gMovieTheaterText" class="text" value="Movie theaters: 30%" type="text">
<input id="gMovieTheater" type="range" value="30" oninput="gMovieTheaterRangeChange()">
<input id="gNightClubText" class="text" value="Night clubs: 30%" type="text">
<input id="gNightClub" type="range" value="30" oninput="gNightClubRangeChange()">


<%-- Foursquare Sport --%>
<input id="fAthleticsSportsText" class="text" value="Athletics and sports: 30%" type="text">
<input id="fAthleticsSports" type="range" value="30" oninput="fAthleticsSportsRangeChange()">
<%-- Google Sport --%>
<input id="gGymText" class="text" value="Gyms: 30%" type="text">
<input id="gGym" type="range" value="30" oninput="gGymRangeChange()">


<%-- Foursquare Auto --%>
<input id="fAutoDealershipText" class="text" value="Auto dealerships: 30%" type="text">
<input id="fAutoDealership" type="range" value="30" oninput="fAutoDealershipRangeChange()">
<input id="fAutoGarageText" class="text" value="Auto garages: 30%" type="text">
<input id="fAutoGarage" type="range" value="30" oninput="fAutoGarageRangeChange()">
<input id="fAutoWorkshopText" class="text" value="Auto workshops: 30%" type="text">
<input id="fAutoWorkshop" type="range" value="30" oninput="fAutoWorkshopRangeChange()">
<input id="fCarWashText" class="text" value="Car washes: 30%" type="text">
<input id="fCarWash" type="range" value="30" oninput="fCarWashRangeChange()">
<%-- Google Auto --%>
<input id="gCarDealerText" class="text" value="Car dealers: 30%" type="text">
<input id="gCarDealer" type="range" value="30" oninput="gCarDealerRangeChange()">
<input id="gCarRentalText" class="text" value="Car rental: 30%" type="text">
<input id="gCarRental" type="range" value="30" oninput="gCarRentalRangeChange()">
<input id="gCarRepairText" class="text" value="Car repairs: 30%" type="text">
<input id="gCarRepair" type="range" value="30" oninput="gCarRepairRangeChange()">
<input id="gCarWashText" class="text" value="Car washes: 30%" type="text">
<input id="gCarWash" type="range" value="30" oninput="gCarWashRangeChange()">

<div id="map"></div>
<c:choose>
    <c:when test="${not empty error}">
        <h1>${error}</h1>
    </c:when>
    <c:otherwise>
        <link href="${mapCss}" rel="stylesheet"/>
        <link href="${cultureCss}" rel="stylesheet"/>
        <link href="${foodCss}" rel="stylesheet"/>
        <link href="${nightLifeCss}" rel="stylesheet"/>
        <link href="${sportCss}" rel="stylesheet"/>
        <link href="${autoCss}" rel="stylesheet"/>
        <link href="${userGUICss}" rel="stylesheet"/>

        <script async defer
                src="https://maps.googleapis.com/maps/api/js?key=<%=System.getenv("GOOGLE_API_KEY")%>&callback=mapInitialization"></script>
        <script>
            var map;
            function mapInitialization() {
                var userPos;
                var mapOptions;
                <c:choose>
                <c:when test="${not empty user}">
                userPos = {lat: ${user.lat}, lng: ${user.lng}};
                $('#latitude').val("${user.lat}");
                $('#longitude').val("${user.lng}");
                $('#city').val("${user_city}");
                $('#country').val("${user_country}");
                mapOptions = {
                    center: userPos,
                    zoom: 10,
                    scaleControl: true,
                    mapTypeControl: false
                };
                //noinspection JSUnresolvedVariable,JSUnresolvedFunction
                map = new google.maps.Map(document.getElementById('map'), mapOptions);
                addMarker(userPos, "${user.icon}", "You are here", "", "", map);
                </c:when>
                <c:otherwise>
                userPos = {lat: 59.957570, lng: 30.307946}; // ITMO University
                mapOptions = {
                    center: userPos,
                    zoom: 3,
                    scaleControl: true,
                    mapTypeControl: false
                };
                //noinspection JSUnresolvedVariable,JSUnresolvedFunction
                map = new google.maps.Map(document.getElementById('map'), mapOptions);
                </c:otherwise>
                </c:choose>

                <c:if test="${not empty boxes}">
                <c:forEach var="box" items="${boxes}" varStatus="loop">
                addRectangle(${box.northeast.lat}, ${box.southwest.lat}, ${box.northeast.lng}, ${box.southwest.lng}, map);
                </c:forEach>
                </c:if>

                var pos;
                <c:if test="${not empty markers}">
                <c:forEach var="marker" items="${markers}" varStatus="loop">
                pos = {lat: ${marker.lat}, lng: ${marker.lng}};
                addMarker(pos, "${marker.icon}", "Marker ${loop.index}, radius: ${marker.rad}", "", "", map);
                addCircle(pos, ${marker.rad});
                </c:forEach>
                </c:if>

                <c:if test="${not empty google_places}">
                <c:forEach var="place" items="${google_places}" varStatus="loop">
                pos = {lat: ${place.lat}, lng: ${place.lng}};
                addMarker(pos, "${place.icon}", "${place.name}", "${place.id}", "${place.address}", map);
                </c:forEach>
                </c:if>

                <c:if test="${not empty foursquare_places}">
                <c:forEach var="place" items="${foursquare_places}" varStatus="loop">
                pos = {lat: ${place.lat}, lng: ${place.lng}};
                addMarker(pos, "${place.icon}", "${place.name}", "${place.id}", "${place.address}", map);
                </c:forEach>
                </c:if>

                <c:if test="${not empty clusters}">
                <c:forEach var="cluster" items="${clusters}" varStatus="loop">
                pos = {lat: ${cluster.lat}, lng: ${cluster.lng}};
                addMarker(pos, "${cluster.icon}", "Cluster rad " + ${cluster.rad}, "", "", map);
                addCircle(pos, ${cluster.rad});
                </c:forEach>
                </c:if>
            }

            function geolocation() {
                geolocationIcon("<%=Properties.getIconUser()%>");
            }

            function normalizeAttributes(attrName, attrVal) {
                return (attrVal.trim().length === 0) ? "" : attrName + attrVal;
            }
        </script>
    </c:otherwise>
</c:choose>
</body>
</html>
