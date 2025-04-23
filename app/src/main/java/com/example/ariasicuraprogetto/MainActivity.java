package com.example.ariasicuraprogetto;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String url;
    private TextView cityText, stateText, countryText, aqiUsText, lastUpdateTextView;
    private LinearLayout aqiBox;
    private TextView pollutionStateText;

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 100) {
                String data = (String) msg.obj;
                PollutionInfo pollutionInfo = new Gson().fromJson(data, PollutionInfo.class);

                if (pollutionInfo != null && pollutionInfo.getData() != null) {
                    PollutionInfo.DataDTO dataDTO = pollutionInfo.getData();

                    cityText.setText(dataDTO.getCity() != null ? dataDTO.getCity() : "N/A");
                    stateText.setText(dataDTO.getState() != null ? dataDTO.getState() : "N/A");
                    countryText.setText(dataDTO.getCountry() != null ? dataDTO.getCountry() : "N/A");

                    if (dataDTO.getCurrent() != null && dataDTO.getCurrent().getPollution() != null) {
                        int aqi = dataDTO.getCurrent().getPollution().getAqius();
                        aqiUsText.setText(String.valueOf(aqi));

                        try {
                            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date date = parser.parse(dataDTO.getCurrent().getPollution().getTs());
                            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH);
                            lastUpdateTextView.setText(formatter.format(date));
                        } catch (ParseException e) {
                            lastUpdateTextView.setText("N/A");
                        }

                        updateAQIVisuals(aqi, pollutionStateText, aqiBox);
                    } else {
                        aqiUsText.setText("N/A");
                        lastUpdateTextView.setText("Dati non disponibili");
                        pollutionStateText.setText("N/A");
                    }

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                    ComponentName thisWidget = new ComponentName(getApplicationContext(), widgetProvider.class);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                    for (int appWidgetId : appWidgetIds) {
                        widgetProvider.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId, dataDTO);
                    }

                } else {
                    cityText.setText("N/A");
                    stateText.setText("N/A");
                    countryText.setText("N/A");
                    aqiUsText.setText("N/A");
                    lastUpdateTextView.setText("N/A");
                    pollutionStateText.setText("N/A");
                }
            }
        }
    };

    private void updateAQIVisuals(int aqi, TextView pollutionStateText, LinearLayout aqiBox) {
        if (aqi <= 50) {
            pollutionStateText.setText("Good");
            pollutionStateText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            aqiBox.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else if (aqi <= 100) {
            pollutionStateText.setText("Moderate");
            pollutionStateText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            aqiBox.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        } else {
            pollutionStateText.setText("Unhealthy");
            pollutionStateText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            aqiBox.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Opening MainActivity");

        cityText = findViewById(R.id.city);
        stateText = findViewById(R.id.state);
        countryText = findViewById(R.id.country);
        aqiUsText = findViewById(R.id.aqi_us);
        lastUpdateTextView = findViewById(R.id.lastUpdate);
        aqiBox = findViewById(R.id.boxAQI);
        pollutionStateText = findViewById(R.id.pollutionStateText);

        // Usa l'URL passato da AirQualityActivity, se disponibile
        Intent intent = getIntent();
        if (intent.hasExtra("url")) {
            url = intent.getStringExtra("url");
            Log.d(TAG, "URL ricevuto da AirQualityActivity: " + url);
        }

        getHttpData(); // Effettua richiesta HTTP
    }

    private void getHttpData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data = response.body().string();
                Message message = new Message();
                message.what = 100;
                message.obj = data;
                handler.sendMessage(message);
            }
        });
    }

    public void openAirQualityActivity(View view) {
        Intent intent = new Intent(MainActivity.this, AirQualityActivity.class);
        startActivity(intent);
    }

    public void openMapActivity(View view) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    public void openTipsActivity(View view) {
        Intent intent = new Intent(MainActivity.this, TipsActivity.class);
        startActivity(intent);
    }
}