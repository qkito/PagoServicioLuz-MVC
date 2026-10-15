package pagoservicioluz.modelo;

/**
 * Comunicación con el Sistema de Pagos externo (simulado).
 * Para demostrar el flujo alterno FA-5 (pago rechazado), las tarjetas que
 * terminan en 0002 (por ejemplo 4000000000000002) son rechazadas;
 * cualquier otra tarjeta válida es autorizada.
 */
public class PasarelaPagos {

    public boolean autorizar(Tarjeta tarjeta, double monto) {
        // Simulación de la solicitud de autorización de cargo al banco emisor.
        return monto > 0 && !tarjeta.getNumeroEnmascarado().endsWith("0002");
    }
}
