package com.example.ariasicuraprogetto;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // Aggiungi una costante TAG per i log

    // Rete: Get specified city data
    private static String url = "http://api.airvisual.com/v2/city?city=Venice&state=Veneto&country=Italy&key=ac6e45f6-571f-4a0a-86ca-52c9d2aba5be";
    private TextView cityText;
    private TextView stateText;
    private TextView countryText;
    private TextView aqiUsText;
    private TextView no2ConcText;
    private TextView lastUpdateTextView;
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 100) {
                String data = (String) msg.obj;
                PollutionInfo pollutionInfo = new Gson().fromJson(data, PollutionInfo.class);

                if (pollutionInfo != null && pollutionInfo.getData() != null) {
                    PollutionInfo.DataDTO dataDTO = pollutionInfo.getData();
                    PollutionInfo.DataDTO.CurrentDTO.PollutionDTO pollution = dataDTO.getCurrent().getPollution();

                    cityText.setText(dataDTO.getCity());
                    stateText.setText(dataDTO.getState());
                    countryText.setText(dataDTO.getCountry());

                    // AQI
                    aqiUsText.setText(String.valueOf(pollution.getAqius()));

                    // NO2
                    String mainPollutant = pollution.getMainus();
                    PollutionInfo.DataDTO.CurrentDTO.PollutionDTO.PollutantDetail mainPollutantDetail = null;

                    switch (mainPollutant) {
                        case "p1": mainPollutantDetail = pollution.getP1(); break;
                        case "p2": mainPollutantDetail = pollution.getP2(); break;
                        case "o3": mainPollutantDetail = pollution.getO3(); break;
                        case "n2": mainPollutantDetail = pollution.getN2(); break;
                        case "s2": mainPollutantDetail = pollution.getS2(); break;
                        case "co": mainPollutantDetail = pollution.getCo(); break;
                    }

                    if (mainPollutantDetail != null && mainPollutantDetail.getConc() != null) {
                        no2ConcText.setText(String.valueOf(mainPollutantDetail.getConc()));
                    } else {
                        no2ConcText.setText("N/A");
                    }

                    // Time
                    try {
                        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date = parser.parse(pollution.getTs());


                        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH);

                        lastUpdateTextView.setText(formatter.format(date));
                    } catch (ParseException e) {
                        lastUpdateTextView.setText("N/A");
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, " Opening MainActivity");

        cityText = findViewById(R.id.city);
        stateText = findViewById(R.id.state);
        countryText = findViewById(R.id.country);
        aqiUsText = findViewById(R.id.aqi_us);
        no2ConcText = findViewById(R.id.no2_conc);
        lastUpdateTextView = findViewById(R.id.lastUpdate);

        getHttpData(); // ottenere info da rete
    }

    private void getHttpData(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("-------", "onFailure: " + e.toString());
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

    // Metodo per aprire l'Activity AirQualityActivity
    public void openAirQualityActivity(View view) {
        Intent intent = new Intent(MainActivity.this, AirQualityActivity.class);
        startActivity(intent);
    }

    // Metodo per aprire l'Activity MapActivity
    public void openMapActivity(View view) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    // Metodo per aprire l'Activity TipsActivity
    public void openTipsActivity(View view) {
        Intent intent = new Intent(MainActivity.this, TipsActivity.class);
        startActivity(intent);
    }

}
