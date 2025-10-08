package xyz.salasar.prestamopantera;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerfilClienteActivity extends AppCompatActivity {
    private String usuarioP,cuentaP,empresaP,cuentaC,url,cifrado,rangoN;
    private TextView nombreC,cuenta;
    private EditText credito,deuda,intereses,porcentaje,fecha;
    private ImageButton volver;
    private Spinner rango;
    private TextView bcancelar1,bcancelar2,bcancelar3,bcancelar4;
    private TextView confirmarCredito,confirmarPorcentaje,confirmarRango,confirmarDeuda,confirmarIntereses,confirmarFecha;
    private TextView rangotxt;
    private String txtrango,txttelefono;
    private CheckBox estadocuenta,recordatorio;
    private Button eliminar, whatsapp;
    private String fechaC,deudaD,interesesD,recordatorioestadocuenta;
    private SimpleDateFormat formatoEntrada=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoSalida=new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat formatoEntrada2=new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat formatoSalida2=new SimpleDateFormat("dd/MM/yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_perfil_cliente);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuarioP=getIntent().getStringExtra("usuarioP");
        cuentaP=getIntent().getStringExtra("cuentaP");
        empresaP=getIntent().getStringExtra("empresaP");
        cuentaC=getIntent().getStringExtra("cuentaC");
        url=getIntent().getStringExtra("url");
        rangoN=getIntent().getStringExtra("rango");
        cifrado=getIntent().getStringExtra("cifrado");
        deudaD=getIntent().getStringExtra("deuda");
        interesesD=getIntent().getStringExtra("intereses");
        nombreC=findViewById(R.id.nombre77);
        cuenta=findViewById(R.id.cuenta77);
        credito=findViewById(R.id.credito77);
        deuda=findViewById(R.id.deuda77);
        intereses=findViewById(R.id.intereses77);
        porcentaje=findViewById(R.id.porcentaje77);
        volver=findViewById(R.id.volver77);
        rango=findViewById(R.id.rango77);
        rangotxt=findViewById(R.id.txtrango77);
        bcancelar1=findViewById(R.id.cancelar177);
        bcancelar2=findViewById(R.id.cancelar277);
        bcancelar3=findViewById(R.id.cancelar377);
        bcancelar4=findViewById(R.id.cancelar477);
        confirmarCredito=findViewById(R.id.confirmarCredito77);
        confirmarPorcentaje=findViewById(R.id.confirmarPorcentaje77);
        confirmarRango=findViewById(R.id.confirmarRango77);
        confirmarDeuda=findViewById(R.id.confirmarDeuda77);
        confirmarIntereses=findViewById(R.id.confirmarIntereses77);
        eliminar=findViewById(R.id.eliminar77);
        fecha=findViewById(R.id.fecha77);
        whatsapp=findViewById(R.id.enviawhatsapp77);
        confirmarFecha=findViewById(R.id.ejecutarFecha77);
        estadocuenta=findViewById(R.id.estadocuenta77);
        recordatorio=findViewById(R.id.recordatoriopago77);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estadocuenta.isChecked()){
                    if(recordatorio.isChecked()){
                        Toast.makeText(getApplicationContext(),"Solo se puede enviar un tipo de mensaje",Toast.LENGTH_LONG).show();
                    }
                    else{
                        recordatorioestadocuenta="\n" +
                                "=======================\n" +
                                "     "+empresaP+"\n" +
                                "=======================\n" +
                                "Estado de cuenta\n" +
                                "=======================\n" +
                                nombreC.getText().toString()+"\n" +
                                "Cuenta: "+cuentaC+"\n" +
                                "=======================\n" +
                                "Fecha limite de pago: "+fecha.getHint().toString()+"\n" +
                                "=======================\n" +
                                "Deuda: L. "+deuda.getHint().toString()+"\n" +
                                "Intereses: L. "+intereses.getHint().toString()+"\n" +
                                "=======================";
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        String uri="whatsapp://send?phone="+txttelefono+"&text="+recordatorioestadocuenta;
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                }
                else{
                    if(recordatorio.isChecked()){
                        recordatorioestadocuenta="\n" +
                                "=======================\n" +
                                "     "+empresaP+"\n" +
                                "=======================\n" +
                                "Fecha limite de pago: "+fecha.getHint().toString()+"\n" +
                                "=======================\n" +
                                "Buen dia estimado/a\n" +
                                nombreC.getText().toString()+"\n" +
                                "=======================\n" +
                                "El motivo de este mensaje es para recordarle que realice el pago antes de la fecha limite. \uD83E\uDEE1\n" +
                                "=======================";
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        String uri="whatsapp://send?phone="+txttelefono+"&text="+recordatorioestadocuenta;
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Debes elegir un tipo de mensaje",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        confirmarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Date fechaf=formatoEntrada.parse(fecha.getText().toString());
                    fechaC=formatoSalida.format(fechaf);
                }
                catch (ParseException e){
                    Toast.makeText(getApplicationContext(),"Fecha no valida",Toast.LENGTH_LONG).show();
                }
                cambiarFecha(fechaC);
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eiminarDialogo();
            }
        });
        confirmarIntereses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intereses.getText().toString().equals("") || Double.parseDouble(intereses.getText().toString())<0.01){
                    Toast.makeText(getApplicationContext(),"Ingresa alguna cantidad significativa",Toast.LENGTH_SHORT).show();
                    intereses.setText("");
                }
                else {
                    confirmarIntereses("1");
                }
            }
        });
        confirmarDeuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deuda.getText().toString().equals("") || Double.parseDouble(deuda.getText().toString())<0.01){
                    Toast.makeText(getApplicationContext(),"Ingresa alguna cantidad significativa",Toast.LENGTH_SHORT).show();
                    deuda.setText("");
                }
                else {
                    confirmarDeuda("1");
                }
            }
        });
        confirmarRango.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rangoN.equals("propietario")) {
                    confirmarRango();
                }
                else{
                    if(rangoN.equals("director")) {
                        if(rangotxt.getText().toString().equals("director")) {
                            Toast.makeText(getApplicationContext(), "No puedes degradarlo de rango", Toast.LENGTH_LONG).show();
                        }
                        else {
                            if(txtrango.equals("director")){
                                Toast.makeText(getApplicationContext(), "No puedes promoverlo a director", Toast.LENGTH_LONG).show();
                            }
                            else {
                                confirmarRango();
                            }
                        }
                    }
                }
            }
        });
        rango.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtrango=parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        confirmarPorcentaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(porcentaje.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Ingresa alguna cantidad",Toast.LENGTH_SHORT).show();
                }
                else {
                    confirmarPorcentaje("1");
                }
            }
        });
        confirmarCredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(credito.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Ingresa alguna cantidad",Toast.LENGTH_SHORT).show();
                }
                else{
                    confirmarCredito("1");
                }
            }
        });
        bcancelar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(credito.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Ingresa alguna cantidad",Toast.LENGTH_SHORT).show();
                }
                else{
                    confirmarCredito("2");
                }
            }
        });
        bcancelar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deuda.getText().toString().equals("") || Double.parseDouble(deuda.getText().toString())<0.01){
                    Toast.makeText(getApplicationContext(),"Ingresa alguna cantidad significativa",Toast.LENGTH_SHORT).show();
                    deuda.setText("");
                }
                else {
                    confirmarDeuda("2");
                }
            }
        });
        bcancelar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intereses.getText().toString().equals("") || Double.parseDouble(intereses.getText().toString())<0.01){
                    Toast.makeText(getApplicationContext(),"Ingresa alguna cantidad significativa",Toast.LENGTH_SHORT).show();
                    intereses.setText("");
                }
                else {
                    confirmarIntereses("2");
                }
            }
        });
        bcancelar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(porcentaje.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Ingresa alguna cantidad",Toast.LENGTH_SHORT).show();
                }
                else {
                    confirmarPorcentaje("2");
                }
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        informacion();
    }
    private void cambiarFecha(String fechaE){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Fecha actualizada",Toast.LENGTH_SHORT).show();
                        fecha.setText("");
                        informacion();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al actualizar la fecha",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 055:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 056:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta", cuentaC);
                params.put("fechaI", fechaE);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "43");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void eiminarDialogo(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Eliminar cliente");
        builder.setMessage("¿Estas seguro de eliminar a "+nombreC.getText().toString()+"?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(rangotxt.getText().toString().equals("usuario")) {
                    desactivarfunciones();
                    eliminarCliente();
                }
                else{
                    Toast.makeText(getApplicationContext(),"No puede eliminar a tu empleado",Toast.LENGTH_LONG).show();
                }
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
    private void desactivarfunciones(){
        confirmarCredito.setVisibility(View.INVISIBLE);
        confirmarPorcentaje.setVisibility(View.INVISIBLE);
        confirmarRango.setVisibility(View.INVISIBLE);
        confirmarDeuda.setVisibility(View.INVISIBLE);
        confirmarIntereses.setVisibility(View.INVISIBLE);
        bcancelar1.setVisibility(View.INVISIBLE);
        bcancelar2.setVisibility(View.INVISIBLE);
        bcancelar3.setVisibility(View.INVISIBLE);
        bcancelar4.setVisibility(View.INVISIBLE);
        eliminar.setEnabled(false);
    }
    private void activarfunciones(){
        confirmarCredito.setVisibility(View.VISIBLE);
        confirmarPorcentaje.setVisibility(View.VISIBLE);
        confirmarRango.setVisibility(View.VISIBLE);
        confirmarDeuda.setVisibility(View.VISIBLE);
        confirmarIntereses.setVisibility(View.VISIBLE);
        bcancelar1.setVisibility(View.VISIBLE);
        bcancelar2.setVisibility(View.VISIBLE);
        bcancelar3.setVisibility(View.VISIBLE);
        bcancelar4.setVisibility(View.VISIBLE);
        eliminar.setEnabled(true);
    }
    private void eliminarCliente(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Cliente eliminado",Toast.LENGTH_SHORT).show();
                        volver();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("negado")) {
                            Toast.makeText(getApplicationContext(), "No puedes eliminar al cliente porque tiene deuda", Toast.LENGTH_LONG).show();
                            activarfunciones();
                        }
                        else {
                            if(confirmacion.getString("mensaje").equals("tiene dinero")){
                                Toast.makeText(getApplicationContext(), "No puedes eliminar al cliente porque tiene dinero", Toast.LENGTH_LONG).show();
                                activarfunciones();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Error al actualizar la salida", Toast.LENGTH_LONG).show();
                                activarfunciones();
                            }
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 057:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 058:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta", cuentaC);
                params.put("empresa",empresaP);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "36");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void confirmarIntereses(String numero){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Intereses actualizado",Toast.LENGTH_SHORT).show();
                        informacion();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("negado")) {
                            Toast.makeText(getApplicationContext(), "El cliente no tiene deuda", Toast.LENGTH_LONG).show();
                            intereses.setText("");
                        }
                        else {
                            if(confirmacion.getString("mensaje").equals("insuficiente")){
                                Toast.makeText(getApplicationContext(), "El cliente no debe esos intereses", Toast.LENGTH_LONG).show();
                                intereses.setText("");
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Error al actualizar el interes", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 059:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 060:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta", cuentaC);
                params.put("cantidad", intereses.getText().toString());
                params.put("empresa",empresaP);
                params.put("cuentaO",cuentaP);
                params.put("id", numero);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "35");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void confirmarDeuda(String numero){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Deuda actualizada",Toast.LENGTH_SHORT).show();
                        informacion();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("negado")) {
                            Toast.makeText(getApplicationContext(), "El cliente no tiene credito suficiente", Toast.LENGTH_LONG).show();
                            deuda.setText("");
                        }
                        else {
                            if(confirmacion.getString("mensaje").equals("insuficiente")){
                                Toast.makeText(getApplicationContext(), "El cliente no debe esa cantidad", Toast.LENGTH_LONG).show();
                                deuda.setText("");
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Error al actualizar el credito", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 061:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 062:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta", cuentaC);
                params.put("cantidad", deuda.getText().toString());
                params.put("empresa",empresaP);
                params.put("cuentaO",cuentaP);
                params.put("id", numero);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "34");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void confirmarRango(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Rango actualizado",Toast.LENGTH_SHORT).show();
                        informacion();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al actualizar el credito",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 063:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 064:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta", cuentaC);
                params.put("rango", txtrango);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "33");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void confirmarPorcentaje(String numero){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Porcentaje actualizado",Toast.LENGTH_SHORT).show();
                        informacion();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("negado")) {
                            Toast.makeText(getApplicationContext(), "El cliente no tiene porcentaje suficiente", Toast.LENGTH_LONG).show();
                            porcentaje.setText("");
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Error al actualizar el credito",Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 065:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 066:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta", cuentaC);
                params.put("porcentaje", porcentaje.getText().toString());
                params.put("id", numero);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "32");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void confirmarCredito(String numero){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Credito actualizado",Toast.LENGTH_SHORT).show();
                        informacion();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("negado")) {
                            Toast.makeText(getApplicationContext(), "El cliente no tiene credito suficiente", Toast.LENGTH_LONG).show();
                            credito.setText("");
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Error al actualizar el credito",Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 067:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 068:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta", cuentaC);
                params.put("cantidad", credito.getText().toString());
                params.put("id", numero);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "31");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    public void informacion(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        nombreC.setText(confirmacion.getString("nombre") + " " + confirmacion.getString("apellido"));
                        cuenta.setText("No. cuenta: " + cuentaC);
                        txttelefono=confirmacion.getString("telefono");
                        DecimalFormat formato = new DecimalFormat("#,##0.00");
                        String formateado = formato.format(Double.parseDouble(confirmacion.getString("credito")));
                        credito.setHint(formateado);
                        formateado = formato.format(Double.parseDouble(confirmacion.getString("deuda")));
                        deuda.setHint(formateado);
                        formateado = formato.format(Double.parseDouble(confirmacion.getString("intereses")));
                        intereses.setHint(formateado);
                        DecimalFormat formato2 = new DecimalFormat("#,##0.##");
                        String formateado2 = formato2.format(Double.parseDouble(confirmacion.getString("porcentaje"))*100);
                        porcentaje.setHint(formateado2);
                        try{
                            Date fechaf=formatoEntrada2.parse(confirmacion.getString("fechaLimite"));
                            fechaC=formatoSalida2.format(fechaf);
                        }
                        catch (ParseException e){
                            Toast.makeText(getApplicationContext(),"Fecha no valida",Toast.LENGTH_LONG).show();
                        }
                        fecha.setHint(fechaC);
                        rangotxt.setText(confirmacion.getString("rango"));
                        if(confirmacion.getString("rango").equals("usuario")) {
                            String[] rangos={"usuario","ayudante","director"};
                            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,rangos);
                            rango.setAdapter(adapter);
                        }
                        else{
                            if(confirmacion.getString("rango").equals("ayudante")){
                                String[] rangos={"ayudante","usuario","director"};
                                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,rangos);
                                rango.setAdapter(adapter);
                            }
                            else{
                                if(confirmacion.getString("rango").equals("director")){
                                    String[] rangos={"director","ayudante","usuario"};
                                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,rangos);
                                    rango.setAdapter(adapter);
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al enviar la informacion",Toast.LENGTH_LONG).show();
                    }
                    credito.setText("");
                    deuda.setText("");
                    intereses.setText("");
                    porcentaje.setText("");
                    fecha.setText("");
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 069:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 070:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta", cuentaC);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "30");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void volver(){
        Intent intent = new Intent(getApplicationContext(), UsuariosAgregadosActivity.class);
        intent.putExtra("usuario",usuarioP);
        intent.putExtra("cuenta",cuentaP);
        intent.putExtra("empresa",empresaP);
        intent.putExtra("url",url);
        intent.putExtra("rango",rangoN);
        intent.putExtra("cifrado",cifrado);
        intent.putExtra("deuda", deudaD);
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