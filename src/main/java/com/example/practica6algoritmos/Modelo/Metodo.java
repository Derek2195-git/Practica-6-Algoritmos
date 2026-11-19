package com.example.practica6algoritmos.Modelo;

public enum Metodo {
    SELECCION("Selección directa"),
    SHELL("Shell sort"),
    QUICKSORT("Quicksort"),
    MERGESORT("Mergesort"),
    ARRAYSORT("Arrays.sort()"),
    PARALLELSORT("Arrays.parallelSort()");


    private final String nombre;

    Metodo(String nombre) {
        this.nombre = nombre;
    }

    /** Nombre legible para mostrar en la tabla, la gráfica o los botones. */
    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}