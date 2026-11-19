package com.example.practica6algoritmos.Modelo;

import java.util.Arrays;
import java.util.Objects;

public class ComparadorTiempos<T extends Comparable<T>> {
    // Medimos en nanosegundos los tiempos
    private final Ordenamientos<T> ordenamientos;
    private final Metodo metodo;

    /**
     * Constructor del comparador de tiempos
     * @param metodo Metodo de ordenamiento a usar
     */
    public ComparadorTiempos(Metodo metodo) {
        this.ordenamientos = new Ordenamientos<>();
        this.metodo = metodo;
    }

    /**
     * Mide el tiempo empleado en realizar un ordenamiento
     * @param arr Arreglo a ordenar
     * @return Tiempo en un tipo de dato Long el cual mide el tiempo tomado en ordenar el arreglo
     */
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