package pagoservicioluz.modelo;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Entidad: tarjeta con la que se paga el servicio.
 * Reglas RN-4 y RN-5: 16 dígitos válidos por Luhn, vencimiento MM/AA no vencido,
 * CVV de 3 dígitos; el número solo se muestra enmascarado.
 */
public class Tarjeta {

    private String numero;
    private String vencimiento;
    private String cvv;

    public Tarjeta(String numero, String vencimiento, String cvv) {
        this.numero = numero == null ? "" : numero.replace(" ", "").trim();
        this.vencimiento = vencimiento == null ? "" : vencimiento.trim();
        this.cvv = cvv == null ? "" : cvv.trim();
    }

    /**
     * Valida la tarjeta completa.
     *
     * @throws PagoException con el motivo si algún dato no es válido
     */
    public void validar() throws PagoException {
        if (!numeroValido()) {
            throw new PagoException("Número de tarjeta inválido");
        }
        if (estaVencida()) {
            throw new PagoException("Tarjeta vencida o fecha de vencimiento inválida (use MM/AA)");
        }
        if (!cvvValido()) {
            throw new PagoException("CVV inválido (debe tener 3 dígitos)");
        }
    }

    /** 16 dígitos y algoritmo de Luhn. */
    private boolean numeroValido() {
        if (!numero.matches("\\d{16}")) {
            return false;
        }
        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
            if (duplicar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            duplicar = !duplicar;
        }
        return suma % 10 == 0;
    }

    /** Vencida si el formato no es MM/AA o si el mes ya pasó. */
    private boolean estaVencida() {
        try {
            YearMonth fecha = YearMonth.parse(vencimiento, DateTimeFormatter.ofPattern("MM/yy"));
            return fecha.isBefore(YearMonth.now());
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    private boolean cvvValido() {
        return cvv.matches("\\d{3}");
    }

    /** Ejemplo: **** **** **** 1111 */
    public String getNumeroEnmascarado() {
        String ultimos = numero.length() >= 4 ? numero.substring(numero.length() - 4) : numero;
        return "**** **** **** " + ultimos;
    }
}
