package xyz.salasar.prestamopantera.configuracion;

public class EmpresasAdaptador {
    private String usuario, empresas,agregado,proceso;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmpresas() {
        return empresas;
    }

    public void setEmpresas(String empresas) {
        this.empresas = empresas;
    }

    public String getAgregado() {
        return agregado;
    }

    public void setAgregado(String agregado) {
        this.agregado = agregado;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public EmpresasAdaptador(String usuario, String empresas, String agregado, String proceso){
        this.empresas=empresas;
        this.usuario=usuario;
        this.agregado=agregado;
        this.proceso=proceso;
    }
}
