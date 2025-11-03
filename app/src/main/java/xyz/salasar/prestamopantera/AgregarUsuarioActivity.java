package xyz.salasar.prestamopantera;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xyz.salasar.prestamopantera.Adaptador.AdaptadorEmpresas;
import xyz.salasar.prestamopantera.Adaptador.AdaptadorSolicitud;
import xyz.salasar.prestamopantera.configuracion.AgregarUsuarioAdaptador;

public class AgregarUsuarioActivity extends AppCompatActivity {
    private String usuario,cuenta,empresa,url,cifrado,rangoN;
    private ArrayList<String> nombre,apellido,telefono,correo,usuarioN,id,identidad;
    private ArrayList<AgregarUsuarioAdaptador> datosSolicitud;
    private ListView listaSolicitud;
    private ImageButton volver,recargar;
    private String usuarioC,idC,nombreC,apellidoC;
    private ConstraintLayout panelagregarUsuario;
    private TextView notinada;
    private String[] b = {"0"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_agregar_usuario);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario=getIntent().getStringExtra("usuario");
        cuenta=getIntent().getStringExtra("cuenta");
        empresa=getIntent().getStringExtra("empresa");
        url=getIntent().getStringExtra("url");
        rangoN=getIntent().getStringExtra("rango");
        cifrado=getIntent().getStringExtra("cifrado");
        listaSolicitud=findViewById(R.id.listasolicitud999);
        volver=findViewById(R.id.volver999);
        recargar=findViewById(R.id.recargar999);
        panelagregarUsuario=findViewById(R.id.panelagregarUsuario999);
        notinada=findViewById(R.id.notificacionNada999);
        panelagregarUsuario.removeView(notinada);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaSolicitud();
                Toast.makeText(getApplicationContext(),"Lista actualizada",Toast.LENGTH_LONG).show();
            }
        });
        listaSolicitud.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long ido) {
                usuarioC=usuarioN.get(position);
                idC=id.get(position);
                nombreC=nombre.get(position);
                apellidoC=apellido.get(position);
                ventanaDialogo();
            }
        });
        listaSolicitud();
    }
    private void volver(){
        if(rangoN.equals("propietario")) {
            Intent intent = new Intent(getApplicationContext(), PanelpropietarioActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("cuenta", cuenta);
            intent.putExtra("url", url);
            intent.putExtra("cifrado", cifrado);
            startActivity(intent);
        }
        else{
            if(rangoN.equals("director")){
                Intent intent = new Intent(getApplicationContext(), PanelDirectorActivity.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("cuenta", cuenta);
                intent.putExtra("url", url);
                intent.putExtra("cifrado", cifrado);
                startActivity(intent);
            }
        }
    }
    private void ventanaDialogo(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Agregar usuario");
        builder.setMessage("¿Agregaras a "+nombreC+" "+apellidoC+" como cliente de tu empresa?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                agregarUsuario();
            }
        });
        builder.setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                rechazarUsuario();
            }
        });
        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    private void rechazarUsuario(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Usuario rechazado",Toast.LENGTH_SHORT).show();
                        listaSolicitud();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 004:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 003:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", idC);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "8");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void agregarUsuario(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        cuentaCreada();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Usuario ya agregado ",Toast.LENGTH_SHORT).show();
                        rechazarUsuario();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 005:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                agregarUsuario();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuarioC);
                params.put("empresa", empresa);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "9");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    public void cuentaCreada(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("cuenta_creada")){
                        sumandoEmpresa();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 0051:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cuentaCreada();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuarioC);
                params.put("empresa", empresa);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "901");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    public void sumandoEmpresa(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("sumado_empresa")){
                        solicitudEliminada();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 0052:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sumandoEmpresa();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("empresa", empresa);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "902");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    public void solicitudEliminada(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("solicitud_eliminada")){
                        Toast.makeText(getApplicationContext(),"Usuario agregado",Toast.LENGTH_SHORT).show();
                        listaSolicitud();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 0053:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                solicitudEliminada();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", idC);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "903");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void listaSolicitud(){
        final String[] a = {"1"};
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray cuentasarray=jsonObject.getJSONArray("aprobacion");
                    AgregarUsuarioAdaptador empresaInformacion=null;
                    datosSolicitud=new ArrayList<AgregarUsuarioAdaptador>();
                    for(int i=0;i<cuentasarray.length();i++){
                        JSONObject rowcuenta=cuentasarray.getJSONObject(i);
                        if(rowcuenta.getString("mensaje").equals("aprobado")) {
                            empresaInformacion = new AgregarUsuarioAdaptador(
                                    rowcuenta.getString("nombre"),
                                    rowcuenta.getString("apellido"),
                                    rowcuenta.getString("telefono"),
                                    rowcuenta.getString("correo"),
                                    rowcuenta.getString("usuario"),
                                    rowcuenta.getString("identidad"),
                                    rowcuenta.getString("id")
                            );
                            datosSolicitud.add(empresaInformacion);
                        }
                        else {
                            a[0] = "0";
                            if (b[0].equals("0")) {
                                panelagregarUsuario.addView(notinada);
                                panelagregarUsuario.removeView(listaSolicitud);
                                b[0] = "1";
                            }
                            break;
                        }
                    }
                    if(a[0].equals("1")) {
                        llenarLista();
                        if (b[0].equals("1")) {
                            panelagregarUsuario.removeView(notinada);
                            panelagregarUsuario.addView(listaSolicitud);
                            b[0] = "0";
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(), "Error 007:"+error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 008:"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empresa", empresa);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "10");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void llenarLista(){
        nombre=new ArrayList<String>();
        apellido=new ArrayList<String>();
        telefono=new ArrayList<String>();
        correo=new ArrayList<String>();
        usuarioN=new ArrayList<String>();
        identidad=new ArrayList<String>();
        id=new ArrayList<String>();
        for(int i=0;i<datosSolicitud.size();i++){
            nombre.add(datosSolicitud.get(i).getNombre());
            apellido.add(datosSolicitud.get(i).getApellido());
            telefono.add(datosSolicitud.get(i).getTelefono());
            correo.add(datosSolicitud.get(i).getCorreo());
            usuarioN.add(datosSolicitud.get(i).getUsuario());
            identidad.add(datosSolicitud.get(i).getIdentidad());
            id.add(datosSolicitud.get(i).getId());
        }
        listaSolicitud.setAdapter(new AdaptadorSolicitud(getApplicationContext(),nombre,apellido,telefono,correo,usuarioN,identidad,id));
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            volver();
            return true;
        }
        return false;
    }
}