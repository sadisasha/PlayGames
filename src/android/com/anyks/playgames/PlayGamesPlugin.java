package com.anyks.playgames;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import com.anyks.playgames.playgamesutils.ActionType;
import com.anyks.playgames.playgamesutils.PlayGamesServices;
import com.anyks.playgames.playgamesutils.PlayGamesServicesInterface;
import com.google.android.gms.common.ConnectionResult;

public class PlayGamesPlugin extends CordovaPlugin implements PlayGamesServicesInterface {

	PlayGamesServices mPlay;
	CallbackContext mCallbackContext;
	
	@Override
	public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {

		mCallbackContext = callbackContext;
		Boolean isValidAction = true;
		
		if (mPlay == null) {
			mPlay = new PlayGamesServices();
			mPlay.setOnPlayGamesServicesCallback(this);
		}
		
		try {
			if ("connect".equals(action)) {
				if (!mPlay.isConnect()) {
	                mPlay.connect();
	                isValidAction = true;
	            }
			} else if ("disconnect".equals(action)) {
	            if (mPlay.isConnect()) {
	                mPlay.disconnect();
	                isValidAction = true;
	            }
			} else if ("reconnect".equals(action)) {
				mPlay.reconnect();
				isValidAction = true;
			} else if ("statusConnection".equals(action)) {
				String result = "false";
				if (mPlay.isConnect()) {
					result = "true";
					isValidAction = true;
				}
				mCallbackContext.success(result);
			} else if ("accountName".equals(action)) {
				if (!mPlay.getAccountName().equals("")) {
					mCallbackContext.success(mPlay.getAccountName());
					isValidAction = true;
				} else {
					mCallbackContext.error("");
					isValidAction = false;
				}
			} else if ("showAchievements".equals(action)) {
				mPlay.showAchievements();
				isValidAction = true;
			} else if ("setIncrement".equals(action)) {
				mPlay.setIncrement(data.getBoolean(0), data.getString(1), data.getInt(2));
				isValidAction = true;
			} else if ("loadAchievements".equals(action)) {
				mPlay.loadAchievements(data.getBoolean(0));
				isValidAction = true;
			} else if ("setReveal".equals(action)) {
				mPlay.setReveal(data.getBoolean(0), data.getString(1));
				isValidAction = true;
			} else if ("setSetSteps".equals(action)) {
				mPlay.setSetSteps(data.getBoolean(0), data.getString(1), data.getInt(2));
				isValidAction = true;
			} else if ("setUnlockAchievement".equals(action)) {
				mPlay.setUnlockAchievement(data.getBoolean(0), data.getString(1));
				isValidAction = true;
			} else if ("showLeaderboards".equals(action)) {
				mPlay.showLeaderboards();
				isValidAction = true;
			} else if ("loadTopScores".equals(action)) {
				mPlay.loadTopScores(data.getString(0), data.getInt(1), data.getInt(2), data.getInt(3), data.getBoolean(4));
				isValidAction = true;
			} else if ("setSubmitScore".equals(action)) {
				mPlay.setSubmitScore(data.getBoolean(0), data.getString(1), data.getLong(2));
				isValidAction = true;
			}
		} catch (IllegalStateException e) {
			callbackContext.error(e.getMessage());
			isValidAction = false;
		} 
		
		return isValidAction;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		mCallbackContext.success();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		mCallbackContext.success();
	}

	@Override
	public void onShowServicesActivity(Intent intent) {
		// TODO Auto-generated method stub
		this.cordova.getActivity().startActivityForResult(intent, 1);
	}

	@Override
	public void onActionResult(ActionType type, boolean isResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadAchievements(ActionType type, JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadTopScores(ActionType type, JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
	}
}