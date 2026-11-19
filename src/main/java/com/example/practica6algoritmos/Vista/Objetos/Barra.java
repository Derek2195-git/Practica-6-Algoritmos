package com.example.practica6algoritmos.Vista.Objetos;

import javafx.scene.layout.Pane;

public class Barra extends Pane {

    private static final double ANCHO_MINIMO = 5;
    private static final double SEPARACION = 2;
    private static final double ALTO = 16;

    private double[] fracciones;

    public Barra() {
         fracciones = new double[0];
         setPrefHeight(ALTO);

    }

    public void setFracciones(double[] fracciones) {
        this.fracciones = fracciones;
        requestLayout();
    }

    @Override
    protected void layoutChildren() {
        int n = getChildren().size();
        if (n == 0 || fracciones.length != n) return;

        double disponible = getWidth() - SEPARACION * (n - 1);
        double[] anchos = repartir(disponible);
        double x = 0;
        for (int i = 0; i < n; i++) {
            getChildren().get(i).resizeRelocate(x, 0, anchos[i], getHeight());
            x += anchos[i] + SEPARACION;
        }
    }

    private double[] repartir(double disponible) {
        int n = fracciones.length;
        double[] anchos = new double[n];

        // Ventana demasiado angosta para respetar el mínimo: todos iguales.
        if (disponible <= ANCHO_MINIMO * n) {
            java.util.Arrays.fill(anchos, Math.max(0, disponible) / n);
            return anchos;
        }

        boolean[] fijo = new boolean[n];
        boolean cambio = true;
        double restante = disponible;
        double sumaLibres = 1;
        while (cambio) {
            cambio = false;
            int fijos = 0;
            sumaLibres = 0;
            for (int i = 0; i < n; i++) {
                if (fijo[i]) fijos++;
                else sumaLibres += fracciones[i];
            }
            restante = disponible - ANCHO_MINIMO * fijos;
            for (int i = 0; i < n; i++) {
                if (!fijo[i] && restante * fracciones[i] / sumaLibres < ANCHO_MINIMO) {
                    fijo[i] = true;
                    cambio = true;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            anchos[i] = fijo[i] ? ANCHO_MINIMO : restante * fracciones[i] / sumaLibres;
        }
        return anchos;
    }
}
