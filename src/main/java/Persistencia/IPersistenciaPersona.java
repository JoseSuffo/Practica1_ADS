package Persistencia;

import Lógica.Persona;

public interface IPersistenciaPersona {
    void guardar(Persona p) throws Exception;
    int eliminar(int id) throws Exception;
    void actualizarNombre(int id, String nombre) throws Exception;
    String consultarTodo() throws Exception;
}