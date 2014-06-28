package com.anyks.playgames.playgamesutils;

import android.content.Intent;

import org.json.JSONObject;

public abstract interface PlayGamesServicesInterface {

	public void onConnected();
	public void onConnectionFailed();
    public void onShowServicesActivity(Intent intent);
    public void onActionResult(ActionType type, boolean isResult);
    public void onLoadAchievements(ActionType type, JSONObject jsonObject);
    public void onLoadTopScores(ActionType type, JSONObject jsonObject);

}
