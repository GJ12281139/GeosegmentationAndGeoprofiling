
function tryingGetUserGeolocationAndRedirect() {
    // Try HTML5 geolocation.
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (pos) {
            window.location.replace(window.location.href
                + "?lat=" + pos.coords.latitude + "&lng=" + pos.coords.longitude + "&box=true&cover=true");
        }, function () {
            handleGeolocationError(true)
        });
    } else {
        handleGeolocationError(false);
    }
}

function handleGeolocationError(browserHasGeolocation) {
    var msg = 'Add your geolocation (example like ?lat=59.9574527&lng=30.3057543) to URL by hands.';
    alert(browserHasGeolocation ? 'Error: The Geolocation service failed. ' + msg
        : 'Error: Your browser does not support geolocation. ' + msg);
}
