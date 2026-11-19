package com.example.practica6algoritmos.Vista.Ventana;

import com.example.practica6algoritmos.Modelo.Metodo;
import com.example.practica6algoritmos.Vista.Objetos.Grafica;
import com.example.practica6algoritmos.Vista.Objetos.TipoGrafica;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;

public class VentanaGrafica extends Stage {
    private BorderPane ventana;
    private FlowPane leyenda;


    public VentanaGrafica(Stage ventanaPrincipal) {
        initOwner(ventanaPrincipal);
        setTitle("Grafica de tiempos de ordenamiento");
        ventana = new BorderPane();
        leyenda = new FlowPane(20, 8);

        leyenda.setAlignment(Pos.CENTER);
        leyenda.setPadding(new Insets(12, 0,0,0));
        ventana.setBottom(leyenda);
        ventana.setPadding(new Insets(18));
        ventana.getStyleClass().add("ventana-graficas");

        Scene escena = new Scene(ventana, 800, 600);
        escena.getStylesheets().add(getClass().getResource("/recursos/estilos.css").toExternalForm());
        setScene(escena);
    }

    public void mostrar(String columna, TipoGrafica tipo, Map<Metodo, Long> tiemposNs) {
        leyenda.getChildren().clear();

        EnumMap<Metodo, Long> ordenados = new EnumMap<>(Metodo.class);
        ordenados.putAll(tiemposNs);

        Grafica grafica = tipo.crear("Tiempo de ordenación de la columna " + columna);
        grafica.cargarTiempos(ordenados);
        ventana.setCenter(grafica.getGrafica());

        for (Map.Entry<Metodo, Long> e : ordenados.entrySet()) {
            leyenda.getChildren().add(crearItemLeyenda(e.getKey(), e.getValue()));
        }
        show();
        toFront();
    }

    public HBox crearItemLeyenda(Metodo metodo, long tiempoNs) {
        Region cuadro = new Region();
        cuadro.getStyleClass().addAll("cuadro-leyenda", Grafica.claseCss(metodo));
        Label texto = new Label(metodo.getNombre() + ": " + Grafica.formatearTiempo(tiempoNs));
        HBox item = new HBox(6, cuadro, texto);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }
}
