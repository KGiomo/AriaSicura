package com.example.ariasicuraprogetto;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TipsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        // Trova il TextView nel layout
        TextView textView = findViewById(R.id.textView);

        // Cambia il testo del TextView
        textView.setText("Il numero AQI viene assegnato in base all'inquinante atmosferico con il numero AQI più alto al momento della misurazione della qualità dell'aria. Vengono misurati solo gli inquinanti disponibili in una determinata stazione di monitoraggio della qualità dell'aria e molte di esse non includono tutti e sei gli inquinanti in egual misura. Poiché la qualità dell'aria cambia nel corso della giornata, l'AQI di una località monitorata varia in base al livello delle concentrazioni di inquinanti atmosferici misurati.\n" +
                "\n" +
                "L'indice rappresenta le concentrazioni di inquinanti atmosferici con un numero che rientra in un intervallo di categorie di qualità dell'aria. All'interno di ogni categoria e intervallo di numeri, vengono identificati i rischi elevati per la salute associati all'aumento delle concentrazioni di inquinanti atmosferici.\n" +
                "\n" +
                "L'indice di qualità dell'aria va da 0 a 500, anche se la qualità dell'aria può essere indicizzata oltre 500 quando ci sono livelli più elevati di inquinamento atmosferico pericoloso. Una buona qualità dell'aria va da 0 a 50, mentre le misurazioni superiori a 300 sono considerate pericolose.");
    }
}
