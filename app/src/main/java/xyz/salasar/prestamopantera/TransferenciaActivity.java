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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

public class TransferenciaActivity extends AppCompatActivity {
    private String usuario, cuentaN, tipoN, rangoN;
    private ImageButton volver, informacionC;
    private EditText cuentaD, cantidadE, codigoS;
    private Button confirmarF, cancelarF;
    private String nombre, apellido, codigo, nombreD, apellidoD,url, cifrado;
    private TextView nombreC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_transferencia);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario=getIntent().getStringExtra("usuario");
        cuentaN=getIntent().getStringExtra("cuenta");
        nombre=getIntent().getStringExtra("nombre");
        apellido=getIntent().getStringExtra("apellido");
        codigo=getIntent().getStringExtra("codigo");
        tipoN=getIntent().getStringExtra("tipo");
        rangoN=getIntent().getStringExtra("rango");
        url=getIntent().getStringExtra("url");
        cifrado=getIntent().getStringExtra("cifrado");
        volver=findViewById(R.id.volver600);
        informacionC=findViewById(R.id.informacionC600);
        cuentaD=findViewById(R.id.cuentaD600);
        cantidadE=findViewById(R.id.cantidadE600);
        codigoS=findViewById(R.id.codigoS600);
        confirmarF=findViewById(R.id.confirmarF600);
        cancelarF=findViewById(R.id.cancelarT600);
        nombreC=findViewById(R.id.nombreC600);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        informacionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informacion();
            }
        });
        confirmarF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cuentaN.equals(cuentaD.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(),"No puedes transferir a ti mismo",Toast.LENGTH_LONG).show();
                    cantidadE.setEnabled(true);
                    confirmarF.setVisibility(View.VISIBLE);
                    cancelarF.setVisibility(View.VISIBLE);
                }
                else {
                    if (cantidadE.getText().toString().equals("")||(Double.parseDouble(cantidadE.getText().toString().trim())<0.01)) {
                        Toast.makeText(getApplicationContext(), "Escribe alguna cantidad signifitiva a depositar", Toast.LENGTH_LONG).show();
                        cantidadE.setEnabled(true);
                        confirmarF.setVisibility(View.VISIBLE);
                        cancelarF.setVisibility(View.VISIBLE);
                    }
                    else {
                        cantidadE.setEnabled(false);
                        confirmarF.setVisibility(View.INVISIBLE);
                        cancelarF.setVisibility(View.INVISIBLE);
                        confirmar();
                    }
                }
            }
        });
        cancelarF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuentaD.setEnabled(true);
                confirmarF.setVisibility(View.INVISIBLE);
                cancelarF.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void confirmar() {
        if(codigo.equals(codigoS.getText().toString())){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                        JSONObject confirmacion=jsonArray.getJSONObject(0);
                        if(confirmacion.getString("mensaje").equals("aprobado")){
                            Toast.makeText(getApplicationContext(),"Tranferencia realizada",Toast.LENGTH_LONG).show();
                            volver();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"No tienes fondos suficientes",Toast.LENGTH_LONG).show();
                            cantidadE.setEnabled(true);
                            confirmarF.setVisibility(View.VISIBLE);
                            cancelarF.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Throwable error){
                        Toast.makeText(getApplicationContext(),"Error 089:"+error.toString(),Toast.LENGTH_LONG).show();
                        cantidadE.setEnabled(true);
                        confirmarF.setVisibility(View.VISIBLE);
                        cancelarF.setVisibility(View.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"Error 090:"+error.toString(),Toast.LENGTH_LONG).show();
                    cantidadE.setEnabled(true);
                    confirmarF.setVisibility(View.VISIBLE);
                    cancelarF.setVisibility(View.VISIBLE);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("cuentaO",cuentaN);
                    params.put("nombreO",nombre);
                    params.put("apellidoO",apellido);
                    params.put("cuentaD",cuentaD.getText().toString());
                    params.put("nombreD",nombreD);
                    params.put("apellidoD",apellidoD);
                    params.put("cantidad",cantidadE.getText().toString());
                    params.put("cifrado", cifrado);
                    params.put("codigoLlave", "24");
                    return params;
                }
            };
            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }
        else{
            cantidadE.setEnabled(true);
            confirmarF.setVisibility(View.VISIBLE);
            cancelarF.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"Codigo erroneo",Toast.LENGTH_LONG).show();
        }
    }
    private void informacion(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        nombreD=confirmacion.getString("nombre");
                        apellidoD=confirmacion.getString("apellido");
                        nombreC.setText(nombreD+" "+apellidoD);
                        confirmarF.setVisibility(View.VISIBLE);
                        cancelarF.setVisibility(View.VISIBLE);
                        cuentaD.setEnabled(false);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No existe la cuenta",Toast.LENGTH_LONG).show();
                        nombreC.setText("");
                        confirmarF.setVisibility(View.INVISIBLE);
                        cancelarF.setVisibility(View.INVISIBLE);
                        cuentaD.setEnabled(true);
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 091:"+error.toString(),Toast.LENGTH_LONG).show();
                    nombreC.setText("");
                    confirmarF.setVisibility(View.INVISIBLE);
                    cancelarF.setVisibility(View.INVISIBLE);
                    cuentaD.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 092:"+error.toString(),Toast.LENGTH_LONG).show();
                nombreC.setText("");
                confirmarF.setVisibility(View.INVISIBLE);
                cancelarF.setVisibility(View.INVISIBLE);
                cuentaD.setEnabled(true);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta",cuentaD.getText().toString());
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "25");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void volver(){
        Intent intent=new Intent();
        if(tipoN.equals("ahorro")) {
            if(rangoN.equals("usuario")) {
                intent = new Intent(getApplicationContext(), PanelusuarioActivity.class);
            }
            else{
                if(rangoN.equals("propietario")) {
                    intent = new Intent(getApplicationContext(), PanelpropietarioActivity.class);
                }
            }
        }
        else{
            if(tipoN.equals("prestamos")) {
                if(rangoN.equals("usuario")) {
                    intent = new Intent(getApplicationContext(), PanelusuarioActivity.class);
                }
                else{
                    if(rangoN.equals("propietario")){
                        intent = new Intent(getApplicationContext(), PanelpropietarioActivity.class);
                    }
                    else{
                        if(rangoN.equals("ayudante")){
                            intent = new Intent(getApplicationContext(), PanelayudanteActivity.class);
                        }
                        else{
                            if(rangoN.equals("director")){
                                intent = new Intent(getApplicationContext(), PanelDirectorActivity.class);
                            }
                        }
                    }
                }
            }
        }
        intent.putExtra("usuario", usuario);
        intent.putExtra("cuenta", cuentaN);
        intent.putExtra("url",url);
        intent.putExtra("cifrado",cifrado);
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