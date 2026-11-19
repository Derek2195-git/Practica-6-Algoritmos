package com.example.practica6algoritmos.Vista.Ventana;

import com.example.practica6algoritmos.Modelo.Cancion;
import com.example.practica6algoritmos.Modelo.Metodo;
import com.example.practica6algoritmos.Vista.Secciones.SeccionSuperior;
import com.example.practica6algoritmos.Vista.Secciones.SeccionTabla;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class VentanaPrincipal {
    private VBox ventana;
    private SeccionSuperior seccionSuperior;
    private SeccionTabla seccionTabla;

    public VentanaPrincipal(ArrayList<String> nombresColumnas) {
        seccionTabla = new SeccionTabla(nombresColumnas);
        seccionSuperior = new SeccionSuperior(nombresColumnas);
        VBox.setVgrow(seccionTabla, Priority.ALWAYS);

        ventana = new VBox(16);
        ventana.getChildren().addAll(seccionSuperior.getContenedor(), seccionTabla);
        ventana.setPadding(new Insets(18));


        ventana.getStylesheets().add(getClass().getResource("/recursos/estilos.css").toExternalForm());

    }

    public SeccionTabla getSeccionTabla() {
        return seccionTabla;
    }

    public SeccionSuperior getSeccionSuperior() {
        return seccionSuperior;
    }

    public VBox getVentana() {
        return ventana;
    }

}
