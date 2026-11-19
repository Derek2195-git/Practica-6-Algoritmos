package com.example.practica6algoritmos;

import com.example.practica6algoritmos.Controlador.Controlador;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Controlador controlador = new Controlador(stage);
    }
}
