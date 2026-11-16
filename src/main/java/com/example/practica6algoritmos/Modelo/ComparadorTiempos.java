package com.example.practica6algoritmos.Modelo;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class ComparadorTiempos <T extends Comparable<T>> {

    private long TAMANO_PILA = 256L * 1024 * 1024;

    public long medirTiempos(Metodo metodo, T[] original, int n) {
        T[] copia = Arrays.copyOf(original, Math.min(n, original.length));
        return ejecutarMedicion(metodo, copia);
    }

    public Map<Metodo, Long> compararMetodos(T[] columna, int n, Metodo... listaMetodos) {
        Metodo[] metodosAProbar = (listaMetodos.length == 0) ? Metodo.values() : listaMetodos;
        Map<Metodo, Long> tiempos = new EnumMap<>(Metodo.class);
        for (Metodo metodo : metodosAProbar) {
            tiempos.put(metodo, medirTiempos(metodo, columna, n));
        }
        return tiempos;
    }

    private long ejecutarMedicion(Metodo metodo, T[] arreglo) {
        Ordenamientos<T> ordenador = new Ordenamientos<>();
        long[] resultado = {-1};

        Thread hilo = new Thread(null, () -> {
            try {
                long inicio = System.nanoTime();
                ordenar(metodo, ordenador, arreglo, arreglo.length);
                resultado[0] = System.nanoTime() - inicio;
            } catch (StackOverflowError e) {
                resultado[0] = -1;
            }
        }, "Ordenamiento", TAMANO_PILA);

        hilo.start();
        try {
            hilo.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return resultado[0];
    }

    public boolean verificar(Metodo metodo, T[] original, int n) {
        int tamano = Math.min(n, original.length);
        T[] obtenido = Arrays.copyOf(original, tamano);
        if (ejecutarMedicion(metodo, obtenido) == -1) return false;

        T[] esperado = Arrays.copyOf(original, tamano);
        Arrays.sort(esperado);
        return Arrays.equals(obtenido, esperado);
    }

    private void ordenar(Metodo metodo, Ordenamientos<T> ordenador,
                                                   T[] a, int n) {
        switch (metodo) {
            case SELECCION -> ordenador.seleccionDirecta(a, n);
            case SHELL -> ordenador.shellDirecto(a, n);
            case QUICKSORT -> ordenador.quickSort(a, n);
            case MERGESORT -> ordenador.mergeSort(a, n);
            case ARRAYSORT -> Arrays.sort(a, 0, n);
            case PARALLELSORT -> Arrays.parallelSort(a, 0, n);
        }
    }


}
