package com.anyks.playgames.playgamesutils;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;

import org.json.JSONObject;

public interface PlayGamesServicesInterface {

    public void onConnected(Bundle connectionHint);
    public void onConnectionFailed(ConnectionResult result);
    public void onShowServicesActivity(Intent intent);
    public void onActionResult(ActionType type, boolean isResult);
    public void onLoadAchievements(ActionType type, JSONObject jsonObject);
    public void onLoadTopScores(ActionType type, JSONObject jsonObject);

}
