package Lógica;

import Persistencia.IPersistenciaPersona;
import Persistencia.PersonaDAO;

public class PersonaService {
    private IPersistenciaPersona persistencia;

    public PersonaService(IPersistenciaPersona persistencia) {
        this.persistencia = persistencia;
    }

    public PersonaService() {
        this.persistencia = new PersonaDAO();
    }

    public void registrarNuevaPersona(Persona p) throws Exception {
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio para el registro.");
        }
        persistencia.guardar(p);
    }

    public void eliminarPersona(int id) throws Exception {
        if (id <= 0) throw new Exception("ID no válido.");
        int filas = persistencia.eliminar(id);
        if (filas == 0) {
            throw new Exception("No se encontró ningún registro con el ID: " + id);
        }
    }

    public void actualizarNombrePersona(int id, String nuevoNombre) throws Exception {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new Exception("El nuevo nombre no puede estar vacío.");
        }
        persistencia.actualizarNombre(id, nuevoNombre);
    }

    public String obtenerListadoCompleto() throws Exception {
        String datos = persistencia.consultarTodo();
        if (datos == null || datos.isEmpty()) {
            return "La agenda se encuentra vacía.";
        }
        return datos;
    }
}