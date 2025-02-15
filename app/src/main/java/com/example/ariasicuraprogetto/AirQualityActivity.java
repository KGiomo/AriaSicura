package com.example.ariasicuraprogetto;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AirQualityActivity extends AppCompatActivity {

    private EditText cityEditText;
    private TextView cityTextView, aqiTextView, pollutionTextView;
    private Button searchButton;
    private RequestQueue requestQueue;
    private static final String API_KEY = BuildConfig.AIRVISUAL_API_KEY;
    private static final String BASE_URL = "https://api.airvisual.com/v2/city?city=%s&state=%s&country=%s&key=" + API_KEY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        // Inizializzazione UI
        cityEditText = findViewById(R.id.cityEditText);
        cityTextView = findViewById(R.id.cityTextView);
        aqiTextView = findViewById(R.id.aqiTextView);
        pollutionTextView = findViewById(R.id.pollutionTextView);
        searchButton = findViewById(R.id.searchButton);

        // Inizializzazione Volley
        requestQueue = Volley.newRequestQueue(this);

        // Imposta il listener per il pulsante di ricerca
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString().trim();
                if (!city.isEmpty()) {
                    fetchAirQualityData(city, "Ile-de-France", "France"); // Sostituisci con parametri variabili se necessario
                } else {
                    Toast.makeText(AirQualityActivity.this, "Inserisci una città", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchAirQualityData(String city, String state, String country) {
        String url = String.format(BASE_URL, city, state, country);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject current = data.getJSONObject("current");
                            JSONObject pollution = current.getJSONObject("pollution");

                            String cityName = data.getString("city");
                            int aqi = pollution.getInt("aqius");
                            String mainPollutant = pollution.getString("mainus");

                            // Aggiornamento UI
                            cityTextView.setText("Città: " + cityName);
                            aqiTextView.setText("AQI: " + aqi);
                            pollutionTextView.setText("Inquinante principale: " + mainPollutant);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AirQualityActivity.this, "Errore nei dati", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AirQualityActivity.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
                    }
                });

        // Aggiungere richiesta alla coda
        requestQueue.add(request);
    }
}
