package com.example.ariasicuraprogetto;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
            ImageView imageView = new ImageView(this);
            imageView.setAdjustViewBounds(true);
            imageView.setMaxHeight(150);
            imageView.setPadding(0, 16, 0, 8);

            TextView titleView = new TextView(this);
            titleView.setText(tip.getTitle());
            titleView.setTextSize(18);
            titleView.setTypeface(null, Typeface.BOLD);
            titleView.setPadding(0, 8, 0, 4);

            TextView descView = new TextView(this);
            descView.setText(tip.getText());
            descView.setTextSize(16);
            descView.setPadding(0, 0, 0, 16);

            tipsLayout.addView(imageView);
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

        if(aqi <= 50) {
            tips.add(new Tips("Attività all'aperto", "Puoi svolgere attività all'aperto senza preoccupazioni."));
            tips.add(new Tips("Sport e passeggiate", "È un ottimo momento per fare sport o passeggiate."));
            tips.add(new Tips("Arieggiare gli ambienti", "Approfitta per arieggiare casa o ufficio."));
            tips.add(new Tips("Qualità dell'aria", "Non sono necessarie precauzioni."));
        }else if (aqi <= 100) {
            tips.add(new Tips("Persone sensibili", "Se soffri di asma o problemi respiratori, limita le attività all'aperto."));
            tips.add(new Tips("Evita il traffico", "Evita le zone trafficate nelle ore di punta."));
            tips.add(new Tips("Attività moderata", "Meglio evitare sforzi fisici prolungati all'aperto."));
            tips.add(new Tips("Monitora l'aria", "Controlla la qualità dell'aria nel caso peggiori più tardi."));
        }else {
            tips.add(new Tips("Riduci il tempo all'aperto", "Soprattutto se fai attività fisica."));
            tips.add(new Tips("Rimani in casa", "Tieni le finestre chiuse e usa filtri d'aria se disponibili."));
            tips.add(new Tips("Evita le zone inquinate", "Stai lontano da aree trafficate o zone industriali."));
            tips.add(new Tips("Proteggi i più vulnerabili", "Bambini, anziani e persone con patologie dovrebbero restare in casa."));
            tips.add(new Tips("Sii preparato", "Tieni a portata di mano i farmaci se hai problemi respiratori."));
        }


        return tips;
    }
}
