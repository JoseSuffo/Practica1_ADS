package Persistencia;

import java.sql.*;
import Lógica.*;

public class PersonaDAO {
    private final String URL = "jdbc:mariadb://localhost:3306/agenda";
    private final String USER = "usuario1";
    private final String PASSWORD = "superpassword";

    public void guardarCompleto(Persona p) throws SQLException {
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
                    int dirId = obtenerOInsertarDireccion(conn, d.getCalle(), sqlBuscarDir, sqlInsertDir);
                    ejecutarVinculo(conn, personaId, dirId, sqlVinculo);
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

    private int obtenerOInsertarDireccion(Connection conn, String calle, String sqlB, String sqlI) throws SQLException {
        PreparedStatement psB = conn.prepareStatement(sqlB);
        psB.setString(1, calle);
        ResultSet rsB = psB.executeQuery();
        if (rsB.next()) return rsB.getInt("id");

        PreparedStatement psI = conn.prepareStatement(sqlI, Statement.RETURN_GENERATED_KEYS);
        psI.setString(1, calle);
        psI.executeUpdate();
        ResultSet rsI = psI.getGeneratedKeys();
        rsI.next();
        return rsI.getInt(1);
    }

    private void ejecutarVinculo(Connection conn, int pId, int dId, String sql) throws SQLException {
        PreparedStatement psV = conn.prepareStatement(sql);
        psV.setInt(1, pId);
        psV.setInt(2, dId);
        psV.executeUpdate();
    }

    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Personas WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public void updateNombre(int id, String nombre) throws SQLException {
        String sql = "UPDATE Personas SET nombre = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public String consultarTodo() throws SQLException {
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
}