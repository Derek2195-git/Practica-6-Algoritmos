package com.example.practica6algoritmos.Vista.Objetos.Graficas;

import com.example.practica6algoritmos.Modelo.Metodo;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public abstract class Grafica {
    // Constante para convertir los nanosegundos a milisegundos
    protected static final double NS_A_MS = 1000000.0;

    public abstract void cargarTiempos(EnumMap<Metodo, Long> tiemposNs);

    // por si las dudas lo voy a regresar como Node, aunque sea un BarChart
    public abstract Node getGrafica();

    public static String formatearTiempo(long tiempo) {
        return String.format("%.3f ms", tiempo / NS_A_MS);
    }

    /**
     * Metodo que cambia el color de una de las barras de la gráfica
     * dependiendo del metodo y gracias al archivo scs
     * @param metodo Metodo a revisar
     * @return Cadena que contiene la clase en css a emplear
     */
    public static String claseCss(Metodo metodo) {
        return "metodo-" + metodo.name().toLowerCase();
    }

    /**
     * Metodo que carga valores decimales en una grafica, usado para la gráfica de barras
     * @param valores ArrayList de valores flotantes (osea de tipo double) a graficar
     */
    public void cargarValores(ArrayList<Double> valores) {
        System.out.println("Cargando valores a la grafica de barras...");
    }

    /**
     * Metodo que carga los valores booleanos en una gráfica, usada para la de pastel
     * @param valores ArrayList con los datos a graficar
     */
    public void cargarValoresBooleanos(ArrayList<Boolean> valores) {
        System.out.println("Cargando valores a la grafica de pastel...");
    }

    /**
     * Metodo abstracto que se usa para convertir la grafica hecha a una celda para la tabla
     */
    public abstract void convertirACelda();
}
