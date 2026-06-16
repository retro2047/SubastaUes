package com.example.subastaues.data.entities;

public class PujaConArticulo {
    public double monto;
    public long timestamp;
    public String nombreArticulo;

    public PujaConArticulo(double monto, long timestamp, String nombreArticulo) {
        this.monto = monto;
        this.timestamp = timestamp;
        this.nombreArticulo = nombreArticulo;
    }
}
