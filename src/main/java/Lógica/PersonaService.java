package Lógica;

import Persistencia.IConsultable;
import Persistencia.IModificable;
import Persistencia.PersonaDAO;

public class PersonaService {
    private IConsultable persistenciaConsulta;
    private IModificable persistenciaModifica;

    public PersonaService() {
        PersonaDAO dao = new PersonaDAO();
        this.persistenciaConsulta = dao;
        this.persistenciaModifica = dao;
    }

    public PersonaService(IConsultable consulta, IModificable modifica) {
        this.persistenciaConsulta = consulta;
        this.persistenciaModifica = modifica;
    }

    public void registrarNuevaPersona(Persona p) throws Exception {
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio para el registro.");
        }
        persistenciaModifica.guardar(p);
    }

    public void eliminarPersona(int id) throws Exception {
        if (id <= 0) throw new Exception("ID no válido.");
        int filas = persistenciaModifica.eliminar(id);
        if (filas == 0) {
            throw new Exception("No se encontró ningún registro con el ID: " + id);
        }
    }

    public void actualizarNombrePersona(int id, String nuevoNombre) throws Exception {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new Exception("El nuevo nombre no puede estar vacío.");
        }
        persistenciaModifica.actualizarNombre(id, nuevoNombre);
    }

    public String obtenerListadoCompleto() throws Exception {
        String datos = persistenciaConsulta.consultarTodo();
        if (datos == null || datos.isEmpty()) {
            return "La agenda se encuentra vacía.";
        }
        return datos;
    }
}