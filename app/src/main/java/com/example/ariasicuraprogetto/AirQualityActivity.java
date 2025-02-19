package com.example.ariasicuraprogetto;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    private Button btnSearch;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        // 初始化新控件
        btnSearch = findViewById(R.id.btnSearch);
        tvResult = findViewById(R.id.tvResult);
        // 设置搜索按钮点击监听
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

    // 新增：获取城市详细数据
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

                        // 解析污染数据
                        JSONObject pollution = current.getJSONObject("pollution");
                        String aqi = pollution.getString("aqius");
                        String mainPollutant = pollution.getString("mainus");

                        // 解析天气数据
                        JSONObject weather = current.getJSONObject("weather");
                        String temp = weather.getString("tp") + "°C";
                        String pressure = weather.getString("pr") + " hPa";
                        String humidity = weather.getString("hu") + "%";

                        // 显示结果
                        String resultText = String.format(
                                "城市: %s\nAQI: %s\n主要污染物: %s\n\n温度: %s\n气压: %s\n湿度: %s",
                                city, aqi, mainPollutant, temp, pressure, humidity
                        );

                        tvResult.setText(resultText);
                        tvResult.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "数据解析错误", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "获取数据失败", Toast.LENGTH_SHORT).show();
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