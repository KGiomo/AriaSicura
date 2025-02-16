package com.example.ariasicuraprogetto;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.util.Log;

public class AirQualityActivity extends AppCompatActivity {

    private EditText cityEditText;
    private Spinner countrySpinner, stateSpinner, citySpinner;
    private TextView cityTextView, aqiTextView, pollutionTextView, temperatureTextView, pressureTextView, humidityTextView, meteoTextView;
    private Button searchButton, locationButton;
    private RequestQueue requestQueue;

    private FusedLocationProviderClient fusedLocationClient;

    private static final String API_KEY = BuildConfig.AIRVISUAL_API_KEY;
    private static final String BASE_URL_CITY = "https://api.airvisual.com/v2/city?city=%s&state=%s&country=%s&key=" + API_KEY;
    private static final String BASE_URL_STATES = "https://api.airvisual.com/v2/states?country=%s&key=" + API_KEY;
    private static final String BASE_URL_CITIES = "https://api.airvisual.com/v2/cities?state=%s&country=%s&key=" + API_KEY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        // Inizializzazione UI
        cityEditText = findViewById(R.id.cityEditText);
        countrySpinner = findViewById(R.id.countrySpinner);
        stateSpinner = findViewById(R.id.stateSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        cityTextView = findViewById(R.id.cityTextView);
        aqiTextView = findViewById(R.id.aqiTextView);
        pollutionTextView = findViewById(R.id.pollutionTextView);
        searchButton = findViewById(R.id.searchButton);
        locationButton = findViewById(R.id.locationButton);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        humidityTextView = findViewById(R.id.humidityTextView);

        // Inizializzazione Volley
        requestQueue = Volley.newRequestQueue(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Caricamento della lista dei paesi
        loadCountries();

        // Listener per il cambio di paese -> aggiorna gli stati
        countrySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = countrySpinner.getSelectedItem().toString();
                loadStates(selectedCountry);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Listener per il cambio di stato -> aggiorna le città
        stateSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedState = stateSpinner.getSelectedItem().toString();
                String selectedCountry = countrySpinner.getSelectedItem().toString();
                loadCities(selectedState, selectedCountry);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });


        // Listener per il pulsante di ricerca
        searchButton.setOnClickListener(v -> {
            String city = cityEditText.getText().toString().trim();
            String state = stateSpinner.getSelectedItem().toString();
            String country = countrySpinner.getSelectedItem().toString();
            if (!city.isEmpty()) {
                fetchAirQualityData(city, state, country);
            } else {
                Toast.makeText(AirQualityActivity.this, "Inserisci una città", Toast.LENGTH_SHORT).show();
            }
        });

        // Pulsante per ottenere la posizione attuale
        locationButton.setOnClickListener(v -> getCurrentLocation());
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Richiede i permessi se non sono stati concessi
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Toast.makeText(AirQualityActivity.this, "Lat: " + latitude + ", Lon: " + longitude, Toast.LENGTH_SHORT).show();

                    // Chiamata all'API con le coordinate attuali
                    fetchAirQualityByCoordinates(latitude, longitude);
                } else {
                    Toast.makeText(AirQualityActivity.this, "Posizione non disponibile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchAirQualityByCoordinates(double latitude, double longitude) {
        String url = String.format("https://api.airvisual.com/v2/nearest_city?lat=%f&lon=%f&key=" + API_KEY, latitude, longitude);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject current = data.getJSONObject("current");
                            JSONObject pollution = current.getJSONObject("pollution");
                            JSONObject weather = current.getJSONObject("weather");

                            String cityName = data.getString("city");
                            int aqi = pollution.getInt("aqius");
                            String mainPollutant = pollution.getString("mainus");
                            double temperature = weather.getDouble("tp");  // Cambiato da "ts" a "tp"
                            int pressure = weather.getInt("pr");
                            int humidity = weather.getInt("hu");

                            cityTextView.setText("Città: " + cityName);
                            aqiTextView.setText("AQI: " + aqi);
                            pollutionTextView.setText("Inquinante principale: " + mainPollutant);
                            temperatureTextView.setText("Temperatura: " + temperature + "°");
                            pressureTextView.setText("Pressione: " + pressure + "hPa");
                            humidityTextView.setText("Umidità: " + humidity + "%");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AirQualityActivity.this, "Errore nei dati ricevuti", Toast.LENGTH_SHORT).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AirQualityActivity.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }

    private void fetchAirQualityData(String city, String state, String country) {
        String url = String.format(BASE_URL_CITY, city, state, country);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (!response.has("data")) {
                            Toast.makeText(AirQualityActivity.this, "Dati non disponibili", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject data = response.getJSONObject("data");

                        if (!data.has("current")) {
                            Toast.makeText(AirQualityActivity.this, "Nessun dato di qualità dell'aria disponibile", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject current = data.getJSONObject("current");

                        if (!current.has("pollution")) {
                            Toast.makeText(AirQualityActivity.this, "Nessun dato di inquinamento disponibile", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!current.has("weather")) {
                            Toast.makeText(AirQualityActivity.this, "Nessun dato sul meteo disponibile", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject pollution = current.getJSONObject("pollution");
                        JSONObject weather = current.getJSONObject("weather");

                        String cityName = data.getString("city");
                        int aqi = pollution.getInt("aqius");
                        String mainPollutant = pollution.getString("mainus");
                        double temperature = weather.getDouble("tp");
                        int pressure = weather.getInt("pr");
                        int humidity = weather.getInt("hu");

                        cityTextView.setText("Città: " + cityName);
                        aqiTextView.setText("AQI: " + aqi);
                        pollutionTextView.setText("Inquinante principale: " + mainPollutant);
                        temperatureTextView.setText("Temperatura: " + temperature + "°C");
                        pressureTextView.setText("Pressione: " + pressure + " hPa");
                        humidityTextView.setText("Umidità: " + humidity + "%");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AirQualityActivity.this, "Errore nei dati ricevuti", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AirQualityActivity.this, "Errore di connessione o dati non disponibili", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }



    private void loadCountries() {
        // Lista degli stati che si vogliono Visualizzare
        List<String> countries = Arrays.asList("Italy");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countries);
        countrySpinner.setAdapter(adapter);
    }

    private void loadStates(String country) {
        String url = String.format(BASE_URL_STATES, country);

        Log.d("DEBUG", "URL Stati: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("DEBUG", "Risposta Stati: " + response.toString()); // Stampa la risposta JSON

                        JSONArray statesArray = response.getJSONArray("data");
                        ArrayList<String> statesList = new ArrayList<>();

                        for (int i = 0; i < statesArray.length(); i++) {
                            statesList.add(statesArray.getJSONObject(i).getString("state"));
                        }

                        if (statesList.isEmpty()) {
                            statesList.add("Nessuno stato disponibile");
                        }

                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statesList);
                            stateSpinner.setAdapter(adapter);
                        });

                        // Carica le città per il primo stato disponibile
                        if (!statesList.get(0).equals("Nessuno stato disponibile")) {
                            loadCities(statesList.get(0), country);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("DEBUG", "Errore parsing Stati: " + e.getMessage());
                        Toast.makeText(this, "Errore nel parsing degli stati", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("DEBUG", "Errore HTTP Stati: " + error.toString());
                    Toast.makeText(this, "Errore nel caricamento degli stati", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }


    private void loadCities(String state, String country) {
        String url = String.format(BASE_URL_CITIES, state, country);
        Log.d("DEBUG", "URL Città: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("DEBUG", "Risposta Città: " + response.toString()); // Stampa la risposta JSON

                        JSONArray citiesArray = response.getJSONArray("data");
                        ArrayList<String> citiesList = new ArrayList<>();

                        for (int i = 0; i < citiesArray.length(); i++) {
                            citiesList.add(citiesArray.getJSONObject(i).getString("city"));
                        }

                        if (citiesList.isEmpty()) {
                            citiesList.add("Nessuna città disponibile");
                        }

                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, citiesList);
                            citySpinner.setAdapter(adapter);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("DEBUG", "Errore parsing Città: " + e.getMessage());
                        Toast.makeText(this, "Errore nel parsing delle città", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error instanceof com.android.volley.ClientError && error.networkResponse != null && error.networkResponse.statusCode == 429) {
                        // Errore: troppo presto, ritenta dopo un breve intervallo
                        Log.e("DEBUG", "Errore, riprovo...");
                        Toast.makeText(this, "Troppi tentativi. Riprova più tardi", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.e("DEBUG", "Errore HTTP Città: " + error.toString());
                    Toast.makeText(this, "Errore nel caricamento delle città", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }
}