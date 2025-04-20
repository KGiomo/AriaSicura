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
        StringBuilder tipsText = new StringBuilder();

        //What if we need to add more tips later on? Make it more generic
        for(int i = 1; i <= 14; i++)
        {
            String tipName= "tip" + i;
            int resId = getResources().getIdentifier(tipName, "string", getPackageName());

            if (resId != 0) {
                tipsText.append(getString(resId)).append("\n\n");
            }
        }
        textView.setText(tipsText.toString());
    }
}
