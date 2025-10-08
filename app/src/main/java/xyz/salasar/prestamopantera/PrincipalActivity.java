package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import xyz.salasar.prestamopantera.Adaptador.AdaptadorCuentas;
import xyz.salasar.prestamopantera.configuracion.CuentasAdaptador;

public class PrincipalActivity extends AppCompatActivity {
    private ImageButton imageBt_cerrar_principal;
    private Button imageBt_recargar_principal, imageBt_crear_principal,btempresa;
    private TextView txtView_cerrar_principal;
    private TextView txtView_nombre_apellido_principal,notificacionNada;
    private String usuario,url,cifrado;
    private ImageView imageView_perfil_principal;
    private ListView lista_cuentas;
    private ArrayList<String> cuentasN, creditoN,cogeladoN,deudaN,empresaN,tipoN,rangoN,interesesN,porcentajeN;
    private ArrayList<CuentasAdaptador> datoscuenta;
    private ConstraintLayout panelPrincipal;
    private String[] b={"0"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_principal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        imageBt_cerrar_principal = findViewById(R.id.imageButton_cerrar_principal);
        txtView_cerrar_principal = findViewById(R.id.textView_cerrar_principal);
        txtView_nombre_apellido_principal = findViewById(R.id.textView_nombre_apellido_principal);
        usuario = getIntent().getStringExtra("usuario");
        url = getIntent().getStringExtra("url");
        cifrado = getIntent().getStringExtra("cifrado");
        imageBt_recargar_principal = findViewById(R.id.imageButton_recargar_principal);
        imageBt_crear_principal = findViewById(R.id.imageButton_crear_principal);
        lista_cuentas = findViewById(R.id.lista_cuentas);
        btempresa = findViewById(R.id.Btempresas320);
        panelPrincipal = findViewById(R.id.panelPrincipal8422);
        notificacionNada = findViewById(R.id.notificacionNada8422);
        panelPrincipal.removeView(notificacionNada);
        cargarPerfil();
        listaCuenta();
        imageBt_cerrar_principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        txtView_cerrar_principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        imageBt_recargar_principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageBt_recargar_principal.setVisibility(View.INVISIBLE);
                cargarPerfil();
                listaCuenta();
            }
        });
        imageBt_crear_principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearCuentaCredito();
            }
        });
        txtView_nombre_apellido_principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pantallaPerfil();
            }
        });
        btempresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SolicitudEmpresaActivity.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("url", url);
                intent.putExtra("cifrado", cifrado);
                startActivity(intent);
            }
        });
        lista_cuentas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(tipoN.get(position).equals("ahorro")){
                    if(rangoN.get(position).equals("propietario")){
                    }
                    else{
                        if(rangoN.get(position).equals("usuario")){
                            Intent intent=new Intent(getApplicationContext(),PanelusuarioActivity.class);
                            intent.putExtra("usuario", usuario);
                            intent.putExtra("cuenta",cuentasN.get(position));
                            intent.putExtra("url",url);
                            intent.putExtra("cifrado",cifrado);
                            startActivity(intent);
                        }
                    }
                }
                else{
                    if(tipoN.get(position).equals("prestamos")){
                        if(rangoN.get(position).equals("propietario")){
                            Intent intent=new Intent(getApplicationContext(),PanelpropietarioActivity.class);
                            intent.putExtra("usuario", usuario);
                            intent.putExtra("cuenta",cuentasN.get(position));
                            intent.putExtra("url",url);
                            intent.putExtra("cifrado",cifrado);
                            startActivity(intent);
                        }
                        else{
                            if(rangoN.get(position).equals("usuario")) {
                                Intent intent = new Intent(getApplicationContext(), PanelusuarioActivity.class);
                                intent.putExtra("usuario", usuario);
                                intent.putExtra("cuenta", cuentasN.get(position));
                                intent.putExtra("url", url);
                                intent.putExtra("cifrado", cifrado);
                                startActivity(intent);
                            }
                            else{
                                if(rangoN.get(position).equals("ayudante")){
                                    Intent intent = new Intent(getApplicationContext(), PanelayudanteActivity.class);
                                    intent.putExtra("usuario", usuario);
                                    intent.putExtra("cuenta", cuentasN.get(position));
                                    intent.putExtra("url", url);
                                    intent.putExtra("cifrado", cifrado);
                                    startActivity(intent);
                                }
                                else{
                                    if(rangoN.get(position).equals("director")){
                                        Intent intent = new Intent(getApplicationContext(), PanelDirectorActivity.class);
                                        intent.putExtra("usuario", usuario);
                                        intent.putExtra("cuenta", cuentasN.get(position));
                                        intent.putExtra("url", url);
                                        intent.putExtra("cifrado", cifrado);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    private void listaCuenta(){
        String[] a={"1"};
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray cuentasarray=jsonObject.getJSONArray("informacionCuentas");
                    CuentasAdaptador cuentaInformacion=null;
                    datoscuenta=new ArrayList<CuentasAdaptador>();
                    JSONObject confirmacion=cuentasarray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        for (int i = 0; i < cuentasarray.length(); i++) {
                            JSONObject rowcuenta = cuentasarray.getJSONObject(i);
                            if (rowcuenta.getString("estado").equals("activo")) {
                                if (rowcuenta.getString("tipo").equals("ahorro")) {
                                    cuentaInformacion = new CuentasAdaptador(
                                            rowcuenta.getString("cuenta"),
                                            rowcuenta.getString("credito"),
                                            rowcuenta.getString("congelado"),
                                            rowcuenta.getString("deuda"),
                                            rowcuenta.getString("empresa"),
                                            rowcuenta.getString("tipo"),
                                            rowcuenta.getString("rango"),
                                            rowcuenta.getString("intereses"),
                                            rowcuenta.getString("porcentaje")
                                    );
                                    datoscuenta.add(cuentaInformacion);
                                }
                            }
                        }
                        for (int i = 0; i < cuentasarray.length(); i++) {
                            JSONObject rowcuenta = cuentasarray.getJSONObject(i);
                            if (rowcuenta.getString("estado").equals("activo")) {
                                if (rowcuenta.getString("tipo").equals("prestamos")) {
                                    cuentaInformacion = new CuentasAdaptador(
                                            rowcuenta.getString("cuenta"),
                                            rowcuenta.getString("credito"),
                                            rowcuenta.getString("congelado"),
                                            rowcuenta.getString("deuda"),
                                            rowcuenta.getString("empresa"),
                                            rowcuenta.getString("tipo"),
                                            rowcuenta.getString("rango"),
                                            rowcuenta.getString("intereses"),
                                            rowcuenta.getString("porcentaje")
                                    );
                                    datoscuenta.add(cuentaInformacion);
                                }
                            }
                        }
                    }
                    else{
                        a[0]="0";
                        if(b[0].equals("0")) {
                            panelPrincipal.removeView(lista_cuentas);
                            panelPrincipal.addView(notificacionNada);
                            b[0] = "1";
                        }
                    }
                    if(a[0].equals("1")) {
                        llenarLista();
                        if(b[0].equals("1")) {
                            panelPrincipal.removeView(notificacionNada);
                            panelPrincipal.addView(lista_cuentas);
                            b[0] = "0";
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(), "Error 071: "+error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 072:"+error.toString(), Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuario", usuario);
                params.put("cifrado", cifrado);
                params.put("codigoLlave","6");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void llenarLista(){
        int y=0;
        int z=0;
        cuentasN=new ArrayList<String>();
        creditoN=new ArrayList<String>();
        cogeladoN=new ArrayList<String>();
        deudaN=new ArrayList<String>();
        empresaN=new ArrayList<String>();
        tipoN=new ArrayList<String>();
        rangoN=new ArrayList<String>();
        interesesN=new ArrayList<String>();
        porcentajeN=new ArrayList<String>();
        for(int i=0;i<datoscuenta.size();i++){
            if(datoscuenta.get(i).getTipo().equals("ahorro")&&y<1){
                cuentasN.add("");
                creditoN.add("");
                cogeladoN.add("");
                deudaN.add("");
                empresaN.add("");
                tipoN.add("1");
                rangoN.add("");
                interesesN.add("");
                porcentajeN.add("");
                i--;
                y++;
            }
            else{
                if(datoscuenta.get(i).getTipo().equals("prestamos")&&z<1) {
                    cuentasN.add("");
                    creditoN.add("");
                    cogeladoN.add("");
                    deudaN.add("");
                    empresaN.add("");
                    tipoN.add("2");
                    rangoN.add("");
                    interesesN.add("");
                    porcentajeN.add("");
                    i--;
                    z++;
                }
                else{
                    cuentasN.add(datoscuenta.get(i).getCuenta());
                    creditoN.add(datoscuenta.get(i).getCredito());
                    cogeladoN.add(datoscuenta.get(i).getCongelado());
                    deudaN.add(datoscuenta.get(i).getDeuda());
                    empresaN.add(datoscuenta.get(i).getEmpresa());
                    tipoN.add(datoscuenta.get(i).getTipo());
                    rangoN.add(datoscuenta.get(i).getRango());
                    interesesN.add(datoscuenta.get(i).getIntereses());
                    porcentajeN.add(datoscuenta.get(i).getPorcentaje());
                }
            }
        }
        lista_cuentas.setAdapter(new AdaptadorCuentas(this,cuentasN,creditoN,cogeladoN,deudaN,empresaN,tipoN,interesesN));
    }
    public void crearCuentaCredito(){
        Intent intent=new Intent(getApplicationContext(),CrearCuentaCreditoActivity.class);
        intent.putExtra("usuario", usuario);
        intent.putExtra("url", url);
        intent.putExtra("cifrado", cifrado);
        startActivity(intent);
    }
    public void pantallaPerfil(){
        Intent intent=new Intent(getApplicationContext(),PerfilActivity.class);
        intent.putExtra("usuario", usuario);
        intent.putExtra("url", url);
        intent.putExtra("cifrado", cifrado);
        startActivity(intent);
    }
    public void cargarPerfil(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("encontrado")){
                        txtView_nombre_apellido_principal.setText(confirmacion.getString("nombre")+" "+confirmacion.getString("apellido"));
                        imageBt_recargar_principal.setVisibility(View.VISIBLE);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al encontrarlo",Toast.LENGTH_LONG).show();
                        imageBt_recargar_principal.setVisibility(View.VISIBLE);
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 073:"+error.toString(),Toast.LENGTH_LONG).show();
                    imageBt_recargar_principal.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 074:"+error.toString(),Toast.LENGTH_LONG).show();
                imageBt_recargar_principal.setVisibility(View.VISIBLE);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "7");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void volver(){
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
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