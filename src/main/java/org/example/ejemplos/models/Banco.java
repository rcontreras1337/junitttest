package org.example.ejemplos.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Banco {

    private List<Cuenta> cuentas;
    private String nombreBanco;

    public Banco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
        this.cuentas = new ArrayList<>();
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public void addCuentas(Cuenta cuentaAgregada) {
        cuentas.add(cuentaAgregada);
        cuentaAgregada.setBanco(this);
    }

    public void trasnferir(Cuenta origen, Cuenta destino, BigDecimal monto) {
        origen.debito(monto);
        destino.credito(monto);

    }


}
