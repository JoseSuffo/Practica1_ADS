package Lógica;
import java.util.ArrayList;
import java.util.List;

public class Persona {
    private String nombre;
    private List<Direccion> direcciones;
    private List<Telefono> telefonos;

    public Persona(String nombre) {
        this.nombre = nombre;
        this.direcciones = new ArrayList<>();
        this.telefonos = new ArrayList<>();
    }

    // Getters
    public String getNombre() { return nombre; }
    public List<Direccion> getDirecciones() { return direcciones; }
    public List<Telefono> getTelefonos() { return telefonos; }

    public void addTelefono(String numero) {
        this.telefonos.add(new Telefono(numero));
    }

    public void addDireccion(Direccion d) { this.direcciones.add(d); }
}
