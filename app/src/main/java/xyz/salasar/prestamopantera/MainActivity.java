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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button bt_acceder_inicio_sesion;
    private ImageButton imageBt_crear_usuario_inicio_sesion;
    private EditText editxt_clave_inicio_sesion;
    private EditText editxt_usuario_inicio_sesion;
    private CheckBox checkBox_mostrar_clave_inicio_sesion;
    private CheckBox checkBox_recordar_usuario_inicio_sesion;
    private TextView txtView_crear_usuario_inicio_sesion;
    private TextView txtView_ovido_clave_inicio_sesion;
    private String url;
    private String cifrado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        //firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
        firebaseAppCheck.getAppCheckToken(true).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        firebaseVerificarSesion();
                    }
                    else{
                        String error=task.getException().toString();
                        Toast.makeText(getApplicationContext(),"Error 099:"+task.getException().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        bt_acceder_inicio_sesion = findViewById(R.id.bt_acceder_inicio_sesion);
        imageBt_crear_usuario_inicio_sesion = findViewById(R.id.imageBt_crear_usuario_inicio_sesion);
        editxt_clave_inicio_sesion = findViewById(R.id.editxt_clave_inicio_sesion);
        editxt_usuario_inicio_sesion = findViewById(R.id.editxt_usuario_inicio_sesion);
        checkBox_mostrar_clave_inicio_sesion = findViewById(R.id.checkBox_mostrar_clave_inicio_sesion);
        checkBox_recordar_usuario_inicio_sesion = findViewById(R.id.checkBox_recordar_usuario_inicio_sesion);
        txtView_crear_usuario_inicio_sesion = findViewById(R.id.txtView_crear_usuario_inicio_sesion);
        txtView_ovido_clave_inicio_sesion = findViewById(R.id.txtView_ovido_clave_inicio_sesion);
        SharedPreferences sharedPreferences=getSharedPreferences("usuario", Context.MODE_PRIVATE);
        editxt_usuario_inicio_sesion.setText(sharedPreferences.getString("usuario",""));
        txtView_crear_usuario_inicio_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });
        imageBt_crear_usuario_inicio_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });
        txtView_ovido_clave_inicio_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                olvidoClave();
            }
        });
        bt_acceder_inicio_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desactivarFunciones();
                validarCampos();
            }
        });
        checkBox_mostrar_clave_inicio_sesion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int cursorPosition = editxt_clave_inicio_sesion.getSelectionEnd();
                if(isChecked){
                    editxt_clave_inicio_sesion.setTransformationMethod(null);
                }
                else{
                    editxt_clave_inicio_sesion.setTransformationMethod(new PasswordTransformationMethod());
                }
                editxt_clave_inicio_sesion.setSelection(cursorPosition);
            }
        });
    }
    private void firebaseVerificarSesion(){
        FirebaseAuth autenticar=FirebaseAuth.getInstance();
        FirebaseUser sujetoValidar=autenticar.getCurrentUser();
        if(sujetoValidar!=null){
            sujetoValidar.getIdToken(true).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser sujeto = autenticar.getCurrentUser();
                    informacionPHP();
                }
                else {
                    autenticar.signOut();
                    firebaseSesion();
                }
            });
        }
        else {
            firebaseSesion();
        }
    }
    private void firebaseSesion(){
        FirebaseAuth autenticar=FirebaseAuth.getInstance();
        autenticar.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser sujeto = autenticar.getCurrentUser();
                informacionPHP();
            }
            else{
                Toast.makeText(getApplicationContext(),"Error 100:"+task.getException().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void informacionPHP(){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference referencia=database.getReference("paginaPrueba");
        referencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                url=snapshot.child("url").getValue(String.class);
                cifrado=snapshot.child("cifrado").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error 099:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void validarCampos(){
        if(verificar(editxt_usuario_inicio_sesion.getText().toString().trim(),1)){
            if(verificar(editxt_clave_inicio_sesion.getText().toString().trim(),2)){
                acceder();
            }
            else{
                Toast.makeText(getApplicationContext(),"Clave invalida",Toast.LENGTH_LONG).show();
                activarFunciones();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Usuario invalido",Toast.LENGTH_LONG).show();
            activarFunciones();
        }
    }
    private boolean verificar(String dato,int opcion){
        String opcion1 = "[a-z0-9]{2,20}";
        String opcion2 = "[a-zA-Z0-9!$@%&*_]{4,16}";
        switch (opcion){
            case 1:{
                return dato.matches(opcion1);
            }
            case 2:{
                return dato.matches(opcion2);
            }
            default:{
                return false;
            }
        }
    }
    public void desactivarFunciones(){
        bt_acceder_inicio_sesion.setEnabled(false);
        editxt_clave_inicio_sesion.setEnabled(false);
        editxt_usuario_inicio_sesion.setEnabled(false);
    }
    public void activarFunciones(){
        bt_acceder_inicio_sesion.setEnabled(true);
        editxt_clave_inicio_sesion.setEnabled(true);
        editxt_usuario_inicio_sesion.setEnabled(true);
    }
    public void recordarUsuario(){
        if(checkBox_recordar_usuario_inicio_sesion.isChecked()){
            SharedPreferences sharedPreferences=getSharedPreferences("usuario", Context.MODE_PRIVATE);
            SharedPreferences.Editor O_editor=sharedPreferences.edit();
            O_editor.putString("usuario",editxt_usuario_inicio_sesion.getText().toString().trim());
            O_editor.commit();
        }
    }
    public void pantallaPrincipal(){
        Intent intent=new Intent(getApplicationContext(),PrincipalActivity.class);
        intent.putExtra("usuario",editxt_usuario_inicio_sesion.getText().toString().trim());
        intent.putExtra("url",url);
        intent.putExtra("cifrado",cifrado);
        startActivity(intent);
    }
    public void verificar(String usuario,String correo,String nombre,String apellido, String correo_origen, String clave_correo_origen){
        Intent intent = new Intent(getApplicationContext(), VerificarActivity.class);
        intent.putExtra("usuario",usuario);
        intent.putExtra("correo",correo);
        intent.putExtra("nombre",nombre);
        intent.putExtra("apellido",apellido);
        intent.putExtra("correo_origen",correo_origen);
        intent.putExtra("clave_correo_origen",clave_correo_origen);
        intent.putExtra("url",url);
        intent.putExtra("cifrado",cifrado);
        startActivity(intent);
    }
    public void acceder() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("activo")){
                        boolean acceso= BCrypt.checkpw(editxt_clave_inicio_sesion.getText().toString().trim(),confirmacion.getString("clave"));
                        if(acceso){
                            actualizarFecha();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Usuario o contraseña incorrectos",Toast.LENGTH_LONG).show();
                            activarFunciones();
                        }
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("no existe")){
                            Toast.makeText(getApplicationContext(),"Usuario o contraseña incorrectos",Toast.LENGTH_LONG).show();
                            activarFunciones();
                        }
                        else{
                            if(confirmacion.getString("mensaje").equals("bloqueado")){
                                Toast.makeText(getApplicationContext(),"Usuario bloqueado",Toast.LENGTH_LONG).show();
                                activarFunciones();
                            }
                            else{
                                if(confirmacion.getString("mensaje").equals("desactivo")){
                                    boolean acceso= BCrypt.checkpw(editxt_clave_inicio_sesion.getText().toString().trim(),confirmacion.getString("clave"));
                                    if(acceso){
                                        verificar(editxt_usuario_inicio_sesion.getText().toString().trim(),confirmacion.getString("correo"),confirmacion.getString("nombre"),confirmacion.getString("apellido"),confirmacion.getString("correo_origen"),confirmacion.getString("clave_correo_origen"));
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Usuario o contraseña incorrectos",Toast.LENGTH_LONG).show();
                                        activarFunciones();
                                    }
                                }
                            }
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(), "Error 002:"+error.toString(),Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                acceder();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", editxt_usuario_inicio_sesion.getText().toString().trim());
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "3");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void actualizarFecha(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("fecha_actualizada")){
                        recordarUsuario();
                        pantallaPrincipal();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(), "Error 0021:"+error.toString(),Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                acceder();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", editxt_usuario_inicio_sesion.getText().toString().trim());
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "301");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void olvidoClave(){
        Intent intent = new Intent(getApplicationContext(), Restablecer2Activity.class);
        intent.putExtra("url",url);
        intent.putExtra("cifrado",cifrado);
        startActivity(intent);
    }
    public void registrar(){
        Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("cifrado",cifrado);
        startActivity(intent);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}