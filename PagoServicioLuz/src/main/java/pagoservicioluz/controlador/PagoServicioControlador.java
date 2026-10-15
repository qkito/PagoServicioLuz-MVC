package pagoservicioluz.controlador;

import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import pagoservicioluz.modelo.IPagoServicioModelo;
import pagoservicioluz.modelo.PagoException;
import pagoservicioluz.modelo.Recibo;
import pagoservicioluz.modelo.Servicio;
import pagoservicioluz.vista.FrmPagoServicio;

/**
 * Controlador: recibe los eventos de la vista, consulta al modelo
 * y le indica a la vista qué mostrar. La vista nunca habla con el modelo.
 */
public class PagoServicioControlador {

    private FrmPagoServicio vista;
    private IPagoServicioModelo modelo;
    private Servicio servicioSeleccionado;

    public PagoServicioControlador(FrmPagoServicio vista, IPagoServicioModelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
    }

    public void iniciar() {
        registrarListeners();
        vista.setVisible(true);
    }

    private void registrarListeners() {
        vista.addBusquedaListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarServicios();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarServicios();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarServicios();
            }
        });
        vista.addSeleccionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarServicio();
            }
        });
        vista.addPagarListener(e -> pagar());
        vista.addCancelarListener(e -> cancelar());
        vista.addNuevoPagoListener(e -> nuevoPago());
    }

    /** Pasos 2-3: búsqueda incremental en cada tecla. */
    public void buscarServicios() {
        String prefijo = vista.getNumeroServicio();
        List<Servicio> coincidencias = modelo.buscarServiciosPorPrefijo(prefijo);
        vista.mostrarCoincidencias(coincidencias);
    }

    /** Pasos 4-6: muestra los datos de consumo del servicio elegido. */
    public void seleccionarServicio() {
        servicioSeleccionado = vista.getServicioSeleccionado();
        if (servicioSeleccionado != null) {
            vista.mostrarConsumo(servicioSeleccionado);
        }
    }

    /** Pasos 7-9: procesa el pago y muestra el recibo, o el error (FA-4, FA-5). */
    public void pagar() {
        try {
            Recibo recibo = modelo.procesarPago(servicioSeleccionado,
                    vista.getNumeroTarjeta(), vista.getVencimiento(), vista.getCvv());
            vista.mostrarRecibo(recibo);
        } catch (PagoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    /** FA-6: cancela sin registrar nada. */
    public void cancelar() {
        servicioSeleccionado = null;
        vista.limpiar();
    }

    /** Inicia otra operación de pago después del recibo. */
    public void nuevoPago() {
        servicioSeleccionado = null;
        vista.limpiar();
    }
}
