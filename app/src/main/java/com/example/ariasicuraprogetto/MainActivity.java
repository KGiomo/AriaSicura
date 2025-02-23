package com.example.ariasicuraprogetto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // Aggiungi una costante TAG per i log

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, " Opening MainActivity");
    }

    // Metodo per aprire l'Activity AirQualityActivity
    public void openAirQualityActivity(View view) {
        Log.d(TAG, "Opening AirQualityActivity");
        Intent intent = new Intent(MainActivity.this, AirQualityActivity.class);
        startActivity(intent);
    }

    // Metodo per aprire l'Activity MapActivity
    public void openMapActivity(View view) {
        Log.d(TAG, "Opening MapActivity");
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    // Metodo per aprire l'Activity TipsActivity
    public void openTipsActivity(View view) {
        Log.d(TAG, "Opening TipsActivity");
        Intent intent = new Intent(MainActivity.this, TipsActivity.class);
        startActivity(intent);
    }

}
