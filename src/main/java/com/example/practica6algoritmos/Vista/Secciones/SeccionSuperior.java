package com.example.practica6algoritmos.Vista.Secciones;

import com.example.practica6algoritmos.Modelo.Metodo;
import com.example.practica6algoritmos.Vista.Objetos.Graficas.TipoGrafica;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SeccionSuperior {
    private HBox contenedor;
    private ComboBox<String> columnaASeleccionar;
    private ComboBox<Metodo> ordenamientoASeleccionar;
    private Button botonOrdenar;
    private Button botonRestablecer;
    private CheckBox checkBoxCompararConMetodos;
    private Button botonMostrarGrafica;
    private Label tiempoOrdenamiento;

    private BooleanProperty ocupado;
    private BooleanProperty tiemposDisponibles;
    private BooleanProperty columnaSinGrafica;
    private Consumer<String> cambiarColumna;

    public SeccionSuperior(ArrayList<String> columnas) {
        columnaASeleccionar = new ComboBox<>();
        ordenamientoASeleccionar = new ComboBox<>();
        botonOrdenar = new Button("Ordenar");
        botonRestablecer = new Button("Restablecer");
        checkBoxCompararConMetodos = new CheckBox("Comparar todos");
        botonMostrarGrafica = new Button("Graficar");
        tiempoOrdenamiento = new Label("--");
        ocupado = new SimpleBooleanProperty(false);
        tiemposDisponibles = new SimpleBooleanProperty(false);
        columnaSinGrafica = new SimpleBooleanProperty(false);

        configurarComboBoxes(columnas);
        configurarBotones();

        contenedor = new HBox(12);
        contenedor.getStyleClass().add("seccion-superior");
        contenedor.setAlignment(Pos.CENTER_LEFT);
        contenedor.setPadding(new Insets(16, 18, 16, 16));
        contenedor.getChildren().addAll(
                new Label("Columna: "), columnaASeleccionar,
                new Label("Metodo: "), ordenamientoASeleccionar,
                botonOrdenar, botonRestablecer,
                checkBoxCompararConMetodos,
                botonMostrarGrafica,
                tiempoOrdenamiento
        );
    }

    public void configurarComboBoxes(ArrayList<String> columnas) {
        columnaASeleccionar.setPrefWidth(200);
        columnaASeleccionar.setVisibleRowCount(12);
        columnaASeleccionar.getItems().setAll(columnas);
        columnaASeleccionar.valueProperty().addListener((obs, ant, nueva) -> {
            columnaSinGrafica.set(nueva == null);
            tiemposDisponibles.set(false);
            tiempoOrdenamiento.setText("--");
            if (cambiarColumna != null && nueva != null) cambiarColumna.accept(nueva);
        });
        columnaASeleccionar.getSelectionModel().selectFirst();

        ordenamientoASeleccionar.setPrefWidth(200);
        ordenamientoASeleccionar.getItems().setAll(Metodo.values());   // se ve con Metodo.toString()
        ordenamientoASeleccionar.setValue(Metodo.QUICKSORT);

        columnaASeleccionar.disableProperty().bind(ocupado);
        ordenamientoASeleccionar.disableProperty().bind(
                ocupado.or(checkBoxCompararConMetodos.selectedProperty()));

    }

    private void configurarBotones() {
        botonOrdenar.disableProperty().bind(ocupado);
        botonRestablecer.disableProperty().bind(ocupado);
        checkBoxCompararConMetodos.disableProperty().bind(ocupado);
        botonMostrarGrafica.disableProperty().bind(ocupado.or(tiemposDisponibles.not()));
    }

    public String getColumnaSeleccionada() {
        return columnaASeleccionar.getValue();
    }

    public Metodo getMetodoSeleccionado() {
        return ordenamientoASeleccionar.getValue();
    }

    public boolean isCompararActivado() {
        return checkBoxCompararConMetodos.isSelected();
    }

    public void setOcupado(boolean valor) {
        ocupado.set(valor);
    }

    public void setTiemposDisponibles(boolean valor) {
        tiemposDisponibles.set(valor);
    }

    public void setTiempoOrdenamiento(String texto) {
        tiempoOrdenamiento.setText(texto);
    }

    public void alOrdenar(Runnable accion) {
        botonOrdenar.setOnAction(e-> accion.run());
    }

    public void alRestablecer(Runnable accion) {
        botonRestablecer.setOnAction(e-> accion.run());
    }

    public void alGraficar(Runnable accion) {
        botonMostrarGrafica.setOnAction(e-> accion.run());
    }

    public void alCambiarColumna(Consumer<String> accion) {
        cambiarColumna = accion;
        if (accion != null && getColumnaSeleccionada() != null) {
            accion.accept(getColumnaSeleccionada());
        }
    }

    public HBox getContenedor() {
        return contenedor;
    }



}
