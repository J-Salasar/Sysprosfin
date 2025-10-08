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

import java.text.DecimalFormat;
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

public class SolicitudPrestamoActivity extends AppCompatActivity {
    private String usuario, cuenta;
    private ImageButton volver;
    private TextView credito;
    private EditText cantidad, codigo;
    private ImageButton verificar;
    private Button enviar, cancelar;
    private int codigoV;
    private Session session;
    private Thread hilo;
    private Message message;
    private String correo;
    private String nombre;
    private String apellido;
    private String correo_origen;
    private String clave_correo_origen;
    private String empresa,url,cifrado;
    private String rangoN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_solicitud_prestamo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario=getIntent().getStringExtra("usuario");
        cuenta=getIntent().getStringExtra("cuenta");
        url=getIntent().getStringExtra("url");
        cifrado=getIntent().getStringExtra("cifrado");
        rangoN=getIntent().getStringExtra("rango");
        volver=findViewById(R.id.volver444);
        credito=findViewById(R.id.credito444);
        cantidad=findViewById(R.id.cantidad444);
        codigo=findViewById(R.id.codigo444);
        verificar=findViewById(R.id.verificar444);
        enviar=findViewById(R.id.enviar444);
        cancelar=findViewById(R.id.cancelar444);
        informacion();
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verficar();
            }
        });
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar();
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });
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
                        DecimalFormat formato = new DecimalFormat("#,##0.00");
                        String formateado=formato.format(Double.parseDouble(confirmacion.getString("credito")));
                        credito.setText("Credito disponible L. "+formateado);
                        correo=confirmacion.getString("correo");
                        nombre=confirmacion.getString("nombre");
                        apellido=confirmacion.getString("apellido");
                        correo_origen=confirmacion.getString("correo_origen");
                        clave_correo_origen=confirmacion.getString("clave_correo_origen");
                        empresa=confirmacion.getString("empresa");
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al procesar",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 085:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 086:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("cuenta", cuenta);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "22");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void cancelar(){
        enviar.setVisibility(View.INVISIBLE);
        cancelar.setVisibility(View.INVISIBLE);
        verificar.setVisibility(View.VISIBLE);
    }
    public void enviar(){
        Double validarCantidad;
        if(cantidad.getText().toString().equals("")){
            validarCantidad=0.0;
        }
        else {
            validarCantidad = Double.parseDouble(cantidad.getText().toString().trim());
        }
        if(validarCantidad<1.0){
            Toast.makeText(getApplicationContext(),"Escribe alguna cantidad a significativa",Toast.LENGTH_LONG).show();
        }
        else{
            if(codigo.getText().toString().equals(String.valueOf(codigoV))){
                cantidad.setEnabled(false);
                enviar.setVisibility(View.INVISIBLE);
                cancelar.setVisibility(View.INVISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                            JSONObject confirmacion=jsonArray.getJSONObject(0);
                            if(confirmacion.getString("mensaje").equals("aprobado")){
                                Toast.makeText(getApplicationContext(),"Solicitud enviada",Toast.LENGTH_LONG).show();
                                volver();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Credito insuficiente",Toast.LENGTH_LONG).show();
                                cantidad.setEnabled(true);
                                enviar.setVisibility(View.VISIBLE);
                                cancelar.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (Throwable error){
                            Toast.makeText(getApplicationContext(),"Error 087:"+error.toString(),Toast.LENGTH_LONG).show();
                            cantidad.setEnabled(true);
                            enviar.setVisibility(View.VISIBLE);
                            cancelar.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error 088:"+error.toString(),Toast.LENGTH_LONG).show();
                        cantidad.setEnabled(true);
                        enviar.setVisibility(View.VISIBLE);
                        cancelar.setVisibility(View.VISIBLE);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("usuario", usuario);
                        params.put("cuenta", cuenta);
                        params.put("cantidad", cantidad.getText().toString().trim());
                        params.put("empresa", empresa);
                        params.put("cifrado", cifrado);
                        params.put("codigoLlave", "23");
                        return params;
                    }
                };
                RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);
            }
            else{
                Toast.makeText(getApplicationContext(),"Codigo incorrecto",Toast.LENGTH_LONG).show();
            }
        }
    }
    public void verficar(){
        enviar.setVisibility(View.VISIBLE);
        cancelar.setVisibility(View.VISIBLE);
        verificar.setVisibility(View.INVISIBLE);
        Random random = new Random();
        codigoV=random.nextInt(999999)+100000;
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
                message.setSubject("Solicitud de prestamo");
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
                message.setContent(
                        "<body>"+
                                "<p>Estimado/a: "+nombre+" "+apellido+"</p>"+
                                "<p>Este es tu codigo para enviar una solicitud de prestamo.</p>"+
                                "<br>"+
                                "<p>Codigo: "+codigoV+"</p>"+
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
        if(rangoN.equals("usuario")) {
            Intent intent = new Intent(getApplicationContext(), PanelusuarioActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("cuenta", cuenta);
            intent.putExtra("url", url);
            intent.putExtra("cifrado", cifrado);
            startActivity(intent);
        }
        else{
            if(rangoN.equals("ayudante")) {
                Intent intent = new Intent(getApplicationContext(), PanelayudanteActivity.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("cuenta", cuenta);
                intent.putExtra("url", url);
                intent.putExtra("cifrado", cifrado);
                startActivity(intent);
            }
            else{
                if(rangoN.equals("director")) {
                    Intent intent = new Intent(getApplicationContext(), PanelDirectorActivity.class);
                    intent.putExtra("usuario", usuario);
                    intent.putExtra("cuenta", cuenta);
                    intent.putExtra("url", url);
                    intent.putExtra("cifrado", cifrado);
                    startActivity(intent);
                }
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