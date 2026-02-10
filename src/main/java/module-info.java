module com.example.practica1_ads {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // Conectividad de Base de Datos
    requires java.sql;
    requires org.mariadb.jdbc;

    // UI Libraries
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.graphics;

    exports Interfaz;
    opens Interfaz to javafx.fxml;
    exports Lógica;
    opens Lógica to javafx.fxml;
}