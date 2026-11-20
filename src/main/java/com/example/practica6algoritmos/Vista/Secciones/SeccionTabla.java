package com.example.practica6algoritmos.Vista.Secciones;

import com.example.practica6algoritmos.Modelo.Cancion;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.Grafica;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.GraficaBarras;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.TipoGrafica;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

import java.util.*;

public class SeccionTabla extends StackPane {
    private final double ALTO_FILA_GRAFICAS = 200;
    private final double ALTO_FILA_DATOS = 100;     // ~3 líneas de texto; súbelo si quieres filas más altas
    private final double ALTO_ENCABEZADO = 62;     // ~2 líneas; súbelo a ~62 para 3 líneas

    private ArrayList<String> columnasAnchas;
    private Cancion filaGraficas;
    private TableView<Cancion> tabla;
    private HashMap<String, TableColumn<Cancion, Object>> columnasPorNombre;
    private HashMap<String, Grafica> graficasPorColumna;
    private HashMap<String, Label> labelsPorColumna;

    public SeccionTabla(ArrayList<String> nombresColumnas) {
        columnasAnchas = new ArrayList<>(Arrays.asList("song_id", "song", "spotify_genre", "spotify_track_album",
                "spotify_track_preview_url", "evidence_span", "cot_rationale"));
        graficasPorColumna = new HashMap<>();
        tabla = new TableView<>();
        columnasPorNombre = new HashMap<>();
        labelsPorColumna = new HashMap<>();

        tabla.getStyleClass().add("tabla");
        tabla.setPlaceholder(new Label("No hay datos por mostrar"));

        for (String nombre : nombresColumnas) {
            // Sin texto: el encabezado se dibuja solo con el graphic (evita duplicados)
            TableColumn<Cancion, Object> columna = new TableColumn<>();

            Label headerLabel = new Label(nombre.replace("_", " "));
            headerLabel.setWrapText(true);
            headerLabel.setTextAlignment(TextAlignment.CENTER);
            headerLabel.setAlignment(Pos.CENTER);
            headerLabel.setMinWidth(0);
            // Sin altura explícita el encabezado mide una sola línea y recorta el wrap con "..."
            headerLabel.setMinHeight(Region.USE_PREF_SIZE);
            headerLabel.setPrefHeight(ALTO_ENCABEZADO);
            headerLabel.setMaxHeight(Region.USE_PREF_SIZE);
            // El label necesita un ancho conocido para poder hacer wrap
            headerLabel.prefWidthProperty().bind(columna.widthProperty().subtract(16));

            columna.setGraphic(headerLabel);
            columna.setCellValueFactory(new PropertyValueFactory<>(nombre));

            columna.setPrefWidth(columnasAnchas.contains(nombre) ? 240 : 110);
            columna.setSortable(false);
            columnasPorNombre.put(nombre, columna);
            tabla.getColumns().add(columna);
        }

        filaGraficas = new Cancion();

        for (String nombre : nombresColumnas) {
            TipoGrafica tipo = TipoGrafica.tipoDeColumna(nombre);
            TableColumn<Cancion, Object> columna = columnasPorNombre.get(nombre);
            if (tipo == TipoGrafica.SIN_GRAFICA) {
                Label label = new Label();
                label.setWrapText(true);
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);

                labelsPorColumna.put(nombre, label);
                columna.setCellFactory(c -> crearCelda(nombre));
                continue;
            }

            Grafica grafica = tipo.crear("");   // sin título, no cabe en la celda
            grafica.convertirACelda();
            if (nombre.equals("spotify_track_duration_ms") && grafica instanceof GraficaBarras barras) {
                barras.setRango(29700, 3080000);   // excepción: rango fijo para la duración
            }
            if (nombre.equals("mode") && grafica instanceof GraficaBarras barras) {   // el nombre es de ejemplo
                barras.setCantidadBarrasFijas(2);
            }
            if (grafica.getGrafica() instanceof Region region) {
                region.setMinSize(0, 0);
                region.setPrefHeight(ALTO_FILA_GRAFICAS - 4);
                region.setMaxHeight(ALTO_FILA_GRAFICAS - 4);
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
                // Las filas se reutilizan, así que también hay que restablecer la altura.
                // En las filas de datos la altura se calcula según el texto envuelto.
                setPrefHeight(esFilaGraficas ? ALTO_FILA_GRAFICAS : USE_COMPUTED_SIZE);
            }
        });

        getChildren().add(tabla);
    }

    private TableCell<Cancion, Object> crearCelda(String nombreColumna) {
        return new TableCell<>() {
            private final Tooltip tooltip = new Tooltip();

            {
                setPadding(new Insets(4, 6, 4, 6));
                setAlignment(Pos.CENTER_LEFT);
                setWrapText(true);                         // la celda ya conoce el ancho de su columna
                setTextOverrun(OverrunStyle.ELLIPSIS);     // si no cabe en la altura fija, corta con "..."
                tooltip.setWrapText(true);
                tooltip.setMaxWidth(400);
            }

            private void fijarAlto(double alto) {
                setMinHeight(alto);
                setPrefHeight(alto);
                setMaxHeight(alto);
            }

            @Override
            protected void updateItem(Object item, boolean vacia) {
                super.updateItem(item, vacia);

                if (vacia) {   // celda vacía: limpiar todo
                    setText(null);
                    setGraphic(null);
                    setTooltip(null);
                    fijarAlto(USE_COMPUTED_SIZE);
                    return;
                }

                if (getIndex() == 0) {   // la fila de gráficas siempre es la primera
                    Node grafica = graficasPorColumna.containsKey(nombreColumna)
                            ? graficasPorColumna.get(nombreColumna).getGrafica()
                            : labelsPorColumna.get(nombreColumna);
                    setText(null);
                    setPadding(Insets.EMPTY);
                    setTooltip(null);
                    setGraphic(grafica);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    fijarAlto(USE_COMPUTED_SIZE);   // la altura la dan las gráficas
                    return;
                }

                // Fila de datos: texto envuelto con altura fija
                String valor = item == null ? null : item.toString();
                setGraphic(null);
                setPadding(new Insets(4, 6, 4, 6));
                setContentDisplay(ContentDisplay.TEXT_ONLY);   // restablecer: las celdas se reutilizan
                setText(valor);
                if (valor == null) {
                    setTooltip(null);
                } else {
                    tooltip.setText(valor);
                    setTooltip(tooltip);
                }
                fijarAlto(ALTO_FILA_DATOS);
            }
        };
    }

    public void mostrarCanciones(ArrayList<Cancion> canciones) {
        cargarGraficas(canciones);
        cargarLabels(canciones);
        ArrayList<Cancion> filas = new ArrayList<>(canciones.size() + 1);
        filas.add(filaGraficas);
        filas.addAll(canciones);
        tabla.setItems(FXCollections.observableList(filas));
    }

    public void cargarGraficas(ArrayList<Cancion> canciones) {
        for (String nombre : graficasPorColumna.keySet()) {
            TableColumn<Cancion, Object> columna = columnasPorNombre.get(nombre);
            TipoGrafica tipo = TipoGrafica.tipoDeColumna(nombre);

            ArrayList<Double> valores = new ArrayList<>();
            ArrayList<Boolean> explicitos = new ArrayList<>();
            for (Cancion c : canciones) {
                Object valor = columna.getCellValueFactory()
                        .call(new TableColumn.CellDataFeatures<>(tabla, columna, c)).getValue();
                if (tipo == TipoGrafica.PASTEL) {
                    explicitos.add((Boolean) valor);
                }
                if (valor == null) continue;

                if (valor instanceof Number numero && !Double.isNaN(numero.doubleValue())){
                    valores.add(numero.doubleValue());
                }
            }
            if (tipo == TipoGrafica.PASTEL) graficasPorColumna.get(nombre).cargarValoresBooleanos(explicitos);
            else graficasPorColumna.get(nombre).cargarValores(valores);
        }
    }

    private void cargarLabels(ArrayList<Cancion> canciones) {
        for (String nombre : labelsPorColumna.keySet()) {
            TableColumn<Cancion, Object> columna = columnasPorNombre.get(nombre);

            HashSet<Object> unicos = new HashSet<>();
            int nulos = 0;
            for (Cancion c : canciones) {
                Object valor = columna.getCellValueFactory()
                        .call(new TableColumn.CellDataFeatures<>(tabla, columna, c)).getValue();
                if (valor == null || valor.toString().isBlank()) nulos++;   // null o vacío
                else unicos.add(valor);
            }

            Label label = labelsPorColumna.get(nombre);
            label.setText(unicos.size() + " Valores únicos. " + "\nDatos nulos: " + nulos);
            if (!label.getStyleClass().contains("label-tabla")) {   // evita duplicar la clase en cada carga
                label.getStyleClass().add("label-tabla");
            }
        }
    }

}