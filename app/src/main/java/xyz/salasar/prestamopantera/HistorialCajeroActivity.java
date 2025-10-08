package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
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

import xyz.salasar.prestamopantera.Adaptador.AdaptadorHistorial;
import xyz.salasar.prestamopantera.configuracion.HistorialAdaptador;

public class HistorialCajeroActivity extends AppCompatActivity {
    private String usuarioP, cuentaP, empresaP, cuenta,url,cifrado,rangoN,deudaD,interesesD;
    private ImageButton volver,recargar,buscar;
    private EditText fechaIncial,fechaFinal;
    private TextView recaudar,nombre,notificacionNada;
    private ListView historialCajero;
    private String fechaI,fechaF;
    private SimpleDateFormat formatoEntrada=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoSalida=new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<HistorialAdaptador> datoshistorial;
    private double totalEfectivo;
    private ConstraintLayout panelhistorialCajero;
    private String[] b={"0"};
    private ArrayList<String> referenciaN,empresaON,tipoON,nombreON,apellidoON,cuentaON,cantidadN,descripcionON,descripcionDN,fechaN,nombreDN,apellidoDN,cuentaDN,empresaDN,tipoDN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_historial_cajero);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuarioP=getIntent().getStringExtra("usuarioP");
        cuentaP=getIntent().getStringExtra("cuentaP");
        empresaP=getIntent().getStringExtra("empresaP");
        cuenta=getIntent().getStringExtra("cuenta");
        url=getIntent().getStringExtra("url");
        rangoN=getIntent().getStringExtra("rango");
        cifrado=getIntent().getStringExtra("cifrado");
        deudaD=getIntent().getStringExtra("deuda");
        interesesD=getIntent().getStringExtra("intereses");
        volver=findViewById(R.id.volver2222);
        recargar=findViewById(R.id.recargar2222);
        buscar=findViewById(R.id.buscar2222);
        fechaIncial=findViewById(R.id.fechaIncial2222);
        fechaFinal=findViewById(R.id.fechaFinal2222);
        recaudar=findViewById(R.id.recaudar2222);
        historialCajero=findViewById(R.id.historialCajero2222);
        nombre=findViewById(R.id.nombre2222);
        notificacionNada=findViewById(R.id.notificacionNada2222);
        panelhistorialCajero=findViewById(R.id.panelhistorialCajero2222);
        panelhistorialCajero.removeView(notificacionNada);
        nombre.setText(getIntent().getStringExtra("nombre")+" "+getIntent().getStringExtra("apellido"));
        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fechaIncial.setText("");
                fechaFinal.setText("");
                Toast.makeText(getApplicationContext(),"Historial actualizado",Toast.LENGTH_SHORT).show();
                historialcajeroLista();
            }
        });
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fechaIncial.getText().toString().equals("")||fechaFinal.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Coloque una fecha valida",Toast.LENGTH_LONG).show();
                }
                else {
                    try{
                        Date fecha=formatoEntrada.parse(fechaIncial.getText().toString());
                        fechaI=formatoSalida.format(fecha);
                        fecha=formatoEntrada.parse(fechaFinal.getText().toString());
                        fechaF=formatoSalida.format(fecha);
                        buscarFecha();
                    }
                    catch (ParseException e){
                        Toast.makeText(getApplicationContext(),"Coloque una fecha validad",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        historialcajeroLista();
    }
    private void historialcajeroLista(){
        String[] a={"1"};
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    totalEfectivo=0.0;
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
                            if(rowcuenta.getString("descripcionO").equals("ejecutastes un pago de prestamo")) {
                                totalEfectivo += Double.parseDouble(rowcuenta.getString("cantidad"));
                            }
                            datoshistorial.add(historialInformacion);
                        }
                        else{
                            a[0] ="0";
                            if(b[0].equals("0")) {
                                panelhistorialCajero.addView(notificacionNada);
                                panelhistorialCajero.removeView(historialCajero);
                                b[0] = "1";
                            }
                            break;
                        }
                    }
                    if(a[0].equals("1")) {
                        DecimalFormat formato = new DecimalFormat("#,##0.00");
                        String formateado = formato.format(totalEfectivo);
                        recaudar.setText("L. " + formateado);
                        llenarLista();
                        if(b[0].equals("1")) {
                            panelhistorialCajero.removeView(notificacionNada);
                            panelhistorialCajero.addView(historialCajero);
                            b[0] = "0";
                        }
                    }
                }
                catch (Throwable error){
                    recaudar.setText("L. 0.00");
                    Toast.makeText(getApplicationContext(),"Error 021:"+error.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 022:"+error.toString(), Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("cuenta", cuenta);
                parametros.put("cifrado", cifrado);
                parametros.put("codigoLlave", "44");
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void buscarFecha(){
        String[] a={"1"};
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    totalEfectivo=0.0;
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
                            totalEfectivo+=Double.parseDouble(rowcuenta.getString("cantidad"));
                            datoshistorial.add(historialInformacion);
                        }
                        else{
                            a[0] ="0";
                            if(b[0].equals("0")) {
                                panelhistorialCajero.addView(notificacionNada);
                                panelhistorialCajero.removeView(historialCajero);
                                b[0] = "1";
                            }
                            break;
                        }
                    }
                    if(a[0].equals("1")) {
                        DecimalFormat formato = new DecimalFormat("#,##0.00");
                        String formateado = formato.format(totalEfectivo);
                        recaudar.setText("L. " + formateado);
                        llenarLista();
                        Toast.makeText(getApplicationContext(), "Lista cargada", Toast.LENGTH_SHORT).show();
                        if(b[0].equals("1")) {
                            panelhistorialCajero.removeView(notificacionNada);
                            panelhistorialCajero.addView(historialCajero);
                            b[0] = "0";
                        }
                    }
                }
                catch (Throwable error){
                    recaudar.setText("L. 0.00");
                    Toast.makeText(getApplicationContext(),"Error 023:"+error.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 024:"+error.toString(), Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("cuenta", cuenta);
                parametros.put("fechaI", fechaI);
                parametros.put("fechaF", fechaF);
                parametros.put("cifrado", cifrado);
                parametros.put("codigoLlave", "45");
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
        historialCajero.setAdapter(new AdaptadorHistorial(getApplicationContext(),referenciaN,empresaON,tipoON,nombreON,apellidoON,cuentaON,cantidadN,descripcionON,descripcionDN,fechaN,nombreDN,apellidoDN,cuentaDN,empresaDN,tipoDN,cuenta));
    }
    public void volver(){
        Intent intent = new Intent(getApplicationContext(), UsuariosAgregadosActivity.class);
        intent.putExtra("usuario",usuarioP);
        intent.putExtra("cuenta",cuentaP);
        intent.putExtra("empresa",empresaP);
        intent.putExtra("url",url);
        intent.putExtra("rango",rangoN);
        intent.putExtra("cifrado",cifrado);
        intent.putExtra("deuda",deudaD);
        intent.putExtra("intereses",interesesD);
        startActivity(intent);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            volver();
            return true;
        }
        return false;
    }
}