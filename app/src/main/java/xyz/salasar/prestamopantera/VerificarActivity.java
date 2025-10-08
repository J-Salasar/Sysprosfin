package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

public class VerificarActivity extends AppCompatActivity {
    private Button bt_validar_verificar;
    private EditText editxt_codigo_verificar;
    private ImageButton imageBt_atras_verificar;
    private String correo;
    private String nombre;
    private String apellido;
    private String usuario;
    private int codigo;
    private String correo_origen,url,cifrado;
    private String clave_correo_origen;
    private Session session;
    private Thread hilo;
    private Message message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_verificar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        bt_validar_verificar = findViewById(R.id.bt_validar_verificar);
        editxt_codigo_verificar = findViewById(R.id.editxt_codigo_verificar);
        imageBt_atras_verificar = findViewById(R.id.bt_atras_verificar);
        correo = getIntent().getStringExtra("correo");
        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");
        usuario= getIntent().getStringExtra("usuario");
        correo_origen= getIntent().getStringExtra("correo_origen");
        clave_correo_origen= getIntent().getStringExtra("clave_correo_origen");
        url= getIntent().getStringExtra("url");
        cifrado= getIntent().getStringExtra("cifrado");
        enviarCorreo();
        imageBt_atras_verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        bt_validar_verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desactivarFunciones();
                validar();
            }
        });
    }
    private void activarFunciones(){
        bt_validar_verificar.setEnabled(true);
        editxt_codigo_verificar.setEnabled(true);
    }
    private void desactivarFunciones(){
        bt_validar_verificar.setEnabled(false);
        editxt_codigo_verificar.setEnabled(false);
    }
    private void confirmar(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("validado")){
                        Toast.makeText(getApplicationContext(),"Validacion exitosa",Toast.LENGTH_LONG).show();
                        volver();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al procesar",Toast.LENGTH_LONG).show();
                        activarFunciones();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 097:"+error.toString(),Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 098:"+error.toString(),Toast.LENGTH_LONG).show();
                activarFunciones();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("cifrado", cifrado);
                params.put("codigoLlave","2");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void validar(){
        if(editxt_codigo_verificar.getText().toString().trim().equals(String.valueOf(codigo))){
            confirmar();
        }
        else{
            Toast.makeText(getApplicationContext(),"Codigo incorrecto",Toast.LENGTH_LONG).show();
            activarFunciones();
        }
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
            session=Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correo_origen,clave_correo_origen);
                }
            });
            if(session!=null){
                message=new MimeMessage(session);
                message.setFrom(new InternetAddress(correo_origen));
                message.setSubject("Verificacion de cuenta");
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
                message.setContent(
                        "<body>"+
                                "<p>Estimado/a: "+nombre+" "+apellido+"</p>"+
                                "<p>Este es tu codigo para verificar tu cuenta.</p>"+
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
            Toast.makeText(getApplicationContext(),"Error al enviar el correo",Toast.LENGTH_LONG).show();
        }
    }
    public void volver(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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