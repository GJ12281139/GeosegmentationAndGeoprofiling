<%@ page import="ru.ifmo.pashaac.common.Properties" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Geosegmentation and geoprofiling</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" charset="utf-8">

    <spring:url value="/resources/js/user-geolocation.js" var="userGeolocationJs"/>
    <spring:url value="/resources/js/map-utils.js" var="mapUtilsJs"/>
    <spring:url value="/resources/css/map.css" var="mapCss"/>
    <spring:url value="/resources/js/culture.js" var="cultureJs"/>
    <spring:url value="/resources/css/culture.css" var="cultureCss"/>
    <spring:url value="/resources/js/food.js" var="foodJs"/>
    <spring:url value="/resources/css/food.css" var="foodCss"/>
    <spring:url value="/resources/js/nightLife.js" var="nightLifeJs"/>
    <spring:url value="/resources/css/nightLife.css" var="nightLifeCss"/>
    <spring:url value="/resources/js/sport.js" var="sportJs"/>
    <spring:url value="/resources/css/sport.css" var="sportCss"/>
    <spring:url value="/resources/js/auto.js" var="autoJs"/>
    <spring:url value="/resources/css/auto.css" var="autoCss"/>

    <script src="${userGeolocationJs}"></script>
    <script src="${mapUtilsJs}"></script>
    <script src="${cultureJs}"></script>
    <script src="${foodJs}"></script>
    <script src="${nightLifeJs}"></script>
    <script src="${sportJs}"></script>
    <script src="${autoJs}"></script>

    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
    <script src="http://code.jquery.com/jquery-latest.js"></script>
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet"
          type="text/css"/>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
</head>
<body>

<input id="latitude" class="textbox" placeholder="latitude (ex. 77.77)" type="text">
<input id="longitude" class="textbox" placeholder="longitude (ex. 77.77)" type="text">
<input id="city" class="textbox" placeholder="city (ex. Saint-Petersburg)" type="text">
<input id="country" class="textbox" placeholder="country (ex. Russia)" type="text">
<input id="findme" class="button" value="Find me" type="button" onclick="geolocation()">
<input id="submit" class="button" value="Submit" type="button" onclick="submit()">

<%-- Categories --%>
<input id="cultureB" class="button" value="Cultural and leisure time" type="button" onclick="culturalLeisure()">
<input id="foodB" class="button" value="Snack / lunch / dinner ..." type="button" onclick="food()">
<input id="nightLifeB" class="button" value="Parties / clubs / bars..." type="button" onclick="nightLife()">
<input id="sportB" class="button" value="Sport places!" type="button" onclick="sport()">
<input id="autoB" class="button" value="Auto-auto-auto" type="button" onclick="auto()">

<%-- Culture --%>
<input id="museumText" class="text" value="Museums: 80%" type="text">
<input id="museum" type="range" value="80" min="0" max="100" onchange="museumsRangeChange()"
       oninput="museumsRangeChange()">
<input id="parkText" class="text" value="Parks: 80%" type="text">
<input id="park" type="range" value="80" min="0" max="100" onchange="parksRangeChange()" oninput="parksRangeChange()">
<input id="plazaText" class="text" value="Plazas: 80%" type="text">
<input id="plaza" type="range" value="80" min="0" max="100" onchange="plazaRangeChange()" oninput="plazaRangeChange()">
<input id="sculptureGardenText" class="text" value="Sculpture gardens: 80%" type="text">
<input id="sculptureGarden" type="range" value="80" min="0" max="100" onchange="sculptureGardenRangeChange()"
       oninput="sculptureGardenRangeChange()">
<input id="spirtualCenterText" class="text" value="Spirtual centers: 80%" type="text">
<input id="spirtualCenter" type="range" value="80" min="0" max="100" onchange="spirtualCenterRangeChange()"
       oninput="spirtualCenterRangeChange()">
<input id="theaterText" class="text" value="Theaters: 80%" type="text">
<input id="theater" type="range" value="80" min="0" max="100" onchange="theaterRangeChange()"
       oninput="theaterRangeChange()">
<input id="fountainText" class="text" value="Fountains: 80%" type="text">
<input id="fountain" type="range" value="80" min="0" max="100" onchange="fountainRangeChange()"
       oninput="fountainRangeChange()">
<input id="gardenText" class="text" value="Gardens: 80%" type="text">
<input id="garden" type="range" value="80" min="0" max="100" onchange="gardenRangeChange()"
       oninput="gardenRangeChange()">
<input id="palaceText" class="text" value="Palaces: 80%" type="text">
<input id="palace" type="range" value="80" min="0" max="100" onchange="palaceRangeChange()"
       oninput="palaceRangeChange()">
<input id="castleText" class="text" value="Castles: 80%" type="text">
<input id="castle" type="range" value="80" min="0" max="100" onchange="castleRangeChange()"
       oninput="castleRangeChange()">

<%-- Food --%>
<input id="foodText" class="text" value="Cafe/restaurants/...: 80%" type="text">
<input id="food" type="range" value="80" min="0" max="100" onchange="foodRangeChange()" oninput="foodRangeChange()">

<%-- Night Life --%>
<input id="nightLifeText" class="text" value="Bars/clubs/disco/...: 80%" type="text">
<input id="nightLife" type="range" value="80" min="0" max="100" onchange="nightLifeRangeChange()"
       oninput="nightLifeRangeChange()">
<input id="bowlingAlleyText" class="text" value="Bowling alleys: 80%" type="text">
<input id="bowlingAlley" type="range" value="80" min="0" max="100" onchange="bowlingAlleyRangeChange()"
       oninput="bowlingAlleyRangeChange()">
<input id="movieTheaterText" class="text" value="Movie theaters: 80%" type="text">
<input id="movieTheater" type="range" value="80" min="0" max="100" onchange="movieTheaterRangeChange()"
       oninput="movieTheaterRangeChange()">
<input id="poolHallText" class="text" value="Pool halls: 80%" type="text">
<input id="poolHall" type="range" value="80" min="0" max="100" onchange="poolHallRangeChange()"
       oninput="poolHallRangeChange()">

<%-- Sport --%>
<input id="athleticsSportsText" class="text" value="Athletics and sports: 80%" type="text">
<input id="athleticsSports" type="range" value="80" min="0" max="100" onchange="athleticsSportsRangeChange()"
       oninput="athleticsSportsRangeChange()">

<%-- Auto --%>
<input id="autoDealershipText" class="text" value="Auto dealerships: 80%" type="text">
<input id="autoDealership" type="range" value="80" min="0" max="100" onchange="autoDealershipRangeChange()"
       oninput="autoDealershipRangeChange()">
<input id="autoGarageText" class="text" value="Auto garages: 80%" type="text">
<input id="autoGarage" type="range" value="80" min="0" max="100" onchange="autoGarageRangeChange()"
       oninput="autoGarageRangeChange()">
<input id="autoWorkshopText" class="text" value="Auto workshops: 80%" type="text">
<input id="autoWorkshop" type="range" value="80" min="0" max="100" onchange="autoWorkshopRangeChange()"
       oninput="autoWorkshopRangeChange()">
<input id="carWashText" class="text" value="Car washes: 80%" type="text">
<input id="carWash" type="range" value="80" min="0" max="100" onchange="carWashRangeChange()"
       oninput="carWashRangeChange()">

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
