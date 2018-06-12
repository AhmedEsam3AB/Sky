package com.a3abcarinho.ahmed.sky.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.a3abcarinho.ahmed.sky.data.SunshinePreferences;
import com.a3abcarinho.ahmed.sky.data.WeatherContract;
import com.a3abcarinho.ahmed.sky.utilities.NetworkUtils;
import com.a3abcarinho.ahmed.sky.utilities.NotificationUtils;
import com.a3abcarinho.ahmed.sky.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class SunshineSyncTask {


    synchronized public static void syncWeather(Context context) {

        try {

            URL weatherRequestUrl = NetworkUtils.getUrl(context);

            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

            ContentValues[] weatherValues = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromJson(context, jsonWeatherResponse);


            if (weatherValues != null && weatherValues.length != 0) {
                ContentResolver sunshineContentResolver = context.getContentResolver();

                sunshineContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                sunshineContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues);


                boolean notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context);


                long timeSinceLastNotification = SunshinePreferences
                        .getEllapsedTimeSinceLastNotification(context);

                boolean oneDayPassedSinceLastNotification = false;

                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }


                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
