package Lógica;

public class Direccion {
    private String calle;

    public Direccion(String calle) { this.calle = calle; }

    public String getCalle() { return calle; }

    @Override
    public String toString() { return calle; }
}