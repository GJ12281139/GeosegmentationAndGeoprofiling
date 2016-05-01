<%@ page import="ru.ifmo.pashaac.common.Properties" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>City boundingbox</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" charset="utf-8">

    <spring:url value="/resources/js/user-geolocation.js" var="userGeolocationJs"/>
    <spring:url value="/resources/js/map-utils.js" var="mapUtilsJs"/>
    <spring:url value="/resources/css/boundingbox.css" var="boundingboxCss"/>
    <script src="${userGeolocationJs}"></script>
    <script src="${mapUtilsJs}"></script>
    <link href="${boundingboxCss}" rel="stylesheet"/>
</head>
<body>
<div id="map"></div>
<c:choose>
    <c:when test="${not empty error}">
        <h1>${error}</h1>
    </c:when>
    <c:when test="${not empty user}">
        <script async defer
                src="https://maps.googleapis.com/maps/api/js?key=${key}&callback=mapInitialization"></script>
        <script>
            var map;
            function mapInitialization() {
                var userPos = {lat: ${user.lat}, lng: ${user.lng}};
                var mapOptions = {
                    center: userPos,
                    zoom: <%=Properties.getMapZoom()%>,
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

                <c:if test="${not empty searchers}">
                    <c:forEach var="searcher" items="${searchers}" varStatus="loop">
                        var pos = {lat: ${searcher.lat}, lng: ${searcher.lng}};
                        addMarker(pos, "${searcher.icon}", "Search radius: ${searcher.rad}", "", "", map);
                        addCircle(pos, ${searcher.rad});
                    </c:forEach>
                </c:if>

                <c:if test="${not empty places}">
                    <c:forEach var="place" items="${places}" varStatus="loop">
                        pos = {lat: ${place.searcher.lat}, lng: ${place.searcher.lng}};
                        addMarker(pos, "${place.searcher.icon}", "${place.name}", "${place.id}", "${place.address}", map);
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
