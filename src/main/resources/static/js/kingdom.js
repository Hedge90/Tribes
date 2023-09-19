'use strict';
const token = window.localStorage.getItem("token");
if (token == null) {
    window.location.href = "/access-denied";
}
document.addEventListener('DOMContentLoaded', async () => {
    const kingdomTexts = await createGetRequest("http://localhost:8080/text/kingdom_texts.json");
    const data = await createGetRequest("http://localhost:8080/kingdom");
    const headerText = document.getElementById('header-text');
    headerText.innerText = data.name;
    document.title = data.name;
    let townhall;
    for (let i = 0; i < data.buildings.length; i++) {
        if (data.buildings[i].type == "TOWNHALL") {
            townhall = data.buildings[i];
        }
    }
    const buildingsBackground = document.getElementById('background');
    const emptySpace = "<img class='empty-space' src='../img/empty-space.png' alt='empty space'/>";
    let buildingInnerHTML = "<div class='building-townhall' id='buildingID-" + townhall.id + "'><p class='level-townhall'>" + townhall.level + "</p><br>" +
        "<img class='townhall' src='../img/townhall.png' alt='townhall'/>" +
        "<br><p class='text-townhall'>" + kingdomTexts.townhall + "</p></div>";
    for (let i = 1; i < 13; i++) {
        let find = false;
        for (let j = 0; j < data.buildings.length; j++) {
            if (i == data.buildings[j].position) {
                if (data.buildings[j].type == "MINE") {
                    buildingInnerHTML += "<div class='building-space-" + i + "' id='buildingID-" + data.buildings[j].id + "'>" +
                        "<p class='level'>" + data.buildings[j].level + "</p><br><img class='mine' src='../img/mine.png' alt='mine'/><br><p class='text-mine'>" + kingdomTexts.mine + "</p>" +
                        "</div>";
                    find = true;
                } else if (data.buildings[j].type == "FARM") {
                    buildingInnerHTML += "<div class='building-space-" + i + "' id='buildingID-" + data.buildings[j].id + "'>" +
                        build("farm", data.buildings[j], kingdomTexts.farm) +
                        "</div>";
                    find = true;
                } else if (data.buildings[j].type == "ACADEMY") {
                    buildingInnerHTML += "<div class='building-space-" + i + "' id='buildingID-" + data.buildings[j].id + "'>" +
                        build("academy", data.buildings[j], kingdomTexts.academy) +
                        "</div>";
                    find = true;
                }
            }
        }
        if (!find) {
            buildingInnerHTML += "<div class='building-space-" + i + "'>" + emptySpace + "</div>";
        }
    }

    buildingsBackground.innerHTML = buildingInnerHTML;
    const troops = document.getElementById('troops');
    troops.innerHTML += "<strong>" + kingdomTexts.location + "</strong>" + data.location.x + " | " + data.location.y;
    troops.innerHTML += "<h2>" + kingdomTexts.troopsTitleText + "<h2>";
    for (let i = 1; i < 12; i++) {
        let count = 0;
        for (let j = 0; j < data.troops.length; j++) {
            if (data.troops[j].level == i) {
                count++;
            }
        }
        if (count != 0) {
            if (i > 10) {
                troops.innerHTML += "<p><img class='troop' src='../img/knight-10.png' alt='troop'/> " + kingdomTexts.troopNames[10] + " - <b id='troopCount-" + i + "'>" + count + "</b>x</p>";
            } else {
                troops.innerHTML += "<p><img class='troop' src='img/knight-" + i + ".png' alt='troop'/> " + kingdomTexts.troopNames[i - 1] + " - <b id='troopCount-" + i + "'>" + count + "</b>x</p>";
            }
        }
    }

    const resources = document.getElementById('resources');
    let gold;
    let food;
    if (data.resources[0].type == "GOLD") {
        gold = data.resources[0].amount;
        food = data.resources[1].amount;
    } else {
        gold = data.resources[1].amount;
        food = data.resources[0].amount;
    }
    resources.innerHTML += "<img class='resource' src='../img/gold.png' alt='gold'> " + kingdomTexts.gold + ": " + gold + "       " +
        "<img class='resource' src='../img/food.png' alt='food'> " + kingdomTexts.food + ": " + food;
    const popUp = document.getElementById("myModal");
    const closeButton = document.getElementsByClassName("close")[0];
    const popupText = popUp.getElementsByClassName("modal-content")[0];
    headerText.addEventListener("click", () => {
        popupText.innerHTML = "<h1>" + kingdomTexts.changeKingdomNameTitle + "</h1><br>" +
            "<p>" + kingdomTexts.changeKingdomNameDescription + "</p><input type='text' name='change-kingdom-name' id='change-kingdom-name-input' value='" + headerText.innerText + "'><br>" +
            "<button id='change-kingdom-name-button'>" + kingdomTexts.changeButton + "</button>";
        popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
        popUp.style.display = "block";
        document.getElementById("change-kingdom-name-button").addEventListener('click', async () => {
            const kingdomNameInput = document.getElementById("change-kingdom-name-input");
            headerText.innerText = kingdomNameInput.value;
            await createRequest("PUT", "http://localhost:8080/kingdom", {"name": kingdomNameInput.value});
            popUp.style.display = "none";
        });
    });
    buildingsBackground.addEventListener('click', async (event) => {
        let upgradeBuilding;
        let position;
        let troopList;
        if (event.target.className == "mine") {
            const id = event.target.parentElement.id.split("-");
            const building = await createGetRequest("http://localhost:8080/kingdom/buildings/" + id[1]);
            upgradeBuilding = building;
            popupText.innerHTML = "<h1>" + kingdomTexts.mine + "</h1><br>" +
                kingdomTexts.mineDescription + "<br>" +
                kingdomTexts.buildingStatusTexts.level + building.level + "<br>" +
                kingdomTexts.buildingStatusTexts.hp + building.hp + "<br>" +
                kingdomTexts.buildingStatusTexts.upgradeTime + convertToMinutesAndSeconds(kingdomTexts.buildingConfigs.times.mine * building.level) + "<br>" +
                kingdomTexts.buildingStatusTexts.cost + kingdomTexts.buildingConfigs.costs.mine * building.level + "<br>" +
                "<button id='upgrade'>" + kingdomTexts.upgradeButton + "</button>";
            popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
            popUp.style.display = "block";
        } else if (event.target.className == "farm") {
            const id = event.target.parentElement.id.split("-");
            const building = await createGetRequest("http://localhost:8080/kingdom/buildings/" + id[1]);
            upgradeBuilding = building;
            popupText.innerHTML = "<h1>" + kingdomTexts.farm + "</h1><br>" +
                kingdomTexts.farmDescription + "<br>" +
                kingdomTexts.buildingStatusTexts.level + building.level + "<br>" +
                kingdomTexts.buildingStatusTexts.hp + building.hp + "<br>" +
                kingdomTexts.buildingStatusTexts.upgradeTime + convertToMinutesAndSeconds(kingdomTexts.buildingConfigs.times.farm * building.level) + "<br>" +
                kingdomTexts.buildingStatusTexts.cost + kingdomTexts.buildingConfigs.costs.farm * building.level + "<br>" +
                "<button id='upgrade'>" + kingdomTexts.upgradeButton + "</button>";
            popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
            popUp.style.display = "block";
        } else if (event.target.className == "townhall") {
            const id = event.target.parentElement.id.split("-");
            const building = await createGetRequest("http://localhost:8080/kingdom/buildings/" + id[1]);
            upgradeBuilding = building;
            popupText.innerHTML = "<h1>" + kingdomTexts.townhall + "</h1><br>" +
                kingdomTexts.townhallDescription + "<br>" +
                kingdomTexts.buildingStatusTexts.level + building.level + "<br>" +
                kingdomTexts.buildingStatusTexts.hp + building.hp + "<br>" +
                kingdomTexts.buildingStatusTexts.upgradeTime + convertToMinutesAndSeconds(kingdomTexts.buildingConfigs.times.townhall * building.level) + "<br>" +
                kingdomTexts.buildingStatusTexts.cost + kingdomTexts.buildingConfigs.costs.townhall * building.level + "<br>" +
                "<button id='upgrade'>" + kingdomTexts.upgradeButton + "</button>";
            popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
            popUp.style.display = "block";
        } else if (event.target.className == "academy") {
            const id = event.target.parentElement.id.split("-");
            const building = await createGetRequest("http://localhost:8080/kingdom/buildings/" + id[1]);
            upgradeBuilding = building;
            popupText.innerHTML = "<h1>" + kingdomTexts.academy + "</h1><hr>" +
                "<p>" + kingdomTexts.academyDescription + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.level + building.level + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.hp + building.hp + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.upgradeTime + convertToMinutesAndSeconds(kingdomTexts.buildingConfigs.times.academy * building.level) + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.cost + kingdomTexts.buildingConfigs.costs.academy * building.level + "</p>" +
                "<button id='upgrade'>" + kingdomTexts.upgradeButton + "</button><hr>" +
                "<h2>" + kingdomTexts.troopTrainTitle + "</h2>" +
                "<p>" + kingdomTexts.troopTrainDescription + "</p>" +
                "<p>" + kingdomTexts.troopStatusTexts.name + kingdomTexts.troopNames[building.level - 1] + "</p>" +
                "<p>" + kingdomTexts.troopStatusTexts.level + building.level + "</p>" +
                "<p>" + kingdomTexts.troopStatusTexts.hp + kingdomTexts.troopConfigs.hp + "</p>" +
                "<p>" + kingdomTexts.troopStatusTexts.time + kingdomTexts.troopConfigs.time + "</p>" +
                "<p>" + kingdomTexts.troopStatusTexts.cost + kingdomTexts.troopConfigs.cost + "</p>" +
                "<button id='train'>" + kingdomTexts.trainButton + "</button><hr>";
            const data = await createGetRequest("http://localhost:8080/kingdom/troops");
            if (data.status == "error") {
                popupText.innerHTML = "<h1>Error</h1><br>" +
                    data.message;
                popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
            } else {
                const troops = data.troops;
                troopList = troops;
                popupText.innerHTML += "<h2>" + kingdomTexts.troopListTitle + "</h2>";
                for (let i = 0; i < troops.length; i++) {
                    let underTrainOrUpgrade;
                    let time;
                    const currentTime = new Date;
                    if (currentTime - troops[i].startedAt > 0) {
                        time = convertToMinutesAndSeconds((currentTime - troops[i].startedAt) / 1000);
                    } else {
                        time = "00:00"
                    }
                    if (troops[i].finishAt == null) {
                        underTrainOrUpgrade = kingdomTexts.troopStatusTexts.time;
                    } else {
                        underTrainOrUpgrade = kingdomTexts.troopStatusTexts.upgradeTime;
                    }
                    popupText.innerHTML += "<li>" + kingdomTexts.troopNames[troops[i].level - 1] + " | " +
                        kingdomTexts.troopStatusTexts.hp + troops[i].hp + " | " +
                        kingdomTexts.troopStatusTexts.attack + troops[i].attack + " | " +
                        kingdomTexts.troopStatusTexts.defence + troops[i].defence + " | " +
                        underTrainOrUpgrade + time + " | " +
                        "<button id='troop-upgrade-" + troops[i].id + "'>" + kingdomTexts.upgradeButton + "</button></li>";
                }
                popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
                popupText.style.height = "500px";
                popUp.style.display = "block";
            }
        } else if (event.target.className == "empty-space") {
            position = event.target.parentElement.className.split("-")[2];
            popupText.innerHTML = "<h1>" + kingdomTexts.buildTitleText + "</h1>" +
                "<p>" + kingdomTexts.buildDescriptionText + "</p><hr>" +
                "<h3>" + kingdomTexts.academy + "</h3>" +
                "<p>" + kingdomTexts.newAcademyDescription + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.hp + kingdomTexts.buildingConfigs.hp.academy + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.buildTime + convertToMinutesAndSeconds(kingdomTexts.buildingConfigs.times.academy) + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.cost + kingdomTexts.buildingConfigs.costs.academy + "</p>" +
                "<button id='build-academy'>" + kingdomTexts.buildButton + "</button><hr>" +
                "<h3>" + kingdomTexts.mine + "</h3><br>" +
                "<p>" + kingdomTexts.newMineDescription + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.hp + kingdomTexts.buildingConfigs.hp.mine + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.buildTime + convertToMinutesAndSeconds(kingdomTexts.buildingConfigs.times.mine) + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.cost + kingdomTexts.buildingConfigs.costs.mine + "</p>" +
                "<button id='build-mine'>" + kingdomTexts.buildButton + "</button><hr>" +
                "<h3>" + kingdomTexts.farm + "</h3><br>" +
                "<p>" + kingdomTexts.newFarmDescription + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.hp + kingdomTexts.buildingConfigs.hp.farm + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.buildTime + convertToMinutesAndSeconds(kingdomTexts.buildingConfigs.times.farm) + "</p>" +
                "<p>" + kingdomTexts.buildingStatusTexts.cost + kingdomTexts.buildingConfigs.costs.farm + "</p>" +
                "<button id='build-farm'>" + kingdomTexts.buildButton + "</button><hr>";
            popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
            popupText.style.height = "500px";
            popUp.style.display = "block";
        }
        if (upgradeBuilding != undefined) {
            document.getElementById("upgrade").addEventListener('click', async () => {
                const result = await createRequest('PUT', 'http://localhost:8080/kingdom/buildings/' + upgradeBuilding.id, {"level": upgradeBuilding.level + 1});
                if (result.status == "error") {
                    popupText.innerHTML = "<h1>Error</h1><br>" +
                        result.message;
                    popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
                } else {
                    popUp.style.display = "none";
                }
            });
            if (upgradeBuilding.type == "ACADEMY") {
                document.getElementById("train").addEventListener('click', async () => {
                    const result = await createRequest('POST', 'http://localhost:8080/kingdom/troops', {
                        "buildingId": upgradeBuilding.id
                    });
                    if (result.status == "error") {
                        popupText.innerHTML = "<h1>Error</h1><br>" +
                            result.message;
                        popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
                    } else {
                        const troopCount = document.getElementById("troopCount-" + result.level);
                        if (troopCount != undefined) {
                            const lastTroopCount = troopCount.innerText
                            troopCount.innerText = +lastTroopCount + 1;
                        }
                    }
                });
            }
            if (troopList != undefined) {
                popupText.addEventListener('click', async (event) => {
                    for (let i = 0; i < troopList.length; i++) {
                        const id = event.target.id.split("-")[2];
                        if (id == troopList[i].id) {
                            const result = await createRequest("PUT", "http://localhost:8080/kingdom/troops/" + troopList[i].id, {
                                "buildingId": upgradeBuilding.id
                            });
                            if (result.status == "error") {
                                popupText.innerHTML = "<h1>Error</h1><br>" +
                                    result.message;
                                popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
                            }
                        }
                    }
                });
            }
        }
        if (position != undefined) {
            document.getElementById("build-academy").addEventListener('click', async () => {
                const result = await createRequest('POST', 'http://localhost:8080/kingdom/buildings', {
                    "type": "academy",
                    "position": position
                });
                if (result.status == "error") {
                    popupText.innerHTML = "<h1>Error</h1><br>" +
                        result.message;
                    popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
                } else {
                    const buildingSpace = document.getElementsByClassName("building-space-" + position)[0];
                    buildingSpace.innerHTML = build("academy", result, kingdomTexts.academy);
                    popUp.style.display = "none";
                }
            });
            document.getElementById("build-mine").addEventListener('click', async () => {
                const result = await createRequest('POST', 'http://localhost:8080/kingdom/buildings', {
                    "type": "mine",
                    "position": position
                });
                if (result.status == "error") {
                    popupText.innerHTML = "<h1>Error</h1><br>" +
                        result.message;
                    popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
                } else {
                    const buildingSpace = document.getElementsByClassName("building-space-" + position)[0];
                    buildingSpace.innerHTML = build("mine", result, kingdomTexts.mine);
                    popUp.style.display = "none";
                }
            });
            document.getElementById("build-farm").addEventListener('click', async () => {
                const result = await createRequest('POST', 'http://localhost:8080/kingdom/buildings', {
                    "type": "farm",
                    "position": position
                });
                if (result.status == "error") {
                    popupText.innerHTML = "<h1>Error</h1><br>" +
                        result.message;
                    popupText.insertBefore(closeButton, popupText.getElementsByTagName('h1')[0]);
                } else {
                    const buildingSpace = document.getElementsByClassName("building-space-" + position)[0];
                    buildingSpace.innerHTML = build("farm", result, kingdomTexts.farm);
                    popUp.style.display = "none";
                }
            });
        }
    });
    closeButton.onclick = function () {
        popUp.style.display = "none";
        popupText.style.height = "auto";
    }
    window.onclick = function (event) {
        if (event.target == popUp) {
            popUp.style.display = "none";
            popupText.style.height = "auto";
        }
    }
    document.getElementById("logout").addEventListener('click', () => {
        window.localStorage.clear();
        window.location.href = "/";
    });
});

async function createGetRequest(url) {
    try {
        const response = await fetch(url, {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });
        const data = await response.json();
        if (!response.ok) {
            if (data.status == "error") {
                return data;
            }
            throw new Error(response.statusText);
        }
        return data;
    } catch (error) {
        window.localStorage.clear();
        window.location.href = "/";
        return error;
    }
}

async function createRequest(requestType, url, sendData) {
    try {
        const response = await fetch(url, {
            method: requestType,
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token,
            },
            body: JSON.stringify(sendData)
        });
        const data = await response.json();
        if (!response.ok) {
            if (data.status == "error") {
                return data;
            }
            throw new Error(response.statusText);
        }
        return data;
    } catch (error) {
        window.localStorage.clear();
        window.location.href = "/";
        return error;
    }
}

function convertToMinutesAndSeconds(seconds) {
    let minutes = Math.floor(seconds / 60);
    let extraSeconds = seconds % 60;
    minutes = minutes < 10 ? "0" + minutes : minutes;
    extraSeconds = Math.round(extraSeconds);
    extraSeconds = extraSeconds < 10 ? "0" + extraSeconds : extraSeconds;
    return minutes + ":" + extraSeconds;
}

function build(type, building, text) {
    let level = 'level-' + type;
    if (type == "mine" || type == "farm") {
        level = 'level';
    }
    return "<p class='" + level + "'>" + building.level + "</p><br><img class='" + type + "' src='img/" + type + ".png' alt='" + type + "'/><br><p class='text-" + type + "'>" + text + "</p>";
}