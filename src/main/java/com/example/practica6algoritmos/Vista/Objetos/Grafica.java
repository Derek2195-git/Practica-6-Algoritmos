package com.example.practica6algoritmos.Vista.Objetos;

import com.example.practica6algoritmos.Modelo.Metodo;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

import java.util.ArrayList;
import java.util.Map;

public abstract class Grafica {
    protected final double NS_A_MS = 1000000.0;

    public abstract void cargarTiempos(Map<Metodo, Long> tiemposNs);

    // por si las dudas lo voy a regresar como Node, aunque sea un BarChart
    public abstract Node getGrafica();

    public String formatearTiempo(long tiempo) {
        return String.format("%.3f ms", tiempo / NS_A_MS);
    }

    public String claseCss(Metodo metodo) {
        return "metodo-" + metodo.name().toLowerCase();
    }


}
