package pe.edu.usat.silviopd.comercial_ws.negocio;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by USER on 03/11/2016.
 */

public class Articulo {

    private String imagen;
    private String nombre;
    private int codigo;
    private double precio;
    private int cantidad;

    public static ArrayList<Articulo> listaArticulo = new ArrayList<Articulo>();

    public Articulo(String imagen, String nombre, int codigo, double precio, int cantidad) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.codigo = codigo;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public Articulo() {

    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public JSONObject getJSONObject(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("codigo_articulo",this.getCodigo());
            obj.put("cantidad",this.getCantidad());
            obj.put("precio",this.getPrecio());
            obj.put("descuento1",0);
            obj.put("descuento2",0);

        }catch(JSONException e) {
            Log.e("error", e.getMessage());
        }
        return obj;
    }
}
