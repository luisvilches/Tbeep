package luisvilches.cl.tbip;

/**
 * Created by vilches on 28-07-16.
 */
public class Tarjeta {
    private String nombreTarjeta;
    private String numeroTarjeta;

    public Tarjeta(String nombreTarjeta, String numeroTarjeta) {

        this.nombreTarjeta = nombreTarjeta;
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }
}
