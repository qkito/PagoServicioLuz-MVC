package pagoservicioluz.modelo;

/**
 * Excepción de negocio: tarjeta inválida, tarjeta vencida, CVV inválido o pago rechazado.
 */
public class PagoException extends Exception {

    public PagoException(String mensaje) {
        super(mensaje);
    }
}
