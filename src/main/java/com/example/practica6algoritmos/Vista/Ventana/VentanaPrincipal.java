package com.example.practica6algoritmos.Vista.Ventana;

import com.example.practica6algoritmos.Vista.Secciones.SeccionSuperior;
import com.example.practica6algoritmos.Vista.Secciones.SeccionTabla;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class VentanaPrincipal {
    private VBox ventana;
    private SeccionSuperior seccionSuperior;
    private SeccionTabla seccionTabla;

    public VentanaPrincipal(ArrayList<String> nombresColumnas) {
        seccionTabla = new SeccionTabla(nombresColumnas);

        ventana = new VBox(seccionTabla);

        ventana.getStylesheets().add(getClass().getResource("/recursos/estilos.css").toExternalForm());
    }

    public SeccionTabla getSeccionTabla() {
        return seccionTabla;
    }

    public VBox getVentana() {
        return ventana;
    }

}
