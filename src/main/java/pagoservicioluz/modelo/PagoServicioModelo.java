package pagoservicioluz.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del modelo: datos en memoria y lógica de negocio del pago.
 */
public class PagoServicioModelo implements IPagoServicioModelo {

    private List<Servicio> servicios;
    private PasarelaPagos pasarela;
    private int consecutivoFolio;

    public PagoServicioModelo() {
        this.servicios = new ArrayList<>();
        this.pasarela = new PasarelaPagos();
        this.consecutivoFolio = 124;
        cargarDatos();
    }

    /** Registro de clientes simulado (precondición del caso de uso). */
    private void cargarDatos() {
        servicios.add(new Servicio("482019003521", "1F",
                new Cliente("Ana López García", "Av. Juárez 125, Col. Centro"),
                new Consumo("Agosto – Septiembre 2026", 15230, 15630, "20/10/2026", 684.00)));
        servicios.add(new Servicio("482019008147", "1F",
                new Cliente("José Martínez Ruiz", "Calle Hidalgo 245, Col. Centro"),
                new Consumo("Agosto – Septiembre 2026", 9810, 10125, "20/10/2026", 512.30)));
        servicios.add(new Servicio("482019009630", "1F",
                new Cliente("María Pérez Soto", "Blvd. Kino 1450, Col. Pitic"),
                new Consumo("Agosto – Septiembre 2026", 22040, 22590, "20/10/2026", 948.75)));
        servicios.add(new Servicio("573120004410", "1",
                new Cliente("Luis Herrera Campos", "Calle Sinaloa 88, Col. Norte"),
                new Consumo("Agosto – Septiembre 2026", 4100, 4310, "22/10/2026", 298.40)));
        servicios.add(new Servicio("573120007752", "1",
                new Cliente("Carmen Ruiz Valdez", "Calle Sonora 310, Col. Cajeme"),
                new Consumo("Agosto – Septiembre 2026", 7020, 7265, "22/10/2026", 341.90)));
    }

    @Override
    public List<Servicio> buscarServiciosPorPrefijo(String prefijo) {
        List<Servicio> coincidencias = new ArrayList<>();
        if (prefijo == null || prefijo.isEmpty()) {
            return coincidencias;
        }
        for (Servicio servicio : servicios) {
            if (servicio.getNumeroServicio().startsWith(prefijo)) {
                coincidencias.add(servicio);
            }
        }
        return coincidencias;
    }

    @Override
    public Recibo procesarPago(Servicio servicio, String numeroTarjeta, String vencimiento, String cvv) throws PagoException {
        if (servicio == null) {
            throw new PagoException("Seleccione un servicio de la lista");
        }
        if (servicio.isPagado()) {
            throw new PagoException("Este servicio no tiene adeudo");
        }
        Tarjeta tarjeta = new Tarjeta(numeroTarjeta, vencimiento, cvv);
        tarjeta.validar();                                           // FA-4
        double monto = servicio.getConsumo().getMontoPagar();
        boolean autorizado = pasarela.autorizar(tarjeta, monto);
        if (!autorizado) {
            throw new PagoException("Pago rechazado por el sistema de pagos");   // FA-5
        }
        return crearRecibo(servicio, tarjeta);
    }

    /** Registra el pago (RN-3: monto total) y genera el recibo con folio único (RN-6). */
    private Recibo crearRecibo(Servicio servicio, Tarjeta tarjeta) {
        consecutivoFolio++;
        String folio = String.format("REC-%06d", consecutivoFolio);
        servicio.marcarPagado();
        return new Recibo(folio, LocalDateTime.now(), servicio,
                servicio.getConsumo().getMontoPagar(), tarjeta.getNumeroEnmascarado());
    }
}
