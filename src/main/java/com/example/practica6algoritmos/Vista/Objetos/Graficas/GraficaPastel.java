package com.example.practica6algoritmos.Vista.Objetos.Graficas;

import com.example.practica6algoritmos.Modelo.Metodo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;

import java.util.ArrayList;
import java.util.Map;

public class GraficaPastel extends Grafica {

    private PieChart grafica;

    public GraficaPastel(String titulo) {
        grafica = new PieChart();
        grafica.setTitle(titulo);
        grafica.setAnimated(false);
        grafica.setLegendVisible(false);   // la leyenda la dibuja la ventana
        grafica.setLabelsVisible(false);
        grafica.setPrefSize(500, 350);
    }

    @Override
    public void cargarTiempos(Map<Metodo, Long> tiemposNs) {
        double totalMs = 0;
        for (long ns : tiemposNs.values()) totalMs += ns / NS_A_MS;

        ObservableList<PieChart.Data> datos = FXCollections.observableArrayList();
        for (Map.Entry<Metodo, Long> entrada : tiemposNs.entrySet()) {
            double ms = entrada.getValue() / NS_A_MS;
            datos.add(new PieChart.Data(entrada.getKey().getNombre(), ms));
        }
        grafica.setData(datos);

        /*
        PieChart reescribe las clases CSS de cada rebanada al crearla, así que la clase del
        metodo se agrega hasta DESPUÉS de asignar los datos a la gráfica.
         */
        int i = 0;
        for (Map.Entry<Metodo, Long> entrada : tiemposNs.entrySet()) {
            Node rebanada = datos.get(i++).getNode();
            double porcentaje = totalMs == 0 ? 0 : entrada.getValue() / NS_A_MS * 100 / totalMs;
            rebanada.getStyleClass().add(claseCss(entrada.getKey()));

        }
    }

    @Override
    public Node getGrafica() {
        return grafica;
    }

    public void cargarValoresBooleanos(ArrayList<Boolean> valores) {
        int verdaderos = 0;
        int falsos = 0;
        int nulos = 0;

        for (Boolean bool : valores) {
            if (bool == null) nulos++;
            else if(bool) verdaderos++;
            else falsos++;
        }

        ObservableList<PieChart.Data> datos = FXCollections.observableArrayList();
        if (verdaderos > 0) datos.add(new PieChart.Data("true", verdaderos));
        if (falsos > 0) datos.add(new PieChart.Data("false", falsos));
        if (nulos > 0) datos.add(new PieChart.Data("null", nulos));
        grafica.setData(datos);
        for (PieChart.Data dato : datos) {
            String texto = switch(dato.getName()) {
                case "true" -> "True";
                case "false"-> "False";
                default-> "Null";
            };
            Tooltip.install(dato.getNode(), new Tooltip(texto + ": "  + dato.getPieValue()));
        }
    }

    @Override
    public void convertirACelda() {

    }
}
