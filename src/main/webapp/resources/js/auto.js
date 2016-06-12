var autoPressed = false;

function auto() {
    if (autoPressed) {
        primitivePlacesOff();
        return;
    }

    primitivePlacesOff();
    if (googleSourcePressed) {
        gAutoOn();
        return;
    }

    if (foursquareSourcePressed) {
        fAutoOn();
        return;
    }

    alert("Выберите источник данных (Foursuqare/Google data source).\n\nChoose data source (Foursuqare/Google data source).")
}

function fAutoOn() {
    autoPressed = true;
    document.getElementById("auto").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
    document.getElementById("fAutoDealershipText").style.visibility = "visible";
    document.getElementById("fAutoDealership").style.visibility = "visible";
    document.getElementById("fAutoGarageText").style.visibility = "visible";
    document.getElementById("fAutoGarage").style.visibility = "visible";
    document.getElementById("fAutoWorkshopText").style.visibility = "visible";
    document.getElementById("fAutoWorkshop").style.visibility = "visible";
    document.getElementById("fCarWashText").style.visibility = "visible";
    document.getElementById("fCarWash").style.visibility = "visible";
}

function fAutoOff() {
    autoPressed = false;
    document.getElementById("auto").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
    document.getElementById("fAutoDealershipText").style.visibility = "hidden";
    document.getElementById("fAutoDealership").style.visibility = "hidden";
    document.getElementById("fAutoGarageText").style.visibility = "hidden";
    document.getElementById("fAutoGarage").style.visibility = "hidden";
    document.getElementById("fAutoWorkshopText").style.visibility = "hidden";
    document.getElementById("fAutoWorkshop").style.visibility = "hidden";
    document.getElementById("fCarWashText").style.visibility = "hidden";
    document.getElementById("fCarWash").style.visibility = "hidden";
}

function gAutoOn() {
    autoPressed = true;
    document.getElementById("auto").style.background = '-webkit-linear-gradient(top, #00bf00 0%, #00bf00 100%)';
    document.getElementById("gCarDealerText").style.visibility = "visible";
    document.getElementById("gCarDealer").style.visibility = "visible";
    document.getElementById("gCarRentalText").style.visibility = "visible";
    document.getElementById("gCarRental").style.visibility = "visible";
    document.getElementById("gCarRepairText").style.visibility = "visible";
    document.getElementById("gCarRepair").style.visibility = "visible";
    document.getElementById("gCarWashText").style.visibility = "visible";
    document.getElementById("gCarWash").style.visibility = "visible";
}

function gAutoOff() {
    autoPressed = false;
    document.getElementById("auto").style.background = '-webkit-linear-gradient(top, #606060 0%, #606060 100%)';
    document.getElementById("gCarDealerText").style.visibility = "hidden";
    document.getElementById("gCarDealer").style.visibility = "hidden";
    document.getElementById("gCarRentalText").style.visibility = "hidden";
    document.getElementById("gCarRental").style.visibility = "hidden";
    document.getElementById("gCarRepairText").style.visibility = "hidden";
    document.getElementById("gCarRepair").style.visibility = "hidden";
    document.getElementById("gCarWashText").style.visibility = "hidden";
    document.getElementById("gCarWash").style.visibility = "hidden";
}

function fAutoDealershipRangeChange() {
    document.getElementById("fAutoDealershipText").value = "Дилеры: " + document.getElementById("fAutoDealership").value + "%";
}

function fAutoGarageRangeChange() {
    document.getElementById("fAutoGarageText").value = "Мастерские: " + document.getElementById("fAutoGarage").value + "%";
}

function fAutoWorkshopRangeChange() {
    document.getElementById("fAutoWorkshopText").value = "Сервисы: " + document.getElementById("fAutoWorkshop").value + "%";
}

function fCarWashRangeChange() {
    document.getElementById("fCarWashText").value = "Мойки: " + document.getElementById("fCarWash").value + "%";
}

function gCarDealerRangeChange() {
    document.getElementById("gCarDealerText").value = "Дилеры: " + document.getElementById("gCarDealer").value + "%";
}

function gCarRentalRangeChange() {
    document.getElementById("gCarRentalText").value = "Аренда: " + document.getElementById("gCarRental").value + "%";
}

function gCarRepairRangeChange() {
    document.getElementById("gCarRepairText").value = "Мастерскии: " + document.getElementById("gCarRepair").value + "%";
}

function gCarWashRangeChange() {
    document.getElementById("gCarWashText").value = "Мойки: " + document.getElementById("gCarWash").value + "%";
}

function autoPercents() {
    if (foursquareSourcePressed) {
        return "[" + document.getElementById("fAutoDealership").value + "," +
            document.getElementById("fAutoGarage").value + "," +
            document.getElementById("fAutoWorkshop").value + "," +
            document.getElementById("fCarWash").value + "]";
    }
    if (googleSourcePressed) {
        return "[" + document.getElementById("gCarDealer").value + "," +
            document.getElementById("gCarRental").value + "," +
            document.getElementById("gCarRepair").value + "," +
            document.getElementById("gCarWash").value + "]";
    }
    return null;
}
