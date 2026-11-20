package com.example.practica6algoritmos.Vista.Objetos.Graficas;

import com.example.practica6algoritmos.Modelo.Metodo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;

import java.util.ArrayList;
import java.util.HashMap;

public class GraficaPastel extends Grafica {

    private PieChart grafica;

    /**
     * Constructor de la gráfica de pastel
     * @param titulo Titulo de la gráfica, aunque como ya no la usamos asi es medio innecesario
     */
    public GraficaPastel(String titulo) {
        grafica = new PieChart();
        grafica.setTitle(titulo);
        grafica.setAnimated(false);
        grafica.setLegendVisible(false);
        grafica.setLabelsVisible(false);
        grafica.setPrefSize(500, 350);
    }

    /**
     * Metodo que cargaba los tiempos medidos en la gráfica de pastel, ahora ya no se usa debido
     * @param tiemposNs HashMap (Si, es un HashMap pero especializado en enumeraciones) para los
     *                  tiempos medidos del arreglo, lo usaba por es más comodo trabajar asi según
     *                  lo que encontre en google
     */
    @Override
    public void cargarTiempos(HashMap<Metodo, Long> tiemposNs) {
        double totalMs = 0;
        for (long ns : tiemposNs.values()) totalMs += ns / NS_A_MS;

        ObservableList<PieChart.Data> datos = FXCollections.observableArrayList();
        for (HashMap.Entry<Metodo, Long> entrada : tiemposNs.entrySet()) {
            double ms = entrada.getValue() / NS_A_MS;
            datos.add(new PieChart.Data(entrada.getKey().getNombre(), ms));
        }
        grafica.setData(datos);

        /*
        No se por que pero no asigna los estilos al momento de introducir los datos, asi que
        tengo que poner los estilos aqui, esta raro
         */
        int i = 0;
        for (HashMap.Entry<Metodo, Long> entrada : tiemposNs.entrySet()) {
            Node rebanada = datos.get(i++).getNode();
            rebanada.getStyleClass().add(claseCss(entrada.getKey()));
        }
    }

    /**
     * Getter de la gráfica
     * @return Gráfica de pastel
     */
    @Override
    public Node getGrafica() {
        return grafica;
    }

    /**
     * Metodo que carga los valores booleanos del cSv
     * en la gráfica de pastel,
     * @param valores ArrayList con todos los booleanos identificados
     */
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
            // Le puse un tooltip por que no se ve nada, literal nada, del tipo de dato
            // Y no quiero seguir modificando el tamaño de la tabla
            Tooltip.install(dato.getNode(), new Tooltip(texto + ": "  + dato.getPieValue()));
        }
    }

    /**
     * Metodo que ajusta la gráfica de pay a la celda, pero aqui no es tan necesario como la de barras
     */
    @Override
    public void convertirACelda() {
        grafica.setPadding(Insets.EMPTY);
    }
}
