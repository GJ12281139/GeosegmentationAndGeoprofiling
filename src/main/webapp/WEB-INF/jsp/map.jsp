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
    <%--@elvariable id="error" type="java"--%>
    <c:when test="${not empty error}">
        <h1>${error.error}</h1>
    </c:when>
    <%--@elvariable id="model" type="java"--%>
    <c:when test="${not empty model}">
        <%--@elvariable id="key" type="java"--%>
        <script async defer
                src="https://maps.googleapis.com/maps/api/js?key=${key}&callback=mapInitialization"></script>
        <script>
            var map;
            function mapInitialization() {
                var userPos = {lat: ${model.user.lat}, lng: ${model.user.lng}};
                var mapOptions = {
                    center: userPos,
                    zoom: <%=Properties.getMapZoom()%>,
                    scaleControl: true,
                    mapTypeControl: false
                };
                map = new google.maps.Map(document.getElementById('map'), mapOptions);

                addMarker(userPos, "${model.user.icon}", "${model.user.placeName}", "${model.user.placeId}", "${model.user.address}", map);
                <c:forEach var="marker" items="${model.box.places}" varStatus="loop">
                    var markerPos = {lat: ${marker.lat}, lng: ${marker.lng}};
                    addMarker(markerPos, "${marker.icon}", "${marker.placeName}", "${marker.placeId}", "${marker.address}", map);
                    addCircle(markerPos, ${marker.rad});
                </c:forEach>

                <c:if test="${not empty box}">
                    addRectangle(${model.box.northeast.lat}, ${model.box.southwest.lat}, ${model.box.northeast.lng}, ${model.box.southwest.lng}, map);
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
