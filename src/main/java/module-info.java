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

    // Paquete principal de JavaFX
    opens com.example.practica1_ads to javafx.fxml;
    exports com.example.practica1_ads;

    // IMPORTANTE: Exportar el paquete donde está tu lógica de DB
    // (Asegúrate de que el nombre coincida exactamente con 'package Clases;' en tu archivo .java)
    exports Clases;
}