package com.example.practica6algoritmos;

import com.example.practica6algoritmos.Controlador.Controlador;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) {
        Controlador controlador = new Controlador(stage);
    }
}
