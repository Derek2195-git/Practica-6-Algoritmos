package com.example.practica6algoritmos.Vista.Objetos.Graficas;

import com.example.practica6algoritmos.Modelo.Metodo;
import javafx.scene.Node;

import java.util.Map;

public abstract class Grafica {
    protected static final double NS_A_MS = 1000000.0;

    public abstract void cargarTiempos(Map<Metodo, Long> tiemposNs);

    // por si las dudas lo voy a regresar como Node, aunque sea un BarChart
    public abstract Node getGrafica();

    public static String formatearTiempo(long tiempo) {
        return String.format("%.3f ms", tiempo / NS_A_MS);
    }

    public static String claseCss(Metodo metodo) {
        return "metodo-" + metodo.name().toLowerCase();
    }


}
