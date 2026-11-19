package com.example.practica6algoritmos.Vista.Objetos;

import com.example.practica6algoritmos.Modelo.Metodo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Map;

public class GraficaProporcion extends Grafica {
    private VBox contenedor;
    private Barra barra;

    public GraficaProporcion(String titulo) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("chart-title");

        barra = new Barra();

        Label lblDescripcion = new Label("Cada tramo es la parte del tiempo total que tardó cada método");
        lblDescripcion.getStyleClass().add("axis-label");

        contenedor = new VBox(18, lblTitulo, barra, lblDescripcion);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(0, 24, 0, 24));
    }

    @Override
    public void cargarTiempos(Map<Metodo, Long> tiemposNs) {
        barra.getChildren().clear();
        int n = tiemposNs.size();
        if (n == 0) return;

        double total = 0;
        for (long ns : tiemposNs.values()) total += ns;

        double[] fracciones = new double[n];
        int i = 0;
        for (Map.Entry<Metodo, Long> entrada : tiemposNs.entrySet()) {
            fracciones[i] = total == 0 ? 1.0 / n : entrada.getValue() / total;

            Region tramo = new Region();
            tramo.getStyleClass().addAll("segmento-tiempo", claseCss(entrada.getKey()));
            if (n == 1) {
                tramo.getStyleClass().add("segmento-unico");
            } else if (i == 0) {
                tramo.getStyleClass().add("segmento-primero");
            } else if (i == n - 1) {
                tramo.getStyleClass().add("segmento-ultimo");
            }
            barra.getChildren().add(tramo);
            i++;
        }
        barra.setFracciones(fracciones);
    }

    @Override
    public Node getGrafica() {
        return contenedor;
    }
}
