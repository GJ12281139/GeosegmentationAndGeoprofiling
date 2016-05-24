var autoPressed = false;

function auto() {
    if (autoPressed) {
        autoOff();
    } else {
        culturalLeisureOff();
        nightLifeOff();
        foodOff();
        sportOff();
        autoOn();
    }
}

function autoOn() {
    autoPressed = true;
    document.getElementById("autoDealershipText").style.visibility = "visible";
    document.getElementById("autoDealership").style.visibility = "visible";
    document.getElementById("autoGarageText").style.visibility = "visible";
    document.getElementById("autoGarage").style.visibility = "visible";
    document.getElementById("autoWorkshopText").style.visibility = "visible";
    document.getElementById("autoWorkshop").style.visibility = "visible";
    document.getElementById("carWashText").style.visibility = "visible";
    document.getElementById("carWash").style.visibility = "visible";
}

function autoOff() {
    autoPressed = false;
    document.getElementById("autoDealershipText").style.visibility = "hidden";
    document.getElementById("autoDealership").style.visibility = "hidden";
    document.getElementById("autoGarageText").style.visibility = "hidden";
    document.getElementById("autoGarage").style.visibility = "hidden";
    document.getElementById("autoWorkshopText").style.visibility = "hidden";
    document.getElementById("autoWorkshop").style.visibility = "hidden";
    document.getElementById("carWashText").style.visibility = "hidden";
    document.getElementById("carWash").style.visibility = "hidden";
}

function autoDealershipRangeChange() {
    document.getElementById("autoDealershipText").value = "Auto dealerships: " + document.getElementById("autoDealership").value + "%";
}

function autoGarageRangeChange() {
    document.getElementById("autoGarageText").value = "Auto garages: " + document.getElementById("autoGarage").value + "%";
}

function autoWorkshopRangeChange() {
    document.getElementById("autoWorkshopText").value = "Auto workshops: " + document.getElementById("autoWorkshop").value + "%";
}

function carWashRangeChange() {
    document.getElementById("carWashText").value = "Car washes: " + document.getElementById("carWash").value + "%";
}

function autoPercents() {
    return "[" + document.getElementById("autoDealership").value + "," +
        document.getElementById("autoGarage").value + "," +
        document.getElementById("autoWorkshop").value + "," +
        document.getElementById("carWash").value + "]";
}
