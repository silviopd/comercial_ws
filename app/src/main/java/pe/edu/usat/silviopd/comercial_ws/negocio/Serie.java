package pe.edu.usat.silviopd.comercial_ws.negocio;

import java.util.ArrayList;

/**
 * Created by USER on 10/11/2016.
 */

public class Serie {

    private int nroSerie;

    public static ArrayList<Serie> listaSerie = new ArrayList<>();

    public int getNroSerie() {
        return nroSerie;
    }

    public void setNroSerie(int nroSerie) {
        this.nroSerie = nroSerie;
    }


}
