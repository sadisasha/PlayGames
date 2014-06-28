var PlayGames = function () {
    this.options = {};
};

PlayGames.prototype.connect = function (success, failed) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "connect", [null]);
};

PlayGames.prototype.isconnected = function (success, failed) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "isconnected", [null]);
};

PlayGames.prototype.getAccountName = function (success, failed) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "getAccountName", [null]);
};

PlayGames.prototype.showAchievements = function (success, failed) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "showAchievements", [null]);
};

PlayGames.prototype.setIncrement = function (success, failed, callback, id, numSteps) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "setIncrement", [callback, id, numSteps]);
};

PlayGames.prototype.loadAchievements = function (success, failed, forceReload) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "loadAchievements", [forceReload]);
};

PlayGames.prototype.setReveal = function (success, failed, callback, id) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "setReveal", [callback, id]);
};

PlayGames.prototype.setSetSteps = function (success, failed, callback, id, numSteps) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "setSetSteps", [callback, id, numSteps]);
};

PlayGames.prototype.setUnlockAchievement = function (success, failed, callback, id) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "setUnlockAchievement", [callback, id]);
};

PlayGames.prototype.showLeaderboards = function (success, failed) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "showLeaderboards", [null]);
};

PlayGames.prototype.loadTopScores = function (success, failed, id, span, leaderboardCollection, maxResults, forceReload) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "loadTopScores", [id, span, leaderboardCollection, maxResults, forceReload]);
};

PlayGames.prototype.setSubmitScore = function (success, failed, callback, id, score) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "setSubmitScore", [callback, id, score]);
};

module.exports = new PlayGames();