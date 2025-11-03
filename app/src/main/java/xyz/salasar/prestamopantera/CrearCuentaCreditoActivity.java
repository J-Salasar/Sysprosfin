package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CrearCuentaCreditoActivity extends AppCompatActivity {
    private String usuario,url,cifrado;
    private ImageButton bt_atras_crearcuentacreadito;
    private Button bt_ahorros_crearcuentacreadito, bt_prestamos_crearcuentacreadito, bt_cerrarpanelcuentaprestamo, bt_guardarpanelcuentaprestamo;
    private ConstraintLayout panelcuentaprestamo;
    private EditText nombreEmpresa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_crear_cuenta_credito);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario = getIntent().getStringExtra("usuario");
        url = getIntent().getStringExtra("url");
        cifrado = getIntent().getStringExtra("cifrado");
        bt_atras_crearcuentacreadito = findViewById(R.id.imageButton_volver_crearcuentacreadito);
        bt_ahorros_crearcuentacreadito = findViewById(R.id.button_cuentaahorros_CrearCuentaCreditoActivity);
        bt_prestamos_crearcuentacreadito = findViewById(R.id.button_cuentaprestamos_CrearCuentaCreditoActivity);
        bt_cerrarpanelcuentaprestamo = findViewById(R.id.button_cerrarpanelcuentaprestamo_CrearCuentaCredito);
        bt_guardarpanelcuentaprestamo = findViewById(R.id.button_guardar_CrearCuentaCredito);
        nombreEmpresa=findViewById(R.id.editTextText_nombre_crearcuentacreadito);
        panelcuentaprestamo = findViewById(R.id.panelcuentaprestamo);
        bt_atras_crearcuentacreadito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        bt_ahorros_crearcuentacreadito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_ahorros_crearcuentacreadito.setEnabled(false);
                bt_prestamos_crearcuentacreadito.setEnabled(false);
                cuentaAhorros();
            }
        });
        bt_prestamos_crearcuentacreadito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_ahorros_crearcuentacreadito.setVisibility(View.INVISIBLE);
                bt_prestamos_crearcuentacreadito.setVisibility(View.INVISIBLE);
                panelcuentaprestamo.setVisibility(View.VISIBLE);
            }
        });
        bt_cerrarpanelcuentaprestamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_ahorros_crearcuentacreadito.setVisibility(View.VISIBLE);
                bt_prestamos_crearcuentacreadito.setVisibility(View.VISIBLE);
                panelcuentaprestamo.setVisibility(View.INVISIBLE);
                nombreEmpresa.setText("");
            }
        });
        bt_guardarpanelcuentaprestamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCampos();
            }
        });
    }
    private void validarCampos(){
        if(verificar(nombreEmpresa.getText().toString().trim(),1)){
            //bt_cerrarpanelcuentaprestamo.setEnabled(false);
            bt_guardarpanelcuentaprestamo.setEnabled(false);
            cuentaPrestamos();
        }
        else{
            Toast.makeText(getApplicationContext(),"Debe ingresar un nombre",Toast.LENGTH_LONG).show();
        }
    }
    private boolean verificar(String dato,int opcion){
        String opcion1 = "[a-zA-Z0-9ñÑ ]{1,32}";
        switch (opcion){
            case 1:{
                return dato.matches(opcion1);
            }
            default:{
                return false;
            }
        }
    }
    private void cuentaAhorros(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("registrado")){
                        Toast.makeText(getApplicationContext(),"Registro exitoso",Toast.LENGTH_LONG).show();
                        volver();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Registro fallido",Toast.LENGTH_LONG).show();
                        bt_ahorros_crearcuentacreadito.setEnabled(true);
                        bt_prestamos_crearcuentacreadito.setEnabled(true);
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 017:"+error.toString(),Toast.LENGTH_LONG).show();
                    bt_ahorros_crearcuentacreadito.setEnabled(true);
                    bt_prestamos_crearcuentacreadito.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 018:"+error.toString(),Toast.LENGTH_LONG).show();
                bt_ahorros_crearcuentacreadito.setEnabled(true);
                bt_prestamos_crearcuentacreadito.setEnabled(true);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("empresa", "personal");
                params.put("tipo", "ahorro");
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "13");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void cuentaPrestamos(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("registrado")){
                        Toast.makeText(getApplicationContext(),"Registro exitoso",Toast.LENGTH_LONG).show();
                        nombreEmpresa.setText("");
                        volver();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("existe")){
                            Toast.makeText(getApplicationContext(),"Ya existe una cuenta con ese nombre",Toast.LENGTH_LONG).show();
                            //bt_cerrarpanelcuentaprestamo.setEnabled(true);
                            bt_guardarpanelcuentaprestamo.setEnabled(true);
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 019:"+error.toString(),Toast.LENGTH_LONG).show();
                    //bt_cerrarpanelcuentaprestamo.setEnabled(true);
                    bt_guardarpanelcuentaprestamo.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 020:"+error.toString(),Toast.LENGTH_LONG).show();
                //bt_cerrarpanelcuentaprestamo.setEnabled(true);
                bt_guardarpanelcuentaprestamo.setEnabled(true);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("empresa", nombreEmpresa.getText().toString().trim());
                params.put("tipo", "prestamos");
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "13");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void volver(){
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.putExtra("usuario", usuario);
        intent.putExtra("url", url);
        intent.putExtra("cifrado", cifrado);
        startActivity(intent);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            volver();
            return true;
        }
        return false;
    }
}