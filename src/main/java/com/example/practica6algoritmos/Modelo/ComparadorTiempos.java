package com.example.practica6algoritmos.Modelo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public class ComparadorTiempos <T extends Comparable<T>> {
    // Medimos en nanosegundos los tiempos
    private final Ordenamientos<T> ordenamientos;
    private Metodo metodo;

    public ComparadorTiempos(Metodo metodo) {
        ordenamientos = new Ordenamientos<>();
        this.metodo = metodo;
    }

    public long medirTiempo(T[] arr, Comparator<T> comp) {
        long TiempoInicio = System.nanoTime();
        switch (metodo) {
            case SHELL -> ordenamientos.shellDirecto(arr, arr.length);
            case ARRAYSORT -> Arrays.sort(arr);
            case MERGESORT -> ordenamientos.mergeSort(arr, arr.length);
            case QUICKSORT -> ordenamientos.quickSort(arr, arr.length);
            case SELECCION -> ordenamientos.seleccionDirecta(arr, arr.length);
            case PARALLELSORT -> Arrays.parallelSort(arr);
            case null, default -> throw new IllegalArgumentException("Algoritmo desconocido" + metodo.getNombre());
        }
        long TiempoFinal = System.nanoTime();
        return TiempoFinal - TiempoInicio;
    }
}
