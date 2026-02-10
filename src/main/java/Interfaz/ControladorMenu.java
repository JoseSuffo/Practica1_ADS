package Interfaz;

import Lógica.Direccion;
import Lógica.Persona;
import Lógica.Telefono;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import java.sql.*;
import java.util.Optional;

public class ControladorMenu {
    private final String URL = "jdbc:mariadb://localhost:3306/agenda";
    private final String USER = "usuario1";
    private final String PASSWORD = "superpassword";

    @FXML
    public void botonAltas() {
        TextInputDialog dialogNombre = new TextInputDialog();
        dialogNombre.setTitle("Nueva Alta");
        dialogNombre.setHeaderText("Registro de Persona");
        dialogNombre.setContentText("Introduce el nombre:");
        Optional<String> nombreOpt = dialogNombre.showAndWait();

        if (nombreOpt.isPresent() && !nombreOpt.get().isEmpty()) {
            Persona p = new Persona(nombreOpt.get());
            boolean añadirMasDir = true;
            while (añadirMasDir) {
                TextInputDialog dialogDir = new TextInputDialog();
                dialogDir.setTitle("Añadir Dirección");
                dialogDir.setHeaderText("Dirección nº " + (p.getDirecciones().size() + 1));
                dialogDir.setContentText("Introduce la calle:");
                Optional<String> dirOpt = dialogDir.showAndWait();

                if (dirOpt.isPresent() && !dirOpt.get().isEmpty()) {
                    p.addDireccion(new Direccion(dirOpt.get()));
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas añadir OTRA dirección?", ButtonType.YES, ButtonType.NO);
                    if (confirm.showAndWait().get() == ButtonType.NO) {
                        añadirMasDir = false;
                    }
                } else {
                    añadirMasDir = false;
                }
            }

            boolean añadirMasTlf = true;
            while (añadirMasTlf) {
                TextInputDialog dialogTlf = new TextInputDialog();
                dialogTlf.setTitle("Añadir Teléfono");
                dialogTlf.setHeaderText("Teléfono nº " + (p.getTelefonos().size() + 1));
                dialogTlf.setContentText("Introduce el número:");
                Optional<String> tlfOpt = dialogTlf.showAndWait();

                if (tlfOpt.isPresent() && !tlfOpt.get().isEmpty()) {
                    p.addTelefono(tlfOpt.get());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas añadir OTRO teléfono?", ButtonType.YES, ButtonType.NO);
                    if (confirm.showAndWait().get() == ButtonType.NO) {
                        añadirMasTlf = false;
                    }
                } else {
                    añadirMasTlf = false;
                }
            }

            if (!p.getDirecciones().isEmpty() || !p.getTelefonos().isEmpty()) {
                try {
                    ejecutarInsert(p);
                    mostrarAlerta("Éxito", "Se ha guardado a " + p.getNombre() + " con " +
                            p.getDirecciones().size() + " direcciones y " +
                            p.getTelefonos().size() + " teléfonos.");
                } catch (SQLException e) {
                    mostrarAlerta("Error", "Error de base de datos: " + e.getMessage());
                }
            }
        }
    }

    public void ejecutarInsert(Persona p) throws SQLException {
        String sqlPersona = "INSERT INTO Personas (nombre) VALUES (?)";
        String sqlBuscarDir = "SELECT id FROM Direcciones WHERE calle = ?";
        String sqlInsertDir = "INSERT INTO Direcciones (calle) VALUES (?)";
        String sqlVinculo = "INSERT INTO Persona_Direccion (personaId, direccionId) VALUES (?, ?)";
        String sqlTlf = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement psP = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
                psP.setString(1, p.getNombre());
                psP.executeUpdate();
                ResultSet rsP = psP.getGeneratedKeys();
                rsP.next();
                int personaId = rsP.getInt(1);

                for (Direccion d : p.getDirecciones()) {
                    int dirId;

                    PreparedStatement psB = conn.prepareStatement(sqlBuscarDir);
                    psB.setString(1, d.getCalle());
                    ResultSet rsB = psB.executeQuery();

                    if (rsB.next()) {
                        dirId = rsB.getInt("id");
                    } else {
                        PreparedStatement psI = conn.prepareStatement(sqlInsertDir, Statement.RETURN_GENERATED_KEYS);
                        psI.setString(1, d.getCalle());
                        psI.executeUpdate();
                        ResultSet rsI = psI.getGeneratedKeys();
                        rsI.next();
                        dirId = rsI.getInt(1);
                    }

                    PreparedStatement psV = conn.prepareStatement(sqlVinculo);
                    psV.setInt(1, personaId);
                    psV.setInt(2, dirId);
                    psV.executeUpdate();
                }

                for (Telefono t : p.getTelefonos()) {
                    PreparedStatement psT = conn.prepareStatement(sqlTlf);
                    psT.setInt(1, personaId);
                    psT.setString(2, t.getNumero());
                    psT.executeUpdate();
                }

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
        dialogId.setTitle("Modificar Nombre");
        dialogId.setHeaderText("Actualizar datos de Persona");
        dialogId.setContentText("Introduce el ID de la persona:");

        dialogId.showAndWait().ifPresent(idStr -> {
            try {
                int id = Integer.parseInt(idStr);

                TextInputDialog dNombre = new TextInputDialog();
                dNombre.setTitle("Nuevo Nombre");
                dNombre.setHeaderText("Modificando registro ID: " + id);
                dNombre.setContentText("Introduce el nuevo nombre:");

                Optional<String> nuevoNom = dNombre.showAndWait();

                if (nuevoNom.isPresent() && !nuevoNom.get().isEmpty()) {
                    ejecutarUpdate(id, nuevoNom.get());
                    mostrarAlerta("Éxito", "El nombre ha sido actualizado correctamente.");
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "El ID debe ser un número válido.");
            } catch (SQLException e) {
                mostrarAlerta("Error de BD", e.getMessage());
            }
        });
    }

    public void ejecutarUpdate(int id, String nuevoNombre) throws SQLException {
        String sql = "UPDATE Personas SET nombre = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevoNombre);
            pstmt.setInt(2, id);

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró ninguna persona con el ID: " + id);
            }
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

        String sql = "SELECT p.id, p.nombre, " +
                "GROUP_CONCAT(DISTINCT d.calle SEPARATOR ', ') AS direcciones, " +
                "GROUP_CONCAT(DISTINCT t.telefono SEPARATOR ' / ') AS telefonos " +
                "FROM Personas p " +
                "LEFT JOIN Persona_Direccion pd ON p.id = pd.personaId " +
                "LEFT JOIN Direcciones d ON pd.direccionId = d.id " +
                "LEFT JOIN Telefonos t ON p.id = t.personaId " +
                "GROUP BY p.id";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String direcciones = rs.getString("direcciones");
                String telefonos = rs.getString("telefonos");

                sb.append(String.format("ID: %d | Nombre: %s\n", id, nombre));
                sb.append(String.format("Direcciones: %s\n", (direcciones != null ? direcciones : "Sin dirección")));
                sb.append(String.format("Telefonos: %s\n", (telefonos != null ? telefonos : "Sin teléfono")));
                sb.append("------------------------------------------\n");
            }
        }
        return sb.toString();
    }

    @FXML
    public void botonSalir(javafx.event.ActionEvent actionEvent){
        System.exit(0);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);

        if (mensaje.length() > 200) {
            TextArea textArea = new TextArea(mensaje);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            alert.getDialogPane().setContent(textArea);
        } else {
            alert.setContentText(mensaje);
        }
        alert.showAndWait();
    }
}