<%--&lt;%&ndash;<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>&ndash;%&gt;--%>
<%--����--%>
<%--&lt;%&ndash;<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>&ndash;%&gt;--%>
<%--<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>--%>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%--<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>--%>
<%--<!DOCTYPE html>--%>
<%--<html>--%>
<%--<head>--%>
<%--����--%>
<%--<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">--%>
<%--����<title>&lt;fmt:message&gt; Demo</title>--%>
<%--��--%>
<%--</head>--%>
<%--��--%>
<%--<body>--%>
<%--����<h1>&lt;fmt:message&gt; Demo</h1>--%>
<%--����<fmt:bundle basename="application">--%>
<%--���������The messages displayed using &lt;fmt:message&gt; tags:<br/><br/>--%>
<%--���������<fmt:message key="min.markers.in.circle" var="iconAzure"/><br/>--%>
<%--���������<fmt:message key="icon.azure.48"/><br>�� �</fmt:bundle>--%>

<%--</body>--%>
<%--</html>--%>


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
<fmt:bundle basename="application">
    <fmt:message key="icon.azure.48" var="azureIc"/>
    <fmt:message key="icon.pink.32" var="pinkIc"/>
    <fmt:message key="map.zoom" var="mapZoom"/>
    <fmt:message key="marker.clickable" var="clickable"/>
</fmt:bundle>
<c:choose>
    <%--@elvariable id="error" type="java"--%>
    <c:when test="${not empty error}">
        <h1>${error.message}</h1>
    </c:when>
    <%--@elvariable id="model" type="java"--%>
    <c:when test="${not empty model}">
        <%--@elvariable id="key" type="java"--%>
        <script async defer
                src="https://maps.googleapis.com/maps/api/js?key=${key}&callback=mapInitialization"></script>
        <script>
            var map;
            function mapInitialization() {
                var userPos = {lat: ${model.userGeolocation.lat}, lng: ${model.userGeolocation.lng}};
                var mapOptions = {
                    center: userPos,
                    zoom: ${mapZoom},
                    scaleControl: true,
                    mapTypeControl: false
                };

                map = new google.maps.Map(document.getElementById('map'), mapOptions);

                addMarker(userPos, ${azureIc}, "You are here", map, ${clickable});
                <c:forEach var="marker" items="${model.markers}" varStatus="loop">
                    var markerPos = {lat: ${marker.lat}, lng: ${marker.lng}};
                    addMarker(markerPos, ${pinkIc}, 'Marker #' + ${loop.index}, map);
                    addCircle(markerPos, ${marker.rad});
                </c:forEach>

                addRectangle(${model.boundingbox.northeast.lat}, ${model.boundingbox.southwest.lat},
                        ${model.boundingbox.northeast.lng}, ${model.boundingbox.southwest.lng}, map);
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
