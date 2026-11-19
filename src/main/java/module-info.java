module com.example.practica6algoritmos {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.practica6algoritmos to javafx.fxml;
    exports com.example.practica6algoritmos;
}