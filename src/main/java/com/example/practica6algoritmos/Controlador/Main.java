package com.example.practica6algoritmos.Controlador;
import com.example.practica6algoritmos.Modelo.ComparadorTiempos;
import com.example.practica6algoritmos.Modelo.ConjuntoDatos;
import com.example.practica6algoritmos.Modelo.Metodo;

import java.io.IOException;
import java.util.Map;


public class Main {
    private static final String RUTA_CSV = "/recursos/billboard_hot100.csv";
    // Integer.MAX_VALUE = "todas las filas de la columna"
    private static final int[] TAMANOS = {1000, 5000, 10000, 20000, Integer.MAX_VALUE};
    private static final int TAM_VERIFICACION = 1000;

    public static void main(String[] args) throws IOException {
        ConjuntoDatos datos = new ConjuntoDatos();
        ComparadorTiempos comparador = new ComparadorTiempos();

        imprimirResumen(datos);
        calentar(datos, comparador);

        for (String columna : datos.getNombresColumnas()) {
            if (datos.esColumnaNumerica(columna)) {
                probarColumna(columna, datos.getColumnaNumerica(columna), comparador);
            } else {
                probarColumna(columna, datos.getColumnaTexto(columna), comparador);
            }
        }
    }

    private static void imprimirResumen(ConjuntoDatos datos) {
        System.out.println("Filas leídas: " + datos.getNumeroFilas()
                + " | Filas descartadas: " + datos.getFilasDescartadas());
        for (String col : datos.getNombresColumnas()) {
            if (datos.esColumnaNumerica(col)) {
                System.out.printf("  %-30s numérica (%d valores)%n", col, datos.getColumnaNumerica(col).length);
            } else {
                System.out.printf("  %-30s texto    (%d valores)%n", col, datos.getColumnaTexto(col).length);
            }
        }
        System.out.println();
    }

    private static void calentar(ConjuntoDatos datos, ComparadorTiempos comparador) {
        for (String col : datos.getNombresColumnas()) {
            if (datos.esColumnaNumerica(col)) {
                for (int i = 0; i < 3; i++) {
                    comparador.compararMetodos(datos.getColumnaNumerica(col), 2000);
                }
                return;
            }
        }
    }

    private static <T extends Comparable<T>> void probarColumna(String nombre, T[] columna,
                                                                ComparadorTiempos comparador) {
        if (columna.length == 0) return;
        System.out.println("=== Columna: " + nombre + " (" + columna.length + " valores) ===");

        // 1) Verificar que cada método ordene correctamente
        for (Metodo metodo : Metodo.values()) {
            if (!comparador.verificar(metodo, columna, TAM_VERIFICACION)) {
                System.out.println("  [!] " + metodo.getNombre()
                        + " NO ordenó correctamente (o desbordó la pila)");
            }
        }

        // 2) Tabla de tiempos en milisegundos
        System.out.printf("%-8s", "N");
        for (Metodo metodo : Metodo.values()) {
            System.out.printf("%22s", metodo.getNombre());
        }
        System.out.println();

        int anterior = -1;
        for (int tamano : TAMANOS) {
            int n = Math.min(tamano, columna.length);
            if (n == anterior) continue;
            anterior = n;

            Map<Metodo, Long> tiempos = comparador.compararMetodos(columna, n);
            System.out.printf("%-8d", n);
            for (Metodo metodo : Metodo.values()) {
                long ns = tiempos.get(metodo);
                if (ns == -1) {
                    System.out.printf("%22s", "ERROR");
                } else {
                    System.out.printf("%19.3f ms", ns / 1_000_000.0);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

}
