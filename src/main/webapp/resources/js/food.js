var foodPressed = false;

function food() {
    
    if (foodPressed) {
        primitivePlacesOff();
        return;
    }

    primitivePlacesOff();
    if (googleSourcePressed) {
        gFoodOn();
        return;
    }

    if (foursquareSourcePressed) {
        fFoodOn();
        return;
    }

    alert("Выберите источник данных (Foursuqare/Google data source).\n\nChoose data source (Foursuqare/Google data source).")
}

function fFoodOn() {
    foodPressed = true;
    document.getElementById("food").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
    document.getElementById("fAsianRestaurantText").style.visibility = "visible";
    document.getElementById("fAsianRestaurant").style.visibility = "visible";
    document.getElementById("fJapaneseRestaurantText").style.visibility = "visible";
    document.getElementById("fJapaneseRestaurant").style.visibility = "visible";
    document.getElementById("fFrenchRestaurantText").style.visibility = "visible";
    document.getElementById("fFrenchRestaurant").style.visibility = "visible";
    document.getElementById("fItalianRestaurantText").style.visibility = "visible";
    document.getElementById("fItalianRestaurant").style.visibility = "visible";
    document.getElementById("fBakeryText").style.visibility = "visible";
    document.getElementById("fBakery").style.visibility = "visible";
    document.getElementById("fBistroText").style.visibility = "visible";
    document.getElementById("fBistro").style.visibility = "visible";
    document.getElementById("fFastFoodRestaurantText").style.visibility = "visible";
    document.getElementById("fFastFoodRestaurant").style.visibility = "visible";
    document.getElementById("fCafeText").style.visibility = "visible";
    document.getElementById("fCafe").style.visibility = "visible";
}

function fFoodOff() {
    foodPressed = false;
    document.getElementById("food").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
    document.getElementById("fAsianRestaurantText").style.visibility = "hidden";
    document.getElementById("fAsianRestaurant").style.visibility = "hidden";
    document.getElementById("fJapaneseRestaurantText").style.visibility = "hidden";
    document.getElementById("fJapaneseRestaurant").style.visibility = "hidden";
    document.getElementById("fFrenchRestaurantText").style.visibility = "hidden";
    document.getElementById("fFrenchRestaurant").style.visibility = "hidden";
    document.getElementById("fItalianRestaurantText").style.visibility = "hidden";
    document.getElementById("fItalianRestaurant").style.visibility = "hidden";
    document.getElementById("fBakeryText").style.visibility = "hidden";
    document.getElementById("fBakery").style.visibility = "hidden";
    document.getElementById("fBistroText").style.visibility = "hidden";
    document.getElementById("fBistro").style.visibility = "hidden";
    document.getElementById("fFastFoodRestaurantText").style.visibility = "hidden";
    document.getElementById("fFastFoodRestaurant").style.visibility = "hidden";
    document.getElementById("fCafeText").style.visibility = "hidden";
    document.getElementById("fCafe").style.visibility = "hidden";
}

function gFoodOn() {
    foodPressed = true;
    document.getElementById("food").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
    document.getElementById("gCafeText").style.visibility = "visible";
    document.getElementById("gCafe").style.visibility = "visible";
    document.getElementById("gRestaurantText").style.visibility = "visible";
    document.getElementById("gRestaurant").style.visibility = "visible";
}

function gFoodOff() {
    foodPressed = false;
    document.getElementById("food").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
    document.getElementById("gCafeText").style.visibility = "hidden";
    document.getElementById("gCafe").style.visibility = "hidden";
    document.getElementById("gRestaurantText").style.visibility = "hidden";
    document.getElementById("gRestaurant").style.visibility = "hidden";
}

// Foursuqare range change handlers
function fAsianRestaurantRangeChange() {
    document.getElementById("fAsianRestaurantText").value = "Азиатские: " + document.getElementById("fAsianRestaurant").value + "%";
}

function fJapaneseRestaurantRangeChange() {
    document.getElementById("fJapaneseRestaurantText").value = "Японские: " + document.getElementById("fJapaneseRestaurant").value + "%";
}

function fFrenchRestaurantRangeChange() {
    document.getElementById("fFrenchRestaurantText").value = "Французские: " + document.getElementById("fFrenchRestaurant").value + "%";
}

function fItalianRestaurantRangeChange() {
    document.getElementById("fItalianRestaurantText").value = "Итальянские: " + document.getElementById("fItalianRestaurant").value + "%";
}

function fBakeryRangeChange() {
    document.getElementById("fBakeryText").value = "Пекарни: " + document.getElementById("fBakery").value + "%";
}

function fBistroRangeChange() {
    document.getElementById("fBistroText").value = "Бистро: " + document.getElementById("fBistro").value + "%";
}

function fFastFoodRestaurantRangeChange() {
    document.getElementById("fFastFoodRestaurantText").value = "Фаст-фуд: " + document.getElementById("fFastFoodRestaurant").value + "%";
}

function fCafeRangeChange() {
    document.getElementById("fCafeText").value = "Кафе: " + document.getElementById("fCafe").value + "%";
}

// Google range change handlers
function gCafeRangeChange() {
    document.getElementById("gCafeText").value = "Кафе: " + document.getElementById("gCafe").value + "%";
}

function gRestaurantRangeChange() {
    document.getElementById("gRestaurantText").value = "Рестораны: " + document.getElementById("gRestaurant").value + "%";
}

function foodPercents() {
    if (foursquareSourcePressed) {
        return "[" + document.getElementById("fAsianRestaurant").value + "," +
            document.getElementById("fJapaneseRestaurant").value + "," +
            document.getElementById("fFrenchRestaurant").value + "," +
            document.getElementById("fItalianRestaurant").value + "," +
            document.getElementById("fBakery").value + "," +
            document.getElementById("fBistro").value + "," +
            document.getElementById("fFastFoodRestaurant").value + "," +
            document.getElementById("fCafe").value + "]";
    }
    if (googleSourcePressed) {
        return "[" + document.getElementById("gCafe").value + "," +
            document.getElementById("gRestaurant").value + "]";
    }
    return null;
}