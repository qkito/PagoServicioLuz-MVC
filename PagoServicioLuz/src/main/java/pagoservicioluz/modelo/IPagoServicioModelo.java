package pagoservicioluz.modelo;

import java.util.List;

/**
 * Contrato del modelo. El controlador solo conoce esta interfaz,
 * lo que lo desacopla de la implementación concreta.
 */
public interface IPagoServicioModelo {

    /** Servicios cuyo número comienza con el prefijo (búsqueda incremental, RN-2). */
    List<Servicio> buscarServiciosPorPrefijo(String prefijo);

    /**
     * Valida la tarjeta, solicita la autorización y registra el pago.
     *
     * @return el recibo del pago
     * @throws PagoException si la tarjeta no es válida o el pago es rechazado
     */
    Recibo procesarPago(Servicio servicio, String numeroTarjeta, String vencimiento, String cvv) throws PagoException;
}
