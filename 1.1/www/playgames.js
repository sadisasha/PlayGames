var PlayGames = function () {
    this.options = {};
};

// Подключение к сервису
// При неудачном подключении, будет еще одна попытка
// Всего 2 попытки
// Изменить число попыток можно в классе PlayGamesPlugin
// Заменив mPlay = new PlayGamesServices(getActivity());
// На mPlay = new PlayGamesServices(getActivity(), число попыток);
// P.S в PlayGamesPlugin есть соответствующий комментарий
PlayGames.prototype.connect = function (success, failed) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "connect", [null]);
};

// Метод для проверки статуса подключения
// Если подключение существует isConnected вернет true (string), иначе false (string)
PlayGames.prototype.isConnected = function (success, failed) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "isConnected", [null]);
};

// Метод вернет email (string)
PlayGames.prototype.getAccountName = function (success, failed) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "getAccountName", [null]);
};

// Метод вызывет нативный activity со всеми существующими достижениями в игре
PlayGames.prototype.showAchievements = function (success, failed) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "showAchievements", [null]);
};

// Увеличивает этап достижения
// callback - (true/false) если true,
// то метод вернет результат (true/false), иначе метод ни чего не вернет
// id - идентификатор достижения
// Например есть достижение achievement_test = CgkI6veY3PEWEAIQFw, то id=CgkI6veY3PEWEAIQFw
// numSteps - (int) число шагов для увеличения
PlayGames.prototype.setIncrement = function (success, failed, callback, id, numSteps) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "setIncrement", [callback, id, numSteps]);
};

// Метод получает json все существующих достижений ('имя': 'id')
// forceReload - если true то локальные данные, будут заменятся последними данными с сервера (очистка кеша)
// Рекомендуется false - из-за преимущества кеширования данных
PlayGames.prototype.loadAchievements = function (success, failed, forceReload) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "loadAchievements", [forceReload]);
};

// Метод устанавливает состояние достижения
// callback - (true/false) если true,
// то метод вернет результат (true/false), иначе метод ни чего не вернет
// id - идентификатор достижения
PlayGames.prototype.setReveal = function (success, failed, callback, id) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "setReveal", [callback, id]);
};

// Метод разблокирует пошаговое достижение
// callback - (true/false) если true,
// то метод вернет результат (true/false), иначе метод ни чего не вернет
// id - идентификатор достижения
// Например есть достижение achievement_test = CgkI6veY3PEWEAIQFw, то id=CgkI6veY3PEWEAIQFw
// numSteps - (int) число шагов для разблокировки
PlayGames.prototype.setSetSteps = function (success, failed, callback, id, numSteps) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "setSetSteps", [callback, id, numSteps]);
};

// Метод разблокирует достижение
// callback - (true/false) если true,
// то метод вернет результат (true/false), иначе метод ни чего не вернет
// id - идентификатор достижения
PlayGames.prototype.setUnlockAchievement = function (success, failed, callback, id) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "setUnlockAchievement", [callback, id]);
};

// Метод вызывет нативный activity со всеми существующими рейтингами игроков
PlayGames.prototype.showLeaderboards = function (success, failed) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "showLeaderboards", [null]);
};

// Асинхронный метод, вернет топ рейтинга у игрока
// id - идентификатор рейтинга
// span - промежуток времени (только 0, 1, 2). 0 - за день, 1 - за неделю, 2 - за все время
// leaderboardCollection - (только 0, 1). 0 - среди всех игроков, 1 - среди игроков в кругах соц. сети
// maxResults - (от 1 до 25). Количество страниц с которых получаем топ
// forceReload - если true то локальные данные, будут заменятся последними данными с сервера (очистка кеша)
// Рекомендуется false - из-за преимущества кеширования данных
PlayGames.prototype.loadTopScores = function (success, failed, id, span, leaderboardCollection, maxResults, forceReload) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "loadTopScores", [id, span, leaderboardCollection, maxResults, forceReload]);
};

// Метод добовляет баллы к рейтингу
// callback - (true/false) если true,
// то метод вернет результат (true/false), иначе метод ни чего не вернет
// id - идентификатор рейтинга
// score - число баллов для поднятия рейтинга
PlayGames.prototype.setSubmitScore = function (success, failed, callback, id, score) {
	return cordova.exec(success, failed, "PlayGamesPlugin", "setSubmitScore", [callback, id, score]);
};

module.exports = new PlayGames();