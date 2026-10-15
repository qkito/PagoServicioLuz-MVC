package pagoservicioluz.modelo;

/**
 * Entidad: titular de un servicio de luz.
 */
public class Cliente {

    private String nombre;
    private String domicilio;

    public Cliente(String nombre, String domicilio) {
        this.nombre = nombre;
        this.domicilio = domicilio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDomicilio() {
        return domicilio;
    }
}
