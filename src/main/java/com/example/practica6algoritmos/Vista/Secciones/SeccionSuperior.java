package com.example.practica6algoritmos.Vista.Secciones;

import com.example.practica6algoritmos.Modelo.Metodo;
import com.example.practica6algoritmos.Vista.Objetos.TipoGrafica;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SeccionSuperior extends HBox {
    private HBox contenedor;
    private ComboBox<String> columnaASeleccionar;
    private ComboBox<String> ordenamientoASeleccionar;
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
        super(12);
        columnaASeleccionar = new ComboBox<>();
        ordenamientoASeleccionar = new ComboBox<>();
        botonOrdenar = new Button("Ordenar columna");
        botonRestablecer = new Button("Restablecer columnas");
        checkBoxCompararConMetodos = new CheckBox("Permitir comparar con otros ordenamientos");
        botonMostrarGrafica = new Button("Graficar ordenamiento");
        tiempoOrdenamiento = new Label();
        ocupado = new SimpleBooleanProperty(false);
        tiemposDisponibles = new SimpleBooleanProperty(false);
        columnaSinGrafica = new SimpleBooleanProperty(false);


        configurarComboBoxes(columnas);
        configurarBotones();

        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(16, 18, 16, 16));
        getChildren().addAll(
                new Label("Columna: "), columnaASeleccionar,
                new Label("Ordenamiento: "), ordenamientoASeleccionar,
                botonOrdenar, botonRestablecer,
                botonMostrarGrafica
        );

        contenedor = new HBox();
    }

    public void configurarComboBoxes(ArrayList<String> columnas) {
        columnaASeleccionar.setPrefWidth(200);
        columnaASeleccionar.setVisibleRowCount(12);
        columnaASeleccionar.getItems().setAll(columnas);
        columnaASeleccionar.valueProperty().addListener((obs, ant, nueva) -> {
            columnaSinGrafica.set(nueva == null || TipoGrafica.deColumna(nueva) == TipoGrafica.SIN_GRAFICA);
            tiemposDisponibles.set(false);
            tiempoOrdenamiento.setText("--");
            if (cambiarColumna != null && nueva != null) cambiarColumna.accept(nueva);

        });
        columnaASeleccionar.getSelectionModel().selectFirst();

        ordenamientoASeleccionar.setPrefWidth(200);
        for (Metodo metodo : Metodo.values()) ordenamientoASeleccionar.getItems().add(metodo.getNombre());
        ordenamientoASeleccionar.setValue(Metodo.QUICKSORT.getNombre());

        columnaASeleccionar.disableProperty().bind(ocupado);
        ordenamientoASeleccionar.disableProperty().bind(ocupado);

    }

    public void configurarBotones() {

        botonOrdenar.disableProperty().bind(ocupado);
        botonRestablecer.disableProperty().bind(ocupado);
        botonMostrarGrafica.disableProperty().bind(ocupado);

    }

    public String getColumnaSeleccionada() {
        return columnaASeleccionar.getValue();
    }

    public ArrayList<Metodo> getMetodoSeleccionado() {
        ArrayList<Metodo> metodoSeleccionado = new ArrayList<>();
        String metodoElegido = ordenamientoASeleccionar.getValue();
        for (Metodo metodo : Metodo.values()) {
            if (metodo.getNombre().equalsIgnoreCase(metodoElegido)) metodoSeleccionado.add(metodo);
        }
        return metodoSeleccionado;
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
    }

}
