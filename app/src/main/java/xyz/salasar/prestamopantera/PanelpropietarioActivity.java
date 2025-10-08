package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import xyz.salasar.prestamopantera.Adaptador.AdaptadorHistorial;
import xyz.salasar.prestamopantera.configuracion.HistorialAdaptador;

public class PanelpropietarioActivity extends AppCompatActivity {
    private String usuario, cuentaN, tipoN, rangoN,url,cifrado;
    private TextView cuenta, empresa, credito, congelado, deuda, intereses,tipo,mes,tiempo,fecha;
    private Button transferir, notificacion,recargar,listaUsuarios, pagarPrestamo, pagoPrestamo;
    private String nombre, apellido, correodestino, correoorigen, claveorigen;
    private int codigo;
    private Session session;
    private Message message;
    private Thread hilo;
    private String fechaI,fechaF;
    private ImageButton buscar,volver;
    private SimpleDateFormat formatoEntrada=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoSalida=new SimpleDateFormat("yyyy-MM-dd");
    private String fechaC;
    private SimpleDateFormat formatoSalida2=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoEntrada2=new SimpleDateFormat("yyyy-MM-dd");
    private ListView listaHistorial;
    private ArrayList<String> referenciaN,empresaON,tipoON,nombreON,apellidoON,cuentaON,cantidadN,descripcionON,descripcionDN,fechaN,nombreDN,apellidoDN,cuentaDN,empresaDN,tipoDN;
    private ArrayList<HistorialAdaptador> datoshistorial;
    private LinearLayout paneldatosSaldos;
    private ConstraintLayout panelCongelado;
    private ConstraintLayout panelDeuda,panelprincipalOficial;
    private TextView notificacionNada;
    private String[] b={"0"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_panelpropietario);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario=getIntent().getStringExtra("usuario");
        cuentaN=getIntent().getStringExtra("cuenta");
        url=getIntent().getStringExtra("url");
        cifrado=getIntent().getStringExtra("cifrado");
        cuenta=findViewById(R.id.cuenta51);
        empresa=findViewById(R.id.empresa51);
        credito=findViewById(R.id.credito51);
        congelado=findViewById(R.id.congelado51);
        deuda=findViewById(R.id.deuda51);
        intereses=findViewById(R.id.intereses51);
        tipo=findViewById(R.id.tipo51);
        volver=findViewById(R.id.volver51);
        transferir=findViewById(R.id.transferir51);
        notificacion=findViewById(R.id.notificacion51);
        listaHistorial=findViewById(R.id.historial51);
        recargar=findViewById(R.id.recargar51);
        listaUsuarios=findViewById(R.id.listaclientes51);
        mes=findViewById(R.id.mes51);
        tiempo=findViewById(R.id.tiempo51);
        buscar=findViewById(R.id.buscar51);
        fecha=findViewById(R.id.fechaCreacion51);
        pagarPrestamo=findViewById(R.id.pagoPrestamos51);
        paneldatosSaldos=findViewById(R.id.paneldatosSaldos51);
        panelCongelado=findViewById(R.id.panelCongelado51);
        panelDeuda=findViewById(R.id.panelDeuda51);
        panelprincipalOficial=findViewById(R.id.panelpropietarioOficial51);
        notificacionNada=findViewById(R.id.notificacionNada51);
        pagoPrestamo=findViewById(R.id.pagoPrestamo51);
        panelprincipalOficial.removeView(notificacionNada);
        pagoPrestamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Proximamente",Toast.LENGTH_SHORT).show();
            }
        });
        pagarPrestamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),PagarPrestamoActivity.class);
                intent.putExtra("usuario",usuario);
                intent.putExtra("cuenta",cuentaN);
                intent.putExtra("empresa",empresa.getText().toString());
                intent.putExtra("rango",rangoN);
                intent.putExtra("url",url);
                intent.putExtra("cifrado", cifrado);
                startActivity(intent);
            }
        });
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mes.getText().toString().equals("")||tiempo.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Coloque una fecha valida",Toast.LENGTH_LONG).show();
                }
                else {
                    try{
                        Date fecha=formatoEntrada.parse(mes.getText().toString());
                        fechaI=formatoSalida.format(fecha);
                        fecha=formatoEntrada.parse(tiempo.getText().toString());
                        fechaF=formatoSalida.format(fecha);
                        buscarFecha();
                    }
                    catch (ParseException e){
                        Toast.makeText(getApplicationContext(),"Coloque una fecha validad",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        listaUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),UsuariosAgregadosActivity.class);
                intent.putExtra("usuario",usuario);
                intent.putExtra("cuenta",cuentaN);
                intent.putExtra("empresa",empresa.getText().toString());
                intent.putExtra("url",url);
                intent.putExtra("intereses", "0");
                intent.putExtra("deuda", "0");
                intent.putExtra("rango",rangoN);
                intent.putExtra("cifrado", cifrado);
                startActivity(intent);
            }
        });
        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mes.setText("");
                tiempo.setText("");
                historial();
                Toast.makeText(getApplicationContext(),"Historial actualizado",Toast.LENGTH_SHORT).show();
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        transferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoCorfirmacion();
            }
        });
        notificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),NotificacionActivity.class);
                intent.putExtra("usuario",usuario);
                intent.putExtra("cuenta",cuentaN);
                intent.putExtra("empresa",empresa.getText().toString());
                intent.putExtra("url",url);
                intent.putExtra("rango",rangoN);
                intent.putExtra("cifrado", cifrado);
                startActivity(intent);
            }
        });
        datosCuenta();
        historial();
    }
    private void buscarFecha(){
        String[] a={"1"};
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray cuentasarray=jsonObject.getJSONArray("aprobacion");
                    HistorialAdaptador historialInformacion=null;
                    datoshistorial=new ArrayList<HistorialAdaptador>();
                    for(int i=0;i<cuentasarray.length();i++){
                        JSONObject rowcuenta=cuentasarray.getJSONObject(i);
                        if(rowcuenta.getString("mensaje").equals("aprobado")) {
                            historialInformacion = new HistorialAdaptador(
                                    rowcuenta.getString("referencia"),
                                    rowcuenta.getString("empresaO"),
                                    rowcuenta.getString("tipoO"),
                                    rowcuenta.getString("nombreO"),
                                    rowcuenta.getString("apellidoO"),
                                    rowcuenta.getString("cuentaO"),
                                    rowcuenta.getString("cantidad"),
                                    rowcuenta.getString("descripcionO"),
                                    rowcuenta.getString("descripcionD"),
                                    rowcuenta.getString("fecha"),
                                    rowcuenta.getString("nombreD"),
                                    rowcuenta.getString("apellidoD"),
                                    rowcuenta.getString("cuentaD"),
                                    rowcuenta.getString("empresaD"),
                                    rowcuenta.getString("tipoD")
                            );
                            datoshistorial.add(historialInformacion);
                        }
                        else{
                            a[0]="0";
                            if(b[0].equals("0")){
                                panelprincipalOficial.addView(notificacionNada);
                                panelprincipalOficial.removeView(listaHistorial);
                                b[0]="1";
                            }
                            break;
                        }
                    }
                    if(a[0].equals("1")) {
                        llenarLista();
                        Toast.makeText(getApplicationContext(), "Lista cargada", Toast.LENGTH_SHORT).show();
                        if(b[0].equals("1")){
                            panelprincipalOficial.addView(listaHistorial);
                            panelprincipalOficial.removeView(notificacionNada);
                            b[0]="0";
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 029:"+error.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 030:"+error.toString(), Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("cuenta", cuentaN);
                parametros.put("fechaI", fechaI);
                parametros.put("fechaF", fechaF);
                parametros.put("cifrado", cifrado);
                parametros.put("codigoLlave", "38");
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void historial(){
        String[] a={"1"};
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray cuentasarray=jsonObject.getJSONArray("aprobacion");
                    HistorialAdaptador historialInformacion=null;
                    datoshistorial=new ArrayList<HistorialAdaptador>();
                    for(int i=0;i<cuentasarray.length();i++){
                        JSONObject rowcuenta=cuentasarray.getJSONObject(i);
                        if(rowcuenta.getString("mensaje").equals("aprobado")) {
                            historialInformacion = new HistorialAdaptador(
                                    rowcuenta.getString("referencia"),
                                    rowcuenta.getString("empresaO"),
                                    rowcuenta.getString("tipoO"),
                                    rowcuenta.getString("nombreO"),
                                    rowcuenta.getString("apellidoO"),
                                    rowcuenta.getString("cuentaO"),
                                    rowcuenta.getString("cantidad"),
                                    rowcuenta.getString("descripcionO"),
                                    rowcuenta.getString("descripcionD"),
                                    rowcuenta.getString("fecha"),
                                    rowcuenta.getString("nombreD"),
                                    rowcuenta.getString("apellidoD"),
                                    rowcuenta.getString("cuentaD"),
                                    rowcuenta.getString("empresaD"),
                                    rowcuenta.getString("tipoD")
                            );
                            datoshistorial.add(historialInformacion);
                        }
                        else{
                            a[0]="0";
                            if(b[0].equals("0")){
                                panelprincipalOficial.addView(notificacionNada);
                                panelprincipalOficial.removeView(listaHistorial);
                                b[0]="1";
                            }
                            break;
                        }
                    }
                    if(a[0].equals("1")) {
                        llenarLista();
                        if(b[0].equals("1")){
                            panelprincipalOficial.addView(listaHistorial);
                            panelprincipalOficial.removeView(notificacionNada);
                            b[0]="0";
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 031:"+error.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 032:"+error.toString(), Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("cuenta", cuentaN);
                parametros.put("cifrado", cifrado);
                parametros.put("codigoLlave", "16");
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void llenarLista(){
        referenciaN=new ArrayList<String>();
        empresaON=new ArrayList<String>();
        tipoON=new ArrayList<String>();
        nombreON=new ArrayList<String>();
        apellidoON=new ArrayList<String>();
        cuentaON=new ArrayList<String>();
        cantidadN=new ArrayList<String>();
        descripcionON=new ArrayList<String>();
        descripcionDN=new ArrayList<String>();
        fechaN=new ArrayList<String>();
        nombreDN=new ArrayList<String>();
        apellidoDN=new ArrayList<String>();
        cuentaDN=new ArrayList<String>();
        empresaDN=new ArrayList<String>();
        tipoDN=new ArrayList<String>();
        for(int i=0;i<datoshistorial.size();i++){
            referenciaN.add(datoshistorial.get(i).getReferencia());
            empresaON.add(datoshistorial.get(i).getEmpresaON());
            tipoON.add(datoshistorial.get(i).getTipoON());
            nombreON.add(datoshistorial.get(i).getNombreON());
            apellidoON.add(datoshistorial.get(i).getApellidoON());
            cuentaON.add(datoshistorial.get(i).getCuentaON());
            cantidadN.add(datoshistorial.get(i).getCantidad());
            descripcionON.add(datoshistorial.get(i).getDescripcionNO());
            descripcionDN.add(datoshistorial.get(i).getDescripcionND());
            fechaN.add(datoshistorial.get(i).getFecha());
            nombreDN.add(datoshistorial.get(i).getNombreDN());
            apellidoDN.add(datoshistorial.get(i).getApellidoDN());
            cuentaDN.add(datoshistorial.get(i).getCuentaDN());
            empresaDN.add(datoshistorial.get(i).getEmpresaDN());
            tipoDN.add(datoshistorial.get(i).getTipoDN());
        }
        listaHistorial.setAdapter(new AdaptadorHistorial(getApplicationContext(),referenciaN,empresaON,tipoON,nombreON,apellidoON,cuentaON,cantidadN,descripcionON,descripcionDN,fechaN,nombreDN,apellidoDN,cuentaDN,empresaDN,tipoDN,cuentaN));
    }
    private void correoCodigo(){
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
                    return new PasswordAuthentication(correoorigen,claveorigen);
                }
            });
            if(session!=null){
                message=new MimeMessage(session);
                message.setFrom(new InternetAddress(correoorigen));
                message.setSubject("Transferencia de dinero");
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correodestino));
                message.setContent(
                        "<body>"+
                                "<p>Estimado/a: "+nombre+" "+apellido+"</p>"+
                                "<p>Este es tu codigo de verificacion para transferir dinero.</p>"+
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
                ejecutarTransferencia();
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error en el envio del correo",Toast.LENGTH_LONG).show();
        }
    }
    private void ejecutarTransferencia(){
        Intent intent=new Intent(getApplicationContext(), TransferenciaActivity.class);
        intent.putExtra("usuario",usuario);
        intent.putExtra("cuenta",cuentaN);
        intent.putExtra("codigo",String.valueOf(codigo));
        intent.putExtra("nombre",nombre);
        intent.putExtra("apellido",apellido);
        intent.putExtra("tipo",tipoN);
        intent.putExtra("rango",rangoN);
        intent.putExtra("url",url);
        intent.putExtra("cifrado", cifrado);
        startActivity(intent);
    }
    private void codigoCorfirmacion(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        nombre=confirmacion.getString("nombre");
                        apellido=confirmacion.getString("apellido");
                        correodestino=confirmacion.getString("correo");
                        correoorigen=confirmacion.getString("correo_origen");
                        claveorigen=confirmacion.getString("clave_correo_origen");
                        correoCodigo();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al enviar el correo",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 033:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 034:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "14");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void datosCuenta(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        tipo.setText("Cuenta de "+confirmacion.getString("tipo"));
                        cuenta.setText("Cuenta: "+cuentaN);
                        empresa.setText(confirmacion.getString("empresa"));
                        DecimalFormat formato = new DecimalFormat("#,##0.00");
                        String formateado=formato.format(Double.parseDouble(confirmacion.getString("credito")));
                        credito.setText("L. "+formateado);
                        formateado=formato.format(Double.parseDouble(confirmacion.getString("congelado")));
                        congelado.setText("L. "+formateado);
                        formateado=formato.format(Double.parseDouble(confirmacion.getString("deuda")));
                        deuda.setText("L. "+formateado);
                        formateado=formato.format(Double.parseDouble(confirmacion.getString("intereses")));
                        Double porciento=Double.parseDouble(confirmacion.getString("porcentaje"))*100;
                        DecimalFormat formato2 = new DecimalFormat("#,##0.##");
                        String formateado2=formato2.format(porciento);
                        intereses.setText("Intereses "+formateado2+"%: L. "+formateado);
                        tipoN=confirmacion.getString("tipo");
                        rangoN=confirmacion.getString("rango");
                        try{
                            Date fechaf=formatoEntrada2.parse(confirmacion.getString("fecha_creacion"));
                            fechaC=formatoSalida2.format(fechaf);
                        }
                        catch (ParseException e){
                            Toast.makeText(getApplicationContext(),"Fecha no valida",Toast.LENGTH_LONG).show();
                        }
                        fecha.setText("Fecha de creacion: "+fechaC);
                        if(Double.parseDouble(confirmacion.getString("congelado"))<0.01){
                            paneldatosSaldos.removeView(panelCongelado);
                        }
                        if(Double.parseDouble(confirmacion.getString("deuda"))<0.01){
                            paneldatosSaldos.removeView(panelDeuda);
                        }
                        if(Double.parseDouble(confirmacion.getString("intereses"))<0.01){
                            paneldatosSaldos.removeView(intereses);
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al conseguir la cuenta",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 035:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 036:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("cuenta", cuentaN);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "15");
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