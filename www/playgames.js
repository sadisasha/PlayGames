var PlayGames = function () {
    this.options = {};
};

PlayGames.prototype.authenticate = function (success, fail) {
	return cordova.exec(success, fail, "PlayGamesPlugin", "authenticate", ["786440797162"]);
};

module.exports = new PlayGames();