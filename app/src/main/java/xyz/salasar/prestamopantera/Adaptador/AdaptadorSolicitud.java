package xyz.salasar.prestamopantera.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import xyz.salasar.prestamopantera.R;

public class AdaptadorSolicitud extends BaseAdapter {
    private ArrayList<String> nombre,apellido,telefono,correo,usuario,id,identidad;
    private Context context;
    private static LayoutInflater inflater=null;
    private TextView txt_nombre, txt_telefono, txt_correo,txt_identidad;
    public AdaptadorSolicitud( Context context, ArrayList<String> nombre, ArrayList<String> apellido, ArrayList<String> telefono, ArrayList<String> correo, ArrayList<String> usuario, ArrayList<String> identidad, ArrayList<String> id){
        this.nombre=nombre;
        this.apellido=apellido;
        this.telefono=telefono;
        this.correo=correo;
        this.usuario=usuario;
        this.identidad=identidad;
        this.id=id;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
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
        final View vista=inflater.inflate(R.layout.solicitud_ingreso,null);
        txt_nombre = vista.findViewById(R.id.nombre665);
        txt_telefono = vista.findViewById(R.id.telefono665);
        txt_correo = vista.findViewById(R.id.correo665);
        txt_identidad = vista.findViewById(R.id.identidad665);
        txt_nombre.setText(nombre.get(position) + " " + apellido.get(position));
        txt_telefono.setText("Telefono: " + telefono.get(position));
        txt_correo.setText(correo.get(position));
        txt_identidad.setText("Identidad: " + identidad.get(position));
        return vista;
    }
}
