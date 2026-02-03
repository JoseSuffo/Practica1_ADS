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
    public void botonAltas(){
        TextInputDialog dialogNombre=new TextInputDialog();
        dialogNombre.setTitle("Nueva Alta");
        dialogNombre.setHeaderText("Paso 1 de 3");
        dialogNombre.setContentText("Introduce el nombre:");
        Optional<String> nombre=dialogNombre.showAndWait();
        if(nombre.isPresent() && !nombre.get().isEmpty()){
            TextInputDialog dialogDir = new TextInputDialog();
            dialogDir.setTitle("Nueva Alta");
            dialogDir.setHeaderText("Paso 2 de 3");
            dialogDir.setContentText("Introduce la dirección:");
            Optional<String> direccion = dialogDir.showAndWait();
            if(direccion.isPresent()){
                TextInputDialog dialogTlf = new TextInputDialog();
                dialogTlf.setTitle("Nueva Alta");
                dialogTlf.setHeaderText("Paso 3 de 3");
                dialogTlf.setContentText("Introduce el teléfono:");
                Optional<String> telefono = dialogTlf.showAndWait();

                try {
                    ejecutarInsert(nombre.get(), direccion.get(), telefono.get());
                    mostrarAlerta("Éxito", "Guardado: " + nombre.get());
                } catch (SQLException e) {
                    mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
                }
            }
        }
    }

    public void ejecutarInsert(String nombre, String direccion, String telefono) throws SQLException {
        String sqlPersona = "INSERT INTO Personas (nombre, direccion) VALUES (?, ?)";
        String sqlTelefono = "INSERT INTO Telefonos (personaId, telefono) VALUES (LAST_INSERT_ID(), ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtP = conn.prepareStatement(sqlPersona);
                 PreparedStatement pstmtT = conn.prepareStatement(sqlTelefono)) {

                pstmtP.setString(1, nombre);
                pstmtP.setString(2, direccion);
                pstmtP.executeUpdate();

                pstmtT.setString(1, telefono);
                pstmtT.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @FXML
    public void botonBajas() {
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Baja de Persona");
        dialogId.setHeaderText("Eliminar por ID");
        dialogId.setContentText("Introduce el ID:");

        dialogId.showAndWait().ifPresent(idStr -> {
            try {
                int id = Integer.parseInt(idStr);
                int filas = ejecutarDelete(id);

                if (filas > 0) {
                    mostrarAlerta("Éxito", "Registro eliminado.");
                } else {
                    mostrarAlerta("Aviso", "No se encontró el ID " + id);
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "El ID debe ser un número.");
            } catch (SQLException e) {
                mostrarAlerta("Error Crítico", e.getMessage());
            }
        });
    }

    public int ejecutarDelete(int id) throws SQLException {
        String sql = "DELETE FROM Personas WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        }
    }

    @FXML
    public void botonModificaciones() {
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Modificar");
        dialogId.setHeaderText("ID a modificar:");

        dialogId.showAndWait().ifPresent(idStr -> {
            try {
                int id = Integer.parseInt(idStr);

                TextInputDialog dNombre = new TextInputDialog();
                dNombre.setContentText("Nuevo nombre:");
                String nuevoNom = dNombre.showAndWait().orElse("");

                TextInputDialog dDir = new TextInputDialog();
                dDir.setContentText("Nueva dirección:");
                String nuevaDir = dDir.showAndWait().orElse("");

                if (!nuevoNom.isEmpty()) {
                    ejecutarUpdate(id, nuevoNom, nuevaDir);
                    mostrarAlerta("Éxito", "Registro actualizado.");
                }
            } catch (Exception e) {
                mostrarAlerta("Error", e.getMessage());
            }
        });
    }

    public void ejecutarUpdate(int id, String nombre, String direccion) throws SQLException {
        String sql = "UPDATE Personas SET nombre = ?, direccion = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, direccion);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }

    @FXML
    public void botonConsultas() {
        try {
            String lista = obtenerDatosConsulta();
            mostrarAlerta("Listado", lista.isEmpty() ? "Vacío" : lista);
        } catch (SQLException e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    public String obtenerDatosConsulta() throws SQLException {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT * FROM Personas";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sb.append(rs.getInt("id")).append(" - ").append(rs.getString("nombre")).append("\n");
            }
        }
        return sb.toString();
    }

    @FXML
    public void botonSalir(javafx.event.ActionEvent actionEvent){
        System.exit(0);
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}