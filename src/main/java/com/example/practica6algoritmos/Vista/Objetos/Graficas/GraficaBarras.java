package com.example.practica6algoritmos.Vista.Objetos.Graficas;

import com.example.practica6algoritmos.Modelo.Metodo;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class GraficaBarras extends Grafica {

    private BarChart<String, Number> grafica;
    private CategoryAxis ejeX;
    private NumberAxis ejeY;
    private Double minimoRango;
    private Double maximoRango;

    public GraficaBarras(String titulo) {
        ejeX = new CategoryAxis();
        ejeX.setLabel("Método de ordenamiento");

        ejeY = new NumberAxis();
        ejeY.setLabel("Tiempo (ms)");

        grafica = new BarChart<>(ejeX, ejeY);
        grafica.setTitle(titulo);
        grafica.setAnimated(false);
        grafica.setLegendVisible(false);
        grafica.setPrefSize(500, 350);
    }

    @Override
    public void cargarTiempos(Map<Metodo, Long> tiemposNs) {
        ArrayList<String> categorias = new ArrayList<>();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();

        for (Map.Entry<Metodo, Long> entrada : tiemposNs.entrySet()) {
            categorias.add(entrada.getKey().getNombre());
            serie.getData().add(new XYChart.Data<>(entrada.getKey().getNombre(), entrada.getValue() / NS_A_MS));
        }

        ejeX.setCategories(FXCollections.observableList(categorias));
        grafica.getData().clear();
        grafica.getData().add(serie);

        int i = 0;
        for (Map.Entry<Metodo, Long> entrada : tiemposNs.entrySet()) {
            Node barra = serie.getData().get(i++).getNode();
            barra.getStyleClass().add(claseCss(entrada.getKey()));
        }
    }

    @Override
    public void cargarValores(ArrayList<Double> valores) {
        if (valores.isEmpty()) return;

        int cantidadBarras = 10;
        double minimo = minimoRango != null ? minimoRango : Collections.min(valores);
        double maximo = maximoRango != null ? maximoRango : Collections.max(valores);
        double ancho = (maximo - minimo) / cantidadBarras;   // tamaño del rango de cada barra

        // conteos[i] = cuántos valores caen en el rango de la barra i
        int[] conteos = new int[cantidadBarras];
        for (double valor : valores) {
            int posicion = ancho == 0 ? 0 : (int) ((valor - minimo) / ancho);
            if (posicion < 0) posicion = 0;                                    // por debajo del rango: primera barra
            if (posicion >= cantidadBarras) posicion = cantidadBarras - 1;
            conteos[posicion]++;
        }

        ArrayList<String> categorias = new ArrayList<>();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (int i = 0; i < cantidadBarras; i++) {
            String nombre = String.valueOf(i + 1);   // nombre único por barra (si dos se llaman igual, JavaFX falla)
            categorias.add(nombre);
            serie.getData().add(new XYChart.Data<>(nombre, conteos[i]));
        }

        ejeX.setLabel(minimo + " - " + maximo);
        ejeX.setCategories(FXCollections.observableList(categorias));
        grafica.getData().clear();
        grafica.getData().add(serie);
    }

    private String formatear(double valor) {
        if (valor >= 1000000) return String.format("%.2fM", valor / 1000000);
        if (valor >= 1000) return String.format("%.1fk", valor / 1000);
        return valor == Math.rint(valor) ? String.valueOf((long) valor) : String.format("%.2f", valor);
    }

    public void setRango(double minimo, double maximo) {
        minimoRango = minimo;
        maximoRango = maximo;
    }

    @Override
    public void convertirACelda() {
        grafica.setTitle(null);
        grafica.setPadding(Insets.EMPTY);


        ejeX.setTickLabelsVisible(false);
        ejeX.setTickMarkVisible(false);
        ejeX.setTickLength(0);

        ejeY.setLabel(null);
        ejeY.setTickLabelsVisible(false);
        ejeY.setTickMarkVisible(false);
        ejeY.setMinorTickVisible(false);
        ejeY.setTickLength(0);

        grafica.setHorizontalGridLinesVisible(false);
        grafica.setVerticalGridLinesVisible(false);
        grafica.setBarGap(0);
        grafica.setCategoryGap(0);
    }

    @Override
    public Node getGrafica() {
        return grafica;
    }
}
