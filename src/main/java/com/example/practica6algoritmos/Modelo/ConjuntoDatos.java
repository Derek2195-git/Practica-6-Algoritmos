package com.example.practica6algoritmos.Modelo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConjuntoDatos {
    // Columnas del CSV
    private static final String[] COLUMNAS = {
            "song_id", "performer", "song", "spotify_genre", "spotify_track_id",
            "spotify_track_preview_url", "spotify_track_duration_ms", "spotify_track_explicit",
            "spotify_track_album", "danceability", "energy", "key", "loudness", "mode",
            "speechiness", "acousticness", "instrumentalness", "liveness", "valence", "tempo",
            "time_signature", "spotify_track_popularity", "peak_position", "weeks_on_chart",
            "first_week", "last_week", "chart_success_tier", "hit_longevity_profile",
            "acoustic_mood_cluster", "musical_era", "evidence_span", "cot_rationale",
            "confidence_score"
    };

    // Todas las canciones del dataset se encuentran en este arraylist
    private final ArrayList<Cancion> canciones = new ArrayList<>();
    private int filasDescartadas = 0;


    /**
     * Constructor de la lógica principal del programa
     * @throws IOException
     */
    public ConjuntoDatos() throws IOException {
        GestorArchivo gestor = new GestorArchivo();
        ArrayList<String[]> filas = gestor.leerFilas();
        if (filas.isEmpty()) {
            throw new IOException("El archivo proporcionado está vacío o no se encontró.");
        }
        // Encabezados de cada columna
        String[] encabezado = filas.get(0);
        int[] posiciones = ubicarColumnas(encabezado);

        for (int i = 1; i < filas.size(); i++) {
            String[] fila = filas.get(i);
            if (fila.length != encabezado.length) {   // fila mal formada
                filasDescartadas++;
                continue;
            }
            // f[c] es el valor de la columna COLUMNAS[c], venga donde venga en el CSV
            String[] f = new String[COLUMNAS.length];
            for (int c = 0; c < f.length; c++) {
                f[c] = fila[posiciones[c]];
            }
            canciones.add(new Cancion(
                    aTexto(f[0]), aTexto(f[1]), aTexto(f[2]), aTexto(f[3]), aTexto(f[4]),
                    aTexto(f[5]), aEntero(f[6]), aBoolean(f[7]), aTexto(f[8]), aDouble(f[9]),
                    aDouble(f[10]), aEntero(f[11]), aDouble(f[12]), aEntero(f[13]),
                    aDouble(f[14]), aDouble(f[15]), aDouble(f[16]), aDouble(f[17]),
                    aDouble(f[18]), aDouble(f[19]), aEntero(f[20]), aEntero(f[21]),
                    aEntero(f[22]), aEntero(f[23]), aTexto(f[24]), aTexto(f[25]),
                    aTexto(f[26]), aTexto(f[27]), aTexto(f[28]), aTexto(f[29]),
                    aTexto(f[30]), aTexto(f[31]), aDouble(f[32])));
        }
    }

    /** Para cada columna de COLUMNAS, en qué posición viene en el CSV. */
    private int[] ubicarColumnas(String[] encabezado) throws IOException {
        Map<String, Integer> indices = new HashMap<>();
        for (int i = 0; i < encabezado.length; i++) {
            indices.put(encabezado[i].trim().toLowerCase(), i);
        }
        int[] posiciones = new int[COLUMNAS.length];
        for (int c = 0; c < COLUMNAS.length; c++) {
            Integer indice = indices.get(COLUMNAS[c]);
            if (indice == null) {
                throw new IOException("No se encontró la columna «" + COLUMNAS[c]
                        + "» en el encabezado del CSV.");
            }
            posiciones[c] = indice;
        }
        return posiciones;
    }


    /**
     * Metodo que convierte una cadena del archivo CSV a una forma más limpia
     * y coherente de entender
     * @param valor Valor a convertir a una cadena
     * @return Cadena formada a partir del valor dado
     */
    private static String aTexto(String valor) {
        if (valor == null) return null;
        String limpio = valor.trim();
        boolean vacio = limpio.isEmpty() || limpio.equalsIgnoreCase("NA")
                || limpio.equalsIgnoreCase("NaN") || limpio.equalsIgnoreCase("null");
        return vacio ? null : limpio;
    }

    /**
     * Metodo que convierte una cadena de una columna a un número decimal
     * @param valor Valor a convertir
     * @return Numero en decimal que representa al valor dado
     */
    private static Double aDouble(String valor) {
        String texto = aTexto(valor);
        if (texto == null) return null;
        try {
            return Double.valueOf(texto);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Metodo que convierte una cadena dado a un número entero
     * @param valor Valor dado por el usuario
     * @return Número entero que representa al valor dado
     */
    private static Integer aEntero(String valor) {
        Double numero = aDouble(valor);
        return numero == null ? null : (int) Math.round(numero);
    }

    /**
     * Metodo que convierte una cadena en un valor tipo booleano
     * @param valor Cadena a convertir
     * @return Booleano que representa a la cadena dada
     */
    private static Boolean aBoolean(String valor) {
        String texto = aTexto(valor);
        if (texto == null) return null;
        if (texto.equalsIgnoreCase("true")) return Boolean.TRUE;
        if (texto.equalsIgnoreCase("false")) return Boolean.FALSE;
        return null;
    }


    /**
     * Metodo que da todos los nombres de cada columna
     */
    public ArrayList<String> getNombresColumnas() {
        return new ArrayList<String>(Arrays.asList(COLUMNAS));
    }

    /**
     * Metodo que da el arreglo del archivo csv sobre el cual trabajaremos
     * @return ArrayList de todas las canciones encontradas en el CSV
     */
    public ArrayList<Cancion> getCanciones() {
        return canciones;
    }

    /**
     * Metodo que copia todos los valores del ArrayList a un arreglo
     * con el fin de no alterar el valor original de este y que se puedan usar varios
     * ordenamientos
     * @return Arreglo con las canciones dadas
     */
    public Cancion[] crearCopiaOrdenable() {
        return canciones.toArray(new Cancion[0]);
    }
}