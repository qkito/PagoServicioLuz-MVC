package pagoservicioluz.modelo;

import java.time.LocalDateTime;

/**
 * Entidad: comprobante del pago realizado.
 */
public class Recibo {

    private String folio;
    private LocalDateTime fechaHora;
    private Servicio servicio;
    private double montoPagado;
    private String tarjetaEnmascarada;
    private String estado;

    public Recibo(String folio, LocalDateTime fechaHora, Servicio servicio, double montoPagado, String tarjetaEnmascarada) {
        this.folio = folio;
        this.fechaHora = fechaHora;
        this.servicio = servicio;
        this.montoPagado = montoPagado;
        this.tarjetaEnmascarada = tarjetaEnmascarada;
        this.estado = "PAGADO";
    }

    public String getFolio() {
        return folio;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public String getTarjetaEnmascarada() {
        return tarjetaEnmascarada;
    }

    public String getEstado() {
        return estado;
    }
}
