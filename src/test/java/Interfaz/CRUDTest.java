package Interfaz;

import Lógica.Direccion;
import Lógica.Persona;
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
    void testIntegracionAltaMultiple() throws SQLException {
        ControladorMenu controlador = new ControladorMenu();

        Persona p = new Persona("John Unit Test");
        p.addDireccion(new Direccion("Calle Test 1"));
        p.addDireccion(new Direccion("Calle Test 2"));
        p.addTelefono("999-888777");

        controlador.ejecutarInsert(p);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM Personas WHERE nombre = ?");
            pstmt.setString(1, "John Unit Test");
            ResultSet rs = pstmt.executeQuery();

            assertTrue(rs.next(), "La persona debería existir en la tabla Personas.");
            idGenerado = rs.getInt("id");

            PreparedStatement pstmtDir = conn.prepareStatement("SELECT COUNT(*) FROM Persona_Direccion WHERE personaId = ?");
            pstmtDir.setInt(1, idGenerado);
            ResultSet rsDir = pstmtDir.executeQuery();
            rsDir.next();
            assertEquals(2, rsDir.getInt(1), "Debería tener 2 direcciones vinculadas.");
        }
    }

    @Test
    @Order(2)
    void testIntegracionConsultaFormateada() throws SQLException {
        ControladorMenu controlador = new ControladorMenu();
        String resultado = controlador.obtenerDatosConsulta();


        assertTrue(resultado.contains("John Unit Test"), "El listado debe contener el nombre.");
        assertTrue(resultado.contains("Calle Test 1, Calle Test 2"), "El listado debe agrupar las direcciones.");
    }

    @Test
    @Order(3)
    void testIntegracionModificarSoloNombre() throws SQLException {
        ControladorMenu controlador = new ControladorMenu();
        String nuevoNombre = "John Modificado";

        controlador.ejecutarUpdate(idGenerado, nuevoNombre);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT nombre FROM Personas WHERE id = ?");
            pstmt.setInt(1, idGenerado);
            ResultSet rs = pstmt.executeQuery();

            assertTrue(rs.next());
            assertEquals(nuevoNombre, rs.getString("nombre"), "El nombre debería haberse actualizado.");
        }
    }

    @Test
    @Order(4)
    void testDireccionCompartida() throws SQLException {
        ControladorMenu controlador = new ControladorMenu();

        Persona p2 = new Persona("Amigo de John");
        p2.addDireccion(new Direccion("Calle Test 1"));

        controlador.ejecutarInsert(p2);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM Direcciones WHERE calle = ?");
            pstmt.setString(1, "Calle Test 1");
            ResultSet rs = pstmt.executeQuery();
            rs.next();

            assertEquals(1, rs.getInt(1), "La dirección no debe duplicarse si es compartida.");
        }
    }

    @Test
    @Order(5)
    void testIntegracionBajaCascada() throws SQLException {
        ControladorMenu controlador = new ControladorMenu();

        int filasAfectadas = controlador.ejecutarDelete(idGenerado);
        assertEquals(1, filasAfectadas);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmtT = conn.prepareStatement("SELECT COUNT(*) FROM Telefonos WHERE personaId = ?");
            pstmtT.setInt(1, idGenerado);
            ResultSet rsT = pstmtT.executeQuery();
            rsT.next();
            assertEquals(0, rsT.getInt(1), "Los teléfonos deberían haberse borrado en cascada.");
        }
    }
}