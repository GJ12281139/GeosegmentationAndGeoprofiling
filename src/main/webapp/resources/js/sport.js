var sportPressed = false;

function sport() {
    if (sportPressed) {
        primitivePlacesOff();
        return;
    }

    primitivePlacesOff();
    if (googleSourcePressed) {
        gSportOn();
        return;
    }

    if (foursquareSourcePressed) {
        fSportOn();
        return;
    }

    alert("Выберите источник данных (Foursuqare/Google data source).\n\nChoose data source (Foursuqare/Google data source).")
}

function fSportOn() {
    sportPressed = true;
    document.getElementById("sport").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
    document.getElementById("fAthleticsSportsText").style.visibility = "visible";
    document.getElementById("fAthleticsSports").style.visibility = "visible";
}

function fSportOff() {
    sportPressed = false;
    document.getElementById("sport").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
    document.getElementById("fAthleticsSportsText").style.visibility = "hidden";
    document.getElementById("fAthleticsSports").style.visibility = "hidden";
}

function gSportOn() {
    sportPressed = true;
    document.getElementById("sport").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
    document.getElementById("gGymText").style.visibility = "visible";
    document.getElementById("gGym").style.visibility = "visible";
}

function gSportOff() {
    sportPressed = false;
    document.getElementById("sport").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
    document.getElementById("gGymText").style.visibility = "hidden";
    document.getElementById("gGym").style.visibility = "hidden";
}


function fAthleticsSportsRangeChange() {
    document.getElementById("fAthleticsSportsText").value = "Спорт: " + document.getElementById("fAthleticsSports").value + "%";
}

function gGymRangeChange() {
    document.getElementById("gGymText").value = "Спортивные залы: " + document.getElementById("gGym").value + "%";
}

function sportPercents() {
    if (foursquareSourcePressed) {
        return "[" + document.getElementById("fAthleticsSports").value + "]";
    }
    if (googleSourcePressed) {
        return "[" + document.getElementById("gGym").value + "]";
    }
    return null;
}