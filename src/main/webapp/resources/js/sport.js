var sportPressed = false;

function sport() {
    if (sportPressed) {
        sportOff()
    } else {
        culturalLeisureOff();
        nightLifeOff();
        foodOff();
        autoOff();
        sportOn();
    }
}

function sportOn() {
    sportPressed = true;
    document.getElementById("athleticsSportsText").style.visibility = "visible";
    document.getElementById("athleticsSports").style.visibility = "visible";
}

function sportOff() {
    sportPressed = false;
    document.getElementById("athleticsSportsText").style.visibility = "hidden";
    document.getElementById("athleticsSports").style.visibility = "hidden";
}

function athleticsSportsRangeChange() {
    document.getElementById("athleticsSportsText").value = "Athletics and sports: " + document.getElementById("athleticsSports").value + "%";
}

function sportPercents() {
    return "[" + document.getElementById("athleticsSports").value + "]";
}