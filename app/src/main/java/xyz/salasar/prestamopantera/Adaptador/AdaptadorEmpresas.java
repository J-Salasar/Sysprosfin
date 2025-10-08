package xyz.salasar.prestamopantera.Adaptador;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import xyz.salasar.prestamopantera.MainActivity;
import xyz.salasar.prestamopantera.PrincipalActivity;
import xyz.salasar.prestamopantera.R;
import xyz.salasar.prestamopantera.SolicitudEmpresaActivity;

public class AdaptadorEmpresas extends BaseAdapter {
    private ArrayList<String> usuario,empresa,agregado,proceso;
    private Context context;
    private static LayoutInflater inflater=null;
    private TextView txt_usuario, txt_empresa, txt_posicion;
    private Button btn_agrega;
    private String usuarioN,llamada,url,cifrado;
    public AdaptadorEmpresas(Context context, ArrayList<String> empresa, ArrayList<String> usuario, ArrayList<String> agregado, ArrayList<String> proceso, String usuarioN,String llamada,String url,String cifrado){
        this.usuario=usuario;
        this.empresa=empresa;
        this.llamada=llamada;
        this.context = context;
        this.usuarioN=usuarioN;
        this.url=url;
        this.agregado=agregado;
        this.proceso=proceso;
        this.cifrado=cifrado;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return usuario.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View vista=inflater.inflate(R.layout.empresas_disponibles,null);
        txt_usuario = vista.findViewById(R.id.usuarios115);
        txt_empresa = vista.findViewById(R.id.empresa115);
        txt_posicion = vista.findViewById(R.id.posicion115);
        btn_agrega = vista.findViewById(R.id.agrega115);
        txt_empresa.setText(empresa.get(position));
        btn_agrega.setTag(position);
        txt_usuario.setText("usuarios: " + usuario.get(position));
        if(agregado.get(position).equals(usuarioN)){
            btn_agrega.setVisibility(View.INVISIBLE);
        }
        if(proceso.get(position).equals(usuarioN)){
            btn_agrega.setEnabled(false);
            btn_agrega.setText("Ingreso en proceso");
            btn_agrega.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(136,27,20)));
        }
        btn_agrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                            JSONObject confirmacion=jsonArray.getJSONObject(0);
                            if(confirmacion.getString("mensaje").equals("registrado")){
                                Toast.makeText(context,"Solicitud enviada",Toast.LENGTH_LONG).show();
                            }
                            else{
                                if(confirmacion.getString("mensaje").equals("existe")) {
                                    Toast.makeText(context, "Ya estas registrado", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    if(confirmacion.getString("mensaje").equals("enviado")) {
                                        Toast.makeText(context, "Ya mandastes una solicitud", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                        catch (Throwable error){
                            Toast.makeText(context,"Error:"+error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"Error de conexion",Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("usuario", usuarioN);
                        params.put("empresa", empresa.get((Integer)v.getTag()));
                        params.put("cifrado", cifrado);
                        params.put("codigoLlave", "26");
                        return params;
                    }
                };
                RequestQueue requestQueue= Volley.newRequestQueue(context);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);
            }
        });
        if(llamada=="0") {
            if (position + 1 == 1) {
                txt_posicion.setTextColor(Color.rgb(255, 152, 0));
                txt_posicion.setText((position + 1) + "st");
            } else {
                if (position + 1 == 2) {
                    txt_posicion.setTextColor(Color.rgb(165, 150, 150));
                    txt_posicion.setText((position + 1) + "nd");
                } else {
                    if (position + 1 == 3) {
                        txt_posicion.setTextColor(Color.rgb(185, 98, 70));
                        txt_posicion.setText((position + 1) + "rd");
                    } else {
                        if (position + 1 == 4) {
                            txt_posicion.setTextColor(Color.rgb(139, 195, 74));
                            txt_posicion.setText((position + 1) + "th");
                        } else {
                            if (position + 1 == 5) {
                                txt_posicion.setTextColor(Color.rgb(251, 115, 71));
                                txt_posicion.setText((position + 1) + "th");
                            } else {
                                if (position + 1 == 6) {
                                    txt_posicion.setTextColor(Color.rgb(0, 188, 212));
                                    txt_posicion.setText((position + 1) + "th");
                                } else {
                                    if (position + 1 == 7) {
                                        txt_posicion.setTextColor(Color.rgb(76, 175, 80));
                                        txt_posicion.setText((position + 1) + "th");
                                    } else {
                                        if (position + 1 == 8) {
                                            txt_posicion.setTextColor(Color.rgb(156, 39, 176));
                                            txt_posicion.setText((position + 1) + "th");
                                        } else {
                                            txt_posicion.setTextColor(Color.rgb(0, 0, 0));
                                            txt_posicion.setText((position + 1) + "th");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else{
            if(llamada=="1") {
                txt_posicion.setVisibility(View.INVISIBLE);
            }
        }
        return vista;
    }
}
