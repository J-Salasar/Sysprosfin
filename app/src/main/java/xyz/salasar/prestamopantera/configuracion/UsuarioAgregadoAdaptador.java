package xyz.salasar.prestamopantera.configuracion;

public class UsuarioAgregadoAdaptador {
    private String nombre,apellido,cuentas,rango,credito,capital,intereses,usuario;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCuentas() {
        return cuentas;
    }

    public void setCuentas(String cuentas) {
        this.cuentas = cuentas;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getIntereses() {
        return intereses;
    }

    public void setIntereses(String intereses) {
        this.intereses = intereses;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCredito() {
        return credito;
    }

    public void setCredito(String credito) {
        this.credito = credito;
    }

    public UsuarioAgregadoAdaptador(String nombre, String apellido, String cuenta, String rango, String credito, String capital, String intereses, String usuario){
        this.nombre=nombre;
        this.apellido=apellido;
        this.cuentas=cuenta;
        this.rango=rango;
        this.credito=credito;
        this.capital=capital;
        this.intereses=intereses;
        this.usuario=usuario;
    }
}
