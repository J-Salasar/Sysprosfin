package xyz.salasar.prestamopantera.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;

import xyz.salasar.prestamopantera.R;

public class AdaptadorCuentas extends BaseAdapter {
    private ArrayList<String> cuentas, credito,congelado,deuda,empresa,tipo,intereses;
    private Context context;
    private static LayoutInflater inflater=null;
    private TextView txtView_cuenta, txtView_disponible, txtView_congelado, txtView_empresa, txtView_tipo, txtView_deuda;
    private ConstraintLayout panelcuentas;
    private LinearLayout paneldeudaCongelado;
    public AdaptadorCuentas(Context context, ArrayList<String> cuentas, ArrayList<String> credito, ArrayList<String> congelado, ArrayList<String> deuda, ArrayList<String> empresa, ArrayList<String> tipo, ArrayList<String> intereses){
        this.context = context;
        this.cuentas = cuentas;
        this.credito = credito;
        this.congelado = congelado;
        this.deuda = deuda;
        this.empresa = empresa;
        this.tipo = tipo;
        this.intereses=intereses;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return cuentas.size();
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
        final View vista=inflater.inflate(R.layout.cuenta_creada,null);
        txtView_cuenta = vista.findViewById(R.id.txtcuenta);
        txtView_disponible = vista.findViewById(R.id.txtdisponible);
        txtView_congelado = vista.findViewById(R.id.txtcongelado);
        txtView_empresa = vista.findViewById(R.id.txtempresa);
        txtView_tipo = vista.findViewById(R.id.txttipocuenta);
        txtView_deuda = vista.findViewById(R.id.txtdeuda);
        panelcuentas = vista.findViewById(R.id.panelcuentas);
        paneldeudaCongelado = vista.findViewById(R.id.deudaCongelado);
        if(tipo.get(position).equals("1")){
            txtView_tipo.setText("Cuentas personales");
            paneldeudaCongelado.removeView(txtView_cuenta);
            paneldeudaCongelado.removeView(txtView_disponible);
            paneldeudaCongelado.removeView(txtView_empresa);
            paneldeudaCongelado.removeView(txtView_congelado);
            paneldeudaCongelado.removeView(txtView_deuda);
        }
        else{
            if(tipo.get(position).equals("2")){
                txtView_tipo.setText("Cuentas de prestamos");
                paneldeudaCongelado.removeView(txtView_cuenta);
                paneldeudaCongelado.removeView(txtView_disponible);
                paneldeudaCongelado.removeView(txtView_empresa);
                paneldeudaCongelado.removeView(txtView_congelado);
                paneldeudaCongelado.removeView(txtView_deuda);
            }
            else{
                DecimalFormat formato = new DecimalFormat("#,##0.00");
                txtView_cuenta.setText("Cuenta: "+cuentas.get(position));
                String formateado=formato.format(Double.parseDouble(credito.get(position)));
                txtView_disponible.setText("L. "+formateado);
                formateado=formato.format(Double.parseDouble(congelado.get(position)));
                txtView_congelado.setText("L. "+formateado);
                txtView_empresa.setText(empresa.get(position));
                txtView_tipo.setText("Cuenta de "+tipo.get(position));
                Double deudaTotal=Double.parseDouble(deuda.get(position))+Double.parseDouble(intereses.get(position));
                formateado=formato.format(deudaTotal);
                txtView_deuda.setText("L. "+formateado);
                if(Double.parseDouble(congelado.get(position))<0.01){
                    paneldeudaCongelado.removeView(txtView_congelado);
                }
                if(deudaTotal<0.01){
                    paneldeudaCongelado.removeView(txtView_deuda);
                }
                paneldeudaCongelado.removeView(txtView_tipo);
                if(tipo.get(position).equals("ahorro")){
                    paneldeudaCongelado.removeView(txtView_empresa);
                }
            }
        }
        return vista;
    }
}
