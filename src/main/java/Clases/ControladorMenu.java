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

                if(telefono.isPresent()){
                    ejecutarInsert(nombre.get(), direccion.get(), telefono.get());
                }
            }
        }
    }

    public void ejecutarInsert(String nombre, String direccion, String telefono) {
        String sqlPersona = "INSERT INTO Personas (nombre, direccion) VALUES (?, ?)";
        String sqlTelefono = "INSERT INTO Telefonos (personaId, telefono) VALUES (LAST_INSERT_ID(), ?)";

        try(Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)){
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtP = conn.prepareStatement(sqlPersona);
                 PreparedStatement pstmtT = conn.prepareStatement(sqlTelefono)) {
                pstmtP.setString(1, nombre);
                pstmtP.setString(2, direccion);
                pstmtP.executeUpdate();

                pstmtT.setString(1, telefono);
                pstmtT.executeUpdate();

                conn.commit();
                mostrarAlerta("Éxito", "Guardado: "+nombre+" con teléfono "+telefono);

            }catch(SQLException e){
                conn.rollback();
                throw e;
            }
        }catch(SQLException e){
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    public void botonBajas() {
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Baja de Persona");
        dialogId.setHeaderText("Eliminar registro");
        dialogId.setContentText("Introduce el ID de la persona:");

        Optional<String> idInput=dialogId.showAndWait();

        if (idInput.isPresent() && !idInput.get().isEmpty()) {
            try{
                int id=Integer.parseInt(idInput.get());
                ejecutarDelete(id);
            }catch(NumberFormatException e){
                mostrarAlerta("Error", "El ID debe ser un número válido.");
            }
        }
    }

    public void ejecutarDelete(int id) {
        String sql="DELETE FROM Personas WHERE id = ?";

        try(Connection conn=DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt=conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();

            if(filasAfectadas > 0){
                mostrarAlerta("Éxito", "Persona con ID "+id+" eliminada correctamente.");
            }else{
                mostrarAlerta("Aviso", "No se encontró ninguna persona con el ID "+id);
            }

        }catch (SQLException e){
            mostrarAlerta("Error", "No se pudo eliminar: "+e.getMessage());
        }
    }

    @FXML
    public void botonModificaciones() {
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Modificar Persona");
        dialogId.setHeaderText("Paso 1: Localizar registro");
        dialogId.setContentText("Introduce el ID de la persona:");

        Optional<String> idInput=dialogId.showAndWait();

        if(idInput.isPresent() && !idInput.get().isEmpty()){
            try{
                int id=Integer.parseInt(idInput.get());

                TextInputDialog dialogNombre = new TextInputDialog();
                dialogNombre.setTitle("Modificar Persona");
                dialogNombre.setHeaderText("Paso 2: Nuevo Nombre");
                dialogNombre.setContentText("Introduce el nombre actualizado:");
                Optional<String> nuevoNombre = dialogNombre.showAndWait();

                if(nuevoNombre.isPresent()){
                    TextInputDialog dialogDir = new TextInputDialog();
                    dialogDir.setTitle("Modificar Persona");
                    dialogDir.setHeaderText("Paso 3: Nueva Dirección");
                    dialogDir.setContentText("Introduce la dirección actualizada:");
                    Optional<String> nuevaDir = dialogDir.showAndWait();

                    if(nuevaDir.isPresent()){
                        ejecutarUpdate(id, nuevoNombre.get(), nuevaDir.get());
                    }
                }
            }catch(NumberFormatException e){
                mostrarAlerta("Error", "El ID debe ser un número válido.");
            }
        }
    }

    public void ejecutarUpdate(int id, String nombre, String direccion) {
        String sql="UPDATE Personas SET nombre = ?, direccion = ? WHERE id = ?";

        try(Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, nombre);
            pstmt.setString(2, direccion);
            pstmt.setInt(3, id);

            int filasAfectadas=pstmt.executeUpdate();

            if(filasAfectadas>0){
                mostrarAlerta("Éxito", "Registro con ID "+id+" actualizado correctamente.");
            }else{
                mostrarAlerta("Aviso", "No se encontró ninguna persona con el ID "+id);
            }
        }catch (SQLException e){
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage());
        }
    }

    @FXML
    public void botonConsultas(){
        StringBuilder resultado=new StringBuilder();
        String sql="SELECT * FROM Personas";

        try(Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                resultado.append("ID: ").append(rs.getInt("id"))
                        .append(" - ").append(rs.getString("nombre")).append("\n");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Consulta de Personas");
            alert.setHeaderText("Registros encontrados:");
            alert.setContentText(!resultado.isEmpty() ? resultado.toString() : "La tabla está vacía.");
            alert.showAndWait();
        }catch (SQLException e){
            mostrarAlerta("Error", "Error al consultar: " + e.getMessage());
        }
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