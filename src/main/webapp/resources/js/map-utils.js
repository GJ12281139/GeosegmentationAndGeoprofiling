
function addMarker(position, icon, title, map, clickable) {
    new google.maps.Marker({
        position: position,
        icon: icon,
        title: title,
        map: map,
        clickable: clickable
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
