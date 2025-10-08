package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class NotificacionActivity extends AppCompatActivity {
    private String usuario,cuenta,empresa,url,cifrado,rangoN;
    private ImageButton volver;
    private Button bt_agregar,bt_prestamos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_notificacion);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario=getIntent().getStringExtra("usuario");
        cuenta=getIntent().getStringExtra("cuenta");
        empresa=getIntent().getStringExtra("empresa");
        url=getIntent().getStringExtra("url");
        rangoN=getIntent().getStringExtra("rango");
        cifrado=getIntent().getStringExtra("cifrado");
        volver=findViewById(R.id.volver333);
        bt_agregar=findViewById(R.id.bt_ingresos333);
        bt_prestamos=findViewById(R.id.bt_prestamos333);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        bt_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), AgregarUsuarioActivity.class);
                intent.putExtra("usuario",usuario);
                intent.putExtra("cuenta",cuenta);
                intent.putExtra("empresa",empresa);
                intent.putExtra("url",url);
                intent.putExtra("rango",rangoN);
                intent.putExtra("cifrado",cifrado);
                startActivity(intent);
            }
        });
        bt_prestamos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), AprobarPrestamoActivity.class);
                intent.putExtra("usuario",usuario);
                intent.putExtra("empresa",empresa);
                intent.putExtra("cuenta",cuenta);
                intent.putExtra("url",url);
                intent.putExtra("rango",rangoN);
                intent.putExtra("cifrado",cifrado);
                startActivity(intent);
            }
        });
    }
    public void volver(){
        if(rangoN.equals("propietario")) {
            Intent intent = new Intent(getApplicationContext(), PanelpropietarioActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("cuenta", cuenta);
            intent.putExtra("url", url);
            intent.putExtra("cifrado", cifrado);
            startActivity(intent);
        }
        else{
            if(rangoN.equals("director")){
                Intent intent = new Intent(getApplicationContext(), PanelDirectorActivity.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("cuenta", cuenta);
                intent.putExtra("url", url);
                intent.putExtra("cifrado", cifrado);
                startActivity(intent);
            }
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            volver();
            return true;
        }
        return false;
    }
}