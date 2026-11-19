package com.example.practica6algoritmos.Modelo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ArrayList;

/** Lee el CSV y lo convierte en filas de campos. No sabe nada de canciones. */
public class GestorArchivo {
    public static final String RUTA_POR_DEFECTO = "/recursos/dataset.csv";
    private static final char SEPARADOR = ',';

    private final String ruta;

    /**
     * Constructor de la clase que gestiona el archivo
     */
    public GestorArchivo() {
        ruta = RUTA_POR_DEFECTO;
    }

    /**
     * Metodo que obtiene todas las filas del archivo y las compila en un arraylist
     * @return ArrayList con cada fila existente en el archivo
     * @throws IOException Error que pueda ocurrir cuando no se encuentre el archivo
     */
    public ArrayList<String[]> leerFilas() throws IOException {
        InputStream flujo = getClass().getResourceAsStream(ruta);
        if (flujo == null) {
            throw new FileNotFoundException("No se encontró el recurso " + ruta
                    + " (debe estar en src/main/resources/recursos/dataset.csv).");
        }
        ArrayList<String[]> filas = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(flujo, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                StringBuilder registro = new StringBuilder(linea);
                // Un campo entre comillas puede contener saltos de línea.
                while (comillasImpares(registro)) {
                    String siguiente = lector.readLine();
                    if (siguiente == null) break;
                    registro.append("\n").append(siguiente);
                }
                if (registro.toString().isBlank()) continue;
                filas.add(dividirLinea(registro.toString()));
            }
        }
        return filas;
    }

    /**
     * Metodo que revisa el archivo con el fin de revisar
     * si una linea posee una cantidad impar de comillas
     * @param registro El atributo que almacena las lineas del csv a si mismo
     * @return booleano que indica si hay una cantidad par o impar de comilllas
     */
    private boolean comillasImpares(StringBuilder registro) {
        int cuenta = 0;
        for (int i = 0; i < registro.length(); i++) {
            if (registro.charAt(i) == '"') cuenta++;
        }
        return cuenta % 2 != 0;
    }

    /**
     * Metodo que divide el contenido de una linea del CSV y la almacena en un Arreglo
     * @param linea Linea del archivo a dividir en varios pedazos
     * @return Arreglo con la linea actual del archivo dividida por el separador
     */
    private String[] dividirLinea(String linea) {
        ArrayList<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char caracter = linea.charAt(i);
            if (caracter == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (caracter == SEPARADOR && !entreComillas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(caracter);
            }
        }
        campos.add(campo.toString());
        return campos.toArray(new String[0]);
    }
}