package xyz.salasar.prestamopantera.configuracion;

public class HistorialAdaptador {
    private String referencia,empresaON,tipoON,nombreON,apellidoON,cuentaON, cantidad, descripcionNO, descripcionND, fecha, nombreDN, apellidoDN, cuentaDN, empresaDN, tipoDN;

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCuentaDN() {
        return cuentaDN;
    }

    public void setCuentaDN(String cuentaDN) {
        this.cuentaDN = cuentaDN;
    }

    public String getApellidoDN() {
        return apellidoDN;
    }

    public void setApellidoDN(String apellidoDN) {
        this.apellidoDN = apellidoDN;
    }

    public String getNombreDN() {
        return nombreDN;
    }

    public void setNombreDN(String nombreDN) {
        this.nombreDN = nombreDN;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcionND() {
        return descripcionND;
    }

    public void setDescripcionND(String descripcionND) {
        this.descripcionND = descripcionND;
    }

    public String getDescripcionNO() {
        return descripcionNO;
    }

    public void setDescripcionNO(String descripcionNO) {
        this.descripcionNO = descripcionNO;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getEmpresaDN() {
        return empresaDN;
    }

    public void setEmpresaDN(String empresaDN) {
        this.empresaDN = empresaDN;
    }

    public String getTipoDN() {
        return tipoDN;
    }

    public void setTipoDN(String tipoDN) {
        this.tipoDN = tipoDN;
    }

    public String getCuentaON() {
        return cuentaON;
    }

    public void setCuentaON(String cuentaON) {
        this.cuentaON = cuentaON;
    }

    public String getEmpresaON() {
        return empresaON;
    }

    public void setEmpresaON(String empresaON) {
        this.empresaON = empresaON;
    }

    public String getApellidoON() {
        return apellidoON;
    }

    public void setApellidoON(String apellidoON) {
        this.apellidoON = apellidoON;
    }

    public String getNombreON() {
        return nombreON;
    }

    public void setNombreON(String nombreON) {
        this.nombreON = nombreON;
    }

    public String getTipoON() {
        return tipoON;
    }

    public void setTipoON(String tipoON) {
        this.tipoON = tipoON;
    }

    public HistorialAdaptador(String referencia, String empresaON, String tipoON, String nombreON, String apellidoON , String cuentaON, String cantidad, String descripcionNO, String descripcionND, String fecha, String nombreDN, String apellidoDN, String cuentaDN, String empresaDN, String tipoDN){
        this.referencia=referencia;
        this.empresaON=empresaON;
        this.tipoON=tipoON;
        this.nombreON=nombreON;
        this.apellidoON=apellidoON;
        this.cuentaON=cuentaON;
        this.cantidad=cantidad;
        this.descripcionNO=descripcionNO;
        this.descripcionND=descripcionND;
        this.fecha=fecha;
        this.nombreDN=nombreDN;
        this.apellidoDN=apellidoDN;
        this.cuentaDN=cuentaDN;
        this.empresaDN=empresaDN;
        this.tipoDN=tipoDN;
    }
}
