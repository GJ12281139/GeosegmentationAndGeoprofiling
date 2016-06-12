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
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
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

<div id="wait" style="display:none;position:absolute;z-index:1;top:15%;left:15%;padding:2px;">
    <img src='wait.gif' width="150" height="150" />
    <h2>Идёт загрузка данных или обработка запроса...<br>Пожалуйста подождите или вернитесь через 5 минут...</h2>
    <h2>Data downloading or your request handling...<br>Please wait or return in 5 minutes...</h2>
</div>


<%-- user buttons --%>
<input id="latitude" class="textbox" placeholder="широта (например, 59.957)">
<input id="longitude" class="textbox" placeholder="долгота (например, 30.307)">
<input id="city" class="textbox" placeholder="город (например, Санкт-Петербург)">
<input id="country" class="textbox" placeholder="страна (например, Россия)">
<input id="findme" class="button" value="Найти меня" type="button" onclick="geolocation()">
<input id="submit" class="button" value="Отправить" type="button" onclick="submit()">
<input id="foursquareSource" class="button" value="Данные Foursquare" type="button"
       onclick="foursquareSourceHandler()">
<input id="googleSource" class="button" value="Данные Google.Maps" type="button" onclick="googleSourceHandler()">
<input id="segmentRadiusText" class="text" type="text" value="Радиус сегмента (от-до):">
<input id="segmentsCountText" class="text" type="text" value="Кол-во сегментов 60%">
<input id="segmentsCount" type="range" value="60" oninput="segmentsCountRangeChange()">
<input id="segmentMinRadius" class="textbox" value="275" onchange="segmentMinRadiusHandler()">
<input id="segmentMaxRadius" class="textbox" value="800" onchange="segmentMaxRadiusHandler()">


<%-- Categories --%>
<input id="culture" class="button" value="Культурно-досуговые" type="button" onclick="culture()">
<input id="food" class="button" value="Зоны кафе-ресторанов" type="button" onclick="food()">
<input id="nightLife" class="button" value="Ночная жизнь" type="button" onclick="nightLife()">
<input id="sport" class="button" value="Места спорта" type="button" onclick="sport()" hidden="hidden">
<input id="auto" class="button" value="Вокруг автомобилей" type="button" onclick="auto()" hidden="hidden">


<%-- Foursquare Culture --%>
<input id="fMuseumText" class="text" value="Музеи: 15%" type="text">
<input id="fMuseum" type="range" value="15" oninput="fMuseumRangeChange()">
<input id="fParkText" class="text" value="Парки: 15%" type="text">
<input id="fPark" type="range" value="15" oninput="fParkRangeChange()">
<input id="fPlazaText" class="text" value="Площади: 15%" type="text">
<input id="fPlaza" type="range" value="15" oninput="fPlazaRangeChange()">
<input id="fSculptureGardenText" class="text" value="Скульптуры: 15%" type="text">
<input id="fSculptureGarden" type="range" value="15" oninput="fSculptureGardenRangeChange()">
<input id="fSpirtualCenterText" class="text" value="Религия: 15%" type="text">
<input id="fSpirtualCenter" type="range" value="15" oninput="fSpirtualCenterRangeChange()">
<input id="fTheaterText" class="text" value="Театры: 15%" type="text">
<input id="fTheater" type="range" value="15" oninput="fTheaterRangeChange()">
<input id="fFountainText" class="text" value="Фонтаны: 15%" type="text">
<input id="fFountain" type="range" value="15" oninput="fFountainRangeChange()">
<input id="fGardenText" class="text" value="Сады: 15%" type="text">
<input id="fGarden" type="range" value="15" oninput="fGardenRangeChange()">
<input id="fPalaceText" class="text" value="Дворцы: 15%" type="text">
<input id="fPalace" type="range" value="15" oninput="fPalaceRangeChange()">
<input id="fCastleText" class="text" value="Замки: 15%" type="text">
<input id="fCastle" type="range" value="15" oninput="fCastleRangeChange()">
<%-- Google culture --%>
<input id="gMuseumText" class="text" value="Музеи: 15%" type="text">
<input id="gMuseum" type="range" value="15" oninput="gMuseumRangeChange()">
<input id="gParkText" class="text" value="Парки: 15%" type="text">
<input id="gPark" type="range" value="15" oninput="gParkRangeChange()">
<input id="gChurchText" class="text" value="Религия: 15%" type="text">
<input id="gChurch" type="range" value="15" oninput="gChurchRangeChange()">


<%-- Foursuqare Food --%>
<input id="fAsianRestaurantText" class="text" value="Азиатские: 15%" type="text">
<input id="fAsianRestaurant" type="range" value="15" oninput="fAsianRestaurantRangeChange()">
<input id="fJapaneseRestaurantText" class="text" value="Японские: 15%" type="text">
<input id="fJapaneseRestaurant" type="range" value="15" oninput="fJapaneseRestaurantRangeChange()">
<input id="fFrenchRestaurantText" class="text" value="Французские: 15%" type="text">
<input id="fFrenchRestaurant" type="range" value="15" oninput="fFrenchRestaurantRangeChange()">
<input id="fItalianRestaurantText" class="text" value="Итальянские: 15%" type="text">
<input id="fItalianRestaurant" type="range" value="15" oninput="fItalianRestaurantRangeChange()">
<input id="fBakeryText" class="text" value="Пекарни: 15%" type="text">
<input id="fBakery" type="range" value="15" oninput="fBakeryRangeChange()">
<input id="fBistroText" class="text" value="Бистро: 15%" type="text">
<input id="fBistro" type="range" value="15" oninput="fBistroRangeChange()">
<input id="fFastFoodRestaurantText" class="text" value="Фаст-фуд: 15%" type="text">
<input id="fFastFoodRestaurant" type="range" value="15" oninput="fFastFoodRestaurantRangeChange()">
<input id="fCafeText" class="text" value="Кафе: 15%" type="text">
<input id="fCafe" type="range" value="15" oninput="fCafeRangeChange()">
<%-- Google food --%>
<input id="gCafeText" class="text" value="Кафе: 15%" type="text">
<input id="gCafe" type="range" value="15" oninput="gCafeRangeChange()">
<input id="gRestaurantText" class="text" value="Рестораны: 15%" type="text">
<input id="gRestaurant" type="range" value="15" oninput="gRestaurantRangeChange()">


<%-- Foursquare Night Life --%>
<input id="fNightLifeSpotText" class="text" value="Бары-клубы: 15%" type="text">
<input id="fNightLifeSpot" type="range" value="15" oninput="fNightLifeSpotRangeChange()">
<input id="fBowlingAlleyText" class="text" value="Боулинг: 15%" type="text">
<input id="fBowlingAlley" type="range" value="15" oninput="fBowlingAlleyRangeChange()">
<input id="fMovieTheaterText" class="text" value="Кинотеатры: 15%" type="text">
<input id="fMovieTheater" type="range" value="15" oninput="fMovieTheaterRangeChange()">
<input id="fPoolHallText" class="text" value="Бильярд: 15%" type="text">
<input id="fPoolHall" type="range" value="15" oninput="fPoolHallRangeChange()">
<%-- Google Night Life --%>
<input id="gBowlingAlleyText" class="text" value="Боулинг: 15%" type="text">
<input id="gBowlingAlley" type="range" value="15" oninput="gBowlingAlleyRangeChange()">
<input id="gMovieTheaterText" class="text" value="Кинотеатры: 15%" type="text">
<input id="gMovieTheater" type="range" value="15" oninput="gMovieTheaterRangeChange()">
<input id="gNightClubText" class="text" value="Ночные клубы: 15%" type="text">
<input id="gNightClub" type="range" value="15" oninput="gNightClubRangeChange()">


<%-- Foursquare Sport --%>
<input id="fAthleticsSportsText" class="text" value="Спорт: 15%" type="text">
<input id="fAthleticsSports" type="range" value="15" oninput="fAthleticsSportsRangeChange()">
<%-- Google Sport --%>
<input id="gGymText" class="text" value="Спортивные залы: 15%" type="text">
<input id="gGym" type="range" value="15" oninput="gGymRangeChange()">


<%-- Foursquare Auto --%>
<input id="fAutoDealershipText" class="text" value="Дилеры: 15%" type="text">
<input id="fAutoDealership" type="range" value="15" oninput="fAutoDealershipRangeChange()">
<input id="fAutoGarageText" class="text" value="Мастерские: 15%" type="text">
<input id="fAutoGarage" type="range" value="15" oninput="fAutoGarageRangeChange()">
<input id="fAutoWorkshopText" class="text" value="Сервисы: 15%" type="text">
<input id="fAutoWorkshop" type="range" value="15" oninput="fAutoWorkshopRangeChange()">
<input id="fCarWashText" class="text" value="Мойки: 15%" type="text">
<input id="fCarWash" type="range" value="15" oninput="fCarWashRangeChange()">
<%-- Google Auto --%>
<input id="gCarDealerText" class="text" value="Дилеры: 15%" type="text">
<input id="gCarDealer" type="range" value="15" oninput="gCarDealerRangeChange()">
<input id="gCarRentalText" class="text" value="Аренда: 15%" type="text">
<input id="gCarRental" type="range" value="15" oninput="gCarRentalRangeChange()">
<input id="gCarRepairText" class="text" value="Мастерскии: 15%" type="text">
<input id="gCarRepair" type="range" value="15" oninput="gCarRepairRangeChange()">
<input id="gCarWashText" class="text" value="Мойки: 15%" type="text">
<input id="gCarWash" type="range" value="15" oninput="gCarWashRangeChange()">

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
                addMarker(userPos, "${user.icon}", "You are here", "", "", "", map);
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
                addMarker(pos, "${marker.icon}", "Marker ${loop.index}, radius: ${marker.rad}", "", "", ${marker.googleRating}, map);
                addCircle(pos, ${marker.rad});
                </c:forEach>
                </c:if>

                <c:if test="${not empty google_places}">
                <c:forEach var="place" items="${google_places}" varStatus="loop">
                pos = {lat: ${place.lat}, lng: ${place.lng}};
                addMarker(pos, "${place.icon}", "${place.name}", "${place.id}", "${place.address}", ${place.googleRating}, map);
                </c:forEach>
                </c:if>

                <c:if test="${not empty foursquare_places}">
                <c:forEach var="place" items="${foursquare_places}" varStatus="loop">
                pos = {lat: ${place.lat}, lng: ${place.lng}};
                addMarker(pos, "${place.icon}", "${place.name}", "${place.id}", "${place.address}", ${place.googleRating}, map);
                </c:forEach>
                </c:if>

                <c:if test="${not empty clusters}">
                <c:forEach var="cluster" items="${clusters}" varStatus="loop">
                pos = {lat: ${cluster.lat}, lng: ${cluster.lng}};
                addMarker(pos, "${cluster.icon}", "Cluster rad " + ${cluster.rad}, "", "", "", map);
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
