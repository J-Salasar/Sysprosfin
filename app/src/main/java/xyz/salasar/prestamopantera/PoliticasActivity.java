package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class PoliticasActivity extends AppCompatActivity {
    private WebView ventanaPoliticas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_politicas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ventanaPoliticas = findViewById(R.id.ventanaPoliticas);
        ventanaPoliticas.clearCache(true);
        //ventanaPoliticas.getSettings().setJavaScriptEnabled(true);
        ventanaPoliticas.getSettings().setLoadWithOverviewMode(true);
        ventanaPoliticas.getSettings().setUseWideViewPort(true);
        ventanaPoliticas.setWebViewClient(new WebViewClient());
        ventanaPoliticas.loadUrl("http://sysprosfin.salasar.xyz");
    }
}