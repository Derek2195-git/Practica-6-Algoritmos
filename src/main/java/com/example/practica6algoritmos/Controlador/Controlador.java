package com.example.practica6algoritmos.Controlador;

import com.example.practica6algoritmos.Modelo.Cancion;

import java.util.Comparator;
import java.util.function.Function;

public class Controlador {

    public Controlador() {

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

}