package com.example.practica6algoritmos.Modelo;

import java.util.Arrays;

public class Ordenamientos <T extends Comparable<T>> {

    public void insercionDirecta(T[] a, int n) {
        for (int i = 1; i < n; i++) {
            T aux = a[i];
            int k = i - 1;
            while (k >= 0 && aux.compareTo(a[k]) < 0) {
                a[k + 1] = a[k--];
            }
            a[k + 1] = aux;
        }
    }

    public void seleccionDirecta(T[] a, int n) {
        T menor;
        for (int i = 0; i < n - 1; i++) {
            menor = a[i];
            int k = i;
            for (int j = i + 1; j < n; j++) {
                if (menor.compareTo(a[j]) > 0) {
                    menor = a[j];
                    k = j;
                }
            }
            a[k] = a[i];
            a[i] = menor;
        }
    }

    public void shellDirecto(T[] a, int n) {
        int inter = n + 1;

        while (inter > 1) {
            inter = inter / 2;
            int band = 0;
            while (band == 0) {
                band = -1;
                for (int i = 0; (i + inter) < n; i++) {
                    T r = a[i];
                    if (r.compareTo(a[i + inter]) > 0) {
                        T aux = a[i];
                        a[i] = a[i + inter];
                        a[i + inter] = aux;

                        band = 0;
                    }
                }
            }
        }
    }

    public void quickSort(T[] a, int n) {
        quickSortRecursivo(0, n - 1, a);
    }

    public void quickSortRecursivo(int ini, int fin, T[] a) {
        int izq = ini;
        int pos = ini;
        int der = fin;
        boolean band = true;
        while (band) {
            band = false;

            while (a[pos].compareTo(a[der]) <= 0 && pos != der) {
                der--;
            }
            if (pos != der) {
                T aux = a[pos];
                a[pos] = a[der];
                a[der] = aux;
                pos = der;
                while (a[pos].compareTo(a[izq]) >= 0 && pos != izq) {
                    izq++;
                }
                band = true;
                aux = a[pos];
                a[pos] = a[izq];
                a[izq] = aux;
                pos = izq;
            }
        }
        if (pos - 1 > ini) {
            quickSortRecursivo(ini, pos - 1, a);
        }
        if (fin > (pos + 1)) quickSortRecursivo(pos + 1, fin, a);
    }

    public void busquedaBinaria(T[] a, int n, T dato) {
        int izq = 0;
        int der = n - 1;
        int b = 0;
        int cen = 0;

        while (izq <= der && b == 0) {
            cen = (izq + der) / 2;
            int compCen = dato.compareTo(a[cen]);
            if (compCen == 0) {
                b = 1;
            } else if (compCen > 0) {
                izq = cen + 1;
            } else {
                der = cen - 1;
            }
        }

        if (b == 1) {
            System.out.println("El elemento está en la posicion " + cen);
        } else System.out.println("El elemento no existe en el arreglo");
    }

    // Mergesort
    public void mergeSort(T[] a, int n) {
        if (n < 2) return;
        T[] aux = Arrays.copyOf(a, n);
        mergeSortRecursivo(a, aux, 0, n-1);
    }

    private void mergeSortRecursivo(T[] a, T[] aux, int inicio, int fin) {
        if (inicio >= fin) return;
        int medio = inicio + (fin - inicio) / 2;
        mergeSortRecursivo(a, aux, inicio, medio);
        mergeSortRecursivo(a, aux, medio + 1, fin);


        if(a[medio].compareTo(a[medio + 1]) <= 0) return;
        for (int i = inicio; i <= fin; i++) {
            aux[i] = a[i];
        }
        int j = inicio;
        int k = medio + 1;
        for (int i = inicio; i <= fin; i++) {
            if (j > medio) {
                a[i] = aux[k++];
            } else if (k > fin) {
                a[i] = aux[j++];
            } else if (aux[k].compareTo(aux[j]) < 0) {
                a[i] = aux[k++];
            } else {
                a[i] = aux[j++];
            }
        }
    }
}