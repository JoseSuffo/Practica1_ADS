package Clases;

import javafx.fxml.FXML;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.util.Optional;

public class ControladorMenu {
    private final String URL = "jdbc:mariadb://localhost:3306/agenda";
    private final String USER = "usuario1";
    private final String PASSWORD = "superpassword";

    @FXML
    public void botonAltas(javafx.event.ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    public void botonBajas(javafx.event.ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    public void botonModificaciones(javafx.event.ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    public void botonConsultas() {
        StringBuilder resultado = new StringBuilder();
        String sql = "SELECT * FROM Personas";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultado.append("ID: ").append(rs.getInt("id"))
                        .append(" - ").append(rs.getString("nombre")).append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Consulta de Personas");
            alert.setHeaderText("Registros encontrados:");
            alert.setContentText(!resultado.isEmpty() ? resultado.toString() : "La tabla está vacía.");
            alert.showAndWait();

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al consultar: " + e.getMessage());
        }
    }

    @FXML
    public void botonSalir(javafx.event.ActionEvent actionEvent) {
        System.exit(0);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
