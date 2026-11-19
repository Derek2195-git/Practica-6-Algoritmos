package com.example.practica6algoritmos.Modelo;

public enum Metodo {
    SELECCION("Selección directa"),
    SHELL("Shell sort"),
    QUICKSORT("Quicksort"),
    MERGESORT("Mergesort"),
    ARRAYSORT("Arrays.sort()"),
    PARALLELSORT("Arrays.parallelSort()");

    // Nombre del arreglo para la vista
    private final String nombre;

    /**
     * Constructor de la enumeración
     * @param nombre
     */
    Metodo(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Getter del nombre del metodo
     * @return Nombre del metodo usado
     */
    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}