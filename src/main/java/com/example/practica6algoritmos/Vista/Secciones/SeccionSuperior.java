package com.example.practica6algoritmos.Vista.Secciones;

import com.example.practica6algoritmos.Modelo.Metodo;
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

    /**
     * Metodo que crea la sección donde van todos los botones, comboboxes y la checkbox de la aplicación
     * @param columnas Nombre de las columnas para el combobox
     */
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

        // Configuramos los el contenido comboboxes y hacemos que no se puedan mover
        // Mientras se este cargando uno de los metodos
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

    /**
     * Metodo que configura el contenido de ambas comoboxes
     * @param columnas Nombre de todas las columnas para el ComboBox de columnas
     */
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
        // Ponemos todos los métodos de la enumeración
        ordenamientoASeleccionar.getItems().setAll(Metodo.values());
        ordenamientoASeleccionar.setValue(Metodo.QUICKSORT);

        columnaASeleccionar.disableProperty().bind(ocupado);
        ordenamientoASeleccionar.disableProperty().bind(
                ocupado.or(checkBoxCompararConMetodos.selectedProperty()));

    }

    /**
     * Metodo que configura los botones para no permitir que podamos
     * hacer otro ordenamiento mientras uno ocurre
     */
    private void configurarBotones() {
        botonOrdenar.disableProperty().bind(ocupado);
        botonRestablecer.disableProperty().bind(ocupado);
        checkBoxCompararConMetodos.disableProperty().bind(ocupado);
        botonMostrarGrafica.disableProperty().bind(ocupado.or(tiemposDisponibles.not()));
    }

    /**
     * Getter del conteneido de la columnas seleccionda en el combobox
     * @return Valor actual del combobox
     */
    public String getColumnaSeleccionada() {
        return columnaASeleccionar.getValue();
    }

    /**
     * Getter del metodo actual que existe en el combobox
     * @return Metodo actual seleccionado por el usuario para el ordenamiento
     */
    public Metodo getMetodoSeleccionado() {
        return ordenamientoASeleccionar.getValue();
    }

    /**
     * Metodo que revisa si la checkbox de comparar con todos los metodos ha sido activada
     * @return Booleano que indica si la checkbox está activada
     */
    public boolean isCompararActivado() {
        return checkBoxCompararConMetodos.isSelected();
    }

    /**
     * Setter de ocupado
     * @param ocupado Booleano que indica si la aplicación está ocupada
     */
    public void setOcupado(boolean ocupado) {
        this.ocupado.set(ocupado);
    }

    /**
     * Setter del atributo que indica si ya se han sacado los tiempos
     * @param tiemposDisponibles Booleano que indica si ya se han obtenido los tiempos con algun ordenamiento
     */
    public void setTiemposDisponibles(boolean tiemposDisponibles) {
        this.tiemposDisponibles.set(tiemposDisponibles);
    }

    /**
     * Metodo que cambia el label de tiempos de ordenamiento
     * @param texto Nuevo mensaje
     */
    public void setTiempoOrdenamiento(String texto) {
        tiempoOrdenamiento.setText(texto);
    }

    /**
     * Metodo que le da una accion al botón de ordenar
     * @param accion Accion a realizar
     */
    public void alOrdenar(Runnable accion) {
        botonOrdenar.setOnAction(e-> accion.run());
    }

    /**
     * Metodo que le da una accion al boton de restablecer
     * @param accion Accion a realizar
     */
    public void alRestablecer(Runnable accion) {
        botonRestablecer.setOnAction(e-> accion.run());
    }

    /**
     * Metodo que le da una accion al botón de graficar
     * @param accion Accion a realizar
     */
    public void alGraficar(Runnable accion) {
        botonMostrarGrafica.setOnAction(e-> accion.run());
    }

    /**
     * Metodo que le da una accion al combobox cuando se cambia una columna
     * @param accion Accion a realizar al cambiar la columna
     */
    public void alCambiarColumna(Consumer<String> accion) {
        cambiarColumna = accion;
        if (accion != null && getColumnaSeleccionada() != null) {
            accion.accept(getColumnaSeleccionada());
        }
    }

    // Getter del contenedor donde está la sección
    public HBox getContenedor() {
        return contenedor;
    }



}
