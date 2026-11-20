package com.example.practica6algoritmos.Controlador;

import com.example.practica6algoritmos.Modelo.*;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.Grafica;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.TipoGrafica;
import com.example.practica6algoritmos.Vista.Ventana.VentanaGrafica;
import com.example.practica6algoritmos.Vista.Ventana.VentanaPrincipal;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Controlador {

    private Stage stage;
    private ConjuntoDatos conjuntoDatos;
    private VentanaPrincipal ventana;
    private VentanaGrafica ventanaGrafica;
    private EnumMap<Metodo, Long> tiempos;
    private final long TAMANO_MAXIMO = 50 * 1024 * 1024;


    public Controlador(Stage stage) throws IOException {
        this.stage = stage;
        conjuntoDatos = new ConjuntoDatos();
        tiempos = new EnumMap<>(Metodo.class);

        ventana = new VentanaPrincipal(conjuntoDatos.getNombresColumnas());
        ventana.getSeccionTabla().mostrarCanciones(conjuntoDatos.getCanciones());

        Scene escena = new Scene(ventana.getVentana(), 800, 600);

        ventana.getSeccionSuperior().alGraficar(this::graficarTabla);
        ventana.getSeccionSuperior().alRestablecer(this::restablecerTabla);
        ventana.getSeccionSuperior().alOrdenar(this::ordenarTabla);
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
            case "spotify_track_duration_ms" -> porNumeroOBool(Cancion::getSpotify_track_duration_ms);
            case "key" -> porNumeroOBool(Cancion::getKey);
            case "mode" -> porNumeroOBool(Cancion::getMode);
            case "time_signature" -> porNumeroOBool(Cancion::getTime_signature);
            case "spotify_track_popularity" -> porNumeroOBool(Cancion::getSpotify_track_popularity);
            case "peak_position" -> porNumeroOBool(Cancion::getPeak_position);
            case "weeks_on_chart" -> porNumeroOBool(Cancion::getWeeks_on_chart);

            // Decimales
            case "danceability" -> porNumeroOBool(Cancion::getDanceability);
            case "energy" -> porNumeroOBool(Cancion::getEnergy);
            case "loudness" -> porNumeroOBool(Cancion::getLoudness);
            case "speechiness" -> porNumeroOBool(Cancion::getSpeechiness);
            case "acousticness" -> porNumeroOBool(Cancion::getAcousticness);
            case "instrumentalness" -> porNumeroOBool(Cancion::getInstrumentalness);
            case "liveness" -> porNumeroOBool(Cancion::getLiveness);
            case "valence" -> porNumeroOBool(Cancion::getValence);
            case "tempo" -> porNumeroOBool(Cancion::getTempo);
            case "confidence_score" -> porNumeroOBool(Cancion::getConfidence_score);

            // Booleano
            case "spotify_track_explicit" -> porNumeroOBool(Cancion::getSpotify_track_explicit);

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
    private static <T extends Comparable<T>> Comparator<Cancion> porNumeroOBool(Function<Cancion, T> campo) {
        return Comparator.comparing(campo, Comparator.nullsLast(Comparator.<T>naturalOrder()));
    }

    /**
     *
     * @param columna
     */
    private void cambiarColumna(String columna) {
        tiempos.clear();
        mostrarTiempos();
    }

    /**
     * 
     */
    private void ordenarTabla() {
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

        Thread hilo = new Thread(null, () -> {
            EnumMap<Metodo, Long> resultados = new EnumMap<>(Metodo.class);
            EnumMap<Metodo, Throwable> fallos = new EnumMap<>(Metodo.class);
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
            Platform.runLater(() -> alTerminarOrdenamiento(columna, resultados, fallos, ordenadas));
        }, "ordenamiento", TAMANO_MAXIMO);
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * 
     * @param columna
     * @param resultados
     * @param fallos
     * @param ordenadas
     */
    private void alTerminarOrdenamiento(String columna, Map<Metodo, Long> resultados,
                                        Map<Metodo, Throwable> fallos, Cancion[] ordenadas) {
        tiempos.putAll(resultados);
        if (ordenadas != null) {
            ventana.getSeccionTabla().mostrarCanciones(new ArrayList<>(Arrays.asList(ordenadas)));
        }
        mostrarTiempos();
        ventana.getSeccionSuperior().setOcupado(false);
        ventana.getSeccionSuperior().setTiemposDisponibles(!tiempos.isEmpty());
        if (!fallos.isEmpty()) mostrarErrores(fallos);
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
        for (Map.Entry<Metodo, Long> e : tiempos.entrySet()) {
            if (masRapido == null || e.getValue() < tiempos.get(masRapido)) masRapido = e.getKey();
        }
        String tiempoRapido = Grafica.formatearTiempo(tiempos.get(masRapido));
        String texto = tiempos.size() == 1
                ? masRapido + ": " + tiempoRapido
                : "Más rápido: " + masRapido;
        ventana.getSeccionSuperior().setTiempoOrdenamiento(texto);
    }

    /**
     * 
     * @param fallos
     */
    private void mostrarErrores(Map<Metodo, Throwable> fallos) {
        StringBuilder mensaje = new StringBuilder("No se pudo ordenar con:\n");
        for (Map.Entry<Metodo, Throwable> f : fallos.entrySet()) {
            String motivo = (f.getValue() instanceof StackOverflowError)
                    ? "se agotó la pila (demasiados valores repetidos)"
                    : String.valueOf(f.getValue());
            mensaje.append("• ").append(f.getKey()).append(": ").append(motivo).append('\n');
        }
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje.toString().trim(), ButtonType.OK);
        alerta.setHeaderText(null);
        alerta.initOwner(stage);
        alerta.showAndWait();
    }

    /**
     * 
     */
    private void restablecerTabla() {
        String columna = ventana.getSeccionSuperior().getColumnaSeleccionada();
        if (columna == null) return;
        cambiarColumna(columna);
        ventana.getSeccionSuperior().setTiemposDisponibles(false);
        ventana.getSeccionSuperior().setTiempoOrdenamiento("--");
    }

    /**
     * 
     */
    private void graficarTabla() {
        String columna = ventana.getSeccionSuperior().getColumnaSeleccionada();
        if (columna == null || tiempos.isEmpty()) return;
        if (ventanaGrafica == null) ventanaGrafica = new VentanaGrafica(stage);
        ventanaGrafica.mostrar(columna, TipoGrafica.BARRAS, tiempos);
    }

}