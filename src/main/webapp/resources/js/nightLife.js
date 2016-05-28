var nightLifePressed = false;

function nightLife() {
    if (nightLifePressed) {
        primitivePlacesOff();
        return;
    }

    primitivePlacesOff();
    if (googleSourcePressed) {
        gNightLifeOn();
        return;
    }

    if (foursquareSourcePressed) {
        fNightLifeOn();
        return;
    }

    alert("Выберите источник данных (Foursuqare/Google data source).\n\nChoose data source (Foursuqare/Google data source).")

}

function fNightLifeOn() {
    nightLifePressed = true;
    document.getElementById("nightLife").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
    document.getElementById("fNightLifeSpotText").style.visibility = "visible";
    document.getElementById("fNightLifeSpot").style.visibility = "visible";
    document.getElementById("fBowlingAlleyText").style.visibility = "visible";
    document.getElementById("fBowlingAlley").style.visibility = "visible";
    document.getElementById("fMovieTheaterText").style.visibility = "visible";
    document.getElementById("fMovieTheater").style.visibility = "visible";
    document.getElementById("fPoolHallText").style.visibility = "visible";
    document.getElementById("fPoolHall").style.visibility = "visible";
}

function fNightLifeOff() {
    nightLifePressed = false;
    document.getElementById("nightLife").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
    document.getElementById("fNightLifeSpotText").style.visibility = "hidden";
    document.getElementById("fNightLifeSpot").style.visibility = "hidden";
    document.getElementById("fBowlingAlleyText").style.visibility = "hidden";
    document.getElementById("fBowlingAlley").style.visibility = "hidden";
    document.getElementById("fMovieTheaterText").style.visibility = "hidden";
    document.getElementById("fMovieTheater").style.visibility = "hidden";
    document.getElementById("fPoolHallText").style.visibility = "hidden";
    document.getElementById("fPoolHall").style.visibility = "hidden";
}


function gNightLifeOn() {
    nightLifePressed = true;
    document.getElementById("nightLife").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
    document.getElementById("gBowlingAlleyText").style.visibility = "visible";
    document.getElementById("gBowlingAlley").style.visibility = "visible";
    document.getElementById("gMovieTheaterText").style.visibility = "visible";
    document.getElementById("gMovieTheater").style.visibility = "visible";
    document.getElementById("gNightClubText").style.visibility = "visible";
    document.getElementById("gNightClub").style.visibility = "visible";
}

function gNightLifeOff() {
    nightLifePressed = false;
    document.getElementById("nightLife").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
    document.getElementById("gBowlingAlleyText").style.visibility = "hidden";
    document.getElementById("gBowlingAlley").style.visibility = "hidden";
    document.getElementById("gMovieTheaterText").style.visibility = "hidden";
    document.getElementById("gMovieTheater").style.visibility = "hidden";
    document.getElementById("gNightClubText").style.visibility = "hidden";
    document.getElementById("gNightClub").style.visibility = "hidden";
}


// Foursuqare range change handlers
function fNightLifeSpotRangeChange() {
    document.getElementById("fNightLifeSpotText").value = "Bars/clubs/disco/...: " + document.getElementById("fNightLifeSpot").value + "%";
}

function fBowlingAlleyRangeChange() {
    document.getElementById("fBowlingAlleyText").value = "Bowling alleys: " + document.getElementById("fBowlingAlley").value + "%";
}

function fMovieTheaterRangeChange() {
    document.getElementById("fMovieTheaterText").value = "Movie theaters: " + document.getElementById("fMovieTheater").value + "%";
}

function fPoolHallRangeChange() {
    document.getElementById("fPoolHallText").value = "Pool halls: " + document.getElementById("fPoolHall").value + "%";
}

// Google range change handlers
function gBowlingAlleyRangeChange() {
    document.getElementById("gBowlingAlleyText").value = "Bowling alleys: " + document.getElementById("gBowlingAlley").value + "%";
}

function gMovieTheaterRangeChange() {
    document.getElementById("gMovieTheaterText").value = "Movie theaters: " + document.getElementById("gMovieTheater").value + "%";
}

function gNightClubRangeChange() {
    document.getElementById("gNightClubText").value = "Night clubs: " + document.getElementById("gNightClub").value + "%";
}


function nightLifePercents() {
    if (foursquareSourcePressed) {
        return "[" + document.getElementById("nightLife").value + "," +
            document.getElementById("fBowlingAlley").value + "," +
            document.getElementById("fMovieTheater").value + "," +
            document.getElementById("fPoolHall").value + "]";
    }
    if (googleSourcePressed) {
        return "[" + document.getElementById("gBowlingAlley").value + "," +
            document.getElementById("gMovieTheater").value + "," +
            document.getElementById("gNightClub").value + "]";
    }
    return null;
}

