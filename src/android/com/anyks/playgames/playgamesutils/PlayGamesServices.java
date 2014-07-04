package com.anyks.playgames.playgamesutils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import com.anyks.playgames.playgamesutils.PlayGamesHelper.ActionType;
import com.anyks.playgames.playgamesutils.PlayGamesHelper.OnPlayGamesListener;
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

public class PlayGamesServices implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = "PlayGamesServices";
	private static GoogleApiClient mGoogleApiClient;

	OnPlayGamesListener mCallback;

	Activity mActivity;

	private int connectionCount = 0;

	public PlayGamesServices(Context cxt) {
		// TODO Auto-generated constructor stub
		setup(cxt);
		PlayGamesHelper.REQUEST_COUNT = 2;
	}

	public PlayGamesServices(Context cxt, int requestCount) {
		// TODO Auto-generated constructor stub
		setup(cxt);
		PlayGamesHelper.REQUEST_COUNT = requestCount;

	}

	public void setOnPlayGamesServicesCallback(OnPlayGamesListener listener) {
		try {
			mCallback = (OnPlayGamesListener) listener;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Calling activity must implement PlayGamesServicesCallback interface");
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.e(TAG, "GoogleApiClient connected");
		if (mCallback != null) {
			mCallback.onConnected();
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.e(TAG, "GoogleApiClient connection suspended");
		retryConnecting();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "GoogleApiClient connection failed: " + result.toString());
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
					mActivity, 0, new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							retryConnecting();
						}
					}).show();
			return;
		}
		try {
			if (connectionCount <= (PlayGamesHelper.REQUEST_COUNT - 1)) {
				Log.e(TAG, Integer.toString(connectionCount));
				result.startResolutionForResult(mActivity,
						PlayGamesHelper.REQUEST_CODE_RESOLUTION);
			} else {
				if (mCallback != null) {
					mCallback.onConnectionFailed();
				}
			}
			connectionCount++;
		} catch (SendIntentException e) {
			Log.e(TAG, "Exception while starting resolution activity", e);
			if (mCallback != null) {
				mCallback.onConnectionFailed();
			}
			retryConnecting();
		}
	}

	public boolean isConnect() {
		return mGoogleApiClient.isConnected();
	}

	public void connect() {
		mGoogleApiClient.connect();
	}

	public static void reconnect() {
		retryConnecting();
	}

	public void refresh() {
		if (mGoogleApiClient != null) {
			Games.signOut(mGoogleApiClient);
		}
		retryConnecting();
	}

	public void signOut() {
		Log.e(TAG, "GoogleApiClient sign out");
		if (!mGoogleApiClient.isConnected()) {
			return;
		}
		Games.signOut(mGoogleApiClient);
		mGoogleApiClient.disconnect();
	}

	public static void disconnect() {
		Log.e(TAG, "GoogleApiClient disconnect");
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
	}

	public String getAccountName() {
		return Games.getCurrentAccountName(mGoogleApiClient);
	}

	//
	// ********** Achievements **********
	//
	public void showAchievements() {
		if (mCallback != null) {
			mCallback.onShowServicesActivity(Games.Achievements
					.getAchievementsIntent(mGoogleApiClient));
		}
	}

	public void setIncrement(boolean callback, String incrementId, int numSteps) {
		if (!callback) {
			Games.Achievements.increment(mGoogleApiClient, incrementId,
					numSteps);
		} else {
			Games.Achievements.incrementImmediate(mGoogleApiClient,
					incrementId, numSteps).setResultCallback(
					new ResultCallback<Achievements.UpdateAchievementResult>() {
						@Override
						public void onResult(
								Achievements.UpdateAchievementResult updateAchievementResult) {
							if (mCallback != null) {
								mCallback.onActionResult(ActionType.INCREMENT,
										getStatusResult(updateAchievementResult
												.getStatus()));
							}
						}
					});
		}
	}

	public void loadAchievements(boolean forceReload) {
		Games.Achievements
				.load(mGoogleApiClient, forceReload)
				.setResultCallback(
						new ResultCallback<Achievements.LoadAchievementsResult>() {
							@Override
							public void onResult(
									Achievements.LoadAchievementsResult loadAchievementsResult) {
								AchievementBuffer buffer = loadAchievementsResult
										.getAchievements();
								String jsonString = "{";
								for (int i = 0; i < buffer.getCount(); i++) {
									jsonString += "\""
											+ buffer.get(i).getAchievementId()
											+ "\":\"" + buffer.get(i).getName();
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
									mCallback.onLoadAchievements(
											ActionType.LOAD, json);
								}
							}
						});
	}

	public void setReveal(boolean callback, String revealId) {
		if (!callback) {
			Games.Achievements.reveal(mGoogleApiClient, revealId);
		} else {
			Games.Achievements
					.revealImmediate(mGoogleApiClient, revealId)
					.setResultCallback(
							new ResultCallback<Achievements.UpdateAchievementResult>() {
								@Override
								public void onResult(
										Achievements.UpdateAchievementResult updateAchievementResult) {
									if (mCallback != null) {
										mCallback
												.onActionResult(
														ActionType.REVEAL,
														getStatusResult(updateAchievementResult
																.getStatus()));
									}
								}
							});
		}
	}

	public void setSetSteps(boolean callback, String stepsId, int numSteps) {
		if (!callback) {
			Games.Achievements.setSteps(mGoogleApiClient, stepsId, numSteps);
		} else {
			Games.Achievements.setStepsImmediate(mGoogleApiClient, stepsId,
					numSteps).setResultCallback(
					new ResultCallback<Achievements.UpdateAchievementResult>() {
						@Override
						public void onResult(
								Achievements.UpdateAchievementResult updateAchievementResult) {
							if (mCallback != null) {
								mCallback.onActionResult(ActionType.SET_STEPS,
										getStatusResult(updateAchievementResult
												.getStatus()));
							}
						}
					});
		}
	}

	public void setUnlockAchievement(boolean callback, String achievementId) {
		if (!callback) {
			Games.Achievements.unlock(mGoogleApiClient, achievementId);
		} else {
			Games.Achievements
					.unlockImmediate(mGoogleApiClient, achievementId)
					.setResultCallback(
							new ResultCallback<Achievements.UpdateAchievementResult>() {
								@Override
								public void onResult(
										Achievements.UpdateAchievementResult updateAchievementResult) {
									if (mCallback != null) {
										mCallback
												.onActionResult(
														ActionType.UNLOCK,
														getStatusResult(updateAchievementResult
																.getStatus()));
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
			mCallback.onShowServicesActivity(Games.Leaderboards
					.getAllLeaderboardsIntent(mGoogleApiClient));
		}
	}

	public void loadTopScores(String leaderboardId, int span,
			int leaderboardCollection, int maxResults, boolean forceReload) {
		Games.Leaderboards.loadTopScores(mGoogleApiClient, leaderboardId, span,
				leaderboardCollection, maxResults, forceReload)
				.setResultCallback(
						new ResultCallback<Leaderboards.LoadScoresResult>() {
							@Override
							public void onResult(
									Leaderboards.LoadScoresResult loadScoresResult) {
								Leaderboard leaderboard = loadScoresResult
										.getLeaderboard();
								LeaderboardScoreBuffer buffer = loadScoresResult
										.getScores();
								String jsonString = "{\""
										+ leaderboard.getDisplayName()
										+ "\":\""
										+ buffer.get(0).getDisplayScore()
										+ "\"}";
								JSONObject json = null;
								try {
									json = new JSONObject(jsonString);
								} catch (JSONException e) {
									e.printStackTrace();
									Log.e(TAG, e.getLocalizedMessage());
								}
								if (mCallback != null) {
									mCallback.onLoadTopScores(
											ActionType.TOP_SCORES, json);
								}
							}
						});
	}

	public void setSubmitScore(boolean callback, String submitScoreId,
			long score) {
		if (!callback) {
			Games.Leaderboards.submitScore(mGoogleApiClient, submitScoreId,
					score);
		} else {
			Games.Leaderboards.submitScoreImmediate(mGoogleApiClient,
					submitScoreId, score).setResultCallback(
					new ResultCallback<Leaderboards.SubmitScoreResult>() {
						@Override
						public void onResult(
								Leaderboards.SubmitScoreResult submitScoreResult) {
							if (mCallback != null) {
								mCallback.onActionResult(
										ActionType.SUBMIT_SCORE,
										getStatusResult(submitScoreResult
												.getStatus()));
							}
						}
					});
		}
	}

	private static void retryConnecting() {
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

	private void setup(Context cxt) {
		mActivity = (Activity) cxt;
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
					.setGravityForPopups(Gravity.TOP).addApi(Games.API)
					.addScope(Games.SCOPE_GAMES).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
		mGoogleApiClient.connect();
	}
}
