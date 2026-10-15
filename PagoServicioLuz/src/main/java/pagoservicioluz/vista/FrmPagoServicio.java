package pagoservicioluz.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import pagoservicioluz.modelo.Consumo;
import pagoservicioluz.modelo.Recibo;
import pagoservicioluz.modelo.Servicio;

/**
 * Vista: ventana "Pago de Servicio de Luz".
 * Zona izquierda: búsqueda. Zona derecha: coincidencias.
 * Zona inferior: datos de consumo + tarjeta, que se reemplaza por el recibo.
 * La vista no contiene lógica de negocio; solo muestra datos y expone eventos.
 */
public class FrmPagoServicio extends JFrame {

    private static final Font FUENTE = new Font("Arial", Font.PLAIN, 13);
    private static final Font FUENTE_NEGRITA = FUENTE.deriveFont(Font.BOLD);
    private static final Color GRIS = new Color(110, 110, 110);

    private JTextField txtNumeroServicio;
    private JList<Servicio> lstCoincidencias;
    private JLabel lblCoincidencias;
    private JPanel pnlZonaInferior;
    private JPanel pnlConsumo;
    private JPanel pnlRecibo;
    private JTextField txtNumeroTarjeta;
    private JTextField txtVencimiento;
    private JPasswordField txtCvv;
    private JButton btnPagar;
    private JButton btnCancelar;
    private JButton btnNuevoPago;

    public FrmPagoServicio() {
        initComponents();
        limpiar();
    }

    private void initComponents() {
        setTitle("Pago de Servicio de Luz");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(840, 560);
        setLocationRelativeTo(null);

        // Filtro que solo acepta dígitos (y opcionalmente '/') hasta una longitud máxima (FA-2).
        class FiltroNumerico extends DocumentFilter {
            private final int maximo;
            private final boolean permitirDiagonal;

            FiltroNumerico(int maximo, boolean permitirDiagonal) {
                this.maximo = maximo;
                this.permitirDiagonal = permitirDiagonal;
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs) throws BadLocationException {
                if (texto == null) {
                    texto = "";
                }
                String permitido = permitirDiagonal ? "[0-9/]*" : "[0-9]*";
                int nuevaLongitud = fb.getDocument().getLength() - length + texto.length();
                if (texto.matches(permitido) && nuevaLongitud <= maximo) {
                    super.replace(fb, offset, length, texto, attrs);
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attrs) throws BadLocationException {
                replace(fb, offset, 0, texto, attrs);
            }
        }

        java.util.function.Function<String, TitledBorder> crearBorde = titulo -> {
            TitledBorder borde = BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(6, 10, 8, 10)), titulo);
            borde.setTitleFont(FUENTE);
            return borde;
        };

        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Zona izquierda: búsqueda -----
        JPanel pnlBuscar = new JPanel(new GridBagLayout());
        pnlBuscar.setBorder(crearBorde.apply("Buscar servicio"));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 0, 4, 0);
        JLabel lblNumero = new JLabel("Número de servicio:");
        lblNumero.setFont(FUENTE);
        c.gridy = 0;
        pnlBuscar.add(lblNumero, c);
        txtNumeroServicio = new JTextField();
        txtNumeroServicio.setFont(FUENTE.deriveFont(14f));
        txtNumeroServicio.setPreferredSize(new Dimension(260, 28));
        ((AbstractDocument) txtNumeroServicio.getDocument()).setDocumentFilter(new FiltroNumerico(12, false));
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        pnlBuscar.add(txtNumeroServicio, c);
        JLabel lblAyuda1 = new JLabel("La lista se actualiza mientras escribe.");
        JLabel lblAyuda2 = new JLabel("Seleccione su servicio en la lista de coincidencias.");
        for (JLabel ayuda : new JLabel[]{lblAyuda1, lblAyuda2}) {
            ayuda.setFont(FUENTE);
            ayuda.setForeground(GRIS);
        }
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(14, 0, 2, 0);
        c.gridy = 2;
        pnlBuscar.add(lblAyuda1, c);
        c.insets = new Insets(2, 0, 2, 0);
        c.gridy = 3;
        c.weighty = 1;
        pnlBuscar.add(lblAyuda2, c);

        // ----- Zona derecha: coincidencias -----
        JPanel pnlCoincidencias = new JPanel(new BorderLayout(0, 6));
        pnlCoincidencias.setBorder(crearBorde.apply("Coincidencias"));
        lstCoincidencias = new JList<>(new DefaultListModel<>());
        lstCoincidencias.setFont(FUENTE);
        lstCoincidencias.setFixedCellHeight(24);
        lstCoincidencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstCoincidencias.setSelectionBackground(new Color(184, 207, 229));
        lstCoincidencias.setSelectionForeground(Color.BLACK);
        pnlCoincidencias.add(new JScrollPane(lstCoincidencias), BorderLayout.CENTER);
        lblCoincidencias = new JLabel(" ");
        lblCoincidencias.setFont(FUENTE);
        lblCoincidencias.setForeground(GRIS);
        pnlCoincidencias.add(lblCoincidencias, BorderLayout.SOUTH);

        JPanel pnlSuperior = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlSuperior.add(pnlBuscar);
        pnlSuperior.add(pnlCoincidencias);
        pnlSuperior.setPreferredSize(new Dimension(800, 200));

        // ----- Zona inferior: consumo / recibo -----
        pnlZonaInferior = new JPanel(new CardLayout());
        pnlZonaInferior.setBorder(crearBorde.apply("Datos de consumo"));

        JLabel lblVacio = new JLabel("Seleccione un servicio de la lista", SwingConstants.CENTER);
        lblVacio.setFont(FUENTE.deriveFont(Font.ITALIC));
        lblVacio.setForeground(GRIS);

        pnlConsumo = new JPanel(new BorderLayout(0, 6));
        pnlConsumo.add(new JPanel(), BorderLayout.NORTH);      // datos: se llenan en mostrarConsumo()
        JPanel pnlTarjeta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTarjeta.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        txtNumeroTarjeta = new JTextField();
        txtNumeroTarjeta.setPreferredSize(new Dimension(200, 28));
        ((AbstractDocument) txtNumeroTarjeta.getDocument()).setDocumentFilter(new FiltroNumerico(16, false));
        txtVencimiento = new JTextField();
        txtVencimiento.setPreferredSize(new Dimension(70, 28));
        ((AbstractDocument) txtVencimiento.getDocument()).setDocumentFilter(new FiltroNumerico(5, true));
        txtCvv = new JPasswordField();
        txtCvv.setPreferredSize(new Dimension(56, 28));
        ((AbstractDocument) txtCvv.getDocument()).setDocumentFilter(new FiltroNumerico(3, false));
        String[] etiquetas = {"Número de tarjeta:   ", "      Vencimiento (MM/AA):   ", "      CVV:   "};
        JTextField[] campos = {txtNumeroTarjeta, txtVencimiento, txtCvv};
        for (int i = 0; i < campos.length; i++) {
            JLabel etiqueta = new JLabel(etiquetas[i]);
            etiqueta.setFont(FUENTE);
            campos[i].setFont(FUENTE.deriveFont(14f));
            pnlTarjeta.add(etiqueta);
            pnlTarjeta.add(campos[i]);
        }
        JPanel pnlSurConsumo = new JPanel(new BorderLayout());
        pnlSurConsumo.add(new JSeparator(), BorderLayout.NORTH);
        pnlSurConsumo.add(pnlTarjeta, BorderLayout.CENTER);
        pnlConsumo.add(pnlSurConsumo, BorderLayout.CENTER);
        JPanel pnlConsumoContenedor = new JPanel(new BorderLayout());
        pnlConsumoContenedor.add(pnlConsumo, BorderLayout.NORTH);

        pnlRecibo = new JPanel(new GridBagLayout());           // se llena en mostrarRecibo()

        pnlZonaInferior.add(lblVacio, "vacio");
        pnlZonaInferior.add(pnlConsumoContenedor, "consumo");
        pnlZonaInferior.add(pnlRecibo, "recibo");

        // ----- Botones -----
        btnPagar = new JButton("Pagar");
        btnCancelar = new JButton("Cancelar");
        btnNuevoPago = new JButton("Nuevo pago");
        for (JButton boton : new JButton[]{btnPagar, btnCancelar, btnNuevoPago}) {
            boton.setFont(FUENTE);
            boton.setFocusPainted(false);
            boton.setPreferredSize(new Dimension(120, 30));
        }
        JPanel pnlBotonesIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlBotonesIzq.add(btnCancelar);
        JPanel pnlBotonesDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBotonesDer.add(btnNuevoPago);
        pnlBotonesDer.add(btnPagar);
        JPanel pnlBotones = new JPanel(new BorderLayout());
        pnlBotones.add(pnlBotonesIzq, BorderLayout.WEST);
        pnlBotones.add(pnlBotonesDer, BorderLayout.EAST);

        JPanel pnlCentro = new JPanel(new BorderLayout(0, 8));
        pnlCentro.add(pnlZonaInferior, BorderLayout.CENTER);
        pnlCentro.add(pnlBotones, BorderLayout.SOUTH);
        raiz.add(pnlSuperior, BorderLayout.NORTH);
        raiz.add(pnlCentro, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    // ===================== Datos capturados =====================

    public String getNumeroServicio() {
        return txtNumeroServicio.getText().trim();
    }

    public Servicio getServicioSeleccionado() {
        return lstCoincidencias.getSelectedValue();
    }

    public String getNumeroTarjeta() {
        return txtNumeroTarjeta.getText().trim();
    }

    public String getVencimiento() {
        return txtVencimiento.getText().trim();
    }

    public String getCvv() {
        return new String(txtCvv.getPassword());
    }

    // ===================== Presentación =====================

    public void mostrarCoincidencias(List<Servicio> servicios) {
        DefaultListModel<Servicio> modeloLista = (DefaultListModel<Servicio>) lstCoincidencias.getModel();
        modeloLista.clear();
        for (Servicio servicio : servicios) {
            modeloLista.addElement(servicio);
        }
        if (getNumeroServicio().isEmpty()) {
            lblCoincidencias.setText("Escriba un número de servicio");
        } else if (servicios.isEmpty()) {
            lblCoincidencias.setText("No se encontraron servicios");          // FA-1
        } else {
            lblCoincidencias.setText(servicios.size() + (servicios.size() == 1 ? " coincidencia" : " coincidencias"));
        }
        ((TitledBorder) pnlZonaInferior.getBorder()).setTitle("Datos de consumo");
        ((CardLayout) pnlZonaInferior.getLayout()).show(pnlZonaInferior, "vacio");
        btnPagar.setEnabled(false);
        pnlZonaInferior.repaint();
    }

    public void mostrarConsumo(Servicio servicio) {
        Consumo consumo = servicio.getConsumo();
        String[][] columna1 = {
            {"Número de servicio:", servicio.getNumeroServicio()},
            {"Titular:", servicio.getCliente().getNombre()},
            {"Domicilio:", servicio.getCliente().getDomicilio()},
            {"Periodo:", consumo.getPeriodo()}};
        String[][] columna2 = {
            {"Lectura anterior:", String.format("%,d", consumo.getLecturaAnterior())},
            {"Lectura actual:", String.format("%,d", consumo.getLecturaActual())},
            {"Consumo:", consumo.getKwhConsumidos() + " kWh"},
            {"Tarifa:", servicio.getTarifa()}};

        JPanel pnlDatos = new JPanel(new BorderLayout(0, 10));
        JPanel pnlColumnas = new JPanel(new GridLayout(1, 2, 20, 0));
        for (String[][] columna : new String[][][]{columna1, columna2}) {
            JPanel pnlColumna = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(3, 0, 3, 12);
            for (int i = 0; i < columna.length; i++) {
                JLabel etiqueta = new JLabel(columna[i][0]);
                etiqueta.setFont(FUENTE);
                JLabel valor = new JLabel(columna[i][1]);
                valor.setFont(FUENTE_NEGRITA);
                c.gridy = i;
                c.gridx = 0;
                c.weightx = 0;
                pnlColumna.add(etiqueta, c);
                c.gridx = 1;
                c.weightx = 1;
                pnlColumna.add(valor, c);
            }
            pnlColumnas.add(pnlColumna);
        }
        JPanel pnlTotales = new JPanel(new BorderLayout());
        JLabel lblFechaLimite = new JLabel("Fecha límite de pago:  " + consumo.getFechaLimite());
        lblFechaLimite.setFont(FUENTE);
        JLabel lblTotal = new JLabel(servicio.isPagado()
                ? "Este servicio no tiene adeudo"                                 // FA-3
                : String.format("Total a pagar:  $%,.2f MXN", consumo.getMontoPagar()));
        lblTotal.setFont(FUENTE_NEGRITA.deriveFont(15f));
        pnlTotales.add(lblFechaLimite, BorderLayout.WEST);
        pnlTotales.add(lblTotal, BorderLayout.EAST);
        pnlDatos.add(pnlColumnas, BorderLayout.NORTH);
        pnlDatos.add(pnlTotales, BorderLayout.SOUTH);

        BorderLayout distribucion = (BorderLayout) pnlConsumo.getLayout();
        pnlConsumo.remove(distribucion.getLayoutComponent(BorderLayout.NORTH));
        pnlConsumo.add(pnlDatos, BorderLayout.NORTH);

        txtNumeroTarjeta.setText("");
        txtVencimiento.setText("");
        txtCvv.setText("");
        btnPagar.setEnabled(!servicio.isPagado());
        ((TitledBorder) pnlZonaInferior.getBorder()).setTitle("Datos de consumo");
        ((CardLayout) pnlZonaInferior.getLayout()).show(pnlZonaInferior, "consumo");
        pnlZonaInferior.revalidate();
        pnlZonaInferior.repaint();
        txtNumeroTarjeta.requestFocusInWindow();
    }

    public void mostrarRecibo(Recibo recibo) {
        Servicio servicio = recibo.getServicio();
        String[][] filas = {
            {"Folio:", recibo.getFolio()},
            {"Fecha:", recibo.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))},
            {"Servicio:", servicio.getNumeroServicio()},
            {"Titular:", servicio.getCliente().getNombre()},
            {"Periodo pagado:", servicio.getConsumo().getPeriodo()},
            {"Monto pagado:", String.format("$%,.2f MXN", recibo.getMontoPagado())},
            {"Tarjeta:", recibo.getTarjetaEnmascarada()},
            {"Estado:", recibo.getEstado()}};

        JPanel pnlTicket = new JPanel(new GridBagLayout());
        pnlTicket.setBackground(Color.WHITE);
        pnlTicket.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createDashedBorder(Color.GRAY, 4, 3), BorderFactory.createEmptyBorder(8, 24, 8, 24)));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        JLabel lblTitulo = new JLabel("RECIBO DE PAGO – SERVICIO DE LUZ");
        lblTitulo.setFont(FUENTE_NEGRITA);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        pnlTicket.add(lblTitulo, c);
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(1, 0, 1, 16);
        for (int i = 0; i < filas.length; i++) {
            JLabel etiqueta = new JLabel(filas[i][0]);
            etiqueta.setFont(FUENTE);
            JLabel valor = new JLabel(filas[i][1]);
            valor.setFont(FUENTE_NEGRITA);
            if (i == filas.length - 1) {
                valor.setForeground(new Color(46, 125, 50));
            }
            c.gridy = i + 1;
            c.gridx = 0;
            pnlTicket.add(etiqueta, c);
            c.gridx = 1;
            pnlTicket.add(valor, c);
        }
        pnlRecibo.removeAll();
        pnlRecibo.add(pnlTicket);

        txtNumeroServicio.setEnabled(false);
        lstCoincidencias.setEnabled(false);
        btnPagar.setVisible(false);
        btnCancelar.setVisible(false);
        btnNuevoPago.setVisible(true);
        ((TitledBorder) pnlZonaInferior.getBorder()).setTitle("Recibo de pago");
        ((CardLayout) pnlZonaInferior.getLayout()).show(pnlZonaInferior, "recibo");
        pnlZonaInferior.revalidate();
        pnlZonaInferior.repaint();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Pago de Servicio de Luz", JOptionPane.WARNING_MESSAGE);
    }

    public void limpiar() {
        txtNumeroServicio.setEnabled(true);
        lstCoincidencias.setEnabled(true);
        txtNumeroServicio.setText("");
        ((DefaultListModel<Servicio>) lstCoincidencias.getModel()).clear();
        lblCoincidencias.setText("Escriba un número de servicio");
        txtNumeroTarjeta.setText("");
        txtVencimiento.setText("");
        txtCvv.setText("");
        btnPagar.setVisible(true);
        btnPagar.setEnabled(false);
        btnCancelar.setVisible(true);
        btnNuevoPago.setVisible(false);
        ((TitledBorder) pnlZonaInferior.getBorder()).setTitle("Datos de consumo");
        ((CardLayout) pnlZonaInferior.getLayout()).show(pnlZonaInferior, "vacio");
        pnlZonaInferior.repaint();
        txtNumeroServicio.requestFocusInWindow();
    }

    // ===================== Registro de eventos (los atiende el controlador) =====================

    public void addBusquedaListener(DocumentListener listener) {
        txtNumeroServicio.getDocument().addDocumentListener(listener);
    }

    public void addSeleccionListener(ListSelectionListener listener) {
        lstCoincidencias.addListSelectionListener(listener);
    }

    public void addPagarListener(ActionListener listener) {
        btnPagar.addActionListener(listener);
    }

    public void addCancelarListener(ActionListener listener) {
        btnCancelar.addActionListener(listener);
    }

    public void addNuevoPagoListener(ActionListener listener) {
        btnNuevoPago.addActionListener(listener);
    }
}
