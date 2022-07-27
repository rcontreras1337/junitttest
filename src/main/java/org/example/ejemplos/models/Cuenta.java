package org.example.ejemplos.models;

import org.example.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Objects;

public class Cuenta {
    private String persona;
    private BigDecimal saldo;

    private Banco banco;

    public Cuenta(String persona, BigDecimal saldo) {
        this.persona = persona;
        this.saldo = saldo;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public void credito(BigDecimal monto) {
        // sumar
        this.saldo = this.saldo.add(monto);
    }

    public void debito(BigDecimal monto) {
        //Bigdecimal es inmutable porque al usar el subtract devuelve una instancia nueva que no esta referenciada a la memoria, por esa razon
        // de forma forzada debe asignarse el valor a this.saldo
        // restar saldo
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException("Dinero insuficiente");
        }
        this.saldo = nuevoSaldo;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cuenta)) {
            return false;
        }
        Cuenta c = (Cuenta) o;

        if (this.persona == null || this.saldo == null) {
            return false;
        }

        return this.persona.equals(c.getPersona()) && this.saldo.equals(c.getSaldo());
    }


}
