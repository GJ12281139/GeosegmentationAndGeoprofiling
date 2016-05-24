var culturePressed = false;

function culturalLeisure() {
    if (culturePressed) {
        culturalLeisureOff();
    } else {
        foodOff();
        nightLifeOff();
        sportOff();
        autoOff();
        culturalLeisureOn();
    }
}

function culturalLeisureOn() {
    culturePressed = true;
    document.getElementById("museumText").style.visibility = "visible";
    document.getElementById("museum").style.visibility = "visible";
    document.getElementById("parkText").style.visibility = "visible";
    document.getElementById("park").style.visibility = "visible";
    document.getElementById("plazaText").style.visibility = "visible";
    document.getElementById("plaza").style.visibility = "visible";
    document.getElementById("sculptureGardenText").style.visibility = "visible";
    document.getElementById("sculptureGarden").style.visibility = "visible";
    document.getElementById("spirtualCenterText").style.visibility = "visible";
    document.getElementById("spirtualCenter").style.visibility = "visible";
    document.getElementById("theaterText").style.visibility = "visible";
    document.getElementById("theater").style.visibility = "visible";
    document.getElementById("fountainText").style.visibility = "visible";
    document.getElementById("fountain").style.visibility = "visible";
    document.getElementById("gardenText").style.visibility = "visible";
    document.getElementById("garden").style.visibility = "visible";
    document.getElementById("palaceText").style.visibility = "visible";
    document.getElementById("palace").style.visibility = "visible";
    document.getElementById("castleText").style.visibility = "visible";
    document.getElementById("castle").style.visibility = "visible";
}

function culturalLeisureOff() {
    culturePressed = false;
    document.getElementById("museumText").style.visibility = "hidden";
    document.getElementById("museum").style.visibility = "hidden";
    document.getElementById("parkText").style.visibility = "hidden";
    document.getElementById("park").style.visibility = "hidden";
    document.getElementById("plazaText").style.visibility = "hidden";
    document.getElementById("plaza").style.visibility = "hidden";
    document.getElementById("sculptureGardenText").style.visibility = "hidden";
    document.getElementById("sculptureGarden").style.visibility = "hidden";
    document.getElementById("spirtualCenterText").style.visibility = "hidden";
    document.getElementById("spirtualCenter").style.visibility = "hidden";
    document.getElementById("theaterText").style.visibility = "hidden";
    document.getElementById("theater").style.visibility = "hidden";
    document.getElementById("fountainText").style.visibility = "hidden";
    document.getElementById("fountain").style.visibility = "hidden";
    document.getElementById("gardenText").style.visibility = "hidden";
    document.getElementById("garden").style.visibility = "hidden";
    document.getElementById("palaceText").style.visibility = "hidden";
    document.getElementById("palace").style.visibility = "hidden";
    document.getElementById("castleText").style.visibility = "hidden";
    document.getElementById("castle").style.visibility = "hidden";
}

function museumsRangeChange() {
    document.getElementById("museumText").value = "Museums: " + document.getElementById("museum").value + "%";
}

function parksRangeChange() {
    document.getElementById("parkText").value = "Parks: " + document.getElementById("park").value + "%";
}

function plazaRangeChange() {
    document.getElementById("plazaText").value = "Plazas: " + document.getElementById("plaza").value + "%";
}

function sculptureGardenRangeChange() {
    document.getElementById("sculptureGardenText").value = "Sculpture gardens: " + document.getElementById("sculptureGarden").value + "%";
}

function spirtualCenterRangeChange() {
    document.getElementById("spirtualCenterText").value = "Spirtual centers: " + document.getElementById("spirtualCenter").value + "%";
}

function theaterRangeChange() {
    document.getElementById("theaterText").value = "Theaters: " + document.getElementById("theater").value + "%";
}

function fountainRangeChange() {
    document.getElementById("fountainText").value = "Fountains: " + document.getElementById("fountain").value + "%";
}

function gardenRangeChange() {
    document.getElementById("gardenText").value = "Gardens: " + document.getElementById("garden").value + "%";
}

function palaceRangeChange() {
    document.getElementById("palaceText").value = "Palaces: " + document.getElementById("palace").value + "%";
}

function castleRangeChange() {
    document.getElementById("castleText").value = "Castles: " + document.getElementById("castle").value + "%";
}

function culturePercents() {
    return "[" + document.getElementById("museum").value + "," +
        document.getElementById("park").value + "," +
        document.getElementById("plaza").value + "," +
        document.getElementById("sculptureGarden").value + "," +
        document.getElementById("spirtualCenter").value + "," +
        document.getElementById("theater").value + "," +
        document.getElementById("fountain").value + "," +
        document.getElementById("garden").value + "," +
        document.getElementById("palace").value + "," +
        document.getElementById("castle").value + "]";
}