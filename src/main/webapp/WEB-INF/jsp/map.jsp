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
    <spring:url value="/resources/css/boundingbox.css" var="boundingboxCss"/>
    <script src="${userGeolocationJs}"></script>
    <script src="${mapUtilsJs}"></script>
</head>
<body>
<div id="map"></div>
<c:choose>
    <c:when test="${not empty error}">
        <h1>${error}</h1>
    </c:when>
    <c:when test="${not empty user}">
        <link href="${boundingboxCss}" rel="stylesheet"/>
        <script async defer
                src="https://maps.googleapis.com/maps/api/js?key=${key}&callback=mapInitialization"></script>
        <script>
            var map;
            function mapInitialization() {
                var userPos = {lat: ${user.lat}, lng: ${user.lng}};
                var mapOptions = {
                    center: userPos,
                    zoom: 11,
                    scaleControl: true,
                    mapTypeControl: false
                };
                map = new google.maps.Map(document.getElementById('map'), mapOptions);

                addMarker(userPos, "${user.icon}", "You are here", "", "", map);

                <c:if test="${not empty boxes}">
                    <c:forEach var="box" items="${boxes}" varStatus="loop">
                        addRectangle(${box.northeast.lat}, ${box.southwest.lat}, ${box.northeast.lng}, ${box.southwest.lng}, map);
                    </c:forEach>
                </c:if>

                var pos;
                <c:if test="${not empty searchers}">
                    <c:forEach var="searcher" items="${searchers}" varStatus="loop">
                        pos = {lat: ${searcher.lat}, lng: ${searcher.lng}};
                        addMarker(pos, "${searcher.icon}", "Searcher ${loop.index}, radius: ${searcher.rad}", "", "", map);
                        addCircle(pos, ${searcher.rad});
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

                <c:if test="${not empty kernels}">
                    <c:forEach var="kernel" items="${kernels}" varStatus="loop">
                        pos = {lat: ${kernel.lat}, lng: ${kernel.lng}};
                        addMarker(pos, "${kernel.icon}", "Kernel #" + ${loop.index}, "", "", map);
                        addCircle(pos, ${kernel.rad});
                </c:forEach>
                </c:if>

            }
        </script>
    </c:when>
    <c:otherwise>
        <script>
            tryingGetUserGeolocationAndRedirect()
        </script>
    </c:otherwise>
</c:choose>
</body>
</html>
