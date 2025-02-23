package com.example.ariasicuraprogetto;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AirQualityActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.airvisual.com/v2/";

    private Spinner countrySpinner, stateSpinner, citySpinner;
    private boolean isInitialCountryLoad = true;
    private ArrayAdapter<String> countryAdapter, stateAdapter, cityAdapter;
    private RequestQueue requestQueue;
    private static final String API_KEY = BuildConfig.AIRVISUAL_API_KEY;

    private FusedLocationProviderClient fusedLocationClient;
    private Location lastLocation;

    private Button btnSearch;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button locationButton = findViewById(R.id.locationButton);
        locationButton.setOnClickListener(v -> getLocation());

        // Recupero le componenti
        btnSearch = findViewById(R.id.btnSearch);
        tvResult = findViewById(R.id.tvResult);


        // Configuro il click del bottone
        btnSearch.setOnClickListener(v -> {
            if (citySpinner.getSelectedItem() != null) {
                fetchCityData(
                        countrySpinner.getSelectedItem().toString(),
                        stateSpinner.getSelectedItem().toString(),
                        citySpinner.getSelectedItem().toString()
                );
            }
        });

        // Inizializzazione UI
        countrySpinner = findViewById(R.id.countrySpinner);
        stateSpinner = findViewById(R.id.stateSpinner);
        citySpinner = findViewById(R.id.citySpinner);


        // Inizializzazione Volley
        requestQueue = Volley.newRequestQueue(this);
        countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        setupSpinners();
        loadCountries();

    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            lastLocation = location;
                            double latitude = lastLocation.getLatitude();
                            double longitude = lastLocation.getLongitude();

                            fetchCityDataFromCoordinates(latitude, longitude);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void fetchCityDataFromCoordinates(double latitude, double longitude) {
        // Prendo i dati della stazione più vicina
        String url = BASE_URL + "nearest_city?lat=" + latitude + "&lon=" + longitude + "&key=" + API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        JSONObject current = data.getJSONObject("current");

                        JSONObject pollution = current.getJSONObject("pollution");
                        String aqi = pollution.getString("aqius");
                        String mainPollutant = pollution.getString("mainus");

                        JSONObject weather = current.getJSONObject("weather");
                        String temp = weather.getString("tp") + "°C";
                        String pressure = weather.getString("pr") + " hPa";
                        String humidity = weather.getString("hu") + "%";


                        Log.d("API Response", response.toString());
                        String resultText = String.format(
                                "AQI: %s\nInquinanti principali: %s\n\nTemperatura: %s\nPressione: %s\nUmidità: %s",
                                aqi, mainPollutant, temp, pressure, humidity
                        );

                        tvResult.setText(resultText);
                        tvResult.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Errore nell'analisi dei dati", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Impossibile ottenere i dati", Toast.LENGTH_SHORT).show();
                    tvResult.setVisibility(View.GONE);
                });

        requestQueue.add(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permesso di geolocalizzazione negato", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Ottengo i dati aggiornati sulla città
    private void fetchCityData(String country, String state, String city) {
        String url = null;
        try {
            url = BASE_URL + "city?city=" + URLEncoder.encode(city, "UTF-8")
                    + "&state=" + URLEncoder.encode(state, "UTF-8")
                    + "&country=" + URLEncoder.encode(country, "UTF-8")
                    + "&key=" + API_KEY;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        JSONObject current = data.getJSONObject("current");
                        JSONObject pollution = current.getJSONObject("pollution");
                        String aqi = pollution.getString("aqius");
                        String mainPollutant = pollution.getString("mainus");
                        JSONObject weather = current.getJSONObject("weather");
                        String temp = weather.getString("tp") + "°C";
                        String pressure = weather.getString("pr") + " hPa";
                        String humidity = weather.getString("hu") + "%";

                        // Stringa che contiene i dati ottenuti dalla richiesta
                        String resultText = String.format(
                                "Città: %s\nAQI: %s\nInquinanti principali: %s\n\nTemperatura: %s\nPressione: %s\nUmidità: %s",
                                city, aqi, mainPollutant, temp, pressure, humidity
                        );

                        // Mostro a schermo i risultati
                        tvResult.setText(resultText);
                        tvResult.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Errore nell'analisi dei dati", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Impossibile ottenere i dati", Toast.LENGTH_SHORT).show();
                    tvResult.setVisibility(View.GONE);
                });

        requestQueue.add(request);
    }

    private void setupSpinners() {

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        countrySpinner.setAdapter(countryAdapter);
        stateSpinner.setAdapter(stateAdapter);
        citySpinner.setAdapter(cityAdapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isInitialCountryLoad) {
                    isInitialCountryLoad = false;
                    return;
                }
                String selectedCountry = parent.getItemAtPosition(position).toString();
                resetStateAndCitySpinners();
                try {
                    loadStates(selectedCountry);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resetCitySpinner();
                String selectedCountry = countrySpinner.getSelectedItem().toString();
                String selectedState = parent.getItemAtPosition(position).toString();
                try {
                    loadCities(selectedCountry, selectedState);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void resetStateAndCitySpinners() {
        stateAdapter.clear();
        cityAdapter.clear();
        stateSpinner.setEnabled(false);
        citySpinner.setEnabled(false);
    }
    private void resetCitySpinner() {
        cityAdapter.clear();
        citySpinner.setEnabled(false);
    }
    private void loadCountries() {
        String url = BASE_URL + "countries?key=" + API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        countryAdapter.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject country = data.getJSONObject(i);
                            countryAdapter.add(country.getString("country"));
                        }
                        countrySpinner.setEnabled(true);


                        if (countrySpinner.getSelectedItem() == null && countryAdapter.getCount() > 0) {
                            countrySpinner.setSelection(0, false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error loading countries", Toast.LENGTH_SHORT).show();
                    resetStateAndCitySpinners();
                });
        requestQueue.add(request);
    }

    private void loadStates(String country) throws UnsupportedEncodingException {
        String url = BASE_URL + "states?country=" + URLEncoder.encode(country, "UTF-8") + "&key=" + API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        stateAdapter.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject state = data.getJSONObject(i);
                            stateAdapter.add(state.getString("state"));
                        }
                        stateSpinner.setEnabled(!stateAdapter.isEmpty());
                        if (!stateAdapter.isEmpty()) {
                            stateSpinner.setSelection(0, false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stateSpinner.setEnabled(false);
                    }
                },
                error -> {
                    Toast.makeText(this, "Error loading states", Toast.LENGTH_SHORT).show();
                    stateSpinner.setEnabled(false);
                    resetCitySpinner();
                });
        requestQueue.add(request);
    }

    private void loadCities(String country, String state) throws UnsupportedEncodingException {
        String url = BASE_URL + "cities?state=" + URLEncoder.encode(state)
                + "&country=" + URLEncoder.encode(country)
                + "&key=" + API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        cityAdapter.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject city = data.getJSONObject(i);
                            cityAdapter.add(city.getString("city"));
                        }
                        citySpinner.setEnabled(!cityAdapter.isEmpty());
                        btnSearch.setEnabled(cityAdapter.getCount() > 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        citySpinner.setEnabled(false);
                    }
                },
                error -> {
                    Toast.makeText(this, "Error loading cities", Toast.LENGTH_SHORT).show();
                    citySpinner.setEnabled(false);
                });
        requestQueue.add(request);

    }
}