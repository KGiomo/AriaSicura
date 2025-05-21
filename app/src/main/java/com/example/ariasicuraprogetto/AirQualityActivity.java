package com.example.ariasicuraprogetto;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.Response;
import java.io.IOException;

public class AirQualityActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.airvisual.com/v2/";
    private static final String API_KEY = "ac6e45f6-571f-4a0a-86ca-52c9d2aba5be";

    private Spinner countrySpinner, stateSpinner, citySpinner;
    private boolean isInitialCountryLoad = true;
    private ArrayAdapter<String> countryAdapter, stateAdapter, cityAdapter;
    private RequestQueue requestQueue;

    private FusedLocationProviderClient fusedLocationClient;
    private Button btnSearch;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestQueue = Volley.newRequestQueue(this);

        countrySpinner = findViewById(R.id.countrySpinner);
        stateSpinner = findViewById(R.id.stateSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        tvResult = findViewById(R.id.tvResult);
        btnSearch = findViewById(R.id.btnSearch);
        Button locationButton = findViewById(R.id.locationButton);

        countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        setupSpinners();
        loadCountries();

        locationButton.setOnClickListener(v -> getLocation());

        btnSearch.setOnClickListener(v -> {
            if (citySpinner.getSelectedItem() != null) {
                String selectedCountry = countrySpinner.getSelectedItem().toString();
                String selectedState = stateSpinner.getSelectedItem().toString();
                String selectedCity = citySpinner.getSelectedItem().toString();

                try {
                    String encodedCountry = URLEncoder.encode(selectedCountry, "UTF-8");
                    String encodedState = URLEncoder.encode(selectedState, "UTF-8");
                    String encodedCity = URLEncoder.encode(selectedCity, "UTF-8");

                    String url = BASE_URL + "city?city=" + encodedCity +
                            "&state=" + encodedState + "&country=" + encodedCountry +
                            "&key=" + API_KEY;

                    Intent intent = new Intent(AirQualityActivity.this, MainActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                    finish();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        countryAdapter.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject country = data.getJSONObject(i);
                            countryAdapter.add(country.getString("country"));
                        }
                        countrySpinner.setEnabled(true);
                        if (countryAdapter.getCount() > 0) {
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        stateAdapter.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject state = data.getJSONObject(i);
                            stateAdapter.add(state.getString("state"));
                        }
                        stateSpinner.setEnabled(true);
                        if (stateAdapter.getCount() > 0) {
                            stateSpinner.setSelection(0, false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stateSpinner.setEnabled(false);
                    }
                },
                error -> {
                    Toast.makeText(this, "Error loading states", Toast.LENGTH_SHORT).show();
                    resetCitySpinner();
                });
        requestQueue.add(request);
    }

    private void loadCities(String country, String state) throws UnsupportedEncodingException {
        String url = BASE_URL + "cities?state=" + URLEncoder.encode(state, "UTF-8")
                + "&country=" + URLEncoder.encode(country, "UTF-8")
                + "&key=" + API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        cityAdapter.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject city = data.getJSONObject(i);
                            cityAdapter.add(city.getString("city"));
                        }
                        citySpinner.setEnabled(true);
                        if (cityAdapter.getCount() > 0) {
                            citySpinner.setSelection(0, false);
                        }
                        btnSearch.setEnabled(cityAdapter.getCount() > 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        citySpinner.setEnabled(false);
                    }
                },
                error -> {
                    Toast.makeText(this, "Error loading city", Toast.LENGTH_SHORT).show();
                    citySpinner.setEnabled(false);
                });
        requestQueue.add(request);
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            fetchCityDataFromCoordinates(location.getLatitude(), location.getLongitude());
                        } else {
                            requestNewLocationData();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
                        Log.e("Location", "Error: " + e.getMessage());
                    });

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(2000)
                .build();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        fetchCityDataFromCoordinates(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(AirQualityActivity.this, "Position not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }, Looper.getMainLooper());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCityDataFromCoordinates(double lat, double lon) {
        String url = BASE_URL + "nearest_city?lat=" + lat + "&lon=" + lon + "&key=" + API_KEY;
        Intent intent = new Intent(AirQualityActivity.this, MainActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
        finish();
    }

    private void fetchDataAndDisplay(String url) {
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(AirQualityActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    tvResult.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                PollutionInfo pollutionInfo = new Gson().fromJson(json, PollutionInfo.class);

                runOnUiThread(() -> {
                    if (pollutionInfo != null && pollutionInfo.getData() != null && pollutionInfo.getData().getCurrent() != null) {
                        PollutionInfo.DataDTO data = pollutionInfo.getData();
                        PollutionInfo.DataDTO.CurrentDTO.PollutionDTO p = data.getCurrent().getPollution();

                        String resultText = String.format(
                                "City: %s\nState: %s\nCountry: %s\n\nAQI: %d\nLatest update: %s",
                                data.getCity(), data.getState(), data.getCountry(), p.getAqius(), p.getTs()
                        );

                        tvResult.setText(resultText);
                        tvResult.setVisibility(View.VISIBLE);

                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                        ComponentName thisWidget = new ComponentName(getApplicationContext(), widgetProvider.class);
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                        for (int appWidgetId : appWidgetIds) {
                            widgetProvider.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId, data);
                        }

                    } else {
                        tvResult.setText("Data not available");
                    }
                });
            }
        });
    }
}
