package com.example.practica6algoritmos.Vista.Objetos.Graficas;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public enum TipoGrafica {
    SIN_GRAFICA,
    BARRAS,
    PASTEL;

    private static ArrayList<String> COLUMNAS_BARRAS = new ArrayList<>(List.of(
            "spotify_track_duration_ms", "danceability", "energy", "key", "loudness", "mode",
            "speechiness", "acousticness", "instrumentalness", "liveness", "valence", "tempo",
            "time_signature", "spotify_track_popularity", "peak_position", "weeks_on_chart",
            //"first_week", "last_week",
            "confidence_score"));

    private static ArrayList<String> COLUMNAS_PASTEL = new ArrayList<>(List.of("spotify_track_explicit"));

    /** Crea la gráfica de este tipo. SIN_GRAFICA no tiene, así que no se puede crear. */
    public Grafica crear(String titulo) {
        return switch (this) {
            case BARRAS -> new GraficaBarras(titulo);
            case PASTEL -> new GraficaPastel(titulo);
            case SIN_GRAFICA -> throw new UnsupportedOperationException("SIN_GRAFICA no tiene gráfica");
        };
    }

    public static TipoGrafica deColumna(String columna) {
        if (COLUMNAS_BARRAS.contains(columna)) return BARRAS;
        if (COLUMNAS_PASTEL.contains(columna)) return PASTEL;
        return SIN_GRAFICA;
    }
}
