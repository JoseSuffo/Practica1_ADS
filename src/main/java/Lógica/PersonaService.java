package Lógica;

import Persistencia.PersonaDAO;

public class PersonaService {
    private PersonaDAO personaDAO = new PersonaDAO();

    public void registrarNuevaPersona(Persona p) throws Exception {
        if (p.getNombre().isEmpty()) throw new Exception("Nombre obligatorio.");
        personaDAO.guardarCompleto(p);
    }

    public void eliminarPersona(int id) throws Exception {
        int filas = personaDAO.eliminar(id);
        if (filas == 0) throw new Exception("El ID no existe.");
    }

    public void actualizarNombrePersona(int id, String nuevoNombre) throws Exception {
        if (nuevoNombre.isEmpty()) throw new Exception("El nuevo nombre no puede estar vacío.");
        personaDAO.updateNombre(id, nuevoNombre);
    }

    public String obtenerListadoCompleto() throws Exception {
        return personaDAO.consultarTodo();
    }
}