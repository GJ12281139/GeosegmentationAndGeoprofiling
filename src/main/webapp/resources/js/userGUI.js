// Find me button - user geolocations
var user = null;
function geolocationIcon(icon) {
    if (user != null) {
        user.setMap(null);
    }
    var json;
    if ($('#latitude').val().length > 0 && $('#longitude').val().length > 0) {
        json = {
            "lat": $('#latitude').val(),
            "lng": $('#longitude').val()
        };
        doGeolocation(json, icon);
        return;
    }
    if ($('#city').val().length > 0) {
        json = {
            "city": $('#city').val(),
            "country": $('#country').val()
        };
        doGeolocation(json, icon);
        return;
    }

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (pos) {
            $('#latitude').val(pos.coords.latitude);
            $('#longitude').val(pos.coords.longitude);
            var json = {
                "lat": pos.coords.latitude,
                "lng": pos.coords.longitude
            };
            doGeolocation(json, icon);
        }, function () {
            handleGeolocationError(true)
        });
    } else {
        handleGeolocationError(false);
    }
}

function doGeolocation(json, icon) {
    $.ajax({
        url: '/geolocation',
        data: JSON.stringify(json),
        type: "POST",
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',

        beforeSend: function (xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        },
        success: function (result) {
            if ($('#latitude').val().length === 0 && $('#longitude').val().length === 0) {
                $('#latitude').val((result.southwest.lat + result.northeast.lat) / 2);
                $('#longitude').val((result.southwest.lng + result.northeast.lng) / 2);
            }
            map.setZoom(12);
            map.setCenter({lat: parseFloat($('#latitude').val()), lng: parseFloat($('#longitude').val())});
            user = addUserMarker(map.center, icon, "Вы здесь\nYou are here", map);
            $('#city').val(result.city);
            $('#country').val(result.country);
        },
        error: function (data) {
            alert(data.responseText);
            console.log(data);
        }
    });
}

function handleGeolocationError(browserHasGeolocation) {
    var msg = 'Введите ваши координаты или город в текстовые поле слева.\n\nAdd your geolocation by hands in text fields on left side.';
    alert(browserHasGeolocation ? 'Сервис геолокации не может получить ваши координаты.\n\nThe Geolocation service failed.\n\n' + msg
        : 'Вша браузер не поддерживает функции геолокации.\n\nYour browser does not support geolocation.\n\n' + msg);
}

// Sumbit - places painting and clearing, clusters, boundingboxes! Very big usability
function submit() {

    if (!autoPressed && !culturePressed && !foodPressed && !nightLifePressed && !sportPressed) {
        alert("Выберите категорию из списка слева.\n\nChoose category from left side list.");
        return;
    }

    var json = {
        "lat": $('#latitude').val(),
        "lng": $('#longitude').val(),
        "city": $('#city').val(),
        "country": $('#country').val(),
        "source": googleSourcePressed ? "GoogleMaps" : (foursquareSourcePressed ? "Foursquare" : null),
        "category": getPressedCategory(),
        "percents": getPressedPercents(),
        "segmentMinRadius": $('#segmentMinRadius').val(),
        "segmentMaxRadius": $('#segmentMaxRadius').val(),
        "segmentsCountPercent": $('#segmentsCount').val(),
        "algorithm": "BLACK_HOLE_TOP_RATING"
    };
    segmentation(json);
}

function segmentation(json) {
    $.ajax({
        url: "/segmentation",
        data: JSON.stringify(json),
        type: "POST",
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',

        beforeSend: function (xhr) {
            $("#wait").css("display", "block");
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        },
        success: function (result) {
            $("#wait").css("display", "none");
            // clearBoundingboxes();
            // fillBoundingboxes(result); // TODO: delete before release
            clearPlaces();
            fillPlaces(result);
            clearClusters();
            fillClusters(result);
            map.setZoom(12);
        },
        error: function (data) {
            $("#wait").css("display", "none");
            alert(data.responseText);
            console.log(data);
        }
    });
}

// category help functions
// places
var places = [];
function clearPlaces() {
    for (var i = 0; i < places.length; i++) {
        places[i].setMap(null);
    }
    places = [];
}

function fillPlaces(result) {
    for (var i = 0; i < result.places.length; i++) {
        var place = result.places[i];
        places.push(addMarker({
            lat: place.lat,
            lng: place.lng
        }, place.icon, place.name, place.id, place.address, place.rating, map));
    }
    for (i = 0; i < result.clusters.length; i++) {
        var cluster = result.clusters[i];
        for (var j = 0; j < cluster.places.length; j++) {
            place = cluster.places[j];
            places.push(addMarker({
                lat: place.lat,
                lng: place.lng
            }, place.icon, place.name, place.id, place.address, place.rating, map));
        }
    }
}
// clusters
var clusters = [];
var clustersCircle = [];
function clearClusters() {
    for (var i = 0; i < clusters.length; i++) {
        clusters[i].setMap(null);
        clustersCircle[i].setMap(null);
    }
    clusters = [];
    clustersCircle = [];
}

function fillClusters(result) {
    for (var i = 0; i < result.clusters.length; i++) {
        var cluster = result.clusters[i];
        clusters.push(addMarker({lat: cluster.lat, lng: cluster.lng}, cluster.icon, cluster.message, "", "", "", map));
        clustersCircle.push(addCircle({lat: cluster.lat, lng: cluster.lng}, cluster.rad));
    }
}
// boundingboxes
var boundingboxes = [];
function clearBoundingboxes() {
    for (var i = 0; i < boundingboxes.length; i++) {
        boundingboxes[i].setMap(null);
    }
    boundingboxes = [];
}

function fillBoundingboxes(result) {
    for (var i = 0; i < result.boundingBoxes.length; i++) {
        var box = result.boundingBoxes[i];
        boundingboxes.push(addRectangle(box.northeast.lat, box.southwest.lat, box.northeast.lng, box.southwest.lng, map))
    }
}

// category handler
function getPressedCategory() {
    if (autoPressed) {
        return "auto";
    }
    if (culturePressed) {
        return "culture";
    }
    if (foodPressed) {
        return "food";
    }
    if (nightLifePressed) {
        return "nightLife";
    }
    if (sportPressed) {
        return "sport";
    }
    return null;
}

function getPressedPercents() {
    if (autoPressed) {
        return autoPercents();
    }
    if (culturePressed) {
        return culturePercents();
    }
    if (foodPressed) {
        return foodPercents();
    }
    if (nightLifePressed) {
        return nightLifePercents();
    }
    if (sportPressed) {
        return sportPercents();
    }
    return null;
}

// clear all ranges/filters
function primitivePlacesOff() {
    fCultureOff();
    gCultureOff();
    fFoodOff();
    gFoodOff();
    fNightLifeOff();
    gNightLifeOff();
    fSportOff();
    gSportOff();
    fAutoOff();
    gAutoOff();
}

// Source handlers
var googleSourcePressed = false;
var foursquareSourcePressed = true;
function googleSourceHandler() {
    primitivePlacesOff();
    if (googleSourcePressed) {
        document.getElementById("googleSource").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
        googleSourcePressed = false;
    } else {
        document.getElementById("foursquareSource").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
        document.getElementById("googleSource").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
        googleSourcePressed = true;
        foursquareSourcePressed = false;
    }
}

function foursquareSourceHandler() {
    primitivePlacesOff();
    if (foursquareSourcePressed) {
        document.getElementById("foursquareSource").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
        foursquareSourcePressed = false;
    } else {
        document.getElementById("googleSource").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
        document.getElementById("foursquareSource").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
        foursquareSourcePressed = true;
        googleSourcePressed = false;
    }
}

function segmentsCountRangeChange() {
    document.getElementById("segmentsCountText").value = "Кол-во сегментов " + document.getElementById("segmentsCount").value + "%";
}

// Segment radius checker
function segmentMinRadiusHandler() {
    var minRad = parseInt(document.getElementById("segmentMinRadius").value);
    var maxRad = parseInt(document.getElementById("segmentMaxRadius").value);
    if (minRad == null || minRad < 100 || minRad >= maxRad || minRad > 1500) {
        alert("Минимальный радиус сегмента должент быть в диапазоне от 100 до 1500 метров и быть меньше максимального.\n\n" +
            "Minimal segment radius should be between 100 and 1500 meters and less maximal segment radius.")
        document.getElementById("segmentMinRadius").value = 250;
    }
}

function segmentMaxRadiusHandler() {
    var minRad = parseInt(document.getElementById("segmentMinRadius").value);
    var maxRad = parseInt(document.getElementById("segmentMaxRadius").value);
    if (maxRad == null || maxRad < 100 || minRad >= maxRad || maxRad > 1500) {
        alert("Максимальный радиус сегмента должент быть в диапазоне от 100 до 1500 метров и быть больше минимального.\n\n" +
            "Maximal segment radius should be between 100 and 1500 meters and more minimal segment radius.")
        document.getElementById("segmentMaxRadius").value = 800;
    }

}
