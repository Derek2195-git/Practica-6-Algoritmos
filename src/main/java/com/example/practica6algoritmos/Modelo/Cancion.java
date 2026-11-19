package com.example.practica6algoritmos.Modelo;

import java.util.Comparator;

/**
 * Una fila del dataset. Solo guarda lo que traía el CSV: lo que faltaba queda en null.
 * Es Comparable solo para poder usar los ordenamientos genéricos: la comparación real
 * la define el criterio que asigne el controlador con setCriterio().
 */
public class Cancion implements Comparable<Cancion> {
    // Criterio con el que se comparan las canciones (se asigna antes de cada ordenamiento).
    private static Comparator<Cancion> criterio;

    private final String song_id;
    private final String performer;
    private final String song;
    private final String spotify_genre;
    private final String spotify_track_id;
    private final String spotify_track_preview_url;
    private final Integer spotify_track_duration_ms;
    private final Boolean spotify_track_explicit;
    private final String spotify_track_album;
    private final Double danceability;
    private final Double energy;
    private final Integer key;
    private final Double loudness;
    private final Integer mode;
    private final Double speechiness;
    private final Double acousticness;
    private final Double instrumentalness;
    private final Double liveness;
    private final Double valence;
    private final Double tempo;
    private final Integer time_signature;
    private final Integer spotify_track_popularity;
    private final Integer peak_position;
    private final Integer weeks_on_chart;
    private final String first_week;
    private final String last_week;
    private final String chart_success_tier;
    private final String hit_longevity_profile;
    private final String acoustic_mood_cluster;
    private final String musical_era;
    private final String evidence_span;
    private final String cot_rationale;
    private final Double confidence_score;


    public static void setCriterio(Comparator<Cancion> nuevoCriterio) {
        criterio = nuevoCriterio;
    }

    public Cancion(String song_id, String performer, String song, String spotify_genre,
                   String spotify_track_id, String spotify_track_preview_url,
                   Integer spotify_track_duration_ms, Boolean spotify_track_explicit,
                   String spotify_track_album, Double danceability, Double energy, Integer key,
                   Double loudness, Integer mode, Double speechiness, Double acousticness,
                   Double instrumentalness, Double liveness, Double valence, Double tempo,
                   Integer time_signature, Integer spotify_track_popularity,
                   Integer peak_position, Integer weeks_on_chart, String first_week,
                   String last_week, String chart_success_tier, String hit_longevity_profile,
                   String acoustic_mood_cluster, String musical_era, String evidence_span,
                   String cot_rationale, Double confidence_score) {
        this.song_id = song_id;
        this.performer = performer;
        this.song = song;
        this.spotify_genre = spotify_genre;
        this.spotify_track_id = spotify_track_id;
        this.spotify_track_preview_url = spotify_track_preview_url;
        this.spotify_track_duration_ms = spotify_track_duration_ms;
        this.spotify_track_explicit = spotify_track_explicit;
        this.spotify_track_album = spotify_track_album;
        this.danceability = danceability;
        this.energy = energy;
        this.key = key;
        this.loudness = loudness;
        this.mode = mode;
        this.speechiness = speechiness;
        this.acousticness = acousticness;
        this.instrumentalness = instrumentalness;
        this.liveness = liveness;
        this.valence = valence;
        this.tempo = tempo;
        this.time_signature = time_signature;
        this.spotify_track_popularity = spotify_track_popularity;
        this.peak_position = peak_position;
        this.weeks_on_chart = weeks_on_chart;
        this.first_week = first_week;
        this.last_week = last_week;
        this.chart_success_tier = chart_success_tier;
        this.hit_longevity_profile = hit_longevity_profile;
        this.acoustic_mood_cluster = acoustic_mood_cluster;
        this.musical_era = musical_era;
        this.evidence_span = evidence_span;
        this.cot_rationale = cot_rationale;
        this.confidence_score = confidence_score;
    }

    public String getSong_id() { return song_id; }
    public String getPerformer() { return performer; }
    public String getSong() { return song; }
    public String getSpotify_genre() { return spotify_genre; }
    public String getSpotify_track_id() { return spotify_track_id; }
    public String getSpotify_track_preview_url() { return spotify_track_preview_url; }
    public Integer getSpotify_track_duration_ms() { return spotify_track_duration_ms; }
    public Boolean getSpotify_track_explicit() { return spotify_track_explicit; }
    public String getSpotify_track_album() { return spotify_track_album; }
    public Double getDanceability() { return danceability; }
    public Double getEnergy() { return energy; }
    public Integer getKey() { return key; }
    public Double getLoudness() { return loudness; }
    public Integer getMode() { return mode; }
    public Double getSpeechiness() { return speechiness; }
    public Double getAcousticness() { return acousticness; }
    public Double getInstrumentalness() { return instrumentalness; }
    public Double getLiveness() { return liveness; }
    public Double getValence() { return valence; }
    public Double getTempo() { return tempo; }
    public Integer getTime_signature() { return time_signature; }
    public Integer getSpotify_track_popularity() { return spotify_track_popularity; }
    public Integer getPeak_position() { return peak_position; }
    public Integer getWeeks_on_chart() { return weeks_on_chart; }
    public String getFirst_week() { return first_week; }
    public String getLast_week() { return last_week; }
    public String getChart_success_tier() { return chart_success_tier; }
    public String getHit_longevity_profile() { return hit_longevity_profile; }
    public String getAcoustic_mood_cluster() { return acoustic_mood_cluster; }
    public String getMusical_era() { return musical_era; }
    public String getEvidence_span() { return evidence_span; }
    public String getCot_rationale() { return cot_rationale; }
    public Double getConfidence_score() { return confidence_score; }

    @Override
    public int compareTo(Cancion otra) {
        return criterio.compare(this, otra);
    }
}