package xyz.salasar.prestamopantera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

public class Restablecer2Activity extends AppCompatActivity {
    private ImageButton bt_atras_restablecer2;
    private EditText editxt_usuario_restablecer2;
    private Button bt_siguiente_restablecer2;
    private String url,cifrado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_restablecer2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        editxt_usuario_restablecer2=findViewById(R.id.editxt_usuario_restablecer2);
        url = getIntent().getStringExtra("url");
        cifrado = getIntent().getStringExtra("cifrado");
        bt_atras_restablecer2=findViewById(R.id.bt_atras_restablecer2);
        bt_siguiente_restablecer2=findViewById(R.id.bt_siguiente_restablecer2);
        bt_atras_restablecer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        bt_siguiente_restablecer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validar(editxt_usuario_restablecer2.getText().toString().trim(),1)){
                    desactivarFunciones();
                    buscarDatos();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Usuario invalido",Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        });
    }
    private boolean validar(String dato,int opcion){
        String opcion1 = "[a-z0-9]{4,16}";
        switch (opcion){
            case 1:{
                return dato.matches(opcion1);
            }
            default:{
                return false;
            }
        }
    }
    public void activarFunciones(){
        bt_siguiente_restablecer2.setEnabled(true);
        editxt_usuario_restablecer2.setEnabled(true);
    }
    public void desactivarFunciones(){
        bt_siguiente_restablecer2.setEnabled(false);
        editxt_usuario_restablecer2.setEnabled(false);
    }
    public void siguente(String nombre, String apellido, String correo, String correo_origen, String clave_correo_origen){
        Intent intent = new Intent(getApplicationContext(), RestablecerActivity.class);
        intent.putExtra("usuario", editxt_usuario_restablecer2.getText().toString().trim());
        intent.putExtra("nombre", nombre);
        intent.putExtra("apellido", apellido);
        intent.putExtra("correo", correo);
        intent.putExtra("correo_origen", correo_origen);
        intent.putExtra("clave_correo_origen", clave_correo_origen);
        intent.putExtra("url", url);
        intent.putExtra("cifrado", cifrado);
        startActivity(intent);
    }
    public void buscarDatos(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("existe")){
                        siguente(confirmacion.getString("nombre"),confirmacion.getString("apellido"),confirmacion.getString("correo"),confirmacion.getString("correo_origen"),confirmacion.getString("clave_correo_origen"));
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("no existe")){
                            Toast.makeText(getApplicationContext(),"Usuario no existente",Toast.LENGTH_LONG).show();
                            activarFunciones();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Error al revisar",Toast.LENGTH_LONG).show();
                            activarFunciones();

                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 077:"+error.toString(),Toast.LENGTH_LONG).show();
                    activarFunciones();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 078:"+error.toString(),Toast.LENGTH_LONG).show();
                activarFunciones();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", editxt_usuario_restablecer2.getText().toString().trim());
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "4");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void volver(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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