package com.example.practica6algoritmos.Vista.Objetos.Graficas;

import java.util.ArrayList;
import java.util.List;

public enum TipoGrafica {
    SIN_GRAFICA,
    BARRAS,
    PASTEL;
    // Columnas que son para las graficas de barras
    private static ArrayList<String> COLUMNAS_BARRAS = new ArrayList<>(List.of(
            "spotify_track_duration_ms", "danceability", "energy", "key", "loudness", "mode",
            "speechiness", "acousticness", "instrumentalness", "liveness", "valence", "tempo",
            "time_signature", "spotify_track_popularity", "peak_position", "weeks_on_chart",
            //"first_week", "last_week",
            "confidence_score"));

    // Columnas que son para las gráficas de pastel
    private static ArrayList<String> COLUMNAS_PASTEL = new ArrayList<>(List.of("spotify_track_explicit"));

    /**
     * Crea una gráfica según el valor de la enumeración
     * @param titulo Titulo de la gráfica
     * @return Gráfica de un distinto tipo según la enumeración
     */
    public Grafica crear(String titulo) {
        return switch (this) {
            case BARRAS -> new GraficaBarras(titulo);
            case PASTEL -> new GraficaPastel(titulo);
            case SIN_GRAFICA -> throw new UnsupportedOperationException("SIN_GRAFICA no tiene gráfica");
        };
    }

    /**
     * Metodo que revisa la columna actual y dice el tipo de gráfica a usar
     * @param columna Nombre de la columna a usar
     * @return Valor de la enumeración que se usará
     */
    public static TipoGrafica tipoDeColumna(String columna) {
        if (COLUMNAS_BARRAS.contains(columna)) return BARRAS;
        if (COLUMNAS_PASTEL.contains(columna)) return PASTEL;
        return SIN_GRAFICA;
    }
}
