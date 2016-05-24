var foodPressed = false;

function food() {
    if (foodPressed) {
        foodOff();
    } else {
        culturalLeisureOff();
        nightLifeOff();
        sportOff();
        autoOff();
        foodOn();
    }
}

function foodOn() {
    foodPressed = true;
    document.getElementById("foodText").style.visibility = "visible";
    document.getElementById("food").style.visibility = "visible";
}

function foodOff() {
    foodPressed = false;
    document.getElementById("foodText").style.visibility = "hidden";
    document.getElementById("food").style.visibility = "hidden";
}

function foodRangeChange() {
    document.getElementById("foodText").value = "Cafe/restaurants/...: " + document.getElementById("food").value + "%";
}

function foodPercents() {
    return "[" + document.getElementById("food").value + "]";
}