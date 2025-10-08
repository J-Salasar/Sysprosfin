package xyz.salasar.prestamopantera.Adaptador;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import xyz.salasar.prestamopantera.R;

public class AdaptadorHistorial extends BaseAdapter {
    private ArrayList<String> referencia,empresaON,tipoON,nombreON,apellidoON,cuentaON,cantidad,descripcionO,descripcionD,fecha,nombreD,apellidoD,cuentaD,empresaD,tipoD;
    private Context context;
    private String cuentaO;
    private String fechaE;
    private SimpleDateFormat formatoSalida=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoEntrada=new SimpleDateFormat("yyyy-MM-dd");
    private static LayoutInflater inflater=null;
    private TextView txt_referencia, txt_cantidad, txt_descripcion, txt_fecha, txt_cuenta, txt_empresa;
    public AdaptadorHistorial(Context context, ArrayList<String> referencia,  ArrayList<String> empresaON, ArrayList<String> tipoON, ArrayList<String> nombreON, ArrayList<String> apellidoON, ArrayList<String> cuentaON, ArrayList<String> cantidad, ArrayList<String> descripcionO, ArrayList<String> descripcionD, ArrayList<String> fecha, ArrayList<String> nombreD, ArrayList<String> apellidoD, ArrayList<String> cuentaD, ArrayList<String> empresaD, ArrayList<String> tipoD, String cuentaO){
        this.referencia=referencia;
        this.empresaON=empresaON;
        this.tipoON=tipoON;
        this.nombreON=nombreON;
        this.apellidoON=apellidoON;
        this.cuentaON=cuentaON;
        this.cantidad=cantidad;
        this.descripcionO=descripcionO;
        this.descripcionD=descripcionD;
        this.fecha=fecha;
        this.nombreD=nombreD;
        this.apellidoD=apellidoD;
        this.cuentaD=cuentaD;
        this.context = context;
        this.empresaD=empresaD;
        this.tipoD=tipoD;
        this.cuentaO=cuentaO;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return referencia.size();
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
        final View vista=inflater.inflate(R.layout.historial_movimientos,null);
        txt_referencia = vista.findViewById(R.id.referencia550);
        txt_cantidad = vista.findViewById(R.id.cantidad550);
        txt_descripcion = vista.findViewById(R.id.descripcion550);
        txt_fecha = vista.findViewById(R.id.fecha550);
        txt_cuenta = vista.findViewById(R.id.cuenta550);
        txt_empresa = vista.findViewById(R.id.empresa550);
        try{
            Date fechaf=formatoEntrada.parse(fecha.get(position));
            fechaE=formatoSalida.format(fechaf);
        }
        catch (ParseException e){
            Toast.makeText(context,"Coloque una fecha validad",Toast.LENGTH_LONG).show();
        }
        Double saldoH=Double.parseDouble(cantidad.get(position));
        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String formateado=formato.format(saldoH);
        if(tipoD.get(position).equals("ahorro")) {
            if (tipoON.get(position).equals("ahorro")) {
                if (cuentaON.get(position).equals(cuentaO)) {
                    txt_referencia.setText("No. Referencia: " + referencia.get(position));
                    txt_fecha.setText("Fecha: " + fechaE);
                    txt_descripcion.setText("Descripcion: " + descripcionO.get(position));
                    txt_cuenta.setText("No. Cuenta: " + cuentaD.get(position));
                    txt_empresa.setText(nombreD.get(position) + " " + apellidoD.get(position));
                    txt_cantidad.setText("L. " + formateado);
                    txt_cantidad.setTextColor(Color.rgb(172, 35, 25));
                } else {
                    if (cuentaD.get(position).equals(cuentaO)) {
                        txt_referencia.setText("No. Referencia: " + referencia.get(position));
                        txt_fecha.setText("Fecha: " + fechaE);
                        txt_descripcion.setText("Descripcion: " + descripcionD.get(position));
                        txt_cuenta.setText("No. Cuenta: " + cuentaON.get(position));
                        txt_empresa.setText(nombreON.get(position) + " " + apellidoON.get(position));
                        txt_cantidad.setText("L. " + formateado);
                        txt_cantidad.setTextColor(Color.rgb(76, 175, 80));
                    }
                }
            }
            else{
                if(tipoON.get(position).equals("prestamos")){
                    if (cuentaON.get(position).equals(cuentaO)) {
                        txt_referencia.setText("No. Referencia: " + referencia.get(position));
                        txt_fecha.setText("Fecha: " + fechaE);
                        txt_descripcion.setText("Descripcion: " + descripcionO.get(position));
                        txt_cuenta.setText("No. Cuenta: " + cuentaD.get(position));
                        txt_empresa.setText(nombreD.get(position) + " " + apellidoD.get(position));
                        txt_cantidad.setText("L. " + formateado);
                        txt_cantidad.setTextColor(Color.rgb(172, 35, 25));
                    } else {
                        if (cuentaD.get(position).equals(cuentaO)) {
                            txt_referencia.setText("No. Referencia: " + referencia.get(position));
                            txt_fecha.setText("Fecha: " + fechaE);
                            txt_descripcion.setText("Descripcion: " + descripcionD.get(position));
                            txt_cuenta.setText("No. Cuenta: " + cuentaON.get(position));
                            txt_empresa.setText(nombreON.get(position) + " " + apellidoON.get(position));
                            txt_cantidad.setText("L. " + formateado);
                            txt_cantidad.setTextColor(Color.rgb(76, 175, 80));
                        }
                    }
                }
            }
        }
        else{
            if(tipoD.get(position).equals("prestamos")){
                if (tipoON.get(position).equals("ahorro")) {
                    if (cuentaON.get(position).equals(cuentaO)) {
                        txt_referencia.setText("No. Referencia: " + referencia.get(position));
                        txt_fecha.setText("Fecha: " + fechaE);
                        txt_descripcion.setText("Descripcion: " + descripcionO.get(position));
                        txt_cuenta.setText("No. Cuenta: " + cuentaD.get(position));
                        txt_empresa.setText(nombreD.get(position) + " " + apellidoD.get(position));
                        txt_cantidad.setText("L. " + formateado);
                        txt_cantidad.setTextColor(Color.rgb(172, 35, 25));
                    } else {
                        if (cuentaD.get(position).equals(cuentaO)) {
                            txt_referencia.setText("No. Referencia: " + referencia.get(position));
                            txt_fecha.setText("Fecha: " + fechaE);
                            txt_descripcion.setText("Descripcion: " + descripcionD.get(position));
                            txt_cuenta.setText("No. Cuenta: " + cuentaON.get(position));
                            txt_empresa.setText(nombreON.get(position) + " " + apellidoON.get(position));
                            txt_cantidad.setText("L. " + formateado);
                            txt_cantidad.setTextColor(Color.rgb(76, 175, 80));
                            if(descripcionO.get(position).equals("envio de prestamo")){
                                txt_empresa.setText(empresaON.get(position));
                            }
                            else{
                                if(descripcionO.get(position).equals("envio de prestamo en efectivo")){
                                    txt_empresa.setText(empresaON.get(position));
                                    txt_cantidad.setTextColor(Color.rgb(120, 31, 135));
                                }
                            }
                        }
                    }
                }
                else{
                    if(tipoON.get(position).equals("prestamos")){
                        if (cuentaON.get(position).equals(cuentaO)) {
                            txt_referencia.setText("No. Referencia: " + referencia.get(position));
                            txt_fecha.setText("Fecha: " + fechaE);
                            txt_descripcion.setText("Descripcion: " + descripcionO.get(position));
                            txt_cuenta.setText("No. Cuenta: " + cuentaD.get(position));
                            txt_empresa.setText(nombreD.get(position) + " " + apellidoD.get(position));
                            txt_cantidad.setText("L. " + formateado);
                            txt_cantidad.setTextColor(Color.rgb(172, 35, 25));
                            if(descripcionO.get(position).equals("envio de prestamo en efectivo")){
                                txt_cantidad.setTextColor(Color.rgb(120, 31, 135));
                            }
                            else{
                                if(descripcionO.get(position).equals("modificacion de la deuda")){
                                    txt_cantidad.setTextColor(Color.rgb(76, 175, 80));
                                }
                                else{
                                    if(descripcionO.get(position).equals("ubicacion de intereses")){
                                        txt_cantidad.setTextColor(Color.rgb(76, 175, 80));
                                    }
                                    else{
                                        if(descripcionO.get(position).equals("ejecutastes un pago de prestamo")){
                                            txt_cantidad.setTextColor(Color.rgb(120, 31, 135));
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if (cuentaD.get(position).equals(cuentaO)) {
                                txt_referencia.setText("No. Referencia: " + referencia.get(position));
                                txt_fecha.setText("Fecha: " + fechaE);
                                txt_descripcion.setText("Descripcion: " + descripcionD.get(position));
                                txt_cuenta.setText("No. Cuenta: " + cuentaON.get(position));
                                txt_empresa.setText(nombreON.get(position) + " " + apellidoON.get(position));
                                txt_cantidad.setText("L. " + formateado);
                                txt_cantidad.setTextColor(Color.rgb(76, 175, 80));
                                if(descripcionD.get(position).equals("prestamo en efectivo")){
                                    txt_empresa.setText(empresaON.get(position));
                                    txt_cantidad.setTextColor(Color.rgb(120, 31, 135));
                                }
                                else{
                                    if(descripcionD.get(position).equals("prestamo")){
                                        txt_empresa.setText(empresaON.get(position));
                                    }
                                    else{
                                        if(descripcionD.get(position).equals("deuda")){
                                            txt_empresa.setText(empresaON.get(position));
                                            txt_cantidad.setTextColor(Color.rgb(172, 35, 25));
                                        }
                                        else{
                                            if(descripcionD.get(position).equals("exoneracion de la deuda")){
                                                txt_empresa.setText(empresaON.get(position));
                                            }
                                            else{
                                                if(descripcionD.get(position).equals("intereses")) {
                                                    txt_empresa.setText(empresaON.get(position));
                                                    txt_cantidad.setTextColor(Color.rgb(172, 35, 25));
                                                }
                                                else{
                                                    if(descripcionD.get(position).equals("exoneracion de intereses")) {
                                                        txt_empresa.setText(empresaON.get(position));
                                                    }
                                                    if(descripcionD.get(position).equals("pago de prestamo")){
                                                        txt_empresa.setText(empresaON.get(position));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return vista;
    }
}
