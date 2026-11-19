package com.example.practica6algoritmos.Vista.Ventana;

import com.example.practica6algoritmos.Vista.Secciones.SeccionSuperior;
import com.example.practica6algoritmos.Vista.Secciones.SeccionTabla;
import javafx.scene.layout.VBox;

public class VentanaPrincipal {
    private VBox ventana;
    private SeccionSuperior seccionSuperior;
    private SeccionTabla seccionTabla;

    public VentanaPrincipal() {

        ventana = new VBox();

        ventana.getStylesheets().add(getClass().getResource("/recursos/estilos.css").toExternalForm());
    }

    public VBox getVentana() {
        return ventana;
    }


}
