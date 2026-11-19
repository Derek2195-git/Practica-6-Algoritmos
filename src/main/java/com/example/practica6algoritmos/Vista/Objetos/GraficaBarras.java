package com.example.practica6algoritmos.Vista.Objetos;

import com.example.practica6algoritmos.Modelo.Metodo;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Map;

public class GraficaBarras extends Grafica {

    private BarChart<String, Number> grafica;
    private CategoryAxis ejeX;
    private NumberAxis ejeY;

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
    public Node getGrafica() {
        return grafica;
    }
}
