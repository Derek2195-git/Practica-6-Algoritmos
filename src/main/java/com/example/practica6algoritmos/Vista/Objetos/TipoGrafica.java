package com.example.practica6algoritmos.Vista.Objetos;

import java.util.Set;

public enum TipoGrafica {
    SIN_GRAFICA,
    BARRAS,
    PASTEL;

    private static final Set<String> COLUMNAS_BARRAS = Set.of(
            "spotify_track_duration_ms", "danceability", "energy", "key", "loudness", "mode",
            "speechiness", "acousticness", "instrumentalness", "liveness", "valence", "tempo",
            "time_signature", "spotify_track_popularity", "peak_position", "weeks_on_chart",
            "first_week", "last_week", "confidence_score");

    private static final Set<String> COLUMNAS_PASTEL = Set.of("spotify_track_explicit");

    /** Lo que no esté en las listas de arriba (columnas de texto) queda sin gráfica. */
    public static TipoGrafica deColumna(String columna) {
        if (COLUMNAS_BARRAS.contains(columna)) return BARRAS;
        if (COLUMNAS_PASTEL.contains(columna)) return PASTEL;
        return SIN_GRAFICA;
    }

    /** Crea la gráfica de este tipo. SIN_GRAFICA no tiene, así que no se puede crear. */
    public Grafica crear(String titulo) {
        return switch (this) {
            case BARRAS -> new GraficaBarras(titulo);
            case PASTEL -> new GraficaPastel(titulo);
            case SIN_GRAFICA -> throw new UnsupportedOperationException("Este tipo no tiene gráfica");
        };
    }
}
