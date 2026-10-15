package pagoservicioluz.modelo;

/**
 * Entidad: servicio de luz identificado por su número de servicio (12 dígitos).
 */
public class Servicio {

    private String numeroServicio;
    private String tarifa;
    private Cliente cliente;
    private Consumo consumo;
    private boolean pagado;

    public Servicio(String numeroServicio, String tarifa, Cliente cliente, Consumo consumo) {
        this.numeroServicio = numeroServicio;
        this.tarifa = tarifa;
        this.cliente = cliente;
        this.consumo = consumo;
        this.pagado = false;
    }

    public String getNumeroServicio() {
        return numeroServicio;
    }

    public String getTarifa() {
        return tarifa;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Consumo getConsumo() {
        return consumo;
    }

    public boolean isPagado() {
        return pagado;
    }

    /** Registra el pago del servicio: el adeudo queda en cero. */
    public void marcarPagado() {
        this.pagado = true;
    }

    /** Texto que se muestra en la lista de coincidencias. */
    @Override
    public String toString() {
        return numeroServicio + "  —  " + cliente.getNombre();
    }
}
