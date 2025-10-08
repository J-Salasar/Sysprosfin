package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {
    private String usuario,url,cifrado;
    private ImageButton volver;
    private TextView textView_volver_perfil;
    private TextView usuario_perfil;
    private TextView textView_nombre_apellido_perfil;
    private TextView textView_identidad_pefil;
    private TextView textView_telefono_perfil;
    private TextView textView_correo_perfil;
    private EditText editTextNumber_identidad_perfil;
    private EditText editTextPhone_telefono_perfil;
    private EditText editTextTextPassword_clave_perfil;
    private ImageButton imageButton_identidad_perfil;
    private ImageButton imageButton_telefono_perfil;
    private ImageButton imageButton_clave_perfil;
    private CheckBox checkBox_mostrar_clave_perfil;
    private String fechaC,fechaA;
    private SimpleDateFormat formatoSalida=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoEntrada=new SimpleDateFormat("yyyy-MM-dd");
    private TextView textView_fecha_creacion_perfil, textView5_fecha_actividad_perfil;
    private static final int cargarTrabajo=11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_perfil);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario = getIntent().getStringExtra("usuario");
        url = getIntent().getStringExtra("url");
        cifrado = getIntent().getStringExtra("cifrado");
        volver = findViewById(R.id.imageButton_volver_perfil);
        textView_volver_perfil = findViewById(R.id.textView_volver_perfil);
        usuario_perfil = findViewById(R.id.textView_usuario_perfil);
        textView_nombre_apellido_perfil = findViewById(R.id.textView_nombre_apellido_perfil);
        textView_identidad_pefil = findViewById(R.id.textView_identidad_pefil);
        textView_telefono_perfil = findViewById(R.id.textView_telefono_perfil);
        textView_correo_perfil = findViewById(R.id.textView_correo_perfil);
        editTextNumber_identidad_perfil = findViewById(R.id.editTextNumber_identidad_perfil);
        editTextPhone_telefono_perfil = findViewById(R.id.editTextPhone_telefono_perfil);
        editTextTextPassword_clave_perfil = findViewById(R.id.editTextTextPassword_clave_perfil);
        imageButton_identidad_perfil = findViewById(R.id.imageButton_identidad_perfil);
        imageButton_telefono_perfil = findViewById(R.id.imageButton_telefono_perfil);
        imageButton_clave_perfil = findViewById(R.id.imageButton_clave_perfil);
        checkBox_mostrar_clave_perfil = findViewById(R.id.checkBox_mostrar_clave_perfil);
        textView_fecha_creacion_perfil = findViewById(R.id.textView_fecha_creacion_perfil);
        textView5_fecha_actividad_perfil = findViewById(R.id.textView5_fecha_actividad_perfil);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        textView_volver_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        imageButton_identidad_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextNumber_identidad_perfil.getText().toString().trim().length()>=13){
                    desactivarFunciones();
                    editarIdentidad();
                }
                else{
                    Toast.makeText(getApplicationContext(),"La identidad debe tener al menos 13 caracteres",Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        });
        imageButton_telefono_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextPhone_telefono_perfil.getText().toString().trim().length()>=8){
                    desactivarFunciones();
                    editarTelefono();
                }
                else{
                    Toast.makeText(getApplicationContext(),"El telefono debe tener al menos 8 caracteres",Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        });
        imageButton_clave_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificar(editTextTextPassword_clave_perfil.getText().toString().trim(),1)){
                    desactivarFunciones();
                    editarClave();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Clave invalida",Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        });
        checkBox_mostrar_clave_perfil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int cursorPosition = editTextTextPassword_clave_perfil.getSelectionEnd();
                if(isChecked){
                    editTextTextPassword_clave_perfil.setTransformationMethod(null);
                }
                else{
                    editTextTextPassword_clave_perfil.setTransformationMethod(new PasswordTransformationMethod());
                }
                editTextTextPassword_clave_perfil.setSelection(cursorPosition);
            }
        });
        usuario_perfil.setText("Usuario: "+usuario);
        buscarDatos();
    }
    private boolean verificar(String dato,int opcion){
        String opcion1 = "[a-zA-Z0-9!@$%&*_]{4,16}";
        switch (opcion) {
            case 1: {
                return dato.matches(opcion1);
            }
            default:{
                return false;
            }
        }
    }
    public void desactivarFunciones(){
        imageButton_identidad_perfil.setVisibility(View.INVISIBLE);
        imageButton_telefono_perfil.setVisibility(View.INVISIBLE);
        imageButton_clave_perfil.setVisibility(View.INVISIBLE);
    }
    public void activarFunciones(){
        imageButton_identidad_perfil.setVisibility(View.VISIBLE);
        imageButton_telefono_perfil.setVisibility(View.VISIBLE);
        imageButton_clave_perfil.setVisibility(View.VISIBLE);
    }
    public void editarIdentidad(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("guardado")){
                        Toast.makeText(getApplicationContext(),"Identidad guardada",Toast.LENGTH_LONG).show();
                        buscarDatos();
                        activarFunciones();
                        editTextNumber_identidad_perfil.setText("");
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"guardado fallido",Toast.LENGTH_LONG).show();
                        activarFunciones();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 047:"+error.toString(),Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 048:"+error.toString(),Toast.LENGTH_LONG).show();
                activarFunciones();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("dni", editTextNumber_identidad_perfil.getText().toString().trim());
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "17");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void editarTelefono(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("guardado")){
                        Toast.makeText(getApplicationContext(),"Telefono guardado",Toast.LENGTH_LONG).show();
                        buscarDatos();
                        activarFunciones();
                        editTextPhone_telefono_perfil.setText("");
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"guardado fallido",Toast.LENGTH_LONG).show();
                        activarFunciones();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 049:"+error.toString(),Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 050:"+error.toString(),Toast.LENGTH_LONG).show();
                activarFunciones();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("telefono", editTextPhone_telefono_perfil.getText().toString().trim());
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "18");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void editarClave(){
        String salt= BCrypt.gensalt(cargarTrabajo);
        String password= BCrypt.hashpw(editTextTextPassword_clave_perfil.getText().toString().trim(),salt);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("guardado")){
                        Toast.makeText(getApplicationContext(),"Contraseña guardada",Toast.LENGTH_LONG).show();
                        buscarDatos();
                        activarFunciones();
                        editTextTextPassword_clave_perfil.setText("");
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"guardado fallido",Toast.LENGTH_LONG).show();
                        activarFunciones();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 051:"+error.toString(),Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 052:"+error.toString(),Toast.LENGTH_LONG).show();
                activarFunciones();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("clave", password);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "19");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void buscarDatos(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("encontrado")){
                        textView_nombre_apellido_perfil.setText(confirmacion.getString("nombre")+" "+confirmacion.getString("apellido"));
                        textView_identidad_pefil.setText("Identidad: "+confirmacion.getString("identidad"));
                        textView_telefono_perfil.setText("Telefono: "+confirmacion.getString("telefono"));
                        textView_correo_perfil.setText("Correo: "+confirmacion.getString("correo"));
                        try{
                            Date fechaf=formatoEntrada.parse(confirmacion.getString("fecha_creacion"));
                            fechaC=formatoSalida.format(fechaf);
                            fechaf=formatoEntrada.parse(confirmacion.getString("fecha_actividad"));
                            fechaA=formatoSalida.format(fechaf);
                        }
                        catch (ParseException e){
                            Toast.makeText(getApplicationContext(),"Fecha no valida",Toast.LENGTH_LONG).show();
                        }
                        textView_fecha_creacion_perfil.setText("Fecha de creacion: "+fechaC);
                        textView5_fecha_actividad_perfil.setText("Fecha de actividad: "+fechaA);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Datos perdidos",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 053:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 054:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "20");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void volver(){
        Intent intent=new Intent(getApplicationContext(),PrincipalActivity.class);
        intent.putExtra("usuario", usuario);
        intent.putExtra("url",url);
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