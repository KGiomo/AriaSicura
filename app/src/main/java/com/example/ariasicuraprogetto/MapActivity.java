package com.example.ariasicuraprogetto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Abilita JavaScript per la mappa
        webSettings.setDomStorageEnabled(true); // Abilita lo storage locale

        webView.setWebViewClient(new WebViewClient()); // Evita di aprire il browser esterno
        webView.loadUrl("https://www.iqair.com/it/air-quality-map"); // Carica la mappa
    }

    public void openMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void openTipsActivity(View view) {
        Intent intent = new Intent(this, TipsActivity.class);
        startActivity(intent);
    }

}