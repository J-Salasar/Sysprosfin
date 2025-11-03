package xyz.salasar.prestamopantera;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PagarPrestamoActivity extends AppCompatActivity {
    private String usuario, cuentaN, empresaN, rangoN,url,cifrado;
    private ImageButton volver, informacion;
    private EditText numeroCuenta, cantidad;
    private TextView nombre,deuda,intereses,total;
    private Button ejecutar, cancelar;
    private String nombreI,apellidoI,cuentaI,descripcionI,cantidadI,fechaI,horaI,referenciaI;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_BLUETOOTH = 1;
    private BluetoothDevice mmDevice;
    public static String printer_id="MP58-01";
    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;
    private Thread workerThread;
    private boolean isOpenBTRequested=true;
    private String phpNombreD,phpApellidoD,phpDeuda,phpIntereses,phpCantidad,phpNombreO,phpApellidoO,phpFecha,phpHora;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_pagar_prestamo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        usuario=getIntent().getStringExtra("usuario");
        cuentaN=getIntent().getStringExtra("cuenta");
        empresaN=getIntent().getStringExtra("empresa");
        rangoN=getIntent().getStringExtra("rango");
        url=getIntent().getStringExtra("url");
        cifrado=getIntent().getStringExtra("cifrado");
        volver=findViewById(R.id.volver98);
        informacion=findViewById(R.id.informacion98);
        numeroCuenta=findViewById(R.id.numeroCuenta98);
        nombre=findViewById(R.id.nombre98);
        ejecutar=findViewById(R.id.ejecutar98);
        cancelar=findViewById(R.id.cancelar98);
        deuda=findViewById(R.id.deuda98);
        intereses=findViewById(R.id.intereses98);
        total=findViewById(R.id.total98);
        cantidad=findViewById(R.id.cantidad98);
        ejecutar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cantidad.getText().toString().trim().equals("")||(Double.parseDouble(cantidad.getText().toString().trim())<0.01)){
                    Toast.makeText(getApplicationContext(),"Ingrese alguna cantidad significativa",Toast.LENGTH_LONG).show();
                    ejecutar.setEnabled(true);
                    cancelar.setEnabled(true);
                    cantidad.setEnabled(true);
                }
                else {
                    ejecutar.setEnabled(false);
                    cancelar.setEnabled(false);
                    cantidad.setEnabled(false);
                    pagarPrestamo();
                }
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar.setVisibility(View.INVISIBLE);
                ejecutar.setVisibility(View.INVISIBLE);
                numeroCuenta.setText("");
                numeroCuenta.setEnabled(true);
                nombre.setText("Nombre y Apellido");
                deuda.setText("Deuda: L. 0.00");
                intereses.setText("Intereses: L. 0.00");
                total.setText("Total: L. 0.00");
            }
        });
        informacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informacion();
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
    }
    private void pagarPrestamo(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado1")){
                        phpNombreD=confirmacion.getString("nombreD");
                        phpApellidoD=confirmacion.getString("apellidoD");
                        phpDeuda=confirmacion.getString("deuda");
                        phpIntereses=confirmacion.getString("intereses");
                        phpCantidad=confirmacion.getString("cantidad");
                        actualizarEstado1();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("fecha_vencida")) {
                            cancelar.setEnabled(true);
                            ejecutar.setEnabled(true);
                            cantidad.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Espera a la nueva fecha limite de pago", Toast.LENGTH_LONG).show();
                        }
                        else{
                            if(confirmacion.getString("mensaje").equals("aprobado2")){
                                phpNombreD=confirmacion.getString("nombreD");
                                phpApellidoD=confirmacion.getString("apellidoD");
                                phpDeuda=confirmacion.getString("deuda");
                                phpIntereses=confirmacion.getString("intereses");
                                phpCantidad=confirmacion.getString("cantidad");
                                actualizarEstado2();
                            }
                            else {
                                if (confirmacion.getString("mensaje").equals("saldo_insuficiente")) {
                                    cancelar.setEnabled(true);
                                    ejecutar.setEnabled(true);
                                    cantidad.setEnabled(true);
                                    Toast.makeText(getApplicationContext(), "El cliente no debe esa cantidad", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }
                catch (Throwable error){
                    ejecutar.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Error 025:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pagarPrestamo();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta",numeroCuenta.getText().toString());
                params.put("cantidad",cantidad.getText().toString());
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "42");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void actualizarEstado1(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        datosOperador();
                    }
                }
                catch (Throwable error){
                    ejecutar.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Error 0251:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                actualizarEstado1();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta",numeroCuenta.getText().toString());
                params.put("cantidad",phpCantidad);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "4201");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void actualizarEstado2(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        datosOperador();
                    }
                }
                catch (Throwable error){
                    ejecutar.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Error 0252:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ejecutar.setEnabled(true);
                Toast.makeText(getApplicationContext(),"Error 026:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta",numeroCuenta.getText().toString());
                params.put("cantidad",cantidad.getText().toString());
                params.put("restante",phpCantidad);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "4202");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void datosOperador(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        phpNombreO=confirmacion.getString("nombreO");
                        phpApellidoO=confirmacion.getString("apellidoO");
                        crearHistorial();
                    }
                }
                catch (Throwable error){
                    ejecutar.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Error 0253:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                datosOperador();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuentaO",cuentaN);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "4203");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void crearHistorial(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        phpFecha=confirmacion.getString("fecha");
                        phpHora=confirmacion.getString("hora");
                        datosImprimir();
                    }
                }
                catch (Throwable error){
                    ejecutar.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Error 0254:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                crearHistorial();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nombreO",phpNombreO);
                params.put("apellidoO",phpApellidoO);
                params.put("cuentaO",cuentaN);
                params.put("empresa",empresaN);
                params.put("cantidad",cantidad.getText().toString());
                params.put("nombreD",phpNombreD);
                params.put("apellidoD",phpApellidoD);
                params.put("cuenta",numeroCuenta.getText().toString());
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "4204");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void datosImprimir(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(), "Pago realizado con exito", Toast.LENGTH_LONG).show();
                        cancelar.setVisibility(View.INVISIBLE);
                        ejecutar.setVisibility(View.INVISIBLE);
                        ejecutar.setEnabled(true);
                        numeroCuenta.setText("");
                        numeroCuenta.setEnabled(true);
                        nombre.setText("Nombre y Apellido");
                        deuda.setText("Deuda: L. 0.00");
                        intereses.setText("Intereses: L. 0.00");
                        total.setText("Total: L. 0.00");
                        cantidad.setText("");
                        nombreI=confirmacion.getString("nombre");
                        apellidoI=confirmacion.getString("apellido");
                        cuentaI=confirmacion.getString("cuenta");
                        descripcionI=confirmacion.getString("descripcion");
                        cantidadI=confirmacion.getString("cantidad");
                        fechaI=confirmacion.getString("fecha");
                        horaI=confirmacion.getString("hora");
                        referenciaI=confirmacion.getString("referencia");
                        imprimirDialogo();
                    }
                }
                catch (Throwable error){
                    ejecutar.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Error 0255:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                datosImprimir();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fechaI",phpFecha);
                params.put("horaI",phpHora);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "4205");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void imprimirDialogo(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Imprimir recibo");
        builder.setMessage("Imprime el recibo para "+nombreI+" "+apellidoI);
        builder.setPositiveButton("Imprimir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                copia();
                dialog.dismiss();
            }
        });
        /*builder.setNegativeButton("Copia", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                copia();
                dialog.dismiss();
            }
        });*/
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

    //Inicio de imprimir
    public void imprimir(){
        try {
            findBT();
            openBT();
            printText();
            closeBT();
        }
        catch (IOException ex){
            ex.printStackTrace();
            imprimirDialogo();
        }
    }
    public void copia(){
        try {
            findBT();
            openBT();
            printText2();
            closeBT();
        }
        catch (IOException ex){
            ex.printStackTrace();
            imprimirDialogo();
        }
    }
    private void printText2(){
        try{
            if (mmOutputStream == null) {
                Toast.makeText(getApplicationContext(), "Error: impresora no disponible", Toast.LENGTH_LONG).show();
                imprimirDialogo();
            }
            else{
                DecimalFormat formato = new DecimalFormat("#,##0.00");
                String formateado=formato.format(Double.parseDouble(cantidadI));
                byte[] leftAlignCommand = new byte[]{0x1B, 0x61, 0x00};
                mmOutputStream.write(leftAlignCommand);
                String recibo1="\n";
                recibo1+="================================\n";
                mmOutputStream.write(recibo1.getBytes());
                byte[] centerCommand = new byte[]{0x1B, 0x61, 0x01};
                mmOutputStream.write(centerCommand);
                String recibo2=empresaN+"\n";
                mmOutputStream.write(recibo2.getBytes());
                mmOutputStream.write(leftAlignCommand);
                String recibo="================================\n";
                recibo+="Descripcion: \n";
                recibo+=descripcionI+"\n";
                recibo+="================================\n";
                recibo+="Referencia: "+referenciaI+"\n";
                recibo+="Fecha: "+fechaI+"\n";
                recibo+="Hora: "+horaI+"\n";
                recibo+="Cajero: "+usuario+"\n";
                recibo+="================================\n";
                recibo+="No. Cuenta: "+cuentaI+"\n";
                recibo+="Cliente:\n";
                recibo+=nombreI+"\n";
                recibo+=apellidoI+"\n";
                recibo+="================================\n";
                recibo+="Cantidad: L. "+formateado+"\n";
                recibo+="================================\n";
                recibo+="\n";
                recibo+="\n";
                recibo+="       Copia del cliente\n";
                recibo+="\n";
                recibo+="\n";
                mmOutputStream.write(recibo.getBytes());
                mmOutputStream.flush();
                imprimirDialogo();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void printText(){
        try{
            if (mmOutputStream == null) {
                Toast.makeText(getApplicationContext(), "Error: impresora no disponible", Toast.LENGTH_LONG).show();
                imprimirDialogo();
            }
            else{
                DecimalFormat formato = new DecimalFormat("#,##0.00");
                String formateado=formato.format(Double.parseDouble(cantidadI));
                byte[] leftAlignCommand = new byte[]{0x1B, 0x61, 0x00};
                mmOutputStream.write(leftAlignCommand);
                String recibo1="\n";
                recibo1+="================================\n";
                mmOutputStream.write(recibo1.getBytes());
                byte[] centerCommand = new byte[]{0x1B, 0x61, 0x01};
                mmOutputStream.write(centerCommand);
                String recibo2=empresaN+"\n";
                mmOutputStream.write(recibo2.getBytes());
                mmOutputStream.write(leftAlignCommand);
                String recibo="================================\n";
                recibo+="Descripcion: \n";
                recibo+=descripcionI+"\n";
                recibo+="================================\n";
                recibo+="Referencia: "+referenciaI+"\n";
                recibo+="Fecha: "+fechaI+"\n";
                recibo+="Hora: "+horaI+"\n";
                recibo+="Cajero: "+usuario+"\n";
                recibo+="================================\n";
                recibo+="No. Cuenta: "+cuentaI+"\n";
                recibo+="Cliente:\n";
                recibo+=nombreI+"\n";
                recibo+=apellidoI+"\n";
                recibo+="================================\n";
                recibo+="Cantidad: L. "+formateado+"\n";
                recibo+="================================\n";
                recibo+="\n";
                recibo+="\n";
                recibo+="\n";
                recibo+="\n";
                recibo+="   --------------------------   \n";
                recibo+="             Firma\n";
                recibo+="       No valido sin firma\n";
                recibo+="\n";
                recibo+="\n";
                recibo+="\n";
                mmOutputStream.write(recibo.getBytes());
                mmOutputStream.flush();
                imprimirDialogo();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Device Bluetooth tidak Tersedia", Toast.LENGTH_SHORT).show();
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            isOpenBTRequested = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.BLUETOOTH_CONNECT,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_BLUETOOTH);
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            mmDevice = pairedDevices.iterator().next();
            /*for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(printer_id)) {
                    mmDevice = device;
                    break;
                }
            }*/
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (isOpenBTRequested) {
                        openBT();  // Reintenta abrir la conexión Bluetooth
                    } else {
                        findBT();  // Reintenta buscar dispositivos
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Permiso Bluetooth denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void openBT() throws IOException {
        try {
            // UUID estándar para conexiones seriales Bluetooth
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            // Verifica si los permisos necesarios están otorgados
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                isOpenBTRequested = true;
                // Solicita los permisos si no están concedidos
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_BLUETOOTH
                );
                return;  // Detén la ejecución hasta que se concedan los permisos
            }

            // Establece la conexión Bluetooth
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();

            // Inicializa los flujos de entrada y salida
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            // Inicia la escucha de datos recibidos
            beginListenForData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );
                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                //myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //fin de imprimir

    private void informacion(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        DecimalFormat formato = new DecimalFormat("#,##0.00");
                        nombre.setText(confirmacion.getString("nombre")+" "+confirmacion.getString("apellido"));
                        String formateado=formato.format(Double.parseDouble(confirmacion.getString("deuda")));
                        deuda.setText("Deuda: L. "+formateado);
                        formateado=formato.format(Double.parseDouble(confirmacion.getString("intereses")));
                        intereses.setText("Intereses: L. "+formateado);
                        double totalg=Double.parseDouble(confirmacion.getString("deuda"))+Double.parseDouble(confirmacion.getString("intereses"));
                        formateado=formato.format(totalg);
                        total.setText("Total: L. "+formateado);
                        ejecutar.setVisibility(View.VISIBLE);
                        cancelar.setVisibility(View.VISIBLE);
                        numeroCuenta.setEnabled(false);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No existe la cuenta",Toast.LENGTH_LONG).show();

                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 027:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 028:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cuenta",numeroCuenta.getText().toString());
                params.put("empresa",empresaN);
                params.put("cuentaO",cuentaN);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "41");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void volver(){
        Intent intent=null;
        if(rangoN.equals("propietario")){
            intent=new Intent(getApplicationContext(),PanelpropietarioActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("cuenta", cuentaN);
            intent.putExtra("url",url);
            intent.putExtra("cifrado",cifrado);
        }
        else{
            if(rangoN.equals("ayudante")){
                intent=new Intent(getApplicationContext(),PanelayudanteActivity.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("cuenta", cuentaN);
                intent.putExtra("url",url);
                intent.putExtra("cifrado",cifrado);
            }
            else{
                if(rangoN.equals("director")){
                    intent=new Intent(getApplicationContext(),PanelDirectorActivity.class);
                    intent.putExtra("usuario", usuario);
                    intent.putExtra("cuenta", cuentaN);
                    intent.putExtra("url",url);
                    intent.putExtra("cifrado",cifrado);
                }
            }
        }
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