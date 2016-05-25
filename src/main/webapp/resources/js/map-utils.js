function geolocationIcon(icon) {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (pos) {
            $('#latitude').val(pos.coords.latitude);
            $('#longitude').val(pos.coords.longitude);
            map.setZoom(11);
            map.setCenter({lat: pos.coords.latitude, lng: pos.coords.longitude});
            var json = {
                "lat": pos.coords.latitude,
                "lng": pos.coords.longitude
            };
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
                    $('#city').val(result.city);
                    $('#country').val(result.country);
                },
                error: function (data) {
                    alert(data.responseText);
                    console.log(data);
                }
            });
            addUserMarker(map.center, icon, "Вы здесь\nYou are here", map);
        }, function () {
            handleGeolocationError(true)
        });
    } else {
        handleGeolocationError(false);
    }
}

function handleGeolocationError(browserHasGeolocation) {
    var msg = 'Введите ваши координаты или город в текстовые поле слева.\n\nAdd your geolocation by hands in text fields on left side.';
    alert(browserHasGeolocation ? 'Сервис геолокации не может получить ваши координаты.\n\nThe Geolocation service failed.\n\n' + msg
        : 'Вша браузер не поддерживает функции геолокации.\n\nYour browser does not support geolocation.\n\n' + msg);
}

var places = [];
var clusters = [];
var clustersCircle = [];
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
        "category": getPressedCategory(),
        "percents": getPressedPercents()
    };

    $.ajax({
        url: '/segmentation/places/foursquare',
        data: JSON.stringify(json),
        type: "POST",
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',

        beforeSend: function (xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        },
        success: function (result) {
            map.setZoom(11);
            map.setCenter(map.center);
            clearPlaces();
            fillPlaces(result);
        },
        error: function (data) {
            alert(data.responseText);
            console.log(data);
        }
    });

    $.ajax({
        url: '/segmentation',
        data: JSON.stringify(json),
        type: "POST",
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',

        beforeSend: function (xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        },
        success: function (result) {
            clearClusters();
            fillClusters(result);
        },
        error: function (data) {
            alert(data.responseText);
            console.log(data);
        }
    });
    //
    // var category;
    // var percents;
    //
    //
    // if (autoPressed) {
    //     category = "auto";
    //     percents = autoPercents();
    // }
    //
    // if (culturePressed) {
    //     category = "culture";
    //     percents = culturePercents();
    // }
    //
    // if (foodPressed) {
    //     category = "food";
    //     percents = foodPercents();
    // }
    //
    // if (nightLifePressed) {
    //     category = "nightLife";
    //     percents = nightLifePercents();
    // }
    //
    // if (sportPressed) {
    //     category = "sport";
    //     percents = sportPercents();
    // }
    //
    //
    // var json = {
    //     "lat": $('#latitude').val(),
    //     "lng": $('#longitude').val(),
    //     "category": "culture",
    //     "percents": "[10,10,10,10,10,10,10,10,10,10]"
    // };
    //
    // $.ajax({
    //     url: '/data ',
    //     data: JSON.stringify(json),
    //     type: "POST",
    //     contentType: 'application/json; charset=utf-8',
    //     dataType: 'json',
    //
    //     beforeSend: function (xhr) {
    //         xhr.setRequestHeader("Accept", "application/json");
    //         xhr.setRequestHeader("Content-Type", "application/json");
    //     },
    //     success: function (result) {
    //         $('#city').val("${result.user_city}");
    //         $('#country').val("${user_country}");
    //         alert("bblalba");
    //     }
    // });
    //
    //
    // return;
    //
    //
    // var url = window.location.href;
    // var arr = url.split("/");
    // var emptyurl = arr[0] + "//" + arr[2];
    //
    // if (latAttr.length > 0 && lngAttr.length > 0) {
    //     window.location.replace(emptyurl
    //         + "?" + latAttr + "&" + lngAttr + "&fData=true&category=" + category + "&percents=" + percents);
    //     alert("Возможно вы первый пользователь, кто использует сервис в этом городу по данной " +
    //         "категории... Если это так, то пожалуйста подождите несколько минут \n\n" +
    //         "Maybe you are first user for this city and category... If so, please wait several minutes");
    //     return
    // }
    // if (cityAttr.length > 0) {
    //     window.location.replace(emptyurl
    //         + "?" + cityAttr + "&" + countryAttr + "&fData=true&category=" + category + "&percents=" + percents);
    //     alert("Возможно вы первый пользователь, кто использует сервис в этом городу по данной " +
    //         "категории... Если это так, то пожалуйста подождите несколько минут \n\n" +
    //         "Maybe you are first user for this city and category... If so, please wait several minutes");
    //     return
    // }
    // alert("Заполните координаты или укажите город. \n\nFill latitude and longitude fields or city (country may not) field.");
}

function clearPlaces() {
    for (var i = 0; i < places.length; i++) {
        places[i].setMap(null);
    }
    places = [];
}

function fillPlaces(result) {
    for (var i = 0; i < result.length; i++) {
        var place = result[i];
        places.push(addMarker({lat: place.lat, lng: place.lng}, place.icon, place.name, place.id, place.address, map));
    }
}

function clearClusters() {
    for (var i = 0; i < clusters.length; i++) {
        clusters[i].setMap(null);
        clustersCircle[i].setMap(null);
    }
    clusters = [];
    clustersCircle = [];
}

function fillClusters(result) {
    for (var i = 0; i < result.length; i++) {
        var cluster = result[i];
        clusters.push(addMarker({lat: cluster.lat, lng: cluster.lng}, cluster.icon, "Cluster rad " + cluster.rad, "", "", map));
        clustersCircle.push(addCircle({lat: cluster.lat, lng: cluster.lng}, cluster.rad));
    }
}

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

function addUserMarker(position, icon, name, map) {
    var marker = new google.maps.Marker({
        position: position,
        title: name,
        icon: icon,
        map: map
    });
    var content = '<h1>' + name + '</h1>';
    var infowindow = new google.maps.InfoWindow({
        content: content
    });
    marker.addListener('click', function () {
        infowindow.open(map, marker);
    });
}


function addMarker(position, icon, name, placeId, address, map) {
    var marker = new google.maps.Marker({
        position: position,
        title: name,
        icon: icon,
        map: map
    });
    var content = '<h1>' + name + '</h1>' +
        '<p> placeId: ' + placeId + '</p>' +
        '<p> address: ' + address + '</p>';
    var infowindow = new google.maps.InfoWindow({
        content: content
    });
    marker.addListener('click', function () {
        infowindow.open(map, marker);
    });
    return marker;
}

function addCircle(position, radius) {
    return new google.maps.Circle({
        center: position,
        strokeColor: '#333333',
        strokeWeight: 2,
        strokeOpacity: 0.6,
        fillOpacity: 0.07,
        map: map,
        radius: radius
    });
}

function addRectangle(north, south, east, west, map) {
    return new google.maps.Rectangle({
        strokeColor: '#333333',
        strokeOpacity: 0.9,
        fillOpacity: 0,
        map: map,
        bounds: {
            north: north,
            south: south,
            east: east,
            west: west
        }
    });
}
