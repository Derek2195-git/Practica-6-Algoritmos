package com.example.practica6algoritmos.Modelo;

import java.util.Arrays;
import java.util.Objects;

public class ComparadorTiempos<T extends Comparable<T>> {
    // Medimos en nanosegundos los tiempos
    private final Ordenamientos<T> ordenamientos;
    private final Metodo metodo;

    public ComparadorTiempos(Metodo metodo) {
        this.ordenamientos = new Ordenamientos<>();
        this.metodo = Objects.requireNonNull(metodo, "El método de ordenamiento no puede ser null");
    }

    /** Ordena arr (en sitio) y devuelve los nanosegundos que tardó. Pásale siempre una copia. */
    public long medirTiempo(T[] arr) {
        long tiempoInicio = System.nanoTime();
        switch (metodo) {
            case SHELL -> ordenamientos.shellDirecto(arr, arr.length);
            case ARRAYSORT -> Arrays.sort(arr);
            case MERGESORT -> ordenamientos.mergeSort(arr, arr.length);
            case QUICKSORT -> ordenamientos.quickSort(arr, arr.length);
            case SELECCION -> ordenamientos.seleccionDirecta(arr, arr.length);
            case PARALLELSORT -> Arrays.parallelSort(arr);
        }
        long tiempoFinal = System.nanoTime();
        return tiempoFinal - tiempoInicio;
    }
}