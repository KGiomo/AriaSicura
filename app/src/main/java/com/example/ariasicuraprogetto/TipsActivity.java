package com.example.ariasicuraprogetto;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TipsActivity extends AppCompatActivity {

    LinearLayout tipsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        tipsLayout = findViewById(R.id.tipsContent);

        int aqi = getIntent().getIntExtra("aqi_value", -1);
        List<Tips> listTips = getConsigliPerAQI(aqi);

        for (Tips tip : listTips) {
            TextView titleView = new TextView(this);
            titleView.setText(tip.getTitle());
            titleView.setTextSize(18);
            titleView.setTypeface(null, Typeface.BOLD);
            titleView.setPadding(0, 12, 0, 4);

            TextView descView = new TextView(this);
            descView.setText(tip.getText());
            descView.setTextSize(16);
            descView.setPadding(0, 0, 0, 16);

            tipsLayout.addView(titleView);
            tipsLayout.addView(descView);
        }
    }

    public void openMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void openMapActivity(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }

    public static List<Tips> getConsigliPerAQI(int aqi) {
        List<Tips> tips = new ArrayList<>();

        if (aqi <= 50) {
            tips.add(new Tips("Outdoor Activities", "You can carry out outdoor activities without concern.", R.drawable.images));
            tips.add(new Tips("Sports and Walks", "A great time for sports or walks.", R.drawable.images));
            tips.add(new Tips("Ventilate Indoors", "Take the opportunity to ventilate your home or office.", R.drawable.images));
            tips.add(new Tips("Air Quality", "No precautions necessary.", R.drawable.images));
        } else if (aqi <= 100) {
            tips.add(new Tips("Sensitive Individuals", "If you have asthma or respiratory issues, consider limiting outdoor activity.", R.drawable.images));
            tips.add(new Tips("Avoid Traffic", "Avoid traffic-heavy areas during peak hours.", R.drawable.images));
            tips.add(new Tips("Moderate Activity", "Better to avoid prolonged physical effort outdoors.", R.drawable.images));
            tips.add(new Tips("Monitor the Air", "Monitor the air quality in case it worsens later.", R.drawable.images));
        } else {
            tips.add(new Tips("Reduce Outdoor Time", "Especially if doing physical activity.", R.drawable.images));
            tips.add(new Tips("Stay Indoors", "Keep windows closed and use air filters if available.", R.drawable.images));
            tips.add(new Tips("Avoid Polluted Areas", "Avoid areas with heavy traffic or industrial zones.", R.drawable.images));
            tips.add(new Tips("Protect Vulnerable Groups", "Children, elderly, and people with conditions should stay indoors.", R.drawable.images));
            tips.add(new Tips("Be Prepared", "Keep medications handy if you have respiratory issues.", R.drawable.images));
        }

        return tips;
    }
}
