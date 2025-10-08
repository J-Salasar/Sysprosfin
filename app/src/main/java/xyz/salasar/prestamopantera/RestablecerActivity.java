package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RestablecerActivity extends AppCompatActivity {
    private ImageButton bt_atras_restablecer;
    private EditText editxt_codigo_restablecer;
    private EditText editxt_clave_restablecer;
    private Button bt_guardar_restablecer;
    private String usuario;
    private String nombre;
    private String apellido;
    private String correo;
    private int codigo;
    private String correo_origen;
    private String clave_correo_origen,url,cifrado;
    private Session session;
    private Thread hilo;
    private Message message;
    private CheckBox checkBox_mostrar_clave_restablecer;
    private static final int cargarTrabajo=11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_restablecer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        bt_atras_restablecer=findViewById(R.id.bt_atras_restablecer);
        editxt_codigo_restablecer=findViewById(R.id.editxt_codigo_restablecer);
        editxt_clave_restablecer=findViewById(R.id.editxt_clave_restablecer);
        bt_guardar_restablecer=findViewById(R.id.bt_guardar_restablecer);
        usuario=getIntent().getStringExtra("usuario");
        nombre=getIntent().getStringExtra("nombre");
        apellido=getIntent().getStringExtra("apellido");
        correo=getIntent().getStringExtra("correo");
        correo_origen=getIntent().getStringExtra("correo_origen");
        clave_correo_origen=getIntent().getStringExtra("clave_correo_origen");
        url=getIntent().getStringExtra("url");
        cifrado=getIntent().getStringExtra("cifrado");
        checkBox_mostrar_clave_restablecer=findViewById(R.id.checkBox_mostrar_clave_restablecer);
        enviarCorreo();
        bt_atras_restablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        bt_guardar_restablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificar(editxt_clave_restablecer.getText().toString().trim(),1)){
                    desactivarFunciones();
                    restablecer();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Clave invalida",Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        });
        checkBox_mostrar_clave_restablecer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int cursorPosition = editxt_clave_restablecer.getSelectionEnd();
                if(isChecked){
                    editxt_clave_restablecer.setTransformationMethod(null);
                }
                else{
                    editxt_clave_restablecer.setTransformationMethod(new PasswordTransformationMethod());
                }
                editxt_clave_restablecer.setSelection(cursorPosition);
            }
        });
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
    public void activarFunciones(){
        bt_guardar_restablecer.setEnabled(true);
        editxt_codigo_restablecer.setEnabled(true);
        editxt_clave_restablecer.setEnabled(true);
    }
    public void desactivarFunciones(){
        bt_guardar_restablecer.setEnabled(false);
        editxt_codigo_restablecer.setEnabled(false);
        editxt_clave_restablecer.setEnabled(false);
    }
    public void restablecer(){
        if(editxt_codigo_restablecer.getText().toString().trim().equals(String.valueOf(codigo))){
            if(editxt_clave_restablecer.getText().toString().trim().length()>=8){
                guadarClave();
            }
            else{
                Toast.makeText(getApplicationContext(),"La clave debe tener al menos 8 caracteres",Toast.LENGTH_LONG).show();
                activarFunciones();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Codigo incorrecto",Toast.LENGTH_LONG).show();
            activarFunciones();
        }
    }
    public void guadarClave() {
        String salt= BCrypt.gensalt(cargarTrabajo);
        String password= BCrypt.hashpw(editxt_clave_restablecer.getText().toString().trim(),salt);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("guardado")){
                        Toast.makeText(getApplicationContext(),"Restablecimiento exitoso",Toast.LENGTH_LONG).show();
                        volver();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al guardar",Toast.LENGTH_LONG).show();
                        activarFunciones();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 079:"+error.toString(),Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 080:"+error.toString(),Toast.LENGTH_LONG).show();
                activarFunciones();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("clave", password);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "5");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void enviarCorreo(){
        Random random = new Random();
        codigo=random.nextInt(999999)+100000;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties=new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        try{
            session= Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correo_origen,clave_correo_origen);
                }
            });
            if(session!=null){
                message=new MimeMessage(session);
                message.setFrom(new InternetAddress(correo_origen));
                message.setSubject("Restablecer contraseña");
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
                message.setContent(
                        "<body>"+
                                "<p>Estimado/a: "+nombre+" "+apellido+"</p>"+
                                "<p>Este es tu codigo para restablecer tu contraseña.</p>"+
                                "<br>"+
                                "<p>Codigo: "+codigo+"</p>"+
                                "<br>"+
                                "<p>De no reconocer este correo, por favor ignoralo. No brindes este codigo por llamadas o mensajes el mismo es de uso personal.</p>"+
                                "</body>",
                        "text/html; charset=utf-8"
                );
                hilo = new Thread(() -> {
                    try {
                        Transport.send(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                hilo.start();
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error en el enviar el correo",Toast.LENGTH_LONG).show();
        }
    }
    public void volver(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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