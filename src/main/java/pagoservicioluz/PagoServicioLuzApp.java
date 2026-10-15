package pagoservicioluz;

import javax.swing.SwingUtilities;
import pagoservicioluz.controlador.PagoServicioControlador;
import pagoservicioluz.modelo.IPagoServicioModelo;
import pagoservicioluz.modelo.PagoServicioModelo;
import pagoservicioluz.vista.FrmPagoServicio;

/**
 * Punto de entrada: crea el modelo, la vista y el controlador (MVC) y los conecta.
 */
public class PagoServicioLuzApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IPagoServicioModelo modelo = new PagoServicioModelo();
            FrmPagoServicio vista = new FrmPagoServicio();
            PagoServicioControlador controlador = new PagoServicioControlador(vista, modelo);
            controlador.iniciar();
        });
    }
}
