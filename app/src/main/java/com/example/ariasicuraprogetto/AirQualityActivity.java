package com.example.ariasicuraprogetto;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AirQualityActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.airvisual.com/v2/";

    private Spinner countrySpinner, stateSpinner, citySpinner;
    private boolean isInitialCountryLoad = true; // 新增标志位
    private ArrayAdapter<String> countryAdapter, stateAdapter, cityAdapter;
    private RequestQueue requestQueue;
    private static final String API_KEY = BuildConfig.AIRVISUAL_API_KEY;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

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
                            stateSpinner.setSelection(0, false); // 不自动触发事件
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
        String url = BASE_URL + "cities?state=" + URLEncoder.encode(state, "UTF-8")
                + "&country=" + URLEncoder.encode(country, "UTF-8")
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