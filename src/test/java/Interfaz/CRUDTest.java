package Interfaz;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CRUDTest {
    private final String URL = "jdbc:mariadb://localhost:3306/agenda";
    private final String USER = "usuario1";
    private final String PASSWORD = "superpassword";
    private static int idGenerado;

    @Test
    @Order(1)
    void testIntegracionAlta() throws SQLException {
        ControladorMenu controlador = new ControladorMenu();
        controlador.ejecutarInsert("Persona Test", "Calle Falsa 123", "111-222333");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM Personas WHERE nombre = 'Persona Test'");
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next(), "La persona debería existir.");
            idGenerado = rs.getInt("id");
        }
    }

    @Test
    @Order(2)
    void testIntegracionConsulta() throws SQLException {
        ControladorMenu controlador = new ControladorMenu();
        String resultado = controlador.obtenerDatosConsulta();

        assertTrue(resultado.contains("Persona Test"), "El listado debería contener a la persona insertada.");
    }

    @Test
    @Order(3)
    void testIntegracionModificacion() throws SQLException {
        ControladorMenu controlador = new ControladorMenu();
        String nuevoNombre = "Nombre Modificado";

        controlador.ejecutarUpdate(idGenerado, nuevoNombre, "Nueva Direccion");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT nombre FROM Personas WHERE id = ?");
            pstmt.setInt(1, idGenerado);
            ResultSet rs = pstmt.executeQuery();

            rs.next();
            assertEquals(nuevoNombre, rs.getString("nombre"), "El nombre no se actualizó en la BD.");
        }
    }

    @Test
    @Order(4)
    void testIntegracionBaja() throws SQLException {
        ControladorMenu controlador = new ControladorMenu();

        int filasAfectadas = controlador.ejecutarDelete(idGenerado);

        assertEquals(1, filasAfectadas, "Debería haber borrado exactamente 1 fila.");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Personas WHERE id = ?");
            pstmt.setInt(1, idGenerado);
            ResultSet rs = pstmt.executeQuery();
            assertFalse(rs.next(), "La persona todavía existe y debería haber sido borrada.");
        }
    }
}