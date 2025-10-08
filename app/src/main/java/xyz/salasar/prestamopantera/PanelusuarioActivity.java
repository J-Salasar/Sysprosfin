package xyz.salasar.prestamopantera;

import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;

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
import java.time.format.DateTimeFormatter;
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

public class PanelusuarioActivity extends AppCompatActivity {
    private String usuario, cuentaN, tipoN, rangoN,url,cifrado;
    private TextView cuenta, empresa, credito, congelado, deuda, intereses,tipo,fecha,notinada;
    private ImageButton volver;
    private Button deposito, retiro, transferencia, recargar;
    private ImageButton buscar;
    private String nombre, apellido, correodestino, correoorigen, claveorigen;
    private int codigo;
    private Session session;
    private Message message;
    private Thread hilo;
    private ListView historial;
    private ConstraintLayout panelCuenta,panelVolver,panelCredito,panelUsuario,panelCongelado,panelDeuda,panelNotificaciones;
    private EditText mes, tiempo;
    private String fechaI,fechaF;
    private LinearLayout panelEmpresa,datosCuentaPersonal,panelfuncionesPrincipales;
    private SimpleDateFormat formatoEntrada=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoSalida=new SimpleDateFormat("yyyy-MM-dd");
    private String fechaC;
    private SimpleDateFormat formatoSalida2=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoEntrada2=new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<String> referenciaN,empresaON,tipoON,nombreON,apellidoON,cuentaON,cantidadN,descripcionON,descripcionDN,fechaN,nombreDN,apellidoDN,cuentaDN,empresaDN,tipoDN;
    private ArrayList<HistorialAdaptador> datoshistorial;
    private TextView salir;
    final String[] b = {"0"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_panelusuario);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario=getIntent().getStringExtra("usuario");
        cuentaN=getIntent().getStringExtra("cuenta");
        url=getIntent().getStringExtra("url");
        cifrado=getIntent().getStringExtra("cifrado");
        cuenta=findViewById(R.id.cuenta50);
        empresa=findViewById(R.id.empresa50);
        credito=findViewById(R.id.credito50);
        congelado=findViewById(R.id.congelado50);
        deuda=findViewById(R.id.deuda50);
        intereses=findViewById(R.id.intereses50);
        tipo=findViewById(R.id.tipo50);
        panelNotificaciones=findViewById(R.id.barranotificacion50);
        volver=findViewById(R.id.volver50);
        deposito=findViewById(R.id.deposito50);
        panelUsuario=findViewById(R.id.panelUsuario50);
        retiro=findViewById(R.id.retiro50);
        notinada=findViewById(R.id.notificaNada50);
        panelfuncionesPrincipales=findViewById(R.id.panelfuncionesPrincipales);
        transferencia=findViewById(R.id.transferencia50);
        recargar=findViewById(R.id.recargar50);
        buscar=findViewById(R.id.buscar50);
        historial=findViewById(R.id.historial50);
        salir=findViewById(R.id.salirEmpresa50);
        mes=findViewById(R.id.mes50);
        tiempo=findViewById(R.id.tiempo50);
        fecha=findViewById(R.id.fechaLimite50);
        panelCuenta=findViewById(R.id.panelCuenta50);
        panelVolver=findViewById(R.id.panelVolver50);
        panelCredito=findViewById(R.id.panelCredito50);
        panelCongelado=findViewById(R.id.panelCongelado50);
        panelDeuda=findViewById(R.id.panelDeuda50);
        panelEmpresa=findViewById(R.id.panelEmpresaN50);
        datosCuentaPersonal=findViewById(R.id.datoscuenta50);
        panelfuncionesPrincipales.removeView(notinada);
        listaHistorial();
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(empresa.getText().toString().equals("personal")){
                    Toast.makeText(getApplicationContext(),"No puedes salirte de tu cuenta de ahorro",Toast.LENGTH_LONG).show();
                }
                else{
                    salirdialogo();
                }
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        deposito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipoN.equals("ahorro")){
                    Toast.makeText(getApplicationContext(),"Proximamente",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(tipoN.equals("prestamos")){
                        Toast.makeText(getApplicationContext(),"Proximamente",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No permitido", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        retiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipoN.equals("ahorro")){
                    /*Intent intent=new Intent(getApplicationContext(),RetiroActivity.class);
                    intent.putExtra("usuario",usuario);
                    intent.putExtra("cuenta",cuentaN);
                    intent.putExtra("url",url);
                    intent.putExtra("rango",rangoN);
                    intent.putExtra("cifrado", cifrado);
                    startActivity(intent);*/
                    Toast.makeText(getApplicationContext(),"Proximamente",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(tipoN.equals("prestamos")){
                        Intent intent=new Intent(getApplicationContext(),SolicitudPrestamoActivity.class);
                        intent.putExtra("usuario",usuario);
                        intent.putExtra("cuenta",cuentaN);
                        intent.putExtra("url",url);
                        intent.putExtra("rango",rangoN);
                        intent.putExtra("cifrado", cifrado);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No permitido", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        transferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipoN.equals("ahorro")){
                    codigoCorfirmacion();
                }
                else{
                    if(tipoN.equals("prestamos")){
                        codigoCorfirmacion();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No permitido",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mes.setText("");
                tiempo.setText("");
                listaHistorial();
                Toast.makeText(getApplicationContext(),"Historial actualizado",Toast.LENGTH_LONG).show();
            }
        });
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mes.getText().toString().equals("")||tiempo.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Coloque una fecha validad",Toast.LENGTH_LONG).show();
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
        datosCuenta();
    }
    private void buscarFecha(){
        final String[] a = {"1"};
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
                            a[0] ="0";
                            if(b[0].equals("0")) {
                                panelfuncionesPrincipales.addView(notinada);
                                panelUsuario.removeView(historial);
                                b[0] = "1";
                            }
                            break;
                        }
                    }
                    if(a[0].equals("1")) {
                        llenarLista();
                        if(b[0].equals("1")) {
                            panelfuncionesPrincipales.removeView(notinada);
                            panelUsuario.addView(historial);
                            b[0] = "0";
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 037:"+error.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 038:"+error.toString(), Toast.LENGTH_LONG).show();
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
    private void salirdialogo(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Salir del empresa");
        builder.setMessage("¿Estas seguro de querer salir de la empresa "+empresa.getText().toString()+"?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                desactivaFunciones();
                salirCliente();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    private void desactivaFunciones(){
        deposito.setVisibility(View.INVISIBLE);
        retiro.setVisibility(View.INVISIBLE);
        transferencia.setVisibility(View.INVISIBLE);
        recargar.setVisibility(View.INVISIBLE);
    }
    private void activaFunciones(){
        deposito.setVisibility(View.VISIBLE);
        retiro.setVisibility(View.VISIBLE);
        transferencia.setVisibility(View.VISIBLE);
        recargar.setVisibility(View.VISIBLE);
    }
    private void salirCliente(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Saliendo de la empresa",Toast.LENGTH_SHORT).show();
                        volver();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("negado")) {
                            Toast.makeText(getApplicationContext(), "No puedes salir de la empresa porque tienes deuda", Toast.LENGTH_LONG).show();
                            activaFunciones();
                        }
                        else {
                            if(confirmacion.getString("mensaje").equals("tiene dinero")){
                                Toast.makeText(getApplicationContext(), "No puedes salir de la empresa porque tienes dinero", Toast.LENGTH_LONG).show();
                                activaFunciones();
                            }
                            else {
                                if(confirmacion.getString("mensaje").equals("es trabajador")){
                                    Toast.makeText(getApplicationContext(), "No puedes salir porque trabajadas aqui, todavia", Toast.LENGTH_LONG).show();
                                    activaFunciones();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Error al actualizar la salida", Toast.LENGTH_LONG).show();
                                    activaFunciones();
                                }
                            }
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 039:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 040:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta", cuentaN);
                params.put("empresa",empresa.getText().toString());
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "37");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void listaHistorial(){
        final String[] a={"1"};
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
                        else {
                            a[0] = "0";
                            if(b[0].equals("0")) {
                                panelfuncionesPrincipales.addView(notinada);
                                panelUsuario.removeView(historial);
                                b[0] = "1";
                            }
                            break;
                        }
                    }
                    if(a[0].equals("1")) {
                        llenarLista();
                        if(b[0].equals("1")) {
                            panelfuncionesPrincipales.removeView(notinada);
                            panelUsuario.addView(historial);
                            b[0] = "0";
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 041:"+error.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 042:"+error.toString(), Toast.LENGTH_LONG).show();
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
        historial.setAdapter(new AdaptadorHistorial(getApplicationContext(),referenciaN,empresaON,tipoON,nombreON,apellidoON,cuentaON,cantidadN,descripcionON,descripcionDN,fechaN,nombreDN,apellidoDN,cuentaDN,empresaDN,tipoDN,cuentaN));
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
                        if(rangoN.equals("usuario")&&tipoN.equals("ahorro")){
                            fecha.setText("Fecha de creacion: "+fechaC);
                            datosCuentaPersonal.removeView(panelEmpresa);

                        }
                        else{
                            if(rangoN.equals("usuario")&&tipoN.equals("prestamos")){
                                fecha.setText("Fecha limite de pago: "+fechaC);
                                deposito.setText("pago de prestamo");
                                retiro.setText("solicitud de prestamo");
                            }
                        }
                        if(Double.parseDouble(confirmacion.getString("congelado"))<0.01){
                            datosCuentaPersonal.removeView(panelCongelado);
                        }
                        if(Double.parseDouble(confirmacion.getString("deuda"))<0.01){
                            datosCuentaPersonal.removeView(panelDeuda);
                            if(rangoN.equals("usuario")&&tipoN.equals("prestamos")) {
                                datosCuentaPersonal.removeView(fecha);
                            }
                        }
                        if(Double.parseDouble(confirmacion.getString("intereses"))<0.01){
                            datosCuentaPersonal.removeView(intereses);
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al conseguir la cuenta",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 043:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 044:"+error.toString(),Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(),"Error 045:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 046:"+error.toString(),Toast.LENGTH_LONG).show();
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