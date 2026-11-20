package com.example.practica6algoritmos.Vista.Objetos.Graficas;

import com.example.practica6algoritmos.Modelo.Metodo;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GraficaBarras extends Grafica {
    private final int MIN_BARRAS = 2;
    private final int MAX_BARRAS = 16;
    private BarChart<String, Number> grafica;
    private CategoryAxis ejeX;
    private NumberAxis ejeY;
    private Double minimoRango;
    private Double maximoRango;
    private int cantidadBarrasFijas = -1;

    /**
     * Constructor de la gráfica de barras
     *
     * @param titulo Titulo que tendrá la gráfica
     */
    public GraficaBarras(String titulo) {
        ejeX = crearEjeX();
        ejeY = crearEjeY();
        grafica = crearGrafica(titulo);
    }

    private CategoryAxis crearEjeX() {
        CategoryAxis eje = new CategoryAxis();
        eje.setLabel("Método de ordenamiento");
        return eje;
    }

    private NumberAxis crearEjeY() {
        NumberAxis eje = new NumberAxis();
        eje.setLabel("Tiempo (ms)");
        return eje;
    }

    private BarChart<String, Number> crearGrafica(String titulo) {
        BarChart<String, Number> barras = new BarChart<>(ejeX, ejeY);
        barras.setTitle(titulo);
        barras.setAnimated(false);
        barras.setLegendVisible(false);
        barras.setPrefSize(500, 350);
        return barras;
    }

    /**
     * Metodo que carga los tiempos de ordenamientos dados a la gráfica y genera las barras
     *
     * @param tiemposNs HashMap (Mapa de enumeración, usado especificamente para el Metodo)
     *                  parametrizado en el metodo y un tipo de dato long para los tiempos
     *                  de cada ordenamiento
     */
    @Override
    public void cargarTiempos(HashMap<Metodo, Long> tiemposNs) {
        ArrayList<String> categorias = new ArrayList<>();
        ArrayList<Metodo> metodos = new ArrayList<>();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();

        for (Metodo metodo : Metodo.values()) {
            Long tiempo = tiemposNs.get(metodo);
            if (tiempo == null) continue;   // método que no se ejecutó

            metodos.add(metodo);
            categorias.add(metodo.getNombre());
            serie.getData().add(new XYChart.Data<>(metodo.getNombre(), tiempo / NS_A_MS));
        }

        ejeX.setCategories(FXCollections.observableList(categorias));
        grafica.getData().clear();
        grafica.getData().add(serie);

        for (int i = 0; i < metodos.size(); i++) {
            Node barra = serie.getData().get(i).getNode();
            barra.getStyleClass().add(claseCss(metodos.get(i)));
        }
    }

    /**
     * Metodo que carga valores decimales en una grafica, usado para la gráfica de barras
     *
     * @param valores ArrayList con los valores del CSV
     */
    public void cargarValores(ArrayList<Double> valores) {
        if (valores.isEmpty()) {
            grafica.getData().clear();
            return;
        }

        double minimo = minimoRango != null ? minimoRango : Collections.min(valores);
        double maximo = maximoRango != null ? maximoRango : Collections.max(valores);

        int cantidadBarras = calcularCantidadBarras(valores, minimo, maximo);
        double ancho = (maximo - minimo) / cantidadBarras;   // tamaño del rango de cada barra

        int[] conteos = contarPorRango(valores, minimo, ancho, cantidadBarras);
        XYChart.Series<String, Number> serie = crearSerie(conteos);

        ejeX.setLabel(formatear(minimo) + " - " + formatear(maximo));
        ejeX.setCategories(FXCollections.observableList(nombresCategorias(cantidadBarras)));
        grafica.getData().clear();
        grafica.getData().add(serie);

        // Los nodos de las barras solo existen después de añadir la serie
        instalarTooltips(serie, conteos, minimo, ancho);
    }

    private int calcularCantidadBarras(ArrayList<Double> valores, double minimo, double maximo) {
        if (this.cantidadBarrasFijas > 0) return cantidadBarrasFijas;
        int n = valores.size();
        double rango = maximo - minimo;
        if (n < 2 || rango <= 0) return MIN_BARRAS;

        ArrayList<Double> ordenados = new ArrayList<>(valores);
        Collections.sort(ordenados);
        double iqr = percentil(ordenados, 0.75) - percentil(ordenados, 0.25);

        double barras;
        if (iqr > 0) {
            double anchoIdeal = 2 * iqr / Math.cbrt(n);
            barras = Math.ceil(rango / anchoIdeal);
        } else {
            barras = Math.ceil(Math.log(n) / Math.log(2)) + 1;
        }
        return (int) Math.max(MIN_BARRAS, Math.min(MAX_BARRAS, barras));
    }

    private double percentil(ArrayList<Double> ordenados, double fraccion) {
        double posicion = fraccion * (ordenados.size() - 1);
        int inferior = (int) Math.floor(posicion);
        int superior = (int) Math.ceil(posicion);
        double peso = posicion - inferior;
        return ordenados.get(inferior) * (1 - peso) + ordenados.get(superior) * peso;
    }

    private int[] contarPorRango(ArrayList<Double> valores, double minimo, double ancho, int cantidadBarras) {
        int[] conteos = new int[cantidadBarras];
        for (double valor : valores) {
            int posicion = ancho == 0 ? 0 : (int) ((valor - minimo) / ancho);
            if (posicion < 0) posicion = 0;                                    // por debajo del rango: primera barra
            if (posicion >= cantidadBarras) posicion = cantidadBarras - 1;     // por encima (o el máximo): última barra
            conteos[posicion]++;
        }
        return conteos;
    }

    private ArrayList<String> nombresCategorias(int cantidadBarras) {
        ArrayList<String> categorias = new ArrayList<>();
        for (int i = 0; i < cantidadBarras; i++) categorias.add(String.valueOf(i + 1));
        return categorias;
    }

    private XYChart.Series<String, Number> crearSerie(int[] conteos) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (int i = 0; i < conteos.length; i++) {
            serie.getData().add(new XYChart.Data<>(String.valueOf(i + 1), conteos[i]));
        }
        return serie;
    }

    private void instalarTooltips(XYChart.Series<String, Number> serie, int[] conteos, double minimo, double ancho) {
        for (int i = 0; i < conteos.length; i++) {
            Node barra = serie.getData().get(i).getNode();
            if (barra == null) continue;

            double desde = minimo + i * ancho;
            double hasta = desde + ancho;
            Tooltip tooltip = new Tooltip(
                    conteos[i] + " datos\n" + formatear(desde) + " - " + formatear(hasta));
            tooltip.setShowDelay(Duration.millis(100));   // por defecto tarda ~1 s en aparecer
            Tooltip.install(barra, tooltip);
        }
    }

    /**
     * Metodo que le da un formato al texto
     * que funciona como rango cuando lo graficamos como celda
     *
     * @param valor Rango dado
     * @return Cadena que representa un formato para el rango
     */
    private String formatear(double valor) {
        if (valor >= 1000000) return String.format("%.2fM", valor / 1000000);
        if (valor >= 1000) return String.format("%.1fk", valor / 1000);
        return valor == Math.round(valor) ? String.valueOf((long) valor) : String.format("%.2f", valor);
    }

    /**
     * Metodo que pone un rango a las gráficas,
     * usado especificamente para esa columna de entre miles y millones >:(
     *
     * @param minimo Rango minimo nuevo
     * @param maximo Rango maximo nuevp
     */
    public void setRango(double minimo, double maximo) {
        minimoRango = minimo;
        maximoRango = maximo;
    }

    /**
     * Metodo que hace que la gráfica de barras entre a una celda
     */
    @Override
    public void convertirACelda() {
        grafica.setTitle(null);
        grafica.setPadding(Insets.EMPTY);
        grafica.getStyleClass().add("grafica-celda");


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
        grafica.setCategoryGap(2);
    }

    public void setCantidadBarrasFijas(int cantidadBarras) {
        this.cantidadBarrasFijas = Math.max(1, cantidadBarras);
    }

    /**
     * Getter del nodo usado para la gráfica
     * @return Representación de la gráfica, en este caso uso un
     * Node por que me da errores con BarChart
     */
    @Override
    public Node getGrafica() {
        return grafica;
    }
}
