package com.anyks.playgames.playgamesutils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.Leaderboards;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayGamesServices implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "PlayGamesServices";
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    private GoogleApiClient mGoogleApiClient;

    PlayGamesServicesInterface mCallback;

    Activity mActivity;

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        if (mCallback != null) {
            mCallback.onConnected(bundle);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (mCallback != null) {
            mCallback.onConnectionFailed(result);
        }
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), mActivity, 0, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }
            ).show();
            return;
        }
        try {
            result.startResolutionForResult(mActivity, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }

    public void setOnPlayGamesServicesCallback(PlayGamesServicesInterface listener) {
        mActivity = (Activity) listener;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                    .addApi(Games.API)
                    .addScope(Games.SCOPE_GAMES)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        try {
            mCallback = (PlayGamesServicesInterface) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling activity must implement PlayGamesServicesCallback interface");
        }
    }

    public boolean isConnect() {
        return mGoogleApiClient.isConnected();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public void reconnect() {
        retryConnecting();
    }

    public String getAccountName() {
        return Games.getCurrentAccountName(mGoogleApiClient);
    }

    //
    // ********** Achievements **********
    //
    public void showAchievements() {
        if (mCallback != null) {
            mCallback.onShowServicesActivity(Games.Achievements.getAchievementsIntent(mGoogleApiClient));
        }
    }

    public void setIncrement(boolean callback, String incrementId, int numSteps) {
        if (!callback) {
            Games.Achievements.increment(mGoogleApiClient, incrementId, numSteps);
        } else {
            Games.Achievements.incrementImmediate(mGoogleApiClient, incrementId, numSteps).setResultCallback(new ResultCallback<Achievements.UpdateAchievementResult>() {
                @Override
                public void onResult(Achievements.UpdateAchievementResult updateAchievementResult) {
                    if (mCallback != null) {
                        mCallback.onActionResult(ActionType.INCREMENT, getStatusResult(updateAchievementResult.getStatus()));
                    }
                }
            });
        }
    }

    public void loadAchievements(boolean forceReload) {
        Games.Achievements.load(mGoogleApiClient, forceReload).setResultCallback(new ResultCallback<Achievements.LoadAchievementsResult>() {
            @Override
            public void onResult(Achievements.LoadAchievementsResult loadAchievementsResult) {
                AchievementBuffer buffer = loadAchievementsResult.getAchievements();
                String jsonString = "{";
                for (int i = 0; i < buffer.getCount(); i++) {
                    jsonString += "\"" + buffer.get(i).getAchievementId() + "\":\"" + buffer.get(i).getName();
                    if (i < buffer.getCount() - 1) {
                        jsonString += "\",";
                    } else {
                        jsonString += "\"}";
                    }
                }
                JSONObject json = null;
                try {
                    json = new JSONObject(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getLocalizedMessage());
                }
                if (mCallback != null) {
                    mCallback.onLoadAchievements(ActionType.LOAD, json);
                }
            }
        });
    }

    public void setReveal(boolean callback, String revealId) {
        if (!callback) {
            Games.Achievements.reveal(mGoogleApiClient, revealId);
        } else {
            Games.Achievements.revealImmediate(mGoogleApiClient, revealId).setResultCallback(new ResultCallback<Achievements.UpdateAchievementResult>() {
                @Override
                public void onResult(Achievements.UpdateAchievementResult updateAchievementResult) {
                    if (mCallback != null) {
                        mCallback.onActionResult(ActionType.REVEAL, getStatusResult(updateAchievementResult.getStatus()));
                    }
                }
            });
        }
    }

    public void setSetSteps(boolean callback, String stepsId, int numSteps) {
        if (!callback) {
            Games.Achievements.setSteps(mGoogleApiClient, stepsId, numSteps);
        } else {
            Games.Achievements.setStepsImmediate(mGoogleApiClient, stepsId, numSteps).setResultCallback(new ResultCallback<Achievements.UpdateAchievementResult>() {
                @Override
                public void onResult(Achievements.UpdateAchievementResult updateAchievementResult) {
                    if (mCallback != null) {
                        mCallback.onActionResult(ActionType.SET_STEPS, getStatusResult(updateAchievementResult.getStatus()));
                    }
                }
            });
        }
    }

    public void setUnlockAchievement(boolean callback, String achievementId) {
        if (!callback) {
            Games.Achievements.unlock(mGoogleApiClient, achievementId);
        } else {
            Games.Achievements.unlockImmediate(mGoogleApiClient, achievementId).setResultCallback(new ResultCallback<Achievements.UpdateAchievementResult>() {
                @Override
                public void onResult(Achievements.UpdateAchievementResult updateAchievementResult) {
                    if (mCallback != null) {
                        mCallback.onActionResult(ActionType.UNLOCK, getStatusResult(updateAchievementResult.getStatus()));
                    }
                }
            });
        }
    }

    //
    // ********** Leaderboards **********
    //
    public void showLeaderboards() {
        if (mCallback != null) {
            mCallback.onShowServicesActivity(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient));
        }
    }

    public void loadTopScores(String leaderboardId, int span, int leaderboardCollection, int maxResults, boolean forceReload) {
        Games.Leaderboards.loadTopScores(mGoogleApiClient, leaderboardId, span, leaderboardCollection, maxResults, forceReload).setResultCallback(new ResultCallback<Leaderboards.LoadScoresResult>() {
            @Override
            public void onResult(Leaderboards.LoadScoresResult loadScoresResult) {
                Leaderboard leaderboard = loadScoresResult.getLeaderboard();
                LeaderboardScoreBuffer buffer = loadScoresResult.getScores();
                String jsonString = "{\"" + leaderboard.getDisplayName() + "\":\"" + buffer.get(0).getDisplayScore() + "\"}";
                JSONObject json = null;
                try {
                    json = new JSONObject(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getLocalizedMessage());
                }
                if (mCallback != null) {
                    mCallback.onLoadTopScores(ActionType.TOP_SCORES, json);
                }
            }
        });
    }

    public void setSubmitScore(boolean callback, String submitScoreId, long score) {
        if (!callback) {
            Games.Leaderboards.submitScore(mGoogleApiClient, submitScoreId, score);
        } else {
            Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, submitScoreId, score).setResultCallback(new ResultCallback<Leaderboards.SubmitScoreResult>() {
                @Override
                public void onResult(Leaderboards.SubmitScoreResult submitScoreResult) {
                    if (mCallback != null) {
                        mCallback.onActionResult(ActionType.SUBMIT_SCORE, getStatusResult(submitScoreResult.getStatus()));
                    }
                }
            });
        }
    }

    private void retryConnecting() {
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    private boolean getStatusResult(Status status) {
        boolean result = false;
        if (status.isSuccess())
            result = true;
        else if (status.isCanceled())
            result = false;
        return result;
    }
}
