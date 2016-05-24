var nightLifePressed = false;

function nightLife() {
    if (nightLifePressed) {
        nightLifeOff();
    } else {
        culturalLeisureOff();
        foodOff();
        sportOff();
        autoOff();
        nightLifeOn();
    }
}

function nightLifeOn() {
    nightLifePressed = true;
    document.getElementById("nightLifeText").style.visibility = "visible";
    document.getElementById("nightLife").style.visibility = "visible";
    document.getElementById("bowlingAlleyText").style.visibility = "visible";
    document.getElementById("bowlingAlley").style.visibility = "visible";
    document.getElementById("movieTheaterText").style.visibility = "visible";
    document.getElementById("movieTheater").style.visibility = "visible";
    document.getElementById("poolHallText").style.visibility = "visible";
    document.getElementById("poolHall").style.visibility = "visible";
}

function nightLifeOff() {
    nightLifePressed = false;
    document.getElementById("nightLifeText").style.visibility = "hidden";
    document.getElementById("nightLife").style.visibility = "hidden";
    document.getElementById("bowlingAlleyText").style.visibility = "hidden";
    document.getElementById("bowlingAlley").style.visibility = "hidden";
    document.getElementById("movieTheaterText").style.visibility = "hidden";
    document.getElementById("movieTheater").style.visibility = "hidden";
    document.getElementById("poolHallText").style.visibility = "hidden";
    document.getElementById("poolHall").style.visibility = "hidden";
}

function nightLifeRangeChange() {
    document.getElementById("nightLifeText").value = "Bars/clubs/disco/...: " + document.getElementById("nightLife").value + "%";
}

function bowlingAlleyRangeChange() {
    document.getElementById("bowlingAlleyText").value = "Bowling alleys: " + document.getElementById("bowlingAlley").value + "%";
}

function movieTheaterRangeChange() {
    document.getElementById("movieTheaterText").value = "Movie theaters: " + document.getElementById("movieTheater").value + "%";
}

function poolHallRangeChange() {
    document.getElementById("poolHallText").value = "Pool halls: " + document.getElementById("poolHall").value + "%";
}

function nightLifePercents() {
    return "[" + document.getElementById("nightLife").value + "," +
        document.getElementById("bowlingAlley").value + "," +
        document.getElementById("movieTheater").value + "," +
        document.getElementById("poolHall").value + "]";
}

