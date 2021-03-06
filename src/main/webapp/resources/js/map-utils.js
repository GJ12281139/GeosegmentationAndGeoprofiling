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
    return marker;
}


function addMarker(position, icon, name, placeId, address, rating, map) {
    var marker = new google.maps.Marker({
        position: position,
        title: name,
        icon: icon,
        map: map
    });
    var content = '<h1>' + name + '</h1>' +
        '<p> rating: ' + rating + '</p>' +
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
