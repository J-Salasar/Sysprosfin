package xyz.salasar.prestamopantera;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xyz.salasar.prestamopantera.Adaptador.AdaptadorClientes;
import xyz.salasar.prestamopantera.configuracion.HistorialAdaptador;
import xyz.salasar.prestamopantera.configuracion.UsuarioAgregadoAdaptador;

public class UsuariosAgregadosActivity extends AppCompatActivity {
    private String usuario, cuentaN, empresaN,url,cifrado,rangoN;
    private ImageButton volver,recargar,buscar;
    private ListView listaUsuario;
    private EditText nombretxt;
    private ArrayList<UsuarioAgregadoAdaptador> clientes;
    private ArrayList<String> nombreC,apellidoC,cuentaC,rangoC,creditoC,capitalC,interesesC,usuarioC;
    private ConstraintLayout panelusuarioAgregado;
    private TextView notificacionNada,activos,ganancias;
    private String[] b={"0"};
    private String deudaD,interesesD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_usuarios_agregados);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario=getIntent().getStringExtra("usuario");
        cuentaN=getIntent().getStringExtra("cuenta");
        empresaN=getIntent().getStringExtra("empresa");
        url=getIntent().getStringExtra("url");
        rangoN=getIntent().getStringExtra("rango");
        cifrado=getIntent().getStringExtra("cifrado");
        deudaD=getIntent().getStringExtra("deuda");
        interesesD=getIntent().getStringExtra("intereses");
        volver=findViewById(R.id.volver55);
        recargar=findViewById(R.id.recargar55);
        listaUsuario=findViewById(R.id.listaUsuario55);
        nombretxt=findViewById(R.id.txtnombre55);
        buscar=findViewById(R.id.buscar55);
        panelusuarioAgregado=findViewById(R.id.panelusuarioAgregado55);
        notificacionNada=findViewById(R.id.notificacionNada55);
        activos=findViewById(R.id.activos55);
        ganancias=findViewById(R.id.ganancias55);
        panelusuarioAgregado.removeView(notificacionNada);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampos(nombretxt.getText().toString().trim(),1)){
                    listaUsuarios();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Nombre invalido",Toast.LENGTH_LONG).show();
                }
            }
        });
        listaUsuario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(rangoC.get(position).equals("ayudante")){
                    dialogoCajero(cuentaC.get(position),nombreC.get(position),apellidoC.get(position));
                }
                else {
                    if(rangoC.get(position).equals("usuario")) {
                        Intent intent = new Intent(getApplicationContext(), PerfilClienteActivity.class);
                        intent.putExtra("usuarioP", usuario);
                        intent.putExtra("cuentaP", cuentaN);
                        intent.putExtra("empresaP", empresaN);
                        intent.putExtra("cuentaC", cuentaC.get(position));
                        intent.putExtra("rango", rangoN);
                        intent.putExtra("url",url);
                        intent.putExtra("deuda",deudaD);
                        intent.putExtra("intereses",interesesD);
                        intent.putExtra("cifrado",cifrado);
                        startActivity(intent);
                    }
                    else{
                        if(rangoC.get(position).equals("director")) {
                            dialogoCajero(cuentaC.get(position),nombreC.get(position),apellidoC.get(position));
                        }
                    }
                }
            }
        });
        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuarioLista();
                nombretxt.setText("");
                Toast.makeText(getApplicationContext(), "Lista actualizada", Toast.LENGTH_LONG).show();
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        usuarioLista();
    }
    private void dialogoCajero(String cuentaL,String nombre,String apellido){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Elige");
        builder.setMessage("¿Que quieres hacer con el perfil de "+nombre+" "+apellido+"?");
        builder.setPositiveButton("Ver perfil", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), PerfilClienteActivity.class);
                intent.putExtra("usuarioP", usuario);
                intent.putExtra("cuentaP", cuentaN);
                intent.putExtra("empresaP", empresaN);
                intent.putExtra("cuentaC", cuentaL);
                intent.putExtra("rango", rangoN);
                intent.putExtra("url",url);
                intent.putExtra("cifrado",cifrado);
                intent.putExtra("deuda",deudaD);
                intent.putExtra("intereses",interesesD);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Ver historial", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getApplicationContext(),HistorialCajeroActivity.class);
                intent.putExtra("usuarioP",usuario);
                intent.putExtra("empresaP",empresaN);
                intent.putExtra("cuentaP",cuentaN);
                intent.putExtra("cuenta",cuentaL);
                intent.putExtra("nombre",nombre);
                intent.putExtra("apellido",apellido);
                intent.putExtra("rango",rangoN);
                intent.putExtra("url",url);
                intent.putExtra("cifrado",cifrado);
                intent.putExtra("deuda",deudaD);
                intent.putExtra("intereses",interesesD);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public void listaUsuarios(){
        double[] total={0.0,0.0};
        if(rangoN.equals("propietario")) {
            String[] a = {"1"};
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray cuentasarray = jsonObject.getJSONArray("aprobacion");
                        UsuarioAgregadoAdaptador clientesLista = null;
                        clientes = new ArrayList<UsuarioAgregadoAdaptador>();
                        for (int i = 0; i < cuentasarray.length(); i++) {
                            JSONObject rowcuenta = cuentasarray.getJSONObject(i);
                            if (rowcuenta.getString("mensaje").equals("aprobado")) {
                                clientesLista = new UsuarioAgregadoAdaptador(
                                        rowcuenta.getString("nombre"),
                                        rowcuenta.getString("apellido"),
                                        rowcuenta.getString("cuenta"),
                                        rowcuenta.getString("rango"),
                                        rowcuenta.getString("credito"),
                                        rowcuenta.getString("capital"),
                                        rowcuenta.getString("intereses"),
                                        rowcuenta.getString("usuario")
                                );
                                total[0]+=Double.parseDouble(rowcuenta.getString("capital"));
                                total[1]+=Double.parseDouble(rowcuenta.getString("intereses"));
                                clientes.add(clientesLista);
                            } else {
                                a[0] = "0";
                                if (b[0].equals("0")) {
                                    panelusuarioAgregado.addView(notificacionNada);
                                    panelusuarioAgregado.removeView(listaUsuario);
                                    b[0] = "1";
                                }
                                break;
                            }
                        }
                        if (a[0].equals("1")) {
                            String formato=new DecimalFormat("#,##0.00").format(total[0]);
                            activos.setText("Activos: L. "+formato);
                            formato=new DecimalFormat("#,##0.00").format(total[1]);
                            ganancias.setText("Ganancias: L. "+formato);
                            llenarLista();
                            if (b[0].equals("1")) {
                                panelusuarioAgregado.removeView(notificacionNada);
                                panelusuarioAgregado.addView(listaUsuario);
                                b[0] = "0";
                            }
                        }
                    } catch (Throwable error) {
                        Toast.makeText(getApplicationContext(), "Error 093:" + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error 094:" + error.toString(), Toast.LENGTH_LONG).show();
                }

            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<String, String>();
                    parametros.put("usuario", usuario);
                    parametros.put("empresa", empresaN);
                    parametros.put("nombre", nombretxt.getText().toString().trim());
                    parametros.put("cifrado", cifrado);
                    parametros.put("codigoLlave", "40");
                    return parametros;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
        else{
            if(rangoN.equals("director")) {
                String[] a = {"1"};
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray cuentasarray = jsonObject.getJSONArray("aprobacion");
                            UsuarioAgregadoAdaptador clientesLista = null;
                            clientes = new ArrayList<UsuarioAgregadoAdaptador>();
                            for (int i = 0; i < cuentasarray.length(); i++) {
                                JSONObject rowcuenta = cuentasarray.getJSONObject(i);
                                if (rowcuenta.getString("mensaje").equals("aprobado")) {
                                    clientesLista = new UsuarioAgregadoAdaptador(
                                            rowcuenta.getString("nombre"),
                                            rowcuenta.getString("apellido"),
                                            rowcuenta.getString("cuenta"),
                                            rowcuenta.getString("rango"),
                                            rowcuenta.getString("credito"),
                                            rowcuenta.getString("capital"),
                                            rowcuenta.getString("intereses"),
                                            rowcuenta.getString("usuario")
                                    );
                                    clientes.add(clientesLista);
                                    total[0]+=Double.parseDouble(rowcuenta.getString("capital"));
                                    total[1]+=Double.parseDouble(rowcuenta.getString("intereses"));
                                } else {
                                    a[0] = "0";
                                    if (b[0].equals("0")) {
                                        panelusuarioAgregado.addView(notificacionNada);
                                        panelusuarioAgregado.removeView(listaUsuario);
                                        b[0] = "1";
                                    }
                                    break;
                                }
                            }
                            if (a[0].equals("1")) {
                                String formato=new DecimalFormat("#,##0.00").format(total[0]);
                                activos.setText("Activos: L. "+formato);
                                formato=new DecimalFormat("#,##0.00").format(total[1]);
                                ganancias.setText("Ganancias: L. "+formato);
                                llenarLista();
                                if (b[0].equals("1")) {
                                    panelusuarioAgregado.removeView(notificacionNada);
                                    panelusuarioAgregado.addView(listaUsuario);
                                    b[0] = "0";
                                }
                            }
                        } catch (Throwable error) {
                            Toast.makeText(getApplicationContext(), "Error 103:" + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error 104:" + error.toString(), Toast.LENGTH_LONG).show();
                    }

                }) {
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<String, String>();
                        parametros.put("usuario", usuario);
                        parametros.put("empresa", empresaN);
                        parametros.put("nombre", nombretxt.getText().toString().trim());
                        parametros.put("cifrado", cifrado);
                        parametros.put("codigoLlave", "47");
                        return parametros;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
        }
    }
    public boolean validarCampos(String dato,int opcion){
        String opcion1 = "[A-Za-zñáéíóúÑÁÉÍÓÚ]{1,20}";
        String opcion2 = "[A-Za-zñáéíóúÑÁÉÍÓÚ]{1,20}[ ][A-Za-zñáéíóúÑÁÉÍÓÚ]{1,20}";
        String opcion3 = "[A-Za-zñáéíóúÑÁÉÍÓÚ]{1,20}[ ][A-Za-zñáéíóúÑÁÉÍÓÚ]{1,20}[ ][A-Za-zñáéíóúÑÁÉÍÓÚ]{1,20}";
        String opcion4 = "[A-Za-zñáéíóúÑÁÉÍÓÚ]{1,20}[ ][A-Za-zñáéíóúÑÁÉÍÓÚ]{1,20}[ ][A-Za-zñáéíóúÑÁÉÍÓÚ]{1,20}[ ][A-Za-zñáéíóúÑÁÉÍÓÚ]{1,20}";
        switch (opcion){
            case 1:{
                return dato.matches(opcion1+"|"+opcion2+"|"+opcion3+"|"+opcion4);
            }
            default:{
                return false;
            }
        }
    }
    private void usuarioLista(){
        double[] total={Double.parseDouble(deudaD),Double.parseDouble(interesesD)};
        if(rangoN.equals("propietario")) {
            String[] a = {"1"};
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray cuentasarray = jsonObject.getJSONArray("aprobacion");
                        UsuarioAgregadoAdaptador clientesLista = null;
                        clientes = new ArrayList<UsuarioAgregadoAdaptador>();
                        for (int i = 0; i < cuentasarray.length(); i++) {
                            JSONObject rowcuenta = cuentasarray.getJSONObject(i);
                            if (rowcuenta.getString("mensaje").equals("aprobado")) {
                                clientesLista = new UsuarioAgregadoAdaptador(
                                        rowcuenta.getString("nombre"),
                                        rowcuenta.getString("apellido"),
                                        rowcuenta.getString("cuenta"),
                                        rowcuenta.getString("rango"),
                                        rowcuenta.getString("credito"),
                                        rowcuenta.getString("capital"),
                                        rowcuenta.getString("intereses"),
                                        rowcuenta.getString("usuario")
                                );
                                clientes.add(clientesLista);
                                total[0]+=Double.parseDouble(rowcuenta.getString("capital"));
                                total[1]+=Double.parseDouble(rowcuenta.getString("intereses"));
                            } else {
                                a[0] = "0";
                                if (b[0].equals("0")) {
                                    panelusuarioAgregado.addView(notificacionNada);
                                    panelusuarioAgregado.removeView(listaUsuario);
                                    b[0] = "1";
                                }
                                break;
                            }
                        }
                        if (a[0].equals("1")) {
                            String formato=new DecimalFormat("#,##0.00").format(total[0]);
                            activos.setText("Activos: L. "+formato);
                            formato=new DecimalFormat("#,##0.00").format(total[1]);
                            ganancias.setText("Ganancias: L. "+formato);
                            llenarLista();
                            if (b[0].equals("1")) {
                                panelusuarioAgregado.removeView(notificacionNada);
                                panelusuarioAgregado.addView(listaUsuario);
                                b[0] = "0";
                            }
                        }
                    } catch (Throwable error) {
                        Toast.makeText(getApplicationContext(), "Error 095:" + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error 096:" + error.toString(), Toast.LENGTH_LONG).show();
                }

            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<String, String>();
                    parametros.put("usuario", usuario);
                    parametros.put("empresa", empresaN);
                    parametros.put("cifrado", cifrado);
                    parametros.put("codigoLlave", "29");
                    return parametros;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
        else{
            if(rangoN.equals("director")) {
                String[] a = {"1"};
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray cuentasarray = jsonObject.getJSONArray("aprobacion");
                            UsuarioAgregadoAdaptador clientesLista = null;
                            clientes = new ArrayList<UsuarioAgregadoAdaptador>();
                            for (int i = 0; i < cuentasarray.length(); i++) {
                                JSONObject rowcuenta = cuentasarray.getJSONObject(i);
                                if (rowcuenta.getString("mensaje").equals("aprobado")) {
                                    clientesLista = new UsuarioAgregadoAdaptador(
                                            rowcuenta.getString("nombre"),
                                            rowcuenta.getString("apellido"),
                                            rowcuenta.getString("cuenta"),
                                            rowcuenta.getString("rango"),
                                            rowcuenta.getString("credito"),
                                            rowcuenta.getString("capital"),
                                            rowcuenta.getString("intereses"),
                                            rowcuenta.getString("usuario")
                                    );
                                    clientes.add(clientesLista);
                                    total[0]+=Double.parseDouble(rowcuenta.getString("capital"));
                                    total[1]+=Double.parseDouble(rowcuenta.getString("intereses"));
                                } else {
                                    a[0] = "0";
                                    if (b[0].equals("0")) {
                                        panelusuarioAgregado.addView(notificacionNada);
                                        panelusuarioAgregado.removeView(listaUsuario);
                                        b[0] = "1";
                                    }
                                    break;
                                }
                            }
                            if (a[0].equals("1")) {
                                String formato=new DecimalFormat("#,##0.00").format(total[0]);
                               activos.setText("Activos: L. "+formato);
                                formato=new DecimalFormat("#,##0.00").format(total[1]);
                                ganancias.setText("Ganancias: L. "+formato);
                                llenarLista();
                                if (b[0].equals("1")) {
                                    panelusuarioAgregado.removeView(notificacionNada);
                                    panelusuarioAgregado.addView(listaUsuario);
                                    b[0] = "0";
                                }
                            }
                        } catch (Throwable error) {
                            Toast.makeText(getApplicationContext(), "Error 101:" + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error 102:" + error.toString(), Toast.LENGTH_LONG).show();
                    }

                }) {
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<String, String>();
                        parametros.put("usuario", usuario);
                        parametros.put("empresa", empresaN);
                        parametros.put("cifrado", cifrado);
                        parametros.put("codigoLlave", "46");
                        return parametros;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
        }
    }
    private void llenarLista(){
        capitalC=new ArrayList<String>();
        rangoC=new ArrayList<String>();
        usuarioC=new ArrayList<String>();
        nombreC=new ArrayList<String>();
        apellidoC=new ArrayList<String>();
        creditoC=new ArrayList<String>();
        cuentaC=new ArrayList<String>();
        interesesC=new ArrayList<String>();
        for(int i=0;i<clientes.size();i++){
            usuarioC.add(clientes.get(i).getUsuario());
            rangoC.add(clientes.get(i).getRango());
            capitalC.add(clientes.get(i).getCapital());
            creditoC.add(clientes.get(i).getCredito());
            nombreC.add(clientes.get(i).getNombre());
            apellidoC.add(clientes.get(i).getApellido());
            cuentaC.add(clientes.get(i).getCuentas());
            interesesC.add(clientes.get(i).getIntereses());
        }
        listaUsuario.setAdapter(new AdaptadorClientes(getApplicationContext(),nombreC,apellidoC,cuentaC,creditoC,capitalC,interesesC,rangoC));
    }
    public void volver(){
        if(rangoN.equals("propietario")) {
            Intent intent = new Intent(getApplicationContext(), PanelpropietarioActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("cuenta", cuentaN);
            intent.putExtra("url", url);
            intent.putExtra("cifrado", cifrado);
            startActivity(intent);
        }
        else{
            if(rangoN.equals("director")) {
                Intent intent = new Intent(getApplicationContext(), PanelDirectorActivity.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("cuenta", cuentaN);
                intent.putExtra("url", url);
                intent.putExtra("cifrado", cifrado);
                startActivity(intent);
            }
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            volver();
            return true;
        }
        return false;
    }
}