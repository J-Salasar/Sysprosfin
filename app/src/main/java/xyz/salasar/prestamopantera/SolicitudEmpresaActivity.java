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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xyz.salasar.prestamopantera.Adaptador.AdaptadorEmpresas;
import xyz.salasar.prestamopantera.configuracion.EmpresasAdaptador;

public class SolicitudEmpresaActivity extends AppCompatActivity {
    private String usuario,url,cifrado;
    private ImageButton btvolver,btrecargar,buscar;
    private ListView listaempresa;
    private ArrayList<String> empresaN,cantidadUsuarios,empresas,agregado,proceso;
    private ArrayList<EmpresasAdaptador> datosempresas;
    private EditText empresab;
    private String llamada="0";
    private ConstraintLayout panelSolicitudEmpresa;
    private TextView notificacionNada;
    private String[] b={"0"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_solicitud_empresa);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario = getIntent().getStringExtra("usuario");
        url=getIntent().getStringExtra("url");
        cifrado=getIntent().getStringExtra("cifrado");
        btvolver=findViewById(R.id.btvolver111);
        listaempresa=findViewById(R.id.listaempresa111);
        btrecargar=findViewById(R.id.bt_recargar111);
        buscar=findViewById(R.id.buscar111);
        empresab=findViewById(R.id.txtbuscar111);
        notificacionNada=findViewById(R.id.notificacionNada111);
        panelSolicitudEmpresa=findViewById(R.id.panelsolicitudEmpresa111);
        panelSolicitudEmpresa.removeView(notificacionNada);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificar(empresab.getText().toString().trim(),1)){
                    buscarEmpresa();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Debe ingresar un nombre valido",Toast.LENGTH_LONG).show();
                }
            }
        });
        btvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        btrecargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empresab.setText("");
                empresasLista();
                Toast.makeText(getApplicationContext(),"Lista actualizada",Toast.LENGTH_LONG).show();
            }
        });
        empresasLista();
    }
    private void buscarEmpresa(){
        String[] a={"1"};
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray cuentasarray=jsonObject.getJSONArray("aprobacion");
                    EmpresasAdaptador empresaInformacion=null;
                    datosempresas=new ArrayList<EmpresasAdaptador>();
                    for(int i=0;i<cuentasarray.length();i++){
                        JSONObject rowcuenta=cuentasarray.getJSONObject(i);
                        if(rowcuenta.getString("mensaje").equals("aprobado")) {
                            empresaInformacion = new EmpresasAdaptador(
                                    rowcuenta.getString("usuario"),
                                    rowcuenta.getString("empresa"),
                                    rowcuenta.getString("agregado"),
                                    rowcuenta.getString("proceso")
                            );
                            datosempresas.add(empresaInformacion);
                        }
                        else{
                            a[0]="0";
                            if(b[0].equals("0")){
                                panelSolicitudEmpresa.addView(notificacionNada);
                                panelSolicitudEmpresa.removeView(listaempresa);
                                b[0]="1";
                            }
                            break;
                        }
                    }
                    if(a[0].equals("1")){
                        llamada="1";
                        llenarLista();
                        if(b[0].equals("1")){
                            panelSolicitudEmpresa.removeView(notificacionNada);
                            panelSolicitudEmpresa.addView(listaempresa);
                            b[0]="0";
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 081:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 082:"+error.toString(), Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("empresa", empresab.getText().toString().trim());
                parametros.put("usuario", usuario);
                parametros.put("cifrado", cifrado);
                parametros.put("codigoLlave", "39");
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private boolean verificar(String dato,int opcion){
        String opcion1 = "[a-zA-Z0-9ñÑ ]{1,32}";
        switch (opcion){
            case 1:{
                return dato.matches(opcion1);
            }
            default:{
                return false;
            }
        }
    }
    private void empresasLista(){
        String[] a={"1"};
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray cuentasarray=jsonObject.getJSONArray("aprobacion");
                    EmpresasAdaptador empresaInformacion=null;
                    datosempresas=new ArrayList<EmpresasAdaptador>();
                    for(int i=0;i<cuentasarray.length();i++){
                        JSONObject rowcuenta=cuentasarray.getJSONObject(i);
                        if(rowcuenta.getString("mensaje").equals("aprobado")) {
                            empresaInformacion = new EmpresasAdaptador(
                                    rowcuenta.getString("usuario"),
                                    rowcuenta.getString("empresa"),
                                    rowcuenta.getString("agregado"),
                                    rowcuenta.getString("proceso")
                            );
                            datosempresas.add(empresaInformacion);
                        }
                        else{
                            a[0]="0";
                            if(b[0].equals("0")){
                                panelSolicitudEmpresa.addView(notificacionNada);
                                panelSolicitudEmpresa.removeView(listaempresa);
                                b[0]="1";
                            }
                            break;
                        }
                    }
                    if(a[0].equals("1")){
                        llamada="0";
                        llenarLista();
                        if(b[0].equals("1")){
                            panelSolicitudEmpresa.removeView(notificacionNada);
                            panelSolicitudEmpresa.addView(listaempresa);
                            b[0]="0";
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 083:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 084:"+error.toString(), Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("cifrado", cifrado);
                parametros.put("codigoLlave", "21");
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void llenarLista(){
        empresaN=new ArrayList<String>();
        cantidadUsuarios=new ArrayList<String>();
        agregado=new ArrayList<String>();
        proceso=new ArrayList<String>();
        for(int i=0;i<datosempresas.size();i++){
            empresaN.add(datosempresas.get(i).getEmpresas());
            cantidadUsuarios.add(datosempresas.get(i).getUsuario());
            agregado.add(datosempresas.get(i).getAgregado());
            proceso.add(datosempresas.get(i).getProceso());

        }
        listaempresa.setAdapter(new AdaptadorEmpresas(getApplicationContext(),empresaN,cantidadUsuarios,agregado,proceso,usuario,llamada,url,cifrado));
    }
    public void volver(){
        Intent intent=new Intent(getApplicationContext(),PrincipalActivity.class);
        intent.putExtra("usuario",usuario);
        intent.putExtra("url",url);
        intent.putExtra("cifrado",cifrado);
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