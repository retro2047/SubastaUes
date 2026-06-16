package com.example.subastaues.data.entities;

public class PujaConArticulo {
    public Double monto;
    public Long timestamp;
    public String nombreArticulo;

    public PujaConArticulo(Double monto, Long timestamp, String nombreArticulo) {
        this.monto = monto;
        this.timestamp = timestamp;
        this.nombreArticulo = nombreArticulo;
    }
}
