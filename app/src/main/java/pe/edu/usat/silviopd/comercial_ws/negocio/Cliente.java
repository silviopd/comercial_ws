package pe.edu.usat.silviopd.comercial_ws.negocio;

import java.util.ArrayList;

/**
 * Created by USER on 10/11/2016.
 */

public class Cliente {

    private int codigoCliente;
    private String nombre;
    private String direccion;
    private String docIde;

    public static ArrayList<Cliente> listaCliente = new ArrayList<Cliente>();

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDocIde() {
        return docIde;
    }

    public void setDocIde(String docIde) {
        this.docIde = docIde;
    }
}
