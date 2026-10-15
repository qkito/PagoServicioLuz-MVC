package pagoservicioluz.modelo;

/**
 * Entidad: datos de consumo del periodo facturado.
 */
public class Consumo {

    private String periodo;
    private int lecturaAnterior;
    private int lecturaActual;
    private String fechaLimite;
    private double montoPagar;

    public Consumo(String periodo, int lecturaAnterior, int lecturaActual, String fechaLimite, double montoPagar) {
        this.periodo = periodo;
        this.lecturaAnterior = lecturaAnterior;
        this.lecturaActual = lecturaActual;
        this.fechaLimite = fechaLimite;
        this.montoPagar = montoPagar;
    }

    public int getKwhConsumidos() {
        return lecturaActual - lecturaAnterior;
    }

    public String getPeriodo() {
        return periodo;
    }

    public int getLecturaAnterior() {
        return lecturaAnterior;
    }

    public int getLecturaActual() {
        return lecturaActual;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public double getMontoPagar() {
        return montoPagar;
    }
}
