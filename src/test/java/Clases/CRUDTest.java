package Clases;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;

public class CRUDTest{
    private final String URL = "jdbc:mariadb://localhost:3306/agenda";
    private final String USER = "usuario1";
    private final String PASSWORD = "superpassword";

    @Test
    void testIntegracionAltaPersona() throws SQLException {
        ControladorMenu controlador = new ControladorMenu();
        String nombrePrueba = "JUnit Test";
        String dirPrueba = "Calle Prueba 123";
        String tlfPrueba = "999888777";

        controlador.ejecutarInsert(nombrePrueba, dirPrueba, tlfPrueba);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Personas WHERE nombre = ?");
            pstmt.setString(1, nombrePrueba);
            ResultSet rs = pstmt.executeQuery();

            assertTrue(rs.next(), "La persona debería existir en la base de datos.");
            assertEquals(dirPrueba, rs.getString("direccion"));

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM Personas WHERE nombre = '" + nombrePrueba + "'");
        }
    }
}
