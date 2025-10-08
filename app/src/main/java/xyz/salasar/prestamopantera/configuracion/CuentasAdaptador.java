package xyz.salasar.prestamopantera.configuracion;

public class CuentasAdaptador {
    private String cuenta, credito, congelado, deuda, empresa, tipo, rango,intereses,porcentaje;

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCredito() {
        return credito;
    }

    public void setCredito(String credito) {
        this.credito = credito;
    }

    public String getDeuda() {
        return deuda;
    }

    public void setDeuda(String deuda) {
        this.deuda = deuda;
    }

    public String getCongelado() {
        return congelado;
    }

    public void setCongelado(String congelado) {
        this.congelado = congelado;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public String getIntereses() {
        return intereses;
    }

    public void setIntereses(String intereses) {
        this.intereses = intereses;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public CuentasAdaptador(String cuenta, String credito, String congelado, String deuda, String empresa, String tipo, String rango, String intereses, String porcentaje) {
        this.cuenta = cuenta;
        this.credito = credito;
        this.congelado = congelado;
        this.deuda = deuda;
        this.empresa = empresa;
        this.tipo = tipo;
        this.rango = rango;
        this.intereses=intereses;
        this.porcentaje=porcentaje;

    }
}
