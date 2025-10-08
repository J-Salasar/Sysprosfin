package xyz.salasar.prestamopantera.Adaptador;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;

import xyz.salasar.prestamopantera.R;

public class AdaptadorClientes extends BaseAdapter {
    private ArrayList<String> nombre,apellido,cuenta,credito,capital,intereses,rango;
    private Context context;
    private TextView nombreN,cuentaN,capitalN,interesesN,creditoN;
    private static LayoutInflater inflater=null;
    private ConstraintLayout rangoPanel;
    public AdaptadorClientes(Context context, ArrayList<String> nombre, ArrayList<String> apellido, ArrayList<String> cuenta, ArrayList<String> credito, ArrayList<String> capital, ArrayList<String> intereses, ArrayList<String> rango){
        this.context=context;
        this.nombre=nombre;
        this.apellido=apellido;
        this.cuenta=cuenta;
        this.capital=capital;
        this.credito=credito;
        this.intereses=intereses;
        this.rango=rango;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return cuenta.size();
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
        final View vista=inflater.inflate(R.layout.clientes_agregados,null);
        nombreN=vista.findViewById(R.id.nombre_apellido56);
        cuentaN=vista.findViewById(R.id.cuenta56);
        capitalN=vista.findViewById(R.id.capital56);
        interesesN=vista.findViewById(R.id.intereses56);
        rangoPanel=vista.findViewById(R.id.panelrango56);
        creditoN=vista.findViewById(R.id.credito56);
        nombreN.setText(nombre.get(position)+" "+apellido.get(position));
        cuentaN.setText(cuenta.get(position));
        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String formateado=formato.format(Double.parseDouble(capital.get(position)));
        capitalN.setText("Deuda: L. "+formateado);
        formateado=formato.format(Double.parseDouble(intereses.get(position)));
        interesesN.setText("Intereses: L. "+formateado);
        formateado=formato.format(Double.parseDouble(credito.get(position)));
        creditoN.setText("Credito: L. "+formateado);
        if(rango.get(position).equals("usuario")){
            rangoPanel.setBackgroundColor(Color.rgb(255,255,255));
        }
        else{
            if(rango.get(position).equals("ayudante")){
                rangoPanel.setBackgroundColor(Color.rgb(246,234,159));
            }
            else{
                if(rango.get(position).equals("director")){
                    rangoPanel.setBackgroundColor(Color.rgb(196,233,242));
                }
            }
        }
        return vista;
    }
}
