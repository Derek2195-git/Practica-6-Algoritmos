package com.example.practica6algoritmos.Modelo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConjuntoDatos {
    private final char SEPARADOR = ',';

    private ArrayList<String> nombresColumnas;
    private HashMap<String, Double[]> columnasNumeros;
    private HashMap<String, String[]> columnasTexto;
    private int numeroFilas = 0;
    private int filasDescartadas = 0;

    public ConjuntoDatos() throws IOException {
        nombresColumnas = new ArrayList<>();
        columnasNumeros = new HashMap<>();
        columnasTexto = new HashMap<>();
        ArrayList<String[]> filas = leerCSV();
        if (filas.isEmpty()) {
            throw new IOException("El archivo proporcionado está vacio o no se encontró." );
        }
        String[] encabezado = filas.get(0);
        ArrayList<String[]> registros = new ArrayList<>();
        for (int i = 1; i < filas.size(); i++) {
            if (filas.get(i).length == encabezado.length) {
                registros.add(filas.get(i));
            } else {
                filasDescartadas++;
            }
        }
        numeroFilas = registros.size();
        for (int c = 0; c < encabezado.length; c++) {
            crearColumna(encabezado[c].trim(), c, registros);
        }

    }

    public ArrayList<String[]> leerCSV() throws IOException {
        InputStream flujo = getClass().getResourceAsStream("/recursos/dataset.csv");
        if (flujo == null) {
            throw new FileNotFoundException("No se encontró el recurso.");
        }
        ArrayList<String[]> filas = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(flujo, StandardCharsets.UTF_8)
        )) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                StringBuilder registro = new StringBuilder(linea);
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

    private boolean comillasImpares(StringBuilder registro) {
        int cuenta = 0;
        for (int i = 0; i < registro.length(); i++) {
            if (registro.charAt(i) == '"') cuenta++;
        }
        return cuenta % 2 != 0;
    }

    private String[] dividirLinea(String linea) {
        ArrayList<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char caracter = linea.charAt(i);
            if (caracter == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i+1) == '"') {
                    campo.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if(caracter == SEPARADOR && !entreComillas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(caracter);
            }
        }
        campos.add(campo.toString());
        return campos.toArray(new String[0]);
    }

    private void crearColumna(String nombre, int indice, ArrayList<String[]> registros) {
        ArrayList<String> valores = new ArrayList<>();
        for (String[] registro : registros) {
            String valor = registro[indice].trim();
            if (!esVacio(valor)) valores.add(valor);
        }
        nombresColumnas.add(nombre);

        Double[] numeros = convertirANumeros(valores);
        if (numeros != null) {
            columnasNumeros.put(nombre, numeros);
        } else {
            columnasTexto.put(nombre, valores.toArray(new String[0]));
        }
    }

    private boolean esVacio(String valor) {
        return valor.isEmpty() || valor.equalsIgnoreCase("NA") ||
                valor.equalsIgnoreCase("NaN") || valor.equalsIgnoreCase("null");
    }

    private Double[] convertirANumeros(ArrayList<String> valores) {
        if (valores.isEmpty()) return null;
        Double[] numeros = new Double[valores.size()];

        try {
            for (int i = 0; i < numeros.length; i++) {
                numeros[i] = Double.parseDouble(valores.get(i));
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return numeros;
    }

    public ArrayList<String> getNombresColumnas() {
        return nombresColumnas;
    }

    public boolean esColumnaNumerica(String nombre) {
        return columnasNumeros.containsKey(nombre);
    }

    public Double[] getColumnaNumerica(String nombre) {
        return columnasNumeros.get(nombre);
    }

    public String[] getColumnaTexto(String nombre) {
        return columnasTexto.get(nombre);
    }

    public int getNumeroFilas() {
        return numeroFilas;
    }

    public int getFilasDescartadas() {
        return filasDescartadas;
    }

    // ---------- Validez (Valid / Missing en Kaggle) ----------

    public int getValoresValidos(String nombre) {
        return esColumnaNumerica(nombre)
                ? columnasNumeros.get(nombre).length
                : columnasTexto.get(nombre).length;
    }

    public int getValoresFaltantes(String nombre) {
        return numeroFilas - getValoresValidos(nombre);
    }

    // ---------- Columnas booleanas ----------

    /** true si todos los valores válidos de una columna de texto son "true" o "false". */
    public boolean esColumnaBooleana(String nombre) {
        String[] valores = columnasTexto.get(nombre);
        if (valores == null || valores.length == 0) return false;
        for (String valor : valores) {
            if (!valor.equalsIgnoreCase("true") && !valor.equalsIgnoreCase("false")) {
                return false;
            }
        }
        return true;
    }

    // ---------- Columnas numéricas ----------

    public double getMedia(String nombre) {
        Double[] valores = columnasNumeros.get(nombre);
        double suma = 0;
        for (double x : valores) suma += x;
        return suma / valores.length;
    }

    /** Desviación estándar muestral (n - 1). */
    public double getDesviacionEstandar(String nombre) {
        Double[] valores = columnasNumeros.get(nombre);
        if (valores.length < 2) return 0;
        double media = getMedia(nombre);
        double suma = 0;
        for (double x : valores) suma += (x - media) * (x - media);
        return Math.sqrt(suma / (valores.length - 1));
    }

    /** Cuantil con interpolación lineal. p va de 0 (mínimo) a 1 (máximo). */
    public double getCuantil(String nombre, double p) {
        double[] v = valoresOrdenados(nombre);
        double pos = p * (v.length - 1);
        int inferior = (int) Math.floor(pos);
        int superior = (int) Math.ceil(pos);
        return v[inferior] + (pos - inferior) * (v[superior] - v[inferior]);
    }

    public double getMinimo(String nombre) {
        return getCuantil(nombre, 0);
    }

    public double getMaximo(String nombre) {
        return getCuantil(nombre, 1);
    }

    /**
     * Conteo de valores en cubetas de igual ancho entre el mínimo y el máximo.
     * El máximo cae en la última cubeta.
     */
    public int[] getHistograma(String nombre, int cubetas) {
        double[] v = valoresOrdenados(nombre);
        double minimo = v[0];
        double ancho = (v[v.length - 1] - minimo) / cubetas;
        int[] conteo = new int[cubetas];
        for (double x : v) {
            int i = (ancho == 0) ? 0 : (int) ((x - minimo) / ancho);
            conteo[Math.min(i, cubetas - 1)]++;
        }
        return conteo;
    }

    private double[] valoresOrdenados(String nombre) {
        Double[] valores = columnasNumeros.get(nombre);
        double[] copia = new double[valores.length];
        for (int i = 0; i < copia.length; i++) copia[i] = valores[i];
        Arrays.sort(copia);
        return copia;
    }

    // ---------- Columnas de texto y booleanas ----------

    /** Cuántas veces aparece cada valor, en orden de primera aparición. */
    public Map<String, Integer> getFrecuencias(String nombre) {
        Map<String, Integer> frecuencias = new LinkedHashMap<>();
        for (String valor : columnasTexto.get(nombre)) {
            frecuencias.merge(valor, 1, Integer::sum);
        }
        return frecuencias;
    }

    public int getValoresUnicos(String nombre) {
        return getFrecuencias(nombre).size();
    }

    public String getMasComun(String nombre) {
        String masComun = null;
        int mayor = 0;
        for (Map.Entry<String, Integer> entrada : getFrecuencias(nombre).entrySet()) {
            if (entrada.getValue() > mayor) {
                mayor = entrada.getValue();
                masComun = entrada.getKey();
            }
        }
        return masComun;
    }


}
