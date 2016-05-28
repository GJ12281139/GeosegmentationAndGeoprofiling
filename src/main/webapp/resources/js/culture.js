var culturePressed = false;

function culture() {

    if (culturePressed) {
        primitivePlacesOff();
        return;
    }

    primitivePlacesOff();
    if (googleSourcePressed) {
        gCultureOn();
        return;
    }

    if (foursquareSourcePressed) {
        fCultureOn();
        return;
    }

    alert("Выберите источник данных (Foursuqare/Google data source).\n\nChoose data source (Foursuqare/Google data source).")
}

function fCultureOn() {
    culturePressed = true;
    document.getElementById("culture").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
    document.getElementById("fMuseumText").style.visibility = "visible";
    document.getElementById("fMuseum").style.visibility = "visible";
    document.getElementById("fParkText").style.visibility = "visible";
    document.getElementById("fPark").style.visibility = "visible";
    document.getElementById("fPlazaText").style.visibility = "visible";
    document.getElementById("fPlaza").style.visibility = "visible";
    document.getElementById("fSculptureGardenText").style.visibility = "visible";
    document.getElementById("fSculptureGarden").style.visibility = "visible";
    document.getElementById("fSpirtualCenterText").style.visibility = "visible";
    document.getElementById("fSpirtualCenter").style.visibility = "visible";
    document.getElementById("fTheaterText").style.visibility = "visible";
    document.getElementById("fTheater").style.visibility = "visible";
    document.getElementById("fFountainText").style.visibility = "visible";
    document.getElementById("fFountain").style.visibility = "visible";
    document.getElementById("fGardenText").style.visibility = "visible";
    document.getElementById("fGarden").style.visibility = "visible";
    document.getElementById("fPalaceText").style.visibility = "visible";
    document.getElementById("fPalace").style.visibility = "visible";
    document.getElementById("fCastleText").style.visibility = "visible";
    document.getElementById("fCastle").style.visibility = "visible";
}

function fCultureOff() {
    culturePressed = false;
    document.getElementById("culture").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
    document.getElementById("fMuseumText").style.visibility = "hidden";
    document.getElementById("fMuseum").style.visibility = "hidden";
    document.getElementById("fParkText").style.visibility = "hidden";
    document.getElementById("fPark").style.visibility = "hidden";
    document.getElementById("fPlazaText").style.visibility = "hidden";
    document.getElementById("fPlaza").style.visibility = "hidden";
    document.getElementById("fSculptureGardenText").style.visibility = "hidden";
    document.getElementById("fSculptureGarden").style.visibility = "hidden";
    document.getElementById("fSpirtualCenterText").style.visibility = "hidden";
    document.getElementById("fSpirtualCenter").style.visibility = "hidden";
    document.getElementById("fTheaterText").style.visibility = "hidden";
    document.getElementById("fTheater").style.visibility = "hidden";
    document.getElementById("fFountainText").style.visibility = "hidden";
    document.getElementById("fFountain").style.visibility = "hidden";
    document.getElementById("fGardenText").style.visibility = "hidden";
    document.getElementById("fGarden").style.visibility = "hidden";
    document.getElementById("fPalaceText").style.visibility = "hidden";
    document.getElementById("fPalace").style.visibility = "hidden";
    document.getElementById("fCastleText").style.visibility = "hidden";
    document.getElementById("fCastle").style.visibility = "hidden";
}

function gCultureOn() {
    culturePressed = true;
    document.getElementById("culture").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
    document.getElementById("gMuseumText").style.visibility = "visible";
    document.getElementById("gMuseum").style.visibility = "visible";
    document.getElementById("gParkText").style.visibility = "visible";
    document.getElementById("gPark").style.visibility = "visible";
    document.getElementById("gChurchText").style.visibility = "visible";
    document.getElementById("gChurch").style.visibility = "visible";
}

function gCultureOff() {
    culturePressed = false;
    document.getElementById("culture").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
    document.getElementById("gMuseumText").style.visibility = "hidden";
    document.getElementById("gMuseum").style.visibility = "hidden";
    document.getElementById("gParkText").style.visibility = "hidden";
    document.getElementById("gPark").style.visibility = "hidden";
    document.getElementById("gChurchText").style.visibility = "hidden";
    document.getElementById("gChurch").style.visibility = "hidden";
}


// Foursuqare range change handlers
function fMuseumRangeChange() {
    document.getElementById("fMuseumText").value = "Museums: " + document.getElementById("fMuseum").value + "%";
}

function fParkRangeChange() {
    document.getElementById("fParkText").value = "Parks: " + document.getElementById("fPark").value + "%";
}

function fPlazaRangeChange() {
    document.getElementById("fPlazaText").value = "Plazas: " + document.getElementById("fPlaza").value + "%";
}

function fSculptureGardenRangeChange() {
    document.getElementById("fSculptureGardenText").value = "Sculpture gardens: " + document.getElementById("fSculptureGarden").value + "%";
}

function fSpirtualCenterRangeChange() {
    document.getElementById("fSpirtualCenterText").value = "Spirtual centers: " + document.getElementById("fSpirtualCenter").value + "%";
}

function fTheaterRangeChange() {
    document.getElementById("fTheaterText").value = "Theaters: " + document.getElementById("fTheater").value + "%";
}

function fFountainRangeChange() {
    document.getElementById("fFountainText").value = "Fountains: " + document.getElementById("fFountain").value + "%";
}

function fGardenRangeChange() {
    document.getElementById("fGardenText").value = "Gardens: " + document.getElementById("fGarden").value + "%";
}

function fPalaceRangeChange() {
    document.getElementById("fPalaceText").value = "Palaces: " + document.getElementById("fPalace").value + "%";
}

function fCastleRangeChange() {
    document.getElementById("fCastleText").value = "Castles: " + document.getElementById("fCastle").value + "%";
}

// Foursuqare range change handlers
function gMuseumRangeChange() {
    document.getElementById("gMuseumText").value = "Museums: " + document.getElementById("gMuseum").value + "%";
}

function gParkRangeChange() {
    document.getElementById("gParkText").value = "Parks: " + document.getElementById("gPark").value + "%";
}

function gChurchRangeChange() {
    document.getElementById("gChurchText").value = "Churches: " + document.getElementById("gChurch").value + "%";
}


function culturePercents() {
    if (foursquareSourcePressed) {
        return "[" + document.getElementById("fMuseum").value + "," +
            document.getElementById("fPark").value + "," +
            document.getElementById("fPlaza").value + "," +
            document.getElementById("fSculptureGarden").value + "," +
            document.getElementById("fSpirtualCenter").value + "," +
            document.getElementById("fTheater").value + "," +
            document.getElementById("fFountain").value + "," +
            document.getElementById("fGarden").value + "," +
            document.getElementById("fPalace").value + "," +
            document.getElementById("fCastle").value + "]";
    }
    if (googleSourcePressed) {
        return "[" + document.getElementById("gMuseum").value + "," +
            document.getElementById("gPark").value + "," +
            document.getElementById("gChurch").value + "]";
    }
    return null;
}