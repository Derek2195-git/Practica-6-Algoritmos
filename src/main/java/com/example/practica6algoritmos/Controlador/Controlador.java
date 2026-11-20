package com.example.practica6algoritmos.Controlador;

import com.example.practica6algoritmos.Modelo.*;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.Grafica;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.TipoGrafica;
import com.example.practica6algoritmos.Vista.Ventana.VentanaGrafica;
import com.example.practica6algoritmos.Vista.Ventana.VentanaPrincipal;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Controlador {

    private Stage stage;
    private ConjuntoDatos conjuntoDatos;
    private VentanaPrincipal ventana;
    private VentanaGrafica ventanaGrafica;
    private HashMap<Metodo, Long> tiempos;
    private final long TAMANO_MAXIMO = 50 * 1024 * 1024;


    /**
     * Constructor
     * @param stage Stage pasado por el launcher
     * @throws IOException Excepciones de entrada y salida generadas en caso
     * de que el conjunto de datos no pueda abrir el csv
     */
    public Controlador(Stage stage) throws IOException {
        this.stage = stage;
        conjuntoDatos = new ConjuntoDatos();
        tiempos = new HashMap<>();

        ventana = new VentanaPrincipal(conjuntoDatos.getNombresColumnas());
        ventana.getSeccionTabla().mostrarCanciones(conjuntoDatos.getCanciones());

        Scene escena = new Scene(ventana.getVentana(), 800, 600);

        ventana.getSeccionSuperior().alGraficar(this::graficarOrdenamientos);
        ventana.getSeccionSuperior().alRestablecer(this::restablecerTabla);
        ventana.getSeccionSuperior().alOrdenar(this::ordenarColumna);
        ventana.getSeccionSuperior().alCambiarColumna(this::cambiarColumna);

        stage.setTitle("Ordenamientos sobre un dataset");
        stage.setScene(escena);
        stage.show();
    }

    /**
     * Devuelve un Comparator para objetos de tipo Cancion
     * dependiendo del campo dado en el CSV
     * El criterio se crea a través de un getter
     */
    private Comparator<Cancion> getComparator(String columna) {
        return switch (columna) {
            // Texto
            case "song_id" -> porTexto(Cancion::getSong_id);
            case "performer" -> porTexto(Cancion::getPerformer);
            case "song" -> porTexto(Cancion::getSong);
            case "spotify_genre" -> porTexto(Cancion::getSpotify_genre);
            case "spotify_track_id" -> porTexto(Cancion::getSpotify_track_id);
            case "spotify_track_preview_url" -> porTexto(Cancion::getSpotify_track_preview_url);
            case "spotify_track_album" -> porTexto(Cancion::getSpotify_track_album);
            case "first_week" -> porTexto(Cancion::getFirst_week);
            case "last_week" -> porTexto(Cancion::getLast_week);
            case "chart_success_tier" -> porTexto(Cancion::getChart_success_tier);
            case "hit_longevity_profile" -> porTexto(Cancion::getHit_longevity_profile);
            case "acoustic_mood_cluster" -> porTexto(Cancion::getAcoustic_mood_cluster);
            case "musical_era" -> porTexto(Cancion::getMusical_era);
            case "evidence_span" -> porTexto(Cancion::getEvidence_span);
            case "cot_rationale" -> porTexto(Cancion::getCot_rationale);

            // Enteros
            case "spotify_track_duration_ms" -> porOrdenNatural(Cancion::getSpotify_track_duration_ms);
            case "key" -> porOrdenNatural(Cancion::getKey);
            case "mode" -> porOrdenNatural(Cancion::getMode);
            case "time_signature" -> porOrdenNatural(Cancion::getTime_signature);
            case "spotify_track_popularity" -> porOrdenNatural(Cancion::getSpotify_track_popularity);
            case "peak_position" -> porOrdenNatural(Cancion::getPeak_position);
            case "weeks_on_chart" -> porOrdenNatural(Cancion::getWeeks_on_chart);

            // Decimales
            case "danceability" -> porOrdenNatural(Cancion::getDanceability);
            case "energy" -> porOrdenNatural(Cancion::getEnergy);
            case "loudness" -> porOrdenNatural(Cancion::getLoudness);
            case "speechiness" -> porOrdenNatural(Cancion::getSpeechiness);
            case "acousticness" -> porOrdenNatural(Cancion::getAcousticness);
            case "instrumentalness" -> porOrdenNatural(Cancion::getInstrumentalness);
            case "liveness" -> porOrdenNatural(Cancion::getLiveness);
            case "valence" -> porOrdenNatural(Cancion::getValence);
            case "tempo" -> porOrdenNatural(Cancion::getTempo);
            case "confidence_score" -> porOrdenNatural(Cancion::getConfidence_score);

            // Booleano
            case "spotify_track_explicit" -> porOrdenNatural(Cancion::getSpotify_track_explicit);

            default -> throw new IllegalArgumentException("Columna desconocida: " + columna);
        };
    }

    /**
     * Crea un "objeto" tipo comparator el cual compara la canción por el texto
     * @param campo Getter a llamar que extrae el atributo de la canción
     * @return Comparador configurado para el tipo de dato especificado
     */
    private static Comparator<Cancion> porTexto(Function<Cancion, String> campo) {
        return Comparator.comparing(campo, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Asigna un objeto Comparator el cual compara la canción por un número o un booleano, lo uso para lo que no sea texto
     * @param campo Getter a llamar que extrae el atributo de la canción
     * @return Comparador configurado para el tipo de dato especificado
     * @param <T> Tipo de dato del atributo
     */
    private static <T extends Comparable<T>> Comparator<Cancion> porOrdenNatural(Function<Cancion, T> campo) {
        return Comparator.comparing(campo, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    /**
     *  Maneja los cambios de columnas al limpiar el tiempo empleado para cambiar dicha columna
     * @param columna Columna cambiada
     */
    private void cambiarColumna(String columna) {
        tiempos.clear();
        mostrarTiempos();
    }

    /**
     * Metodo que realiza el ordenamiento en una de las columnas de la tabla
     */
    private void ordenarColumna() {
        String columna = ventana.getSeccionSuperior().getColumnaSeleccionada();
        Metodo elegido = ventana.getSeccionSuperior().getMetodoSeleccionado();
        boolean todos = ventana.getSeccionSuperior().isCompararActivado();
        if (columna == null || (!todos && elegido == null)) return;

        List<Metodo> metodos = todos ? Arrays.asList(Metodo.values()) : List.of(elegido);

        tiempos.clear();
        Cancion.setCriterio(getComparator(columna));

        ventana.getSeccionSuperior().setOcupado(true);
        ventana.getSeccionSuperior().setTiemposDisponibles(false);
        ventana.getSeccionSuperior().setTiempoOrdenamiento("Ordenando…");

        // Creamos un hilo donde hagamos el ordenamiento por si ocurren errores
        Thread hilo = new Thread(null, () -> {
            HashMap<Metodo, Long> resultados = new HashMap<>();
            HashMap<Metodo, Throwable> fallos = new HashMap<>();
            Cancion[] ultimaOrdenada = null;

            for (int i = 0; i < metodos.size(); i++) {
                Metodo metodo = metodos.get(i);
                int numero = i + 1;
                Platform.runLater(() -> ventana.getSeccionSuperior().setTiempoOrdenamiento(
                        "Ordenando con " + metodo + " (" + numero + " de " + metodos.size() + ")"));

                Cancion[] copia = conjuntoDatos.crearCopiaOrdenable();   // cada método parte del orden original
                try {
                    long ns = new ComparadorTiempos<Cancion>(metodo).medirTiempo(copia);
                    resultados.put(metodo, ns);
                    ultimaOrdenada = copia;
                } catch (Throwable error) {
                    fallos.put(metodo, error);
                }
            }

            Cancion[] ordenadas = ultimaOrdenada;
            Platform.runLater(() -> alTerminarOrdenamiento(resultados, ordenadas));
        }, "ordenamiento", TAMANO_MAXIMO);
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Metodo que muestra los tiempos de la columna empleada una vez acabado el ordenamiento
     * @param resultados  Tiempos de cada ordenamiento
     * @param ordenadas
     */
    private void alTerminarOrdenamiento(HashMap<Metodo, Long> resultados, Cancion[] ordenadas) {
        tiempos.putAll(resultados);
        if (ordenadas != null) {
            ventana.getSeccionTabla().mostrarCanciones(new ArrayList<>(Arrays.asList(ordenadas)));
        }
        mostrarTiempos();
        ventana.getSeccionSuperior().setOcupado(false);
        ventana.getSeccionSuperior().setTiemposDisponibles(!tiempos.isEmpty());
    }

    /**
     * 
     */
    private void mostrarTiempos() {
        if (tiempos.isEmpty()) {
            ventana.getSeccionSuperior().setTiempoOrdenamiento("--");
            return;
        }
        Metodo masRapido = null;
        for (HashMap.Entry<Metodo, Long> e : tiempos.entrySet()) {
            if (masRapido == null || e.getValue() < tiempos.get(masRapido)) masRapido = e.getKey();
        }
        String tiempoRapido = Grafica.formatearTiempo(tiempos.get(masRapido));
        String texto = tiempos.size() == 1
                ? masRapido + ": " + tiempoRapido
                : "Más rápido: " + masRapido;
        ventana.getSeccionSuperior().setTiempoOrdenamiento(texto);
    }

    /**
     * Metodo que restablece los tiempos de la tabla
     */
    private void restablecerTabla() {
        String columna = ventana.getSeccionSuperior().getColumnaSeleccionada();
        if (columna == null) return;
        cambiarColumna(columna);
        ventana.getSeccionSuperior().setTiemposDisponibles(false);
        ventana.getSeccionSuperior().setTiempoOrdenamiento("--");
    }

    /**
     * Metodo que gráfica los resultados dados por los ordenamientos
     */
    private void graficarOrdenamientos() {
        String columna = ventana.getSeccionSuperior().getColumnaSeleccionada();
        if (columna == null || tiempos.isEmpty()) return;
        if (ventanaGrafica == null) ventanaGrafica = new VentanaGrafica(stage);
        ventanaGrafica.mostrar(columna, TipoGrafica.BARRAS, tiempos);
    }

}