package pe.edu.usat.silviopd.comercial_ws;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import pe.edu.usat.silviopd.comercial_ws.negocio.Articulo;
import pe.edu.usat.silviopd.comercial_ws.negocio.Cliente;
import pe.edu.usat.silviopd.comercial_ws.negocio.Serie;
import pe.edu.usat.silviopd.comercial_ws.negocio.Sesion;
import pe.edu.usat.silviopd.comercial_ws.util.Funciones;

public class VentaConfirmar extends AppCompatActivity implements View.OnClickListener{

    Spinner spSeries,spClientes;
    TCTask tareaSeries;
    RadioButton rbBoleta, rbFactura;
    Button btnRegistrarVenta;
    EditText txtNroVta, txtNDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta_confirmar);

        spSeries = (Spinner) findViewById(R.id.spSerie);
        spClientes = (Spinner) findViewById(R.id.spCliente);
        rbBoleta = (RadioButton) findViewById(R.id.rbBoleta);
        rbFactura = (RadioButton) findViewById(R.id.rbFactura);

        btnRegistrarVenta = (Button) findViewById(R.id.btnRegistrarVenta);
        txtNroVta = (EditText)findViewById(R.id.txtNumeroVenta);
        txtNDoc =    (EditText)findViewById(R.id.txtNumeroComprobante);

        tareaSeries = new TCTask();
        tareaSeries.execute("03");

        rbBoleta.setOnClickListener(this);
        rbFactura.setOnClickListener(this);
        btnRegistrarVenta.setOnClickListener(this);

        new TCCliente().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rbBoleta:
                tareaSeries = new TCTask();
                tareaSeries.execute("03");
                break;

            case R.id.rbFactura:
                tareaSeries = new TCTask();
                tareaSeries.execute("01");
                break;
            case R.id.btnRegistrarVenta:
                registrarVenta();
                break;
        }
    }

    private void registrarVenta(){
        boolean r = Funciones.mensajeConfirmacion(this, "Confirme", "Desea grabar la venta...");
        if(r){
            //Log.e("Resultado", "Grabando");
            String tipoCom;
            if(rbBoleta.isChecked()){
                tipoCom = "03";
            }else{
                tipoCom = "01";
            }
            int serie = Serie.listaSerie.get(spSeries.getSelectedItemPosition()).getNroSerie();
            int cliente = Cliente.listaCliente.get(spClientes.getSelectedItemPosition()).getCodigoCliente();
            int codigoUsuario = Sesion.LOGIN_USUARIO_CODIGO;
            String token = Sesion.LOGIN_TOKEN;
            Log.e("serie", String.valueOf(serie));
            Log.e("tipoCom", tipoCom);
            Log.e("cliente", String.valueOf(cliente));

            //Generando el detalle de la venta en JSON
            JSONArray jsonArrayDet = new JSONArray();
            for (int i = 0; i< ArticuloAdapter.listaDatos.size(); i++){
                Articulo item = ArticuloAdapter.listaDatos.get(i);
                if(item.getCantidad()>0){
                    jsonArrayDet.put(item.getJSONObject());
                }
            }
            String detalleVta= jsonArrayDet.toString();
            Log.e("Detalle vta", detalleVta);

            new GrabarVentaTask(detalleVta,serie,cliente,codigoUsuario,token,tipoCom).execute();
            //Generando el detalle de la venta en JSON

        }
    }

    public class GrabarVentaTask extends AsyncTask<Void, Void, String> {
        private String tipoCom;
        private int serie;
        private int cliente;
        private  int codigoUsuario;
        private String token;
        private String detalle;

        public GrabarVentaTask(String detalle, int serie, int cliente, int codigoUsuario, String token, String tipoCom) {
            this.detalle = detalle;
            this.serie = serie;
            this.cliente = cliente;
            this.codigoUsuario = codigoUsuario;
            this.token = token;
            this.tipoCom = tipoCom;
        }

        @Override
        protected String doInBackground(Void... params) {
            // LLamar a la WS para grabar la venta
            try {
                String ws = Funciones.URL_WS + "venta.agregar.php";
                HashMap parametros = new HashMap<String, String>();
                parametros.put("p_tc",String.valueOf(this.tipoCom));
                parametros.put("p_nser",String.valueOf(this.serie));
                parametros.put("p_cli",String.valueOf(this.cliente));
                parametros.put("p_cu",String.valueOf(this.codigoUsuario));
                parametros.put("p_det",this.detalle);
                parametros.put("token",this.token);

                String resultado = new Funciones().getHttpContent(ws, parametros);
                return resultado;

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return "";
            }
        }


        protected void onPostExecute(final String resultado) {
            Log.e("resultado",resultado);
            if ( ! resultado.isEmpty() ) {
                try {
                    JSONObject json = new JSONObject(resultado);

                    int estado = json.getInt("estado");
                    if (estado==200) {
                        JSONObject jsonDatos = json.getJSONObject("datos");
                        txtNroVta.setText(String.valueOf(jsonDatos.getInt("nv")));
                        txtNDoc.setText(String.valueOf(jsonDatos.getInt("ndoc")));

                        Funciones.mensajeInformacion(VentaConfirmar.this,"Venta","Registrado Correctamente");
                        VentaConfirmar.this.finish();
                    }else{
                        Funciones.mensajeError(VentaConfirmar.this,"Error",json.getString("mensaje"));
                    }
                }catch (Exception e){
                    Toast.makeText(VentaConfirmar.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }



    public class TCTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String codigoTC = params[0]; //recibe el codigo tipo de comprobante

            String ws =  Funciones.URL_WS + "series.listar.php";

            HashMap parametros = new HashMap<String,String>();
            parametros.put("token", Sesion.LOGIN_TOKEN);
            parametros.put("p_tc",codigoTC);

            String resultado = new Funciones().getHttpContent(ws, parametros);

            return resultado;
        }


        protected void onPostExecute(final String resultado) {

            if ( ! resultado.isEmpty() ) {
                try {
                    JSONObject json = new JSONObject(resultado);

                    int estado = json.getInt("estado");
                    if (estado==200){
                        JSONArray jsonArray = json.getJSONArray("datos");

                        Serie.listaSerie.clear();
                        String arraySerries[] = new String[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objItem = jsonArray.getJSONObject(i);

                            Serie objSerie = new Serie();
                            objSerie.setNroSerie(objItem.getInt("numero_serie"));
                            Serie.listaSerie.add(objSerie);

                            arraySerries[i] = String.valueOf(objItem.getInt("numero_serie"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                VentaConfirmar.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                arraySerries
                        );

                        spSeries.setAdapter(adapter);
                    }

                }catch (Exception e){
                    Toast.makeText(VentaConfirmar.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

        }

    }

    public class TCCliente extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String ws =  Funciones.URL_WS + "clientes.listar.php";

            HashMap parametros = new HashMap<String,String>();
            parametros.put("token", Sesion.LOGIN_TOKEN);

            String resultado = new Funciones().getHttpContent(ws, parametros);

            return resultado;
        }


        protected void onPostExecute(final String resultado) {

            if ( ! resultado.isEmpty() ) {
                try {
                    JSONObject json = new JSONObject(resultado);

                    int estado = json.getInt("estado");
                    if (estado==200){
                        JSONArray jsonArray = json.getJSONArray("datos");

                        Cliente.listaCliente.clear();
                        String arrayClientes[] = new String[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objItem = jsonArray.getJSONObject(i);

                            Cliente objCliente = new Cliente();
                            objCliente.setCodigoCliente(objItem.getInt("codcli"));
                            objCliente.setNombre(objItem.getString("nombre"));
                            objCliente.setDireccion(objItem.getString("direccion"));
                            objCliente.setDocIde(objItem.getString("docide"));
                            Cliente.listaCliente.add(objCliente);

                            arrayClientes[i] = objItem.getString("nombre");
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                VentaConfirmar.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                arrayClientes
                        );

                        spClientes.setAdapter(adapter);
                    }

                }catch (Exception e){
                    Toast.makeText(VentaConfirmar.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

        }

    }
}
