package com.example.practica6algoritmos.Controlador;

import com.example.practica6algoritmos.Modelo.*;
import com.example.practica6algoritmos.Vista.Objetos.Grafica;
import com.example.practica6algoritmos.Vista.Objetos.TipoGrafica;
import com.example.practica6algoritmos.Vista.Secciones.SeccionSuperior;
import com.example.practica6algoritmos.Vista.Secciones.SeccionTabla;
import com.example.practica6algoritmos.Vista.Ventana.VentanaGrafica;
import com.example.practica6algoritmos.Vista.Ventana.VentanaPrincipal;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
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

        stage.setTitle("CSV");
        stage.setScene(escena);
        stage.show();
    }

    /**
     * Devuelve el criterio de comparación para la columna indicada.
     * Los nombres coinciden con ConjuntoDatos.getNombresColumnas().
     * Los valores null (datos faltantes en el CSV) quedan siempre al final.
     */
    private Comparator<Cancion> getComparator(String columna) {
        return switch (columna) {
            // ---- Texto (sin distinguir mayúsculas/minúsculas) ----
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

            // ---- Enteros ----
            case "spotify_track_duration_ms" -> porNatural(Cancion::getSpotify_track_duration_ms);
            case "key" -> porNatural(Cancion::getKey);
            case "mode" -> porNatural(Cancion::getMode);
            case "time_signature" -> porNatural(Cancion::getTime_signature);
            case "spotify_track_popularity" -> porNatural(Cancion::getSpotify_track_popularity);
            case "peak_position" -> porNatural(Cancion::getPeak_position);
            case "weeks_on_chart" -> porNatural(Cancion::getWeeks_on_chart);

            // ---- Decimales ----
            case "danceability" -> porNatural(Cancion::getDanceability);
            case "energy" -> porNatural(Cancion::getEnergy);
            case "loudness" -> porNatural(Cancion::getLoudness);
            case "speechiness" -> porNatural(Cancion::getSpeechiness);
            case "acousticness" -> porNatural(Cancion::getAcousticness);
            case "instrumentalness" -> porNatural(Cancion::getInstrumentalness);
            case "liveness" -> porNatural(Cancion::getLiveness);
            case "valence" -> porNatural(Cancion::getValence);
            case "tempo" -> porNatural(Cancion::getTempo);
            case "confidence_score" -> porNatural(Cancion::getConfidence_score);

            // ---- Booleano (false < true) ----
            case "spotify_track_explicit" -> porNatural(Cancion::getSpotify_track_explicit);

            default -> throw new IllegalArgumentException("Columna desconocida: " + columna);
        };
    }

    /** Compara por un campo de texto, ignorando mayúsculas y con null al final. */
    private static Comparator<Cancion> porTexto(Function<Cancion, String> campo) {
        return Comparator.comparing(campo, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
    }

    /** Compara por un campo con orden natural (Integer, Double, Boolean) y con null al final. */
    private static <T extends Comparable<T>> Comparator<Cancion> porNatural(Function<Cancion, T> campo) {
        return Comparator.comparing(campo, Comparator.nullsLast(Comparator.<T>naturalOrder()));
    }

    private void cambiarColumna(String columna) {
        tiempos.clear();
        ventana.getSeccionTabla().mostrarCanciones(conjuntoDatos.getCanciones());
        ventana.getSeccionTabla().mostrarColumna(columna);
        mostrarTiempos();
    }

    private void ordenarTabla() {
        String columna = ventana.getSeccionSuperior().getColumnaSeleccionada();
        Metodo metodo = ventana.getSeccionSuperior().getMetodoSeleccionado();
        if (columna == null || metodo == null) return;

        if (!ventana.getSeccionSuperior().isCompararActivado()) tiempos.clear();

        Cancion.setCriterio(getComparator(columna));
        Cancion[] copia = conjuntoDatos.crearCopiaOrdenable();

        ventana.getSeccionSuperior().setOcupado(true);
        ventana.getSeccionSuperior().setTiemposDisponibles(false);
        ventana.getSeccionSuperior().setTiempoOrdenamiento("Ordenando con " + metodo);

        Thread hilo = new Thread(null, () -> {
            try {
                long ns = new ComparadorTiempos<Cancion>(metodo).medirTiempo(copia);
                Platform.runLater(() -> alTerminarOrdenamiento(columna, metodo, copia, ns));
            } catch (Throwable error) {   // incluye StackOverflowError
                Platform.runLater(() -> alFallarOrdenamiento(metodo, error));
            }
        }, "ordenamiento", TAMANO_MAXIMO);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void alTerminarOrdenamiento(String columna, Metodo metodo, Cancion[] ordenadas, long ns) {
        tiempos.put(metodo, ns);
        ventana.getSeccionTabla().mostrarCanciones(new ArrayList<>(Arrays.asList(ordenadas)));
        ventana.getSeccionTabla().mostrarColumna(columna);
        ventana.getSeccionSuperior().setTiempoOrdenamiento(metodo + ": " + Grafica.formatearTiempo(ns));
        ventana.getSeccionSuperior().setOcupado(false);
        ventana.getSeccionSuperior().setTiemposDisponibles(true);
    }

    private void alFallarOrdenamiento(Metodo metodo, Throwable error) {
        ventana.getSeccionSuperior().setOcupado(false);
        ventana.getSeccionSuperior().setTiemposDisponibles(!tiempos.isEmpty());
        ventana.getSeccionSuperior().setTiempoOrdenamiento("--");

        String mensaje = (error instanceof StackOverflowError)
                ? metodo + " agotó la pila de memoria con esta columna (demasiados valores repetidos)."
                : "No se pudo ordenar la columna: " + error;
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alerta.getDialogPane().getStylesheets().add(
                getClass().getResource("/recursos/estilos.css").toExternalForm());
        alerta.setHeaderText(null);
        alerta.initOwner(stage);
        alerta.showAndWait();
    }

    private void restablecerTabla() {
        String columna = ventana.getSeccionSuperior().getColumnaSeleccionada();
        if (columna == null) return;
        cambiarColumna(columna);
        ventana.getSeccionSuperior().setTiemposDisponibles(false);
        ventana.getSeccionSuperior().setTiempoOrdenamiento("--");
    }

    private void graficarTabla() {
        String columna = ventana.getSeccionSuperior().getColumnaSeleccionada();
        if (columna == null || tiempos.isEmpty()) return;
        if (ventanaGrafica == null) ventanaGrafica = new VentanaGrafica(stage);
        ventanaGrafica.mostrar(columna, TipoGrafica.deColumna(columna), tiempos);
    }

}