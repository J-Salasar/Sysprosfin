package xyz.salasar.prestamopantera.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import xyz.salasar.prestamopantera.R;

public class AdaptadorPrestamo extends BaseAdapter {
    private ArrayList<String> nombre,apellido,telefono,correo,cantidad;
    private Context context;
    private static LayoutInflater inflater=null;
    private TextView txt_nombre, txt_telefono, txt_correo,txt_cantidad;
    public AdaptadorPrestamo(Context context,ArrayList<String> nombre, ArrayList<String> apellido, ArrayList<String> telefono, ArrayList<String> correo, ArrayList<String> cantidad) {
        this.nombre=nombre;
        this.apellido=apellido;
        this.telefono=telefono;
        this.correo=correo;
        this.cantidad=cantidad;
        this.context=context;
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return nombre.size();
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
        final View vista=inflater.inflate(R.layout.solicitud_prestamo,null);
        txt_nombre=(TextView) vista.findViewById(R.id.nombre775);
        txt_nombre.setText(nombre.get(position)+" "+apellido.get(position));
        txt_telefono=(TextView) vista.findViewById(R.id.telefono775);
        txt_telefono.setText("Telefono: "+telefono.get(position));
        txt_correo=(TextView) vista.findViewById(R.id.correo775);
        txt_correo.setText(correo.get(position));
        txt_cantidad=(TextView) vista.findViewById(R.id.cantidad775);
        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String formateado=formato.format(Double.parseDouble(cantidad.get(position)));
        txt_cantidad.setText("L. "+formateado);
        return vista;
    }
}
