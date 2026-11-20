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

    /**
     * Metodo que crea el eje X de la gráfica
     * @return Eje de las abscisas para la gráfica, la cual representa el método de ordenamiento
     */
    private CategoryAxis crearEjeX() {
        CategoryAxis eje = new CategoryAxis();
        eje.setLabel("Método de ordenamiento");
        return eje;
    }

    /**
     * Metodo que crea el eje Y de la gráfica
     * @return Eje de las ordenadas para la gráfica, la cual mide el tiempo en milisegundos
     */
    private NumberAxis crearEjeY() {
        NumberAxis eje = new NumberAxis();
        eje.setLabel("Tiempo (ms)");
        return eje;
    }

    /**
     * Metodo que crea una gráfica de barras
     * @param titulo Titulo de la gráfica que tendrá
     * @return BarChart que representa a la gráfica, usando los ejes
     * y el titulo creados anteriormente
     */
    private BarChart<String, Number> crearGrafica(String titulo) {
        BarChart<String, Number> barras = new BarChart<>(ejeX, ejeY);
        barras.setTitle(titulo);
        barras.setAnimated(false);
        barras.setLegendVisible(false);
        barras.setPrefSize(500, 350);
        return barras;
    }

    /**
     * Metodo que carga los tiempos de ordenamientos dados a la
     * gráfica y genera las barras
     *
     * @param tiemposNs HashMap parametrizado en el metodo y un tipo de dato long para los tiempos
     *                  de cada ordenamiento
     */
    @Override
    public void cargarTiempos(HashMap<Metodo, Long> tiemposNs) {
        ArrayList<String> categorias = new ArrayList<>();
        ArrayList<Metodo> metodos = new ArrayList<>();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();

        for (Metodo metodo : Metodo.values()) {
            Long tiempo = tiemposNs.get(metodo);
            if (tiempo == null) continue;   // Todos los métodos no ejecutados no se añaden

            metodos.add(metodo);
            categorias.add(metodo.getNombre());
            serie.getData().add(new XYChart.Data<>(metodo.getNombre(), tiempo / NS_A_MS));
        }

        // Limpiamos la gráfica y añadimos los nuevos datos
        ejeX.setCategories(FXCollections.observableList(categorias));
        grafica.getData().clear();
        grafica.getData().add(serie);

        // Creamos las barras por cada metodo a gráficar
        for (int i = 0; i < metodos.size(); i++) {
            Node barra = serie.getData().get(i).getNode();
            barra.getStyleClass().add(claseCss(metodos.get(i)));
        }
    }

    /**
     * Metodo que carga las celdas que usan valores decimales en la gráfica de barras
     *
     * @param valores ArrayList con los valores del CSV a gráficar
     */
    public void cargarValores(ArrayList<Double> valores) {
        if (valores.isEmpty()) {
            grafica.getData().clear();
            return;
        }

        // Asignamos el rango de la gráfica, especificamente para
        // cambiar el rango con la columna de duration ms
        double minimo = minimoRango != null ? minimoRango : Collections.min(valores);
        double maximo = maximoRango != null ? maximoRango : Collections.max(valores);

        // Calculamos la cantidad de barras a gráficar,
        // aunque en la seccion de tablas realmente las asignamos
        int cantidadBarras = calcularCantidadBarras(valores, minimo, maximo);
        // El ancho es el rango que existe entre un intervalo a otro
        double ancho = (maximo - minimo) / cantidadBarras;

        // Contamos la cantidad de valores que entran en el rango dado
        int[] conteos = contarPorRango(valores, minimo, ancho, cantidadBarras);
        XYChart.Series<String, Number> serie = crearSerie(conteos);

        // Le damos al ejeX los rangos creados para visualizar eso en la tabla
        ejeX.setLabel(formatear(minimo) + " - " + formatear(maximo));
        ejeX.setCategories(FXCollections.observableList(nombresCategorias(cantidadBarras)));
        grafica.getData().clear();
        grafica.getData().add(serie);

        // Añadimos un tooltip para al menos dar un sentido de que cosas poner
        instalarTooltips(serie, conteos, minimo, ancho);
    }

    /**
     * Metodo que calcula la cantidad de barras en la grafica
     * @param valores ArrayList con los valores a gráficar
     * @param minimo Valor que indica el minímo del rango
     * @param maximo Valor que indica el máximo del rango
     * @return Valor que indica la cantidad de barras a graficar
     */
    private int calcularCantidadBarras(ArrayList<Double> valores, double minimo, double maximo) {
        // Si ya tenemos una cantidad de barras fijas, retornamos eso
        if (this.cantidadBarrasFijas > 0) return cantidadBarrasFijas;
        int n = valores.size();
        double rango = maximo - minimo;
        if (n < 2 || rango <= 0) return MIN_BARRAS;

        // Ordenamos en otro arraylist los valores
        ArrayList<Double> ordenados = new ArrayList<>(valores);
        Collections.sort(ordenados);
        // Ahora obtenemos el percentil con el fin de calcular el ancho ideal y la cantidad de barras a usar
        double iqr = percentil(ordenados, 0.75) - percentil(ordenados, 0.25);

        double barras;
        if (iqr > 0) {
            // (2 * iqr(x))/sqrt^3(n
            double anchoIdeal = 2 * iqr / Math.cbrt(n);
            barras = Math.ceil(rango / anchoIdeal);
        } else {
            barras = Math.ceil(Math.log(n) / Math.log(2)) + 1;
        }
        // Regresamos ya sea el minimo de barras
        return (int) Math.max(MIN_BARRAS, Math.min(MAX_BARRAS, barras));
    }

    /**
     * Metodo que calcula el percentil indicado sobre la lista de datos ordenados
     * @param ordenados ArrayList con los datos, ya previamente ordenados
     * @param fraccion Fracción correspondiente al percentil que deseamos
     * @return Valor correspondiente al percentil calculado
     */
    private double percentil(ArrayList<Double> ordenados, double fraccion) {
        // Obtenemos la posición multiplicando el tamaño del arraylist por la fraccion,
        // y luego obtenemos su dato inferior y superior
        double posicion = fraccion * (ordenados.size() - 1);
        int inferior = (int) Math.floor(posicion);
        int superior = (int) Math.ceil(posicion);
        double peso = posicion - inferior;
        return ordenados.get(inferior) * (1 - peso) + ordenados.get(superior) * peso;
    }

    /**
     * Metodo que cuenta todos los valores que entran al rango
     * @param valores ArrayLsit de valores
     * @param minimo Limite inferior del rango actual
     * @param ancho Amplitud existente en cada intervalor
     * @param cantidadBarras Cantidad de barras que existen en la gráfica
     * @return Arreglo de datos donde cada posición almacena la frecuencia de datos en la barra
     */
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

    /**
     * Metodo que le asigna etiquetas secuenciales para las categorias del eje x
     * @param cantidadBarras Cantidad de categorias a generar
     * @return ArrayList con los indices empezando en 1
     */
    private ArrayList<String> nombresCategorias(int cantidadBarras) {
        ArrayList<String> categorias = new ArrayList<>();
        for (int i = 0; i < cantidadBarras; i++) categorias.add(String.valueOf(i + 1));
        return categorias;
    }

    /**
     * Metodo que crea la serie de numeros
     * @param conteos Arreglo con las frecuencias de cada barra
     * @return Serie de datos lista para ser añadida a la grafica
     */
    private XYChart.Series<String, Number> crearSerie(int[] conteos) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (int i = 0; i < conteos.length; i++) {
            serie.getData().add(new XYChart.Data<>(String.valueOf(i + 1), conteos[i]));
        }
        return serie;
    }

    /**
     * Asocia un tooltip a las gráficas, usado solo para la de celda
     * @param serie Serie de datos añadidos a la tabla
     * @param conteos Arreglo con las frecuencias de cada barra
     * @param minimo Valor minimo del rango general
     * @param ancho Amplitud de cada intervalo
     */
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
