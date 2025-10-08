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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import xyz.salasar.prestamopantera.Adaptador.AdaptadorPrestamo;
import xyz.salasar.prestamopantera.Adaptador.AdaptadorSolicitud;
import xyz.salasar.prestamopantera.configuracion.AceptarPrestamoAdaptador;
import xyz.salasar.prestamopantera.configuracion.AgregarUsuarioAdaptador;

public class AprobarPrestamoActivity extends AppCompatActivity {
    private String usuario,cuenta,empresa,url,cifrado,rangoN;
    private ListView listaprestamos;
    private ImageView recargar,volver;
    private ArrayList <AceptarPrestamoAdaptador> datosSolicitud;
    private ArrayList<String> nombre,apellido,telefono,correo,usuarioN,id,cantidad;
    private String usuarioC,idC,nombreC,apellidoC,cantidadC;
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
    private ConstraintLayout panelAprobarPrestamo;
    private TextView notificaNada;
    private String[] b={"0"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_aprobar_prestamo);
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
        listaprestamos=findViewById(R.id.listaprestamos221);
        recargar=findViewById(R.id.recargar221);
        volver=findViewById(R.id.volver221);
        panelAprobarPrestamo=findViewById(R.id.panelaprobarPrestamo221);
        notificaNada=findViewById(R.id.notificacionNada221);
        panelAprobarPrestamo.removeView(notificaNada);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaPrestamos();
                Toast.makeText(getApplicationContext(),"Lista actualizada",Toast.LENGTH_LONG).show();
            }
        });
        listaprestamos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long idt) {
                usuarioC=usuarioN.get(position);
                idC=id.get(position);
                nombreC=nombre.get(position);
                apellidoC=apellido.get(position);
                cantidadC=cantidad.get(position);
                ventanaDialogo();
            }
        });
        listaPrestamos();
    }
    private void ventanaDialogo(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Prestar Dinero");
        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String formateado=formato.format(Double.parseDouble(cantidadC));
        builder.setMessage("¿Le prestaras a "+nombreC+" "+apellidoC+" "+formateado+" lempiras?");
        builder.setPositiveButton("Credito", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                credito();
            }
        });
        builder.setNegativeButton("Efectivo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                efectivo();
            }
        });
        builder.setNeutralButton("Rechazar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rechazarPrestamo();
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    private void efectivo(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Credito enviado",Toast.LENGTH_SHORT).show();
                        nombreI=confirmacion.getString("nombre");
                        apellidoI=confirmacion.getString("apellido");
                        cuentaI=confirmacion.getString("cuenta");
                        descripcionI=confirmacion.getString("descripcion");
                        cantidadI=confirmacion.getString("cantidad");
                        fechaI=confirmacion.getString("fecha");
                        horaI=confirmacion.getString("hora");
                        referenciaI=confirmacion.getString("referencia");
                        imprimirDialogo();
                        listaPrestamos();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("insuficiente")) {
                            Toast.makeText(getApplicationContext(), "Saldo insuficiente", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 009:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 010:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", idC);
                params.put("empresa", empresa);
                params.put("usuario", usuarioC);
                params.put("cantidad", cantidadC);
                params.put("usuarioP", usuario);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "28");
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
                imprimir();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Copia", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                copia();
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
                String recibo2=empresa+"\n";
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
                String recibo2=empresa+"\n";
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

    private void credito(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Credito enviado",Toast.LENGTH_SHORT).show();
                        nombreI=confirmacion.getString("nombre");
                        apellidoI=confirmacion.getString("apellido");
                        cuentaI=confirmacion.getString("cuenta");
                        descripcionI=confirmacion.getString("descripcion");
                        cantidadI=confirmacion.getString("cantidad");
                        fechaI=confirmacion.getString("fecha");
                        horaI=confirmacion.getString("hora");
                        referenciaI=confirmacion.getString("referencia");
                        imprimirDialogo();
                        listaPrestamos();
                    }
                    else{
                        if(confirmacion.getString("mensaje").equals("insuficiente")) {
                            Toast.makeText(getApplicationContext(), "Saldo insuficiente", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 011:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 012:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", idC);
                params.put("empresa", empresa);
                params.put("usuario", usuarioC);
                params.put("cantidad", cantidadC);
                params.put("usuarioP", usuario);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "27");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }
    private void rechazarPrestamo(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("aprobacion");
                    JSONObject confirmacion=jsonArray.getJSONObject(0);
                    if(confirmacion.getString("mensaje").equals("aprobado")){
                        Toast.makeText(getApplicationContext(),"Prestamo rechazado",Toast.LENGTH_SHORT).show();
                        listaPrestamos();
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(),"Error 013:"+error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 014:"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", idC);
                params.put("empresa", empresa);
                params.put("usuario", usuarioC);
                params.put("cantidad", cantidadC);
                params.put("cifrado", cifrado);
                params.put("codigoLlave", "11");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void listaPrestamos(){
        String[] a={"1"};
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray cuentasarray=jsonObject.getJSONArray("aprobacion");
                    AceptarPrestamoAdaptador empresaInformacion=null;
                    datosSolicitud=new ArrayList<AceptarPrestamoAdaptador>();
                    for(int i=0;i<cuentasarray.length();i++){
                        JSONObject rowcuenta=cuentasarray.getJSONObject(i);
                        if(rowcuenta.getString("mensaje").equals("aprobado")) {
                            empresaInformacion = new AceptarPrestamoAdaptador(
                                    rowcuenta.getString("nombre"),
                                    rowcuenta.getString("apellido"),
                                    rowcuenta.getString("telefono"),
                                    rowcuenta.getString("correo"),
                                    rowcuenta.getString("usuario"),
                                    rowcuenta.getString("id"),
                                    rowcuenta.getString("cantidad")
                            );
                            datosSolicitud.add(empresaInformacion);
                        }
                        else{
                            a[0] ="0";
                            if(b[0].equals("0")) {
                                panelAprobarPrestamo.removeView(listaprestamos);
                                panelAprobarPrestamo.addView(notificaNada);
                                b[0] = "1";
                            }
                            break;
                        }
                    }
                    if(a[0].equals("1")) {
                        llenarLista();
                        if(b[0].equals("1")) {
                            panelAprobarPrestamo.removeView(notificaNada);
                            panelAprobarPrestamo.addView(listaprestamos);
                            b[0] = "0";
                        }
                    }
                }
                catch (Throwable error){
                    Toast.makeText(getApplicationContext(), "Error 015:"+error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 016:"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("empresa", empresa);
                parametros.put("cifrado", cifrado);
                parametros.put("codigoLlave", "12");
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void llenarLista(){
        nombre=new ArrayList<String>();
        apellido=new ArrayList<String>();
        telefono=new ArrayList<String>();
        correo=new ArrayList<String>();
        usuarioN=new ArrayList<String>();
        id=new ArrayList<String>();
        cantidad=new ArrayList<String>();
        for(int i=0;i<datosSolicitud.size();i++){
            nombre.add(datosSolicitud.get(i).getNombre());
            apellido.add(datosSolicitud.get(i).getApellido());
            telefono.add(datosSolicitud.get(i).getTelefono());
            correo.add(datosSolicitud.get(i).getCorreo());
            usuarioN.add(datosSolicitud.get(i).getUsuario());
            id.add(datosSolicitud.get(i).getId());
            cantidad.add(datosSolicitud.get(i).getCantidad());
        }
        listaprestamos.setAdapter(new AdaptadorPrestamo(getApplicationContext(),nombre,apellido,telefono,correo,cantidad));
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            volver();
            return true;
        }
        return false;
    }
}