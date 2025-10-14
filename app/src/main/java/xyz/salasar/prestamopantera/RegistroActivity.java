package xyz.salasar.prestamopantera;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {
    private Button bt_registrar;
    private ImageButton bt_atras_registro;
    private EditText editxt_nombre_registro, editxt_apellido_registro, editxt_dni_registro, editxt_telefono_registro, editxt_correo_registro, editxt_usuario_registro,editxt_clave_registro;
    private CheckBox checkBox_mostrar_clave_registro,checkBox_politicas_registro;
    private TextView textView_politicas_registro;
    private String correo_origen,url,cifrado;
    private String clave_correo_origen;
    private static final int cargarTrabajo=11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_registro);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        bt_registrar = findViewById(R.id.bt_registrar);
        url = getIntent().getStringExtra("url");
        cifrado = getIntent().getStringExtra("cifrado");
        bt_atras_registro = findViewById(R.id.bt_atras_registro);
        editxt_nombre_registro = findViewById(R.id.editxt_nombre_registro);
        editxt_apellido_registro = findViewById(R.id.editxt_apellido_registro);
        editxt_dni_registro = findViewById(R.id.editxt_dni_registro);
        editxt_telefono_registro = findViewById(R.id.editxt_telefono_registro);
        editxt_correo_registro = findViewById(R.id.editxt_correo_registro);
        editxt_usuario_registro = findViewById(R.id.editxt_usuario_registro);
        editxt_clave_registro = findViewById(R.id.editxt_clave_registro);
        checkBox_mostrar_clave_registro = findViewById(R.id.checkBox_mostrar_clave_registro);
        checkBox_politicas_registro = findViewById(R.id.checkBox_politicas_registro);
        textView_politicas_registro = findViewById(R.id.textView_politicas_registro);
        textView_politicas_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PoliticasActivity.class);
                startActivity(intent);
            }
        });
        bt_atras_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        bt_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar();
            }
        });
        checkBox_mostrar_clave_registro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int cursorPosition = editxt_clave_registro.getSelectionEnd();
                if(isChecked){
                    editxt_clave_registro.setTransformationMethod(null);
                }
                else{
                    editxt_clave_registro.setTransformationMethod(new PasswordTransformationMethod());
                }
                editxt_clave_registro.setSelection(cursorPosition);
            }
        });
    }
    public void validar(){
        if(validarCampos(editxt_nombre_registro.getText().toString().trim(),1)){
            if(validarCampos(editxt_apellido_registro.getText().toString().trim(),1)){
                if(validarCampos(editxt_dni_registro.getText().toString().trim(),2)&&!editxt_dni_registro.getText().toString().trim().equals("0000000000000")){
                    if(validarCampos(editxt_telefono_registro.getText().toString().trim(),3)){
                        if(validarCampos(editxt_correo_registro.getText().toString().trim(),4)){
                            if(validarCampos(editxt_usuario_registro.getText().toString().trim(),5)) {
                                if(validarCampos(editxt_clave_registro.getText().toString().trim(),6)){
                                    if(checkBox_politicas_registro.isChecked()){
                                        desactivarFunciones();
                                        registrarDatos();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Debes aceptar las politicas",Toast.LENGTH_LONG).show();
                                    }
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Clave invalida",Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Usuario invalido",Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Correo electronico invalido",Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Telefono invalido",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"DNI invalido",Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Apellido invalido",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Nombre invalido",Toast.LENGTH_LONG).show();
        }
    }
    public void activarFunciones(){
        bt_registrar.setEnabled(true);
        editxt_nombre_registro.setEnabled(true);
        editxt_apellido_registro.setEnabled(true);
        editxt_dni_registro.setEnabled(true);
        editxt_telefono_registro.setEnabled(true);
        editxt_correo_registro.setEnabled(true);
        editxt_usuario_registro.setEnabled(true);
        editxt_clave_registro.setEnabled(true);
    }
    public void desactivarFunciones(){
        bt_registrar.setEnabled(false);
        editxt_nombre_registro.setEnabled(false);
        editxt_apellido_registro.setEnabled(false);
        editxt_dni_registro.setEnabled(false);
        editxt_telefono_registro.setEnabled(false);
        editxt_correo_registro.setEnabled(false);
        editxt_usuario_registro.setEnabled(false);
        editxt_clave_registro.setEnabled(false);
    }
    public boolean validarCampos(String dato,int opcion){
        String opcion1 = "[A-ZÑÁÉÍÓÚ][a-zñáéíóú]{1,20}";
        String opcion2 = "[A-ZÑÁÉÍÓÚ][a-zñáéíóú]{1,20}[ ][A-ZÑÁÉÍÓÚ][a-zñáéíóú]{1,20}";
        String opcion3 = "[A-ZÑÁÉÍÓÚ][a-zñáéíóú]{1,20}[ ][A-ZÑÁÉÍÓÚ][a-zñáéíóú]{1,20}[ ][A-ZÑÁÉÍÓÚ][a-zñáéíóú]{1,20}";
        String opcion4 = "[0-9]{13}";
        String opcion5 = "[0-9]{8}";
        String opcion6 = "[a-z0-9]{2,50}[@][a-z0-9]{2,50}[.][a-z]{2,50}";
        String opcion7 = "[a-z0-9]{2,50}[._][a-z0-9]{2,50}[@][a-z0-9]{2,50}[.][a-z]{2,50}";
        String opcion8 = "[a-z0-9]{2,50}[._][a-z0-9]{2,50}[._][a-z0-9]{2,50}[@][a-z0-9]{2,50}[.][a-z]{2,50}";
        String opcion9 = "[a-z0-9]{2,50}[._][a-z0-9]{2,50}[._][a-z0-9]{2,50}[._][a-z0-9]{2,50}[@][a-z0-9]{2,50}[.][a-z]{2,50}";
        String opcion10 = "[a-z0-9]{4,16}";
        String opcion11 = "[a-zA-Z0-9!@$%&*_]{4,16}";
        switch (opcion){
            case 1:{
                return dato.matches(opcion1+"|"+opcion2+"|"+opcion3);
            }
            case 2:{
                return dato.matches(opcion4);
            }
            case 3:{
                return dato.matches(opcion5);
            }
            case 4:{
                return dato.matches(opcion6+"|"+opcion7+"|"+opcion8+"|"+opcion9);
            }
            case 5:{
                return dato.matches(opcion10);
            }
            case 6:{
                return dato.matches(opcion11);
            }
            default:{
                return false;
            }
        }
    }
    public void registrarDatos(){
        String salt= BCrypt.gensalt(cargarTrabajo);
        String password= BCrypt.hashpw(editxt_clave_registro.getText().toString().trim(),salt);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("registrado")){
                        Toast.makeText(getApplicationContext(),"Registro exitoso",Toast.LENGTH_LONG).show();
                        correo_origen=confirmacion.getString("correo_origen");
                        clave_correo_origen=confirmacion.getString("clave_correo_origen");
                        confirmarCorreo();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("usuario_repetido")){
                            Toast.makeText(getApplicationContext(),"Usuario existente",Toast.LENGTH_LONG).show();
                            activarFunciones();
                        }
                        else{
                            if(confirmacion.getString("mensaje").equals("correo_repetido")){
                                Toast.makeText(getApplicationContext(),"Correo existente",Toast.LENGTH_LONG).show();
                                activarFunciones();
                            }
                            else{
                                if(confirmacion.getString("mensaje").equals("identidad_repetida")){
                                    Toast.makeText(getApplicationContext(),"DNI existente",Toast.LENGTH_LONG).show();
                                    activarFunciones();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Error al registrar", Toast.LENGTH_LONG).show();
                                    activarFunciones();
                                }
                            }
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 075:"+error.toString(),Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 076:"+error.toString(),Toast.LENGTH_LONG).show();
                activarFunciones();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", editxt_nombre_registro.getText().toString().trim());
                params.put("apellido", editxt_apellido_registro.getText().toString().trim());
                params.put("dni", editxt_dni_registro.getText().toString().trim());
                params.put("telefono", editxt_telefono_registro.getText().toString().trim());
                params.put("correo", editxt_correo_registro.getText().toString().trim());
                params.put("usuario", editxt_usuario_registro.getText().toString().trim());
                params.put("clave", password);
                params.put("cifrado", cifrado);
                params.put("codigoLlave","1");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void volver(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    public void confirmarCorreo(){
        Intent intent = new Intent(getApplicationContext(), VerificarActivity.class);
        intent.putExtra("correo", editxt_correo_registro.getText().toString().trim());
        intent.putExtra("nombre", editxt_nombre_registro.getText().toString().trim());
        intent.putExtra("apellido", editxt_apellido_registro.getText().toString().trim());
        intent.putExtra("usuario", editxt_usuario_registro.getText().toString().trim());
        intent.putExtra("correo_origen", correo_origen);
        intent.putExtra("clave_correo_origen", clave_correo_origen);
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