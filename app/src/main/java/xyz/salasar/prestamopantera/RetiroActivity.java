package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class RetiroActivity extends AppCompatActivity {
    private String usuario, cuenta;
    private String url,cifrado,rangoN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_retiro);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario=getIntent().getStringExtra("usuario");
        cuenta=getIntent().getStringExtra("cuenta");
        url=getIntent().getStringExtra("url");
        cifrado=getIntent().getStringExtra("cifrado");
        rangoN=getIntent().getStringExtra("rango");
    }
    private void volver(){
        Intent intent = new Intent(getApplicationContext(), PanelusuarioActivity.class);
        intent.putExtra("usuario", usuario);
        intent.putExtra("cuenta", cuenta);
        intent.putExtra("url", url);
        intent.putExtra("cifrado", cifrado);
        startActivity(intent);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            volver();
            return true;
        }
        return false;
    }
}