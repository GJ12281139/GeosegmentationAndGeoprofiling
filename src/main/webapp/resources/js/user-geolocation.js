
function addMarker(position, icon, title, map) {
    new google.maps.Marker({
        position: position,
        map: map,
        icon: icon,
        // animation: google.maps.Animation.NONE,
        clickable: true,
        title: title
    });
}

function addCircle(position, radius) {
    new google.maps.Circle({
        center: position,
        strokeColor: '#585858',
        strokeOpacity: 0.4,
        fillOpacity: 0.1,
        map: map,
        radius: radius
    });
    
}

function addRectangle(north, south, east, west, map) {
    new google.maps.Rectangle({
        strokeColor: '##1C1C1C',
        strokeOpacity: 0.8,
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

function tryingGetUserGeolocationAndRedirect() {
    // Try HTML5 geolocation.
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (pos) {
            window.location.replace(window.location.href
                + "?lat=" + pos.coords.latitude + "&lng=" + pos.coords.longitude);
        }, function () {
            handleGeolocationError(true)
        });
    } else {
        handleGeolocationError(false);
    }
}

function handleGeolocationError(browserHasGeolocation) {
    var msg = 'Add your geolocation (example: ?lat=59.9574527&lng=30.3057543) to URL by hands.';
    alert(browserHasGeolocation ? 'Error: The Geolocation service failed. ' + msg
        : 'Error: Your browser does not support geolocation. ' + msg);
}
