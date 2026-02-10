package Lógica;

public class Direccion {
    private int id;
    private String calle;

    public Direccion(String calle) { this.calle = calle; }
    public Direccion(int id, String calle) {
        this.id = id;
        this.calle = calle;
    }

    public int getId() { return id; }
    public String getCalle() { return calle; }

    @Override
    public String toString() { return calle; }
}