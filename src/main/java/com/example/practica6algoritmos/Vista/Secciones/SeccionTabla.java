package com.example.practica6algoritmos.Vista.Secciones;

import com.example.practica6algoritmos.Modelo.Cancion;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.Grafica;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.GraficaBarras;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.GraficaPastel;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.TipoGrafica;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.*;

public class SeccionTabla extends StackPane {
    private ArrayList<String> columnasAnchas;
    private Cancion filaGraficas;

    private TableView<Cancion> tabla;
    private HashMap<String, TableColumn<Cancion, Object>> columnasPorNombre;
    private HashMap<String, Grafica> graficasPorColumna;

    public SeccionTabla(ArrayList<String> nombresColumnas) {
        columnasAnchas = new ArrayList<>(Arrays.asList("song_id", "song", "spotify_genre", "spotify_track_album",
                "spotify_track_preview_url", "evidence_span", "cot_rationale"));
        graficasPorColumna = new HashMap<>();
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

        for (String nombre : nombresColumnas) {
            if (TipoGrafica.deColumna(nombre) != TipoGrafica.BARRAS) continue;

            TableColumn<Cancion, Object> columna = columnasPorNombre.get(nombre);
            Grafica grafica = TipoGrafica.BARRAS.crear("");   // sin título, no cabe en la celda
            grafica.convertirACelda();
            if (nombre.equals("spotify_track_duration_ms") && grafica instanceof GraficaBarras barras) {
                barras.setRango(29700, 3080000);   // excepción: rango fijo para la duración
            }
            if (grafica.getGrafica() instanceof Region region) {
                region.setMinSize(0, 0);
                region.setPrefHeight(160 - 4);
                region.setMaxHeight(160 - 4);
                region.prefWidthProperty().bind(columna.widthProperty().subtract(4));
                region.maxWidthProperty().bind(columna.widthProperty().subtract(4));
            }
            graficasPorColumna.put(nombre, grafica);
            columna.setCellFactory(c -> crearCelda(nombre));
        }

        tabla.setRowFactory(tabla -> new TableRow<>() {
            @Override
            protected void updateItem(Cancion item, boolean vacia) {
                super.updateItem(item, vacia);
                boolean esFilaGraficas = !vacia && item == filaGraficas;
                // Las filas se reutilizan, así que también hay que restablecer la altura
                setPrefHeight(esFilaGraficas ? 160 : USE_COMPUTED_SIZE);
                setMouseTransparent(esFilaGraficas);
            }
        });

        getChildren().add(tabla);
    }

    private TableCell<Cancion, Object> crearCelda(String nombreColumna) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean vacia) {
                super.updateItem(item, vacia);
                Node grafica = graficasPorColumna.get(nombreColumna).getGrafica();

                if (!vacia && getIndex() == 0) {   // la fila de gráficas siempre es la primera
                    setText(null);
                    setGraphic(grafica);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } else {
                    setGraphic(null);
                    setText(vacia || item == null ? null : item.toString());
                }
            }
        };
    }

    public void mostrarCanciones(ArrayList<Cancion> canciones) {
        cargarGraficas(canciones);
        ArrayList<Cancion> filas = new ArrayList<>(canciones.size() + 1);
        filas.add(filaGraficas);
        filas.addAll(canciones);
        tabla.setItems(FXCollections.observableList(filas));
    }

    public void cargarGraficas(ArrayList<Cancion> canciones) {
        for (String nombre : graficasPorColumna.keySet()) {
            TableColumn<Cancion, Object> columna = columnasPorNombre.get(nombre);

            ArrayList<Double> valores = new ArrayList<>();
            for (Cancion c : canciones) {
                Object valor = columna.getCellValueFactory()
                        .call(new TableColumn.CellDataFeatures<>(tabla, columna, c)).getValue();
                valores.add(valor instanceof Number numero ? numero.doubleValue() : 0.0);
            }
            graficasPorColumna.get(nombre).cargarValores(valores);
        }
    }

}
