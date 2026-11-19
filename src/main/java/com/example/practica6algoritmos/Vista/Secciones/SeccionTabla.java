package com.example.practica6algoritmos.Vista.Secciones;

import com.example.practica6algoritmos.Modelo.Cancion;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.util.*;

public class SeccionTabla extends StackPane {
    private ArrayList<String> columnasAnchas;
    private Cancion filaGraficas;

    private TableView<Cancion> tabla;
    private HashMap<String, TableColumn<Cancion, Object>> columnasPorNombre;

    public SeccionTabla(ArrayList<String> nombresColumnas) {
        columnasAnchas = new ArrayList<>(Arrays.asList("song_id", "song", "spotify_genre", "spotify_track_album",
                "spotify_track_preview_url", "evidence_span", "cot_rationale"));
        tabla = new TableView<>();
        columnasPorNombre = new HashMap<>();

        tabla.getStyleClass().add("tabla");
        tabla.setPlaceholder(new Label("No hay datos por mostrar"));

        for (String nombre : nombresColumnas) {
            TableColumn<Cancion, Object> columna = new TableColumn<>(nombre);
            columna.setCellValueFactory(new PropertyValueFactory<>(nombre));
            columna.setPrefWidth(columnasAnchas.contains(nombre) ? 240 : 110);
            columna.setSortable(false);
            columnasPorNombre.put(nombre, columna);
            tabla.getColumns().add(columna);
        }

        filaGraficas = new Cancion();
        tabla.setRowFactory(tabla ->  new TableRow<>() {
            @Override
            protected void updateItem(Cancion item, boolean vacia) {
                super.updateItem(item, vacia);
                boolean esFilaNueva = !vacia && item == filaGraficas;
                if (item == filaGraficas) {
                    setPrefHeight(120);
                }
                //setStyle(esFilaNueva ? "-fx-background-color: #f0f0f0" : "");
                setMouseTransparent(esFilaNueva);
            }
        });

        getChildren().add(tabla);
    }

    public void mostrarCanciones(ArrayList<Cancion> canciones) {
        ArrayList<Cancion> filas = new ArrayList<>(canciones.size() + 1);
        filas.add(filaGraficas);
        filas.addAll(canciones);
        tabla.setItems(FXCollections.observableList(filas));
    }

    public void mostrarColumna(String nombre) {
        TableColumn<Cancion, Object> columna = columnasPorNombre.get(nombre);


    }
}
