package com.psucoders.shuttler.utils.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.psucoders.shuttler.R;
import com.psucoders.shuttler.data.model.NotificationSentModel;
import com.psucoders.shuttler.ui.login.LoginActivity;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    public static void sendNotification(String locationName) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://us-central1-shuttler-p001.cloudfunctions.net/").addConverterFactory(GsonConverterFactory.create()).build();
        NotificationService notificationService = retrofit.create(NotificationService.class);
        Call<NotificationSentModel> call = notificationService.sendNotification(locationName);

        call.enqueue(new Callback<NotificationSentModel>() {
            @Override
            public void onResponse(@NotNull Call<NotificationSentModel> call, @NotNull Response<NotificationSentModel> response) {
                Log.d("test", "test$response");
            }

            @Override
            public void onFailure(@NotNull Call<NotificationSentModel> call, Throwable t) {
                Log.d("test", "test${t.toString()}");
            }
        });
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", token).apply();
    }

    public void setNewToken(String token, Context context) {
        Log.d(TAG, "Token: " + token);
        context.getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", token).apply();
    }
}