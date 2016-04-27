
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
    marker.addListener('click', function() {
        infowindow.open(map, marker);
    });
}

function addCircle(position, radius) {
    new google.maps.Circle({
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
    new google.maps.Rectangle({
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
