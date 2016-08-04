package luisvilches.cl.tbip;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    TextView textSaldo,status;
    EditText tarjeta;
    String objeto,url1,data,RegDelete,idDelete,UpdateSaldo,UpdateId,UpdateNombre,UpdateCodigo;
    String url2 = "http://bip-servicio.herokuapp.com/api/v1/solicitudes.json?bip=";
    JSONObject jsonObject;
    ProgressBar barra;
    DataBaseManager manager;
    Cursor cursor;
    ListView list;;
    SimpleCursorAdapter adapter;
    String[] from;
    int[] to;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tarjeta = (EditText) findViewById(R.id.editText);
        textSaldo = (TextView) findViewById(R.id.textSaldo);
        status = (TextView) findViewById(R.id.tuSaldo);
        barra = (ProgressBar) findViewById(R.id.barra);
        list = (ListView) findViewById(R.id.ListTarjetas);

        comprobarConexion();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

    }
    /////////////////////////////////////////////////////////////////////////////////
    /////// FUNCION QUE CARGA EL LISTVIEW CON LOS REGISTROS DE LA DB
    /////////////////////////////////////////////////////////////////////////////////

    public void cargarList() {

        from = new String[]{manager.NAME, manager.SALDO};
        to = new int[]{R.id.textNombre, R.id.textNumero};
        cursor = manager.cargarCursorTarjetas();

        adapter = new SimpleCursorAdapter(this,R.layout.itemlist,cursor,from,to,0);
        list.setAdapter(adapter);

    ///////////////////////////////////////////////////////////////////////////////////////
    //////// LIST LISTENER CLICK
    ///////////////////////////////////////////////////////////////////////////////////////

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {



            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
                Cursor c = (Cursor) parent.getItemAtPosition(i);
                idDelete = c.getString(0);
                RegDelete = c.getString(1);
                dialogEliminar(view);
                return false;
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////
    ///DIALOGO PARA CONFIRMAR LA ELIMINACION DE UN REGISTRO DE TARJETA BIP
    ////////////////////////////////////////////////////////////////////////

    public void dialogEliminar(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Desea eliminar esta tarjeta?");

        //ASOCIAR UN LAYOUT REMOTO
        LayoutInflater inflater = this.getLayoutInflater();
        final View viewRemoto = inflater.inflate(R.layout.dialog_eliminar, null);
        builder.setView(viewRemoto);

        TextView titulo = (TextView)viewRemoto.findViewById(R.id.nombreTarjetaEliminar);

        titulo.setText(RegDelete);


        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                manager.eliminar(idDelete);
                cargarList();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    ////// FUNCION QUE COMPRUEBA SI HAY CONEXION A INTERNET
    ////////////////////////////////////////////////////////////////////////////////////

    public void comprobarConexion() {

        if (!Utils.isHayConexion(this)) {

            Intent intent = new Intent(this, SinConexion.class);
            startActivity(intent);

        }else {

            Resources res = getResources();

            TabHost tabs = (TabHost) findViewById(R.id.tabHost);
            tabs.setup();

            TabHost.TabSpec spec = tabs.newTabSpec("tabinicio");
            spec.setContent(R.id.TabInicio);
            spec.setIndicator("Inicio");
            tabs.addTab(spec);

            spec = tabs.newTabSpec("tabmistarjetas");
            spec.setContent(R.id.TabMisTarjetas);
            spec.setIndicator("Mis Tarjetas");
            tabs.addTab(spec);

            tabs.setCurrentTab(0);

            textSaldo.setText("Ingrese el N° de la Tarjeta Bip! a consultar");
            textSaldo.setTextSize(25);

            manager = new DataBaseManager(this);
            new ConsultaEnSegundoPlano().execute(url2);
            cargarList();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    ////// ENLACE A LA VISTA DE MAPAS
    //////////////////////////////////////////////////////////////////////////////////////

    public void onMaps(View v) {

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ////// INSERTAR TARJETA EN LA BD
    //////////////////////////////////////////////////////////////////////////////////////////

    public void save(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Datos nueva tarjeta");

        //ASOCIAR UN LAYOUT REMOTO
        LayoutInflater inflater = this.getLayoutInflater();
        final View viewRemoto = inflater.inflate(R.layout.activity_ingresar_bip, null);
        builder.setView(viewRemoto);


        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                EditText nombreTarjeta = (EditText) viewRemoto.findViewById(R.id.editText2);
                EditText numeroTarjeta = (EditText) viewRemoto.findViewById(R.id.editText3);

                String nombre = nombreTarjeta.getText().toString();
                String numero = numeroTarjeta.getText().toString();

                if (numero.length() >= 8 && numero.length() <= 10) {

                    manager.insertar(nombre, numero);

                    nombreTarjeta.setText("");
                    numeroTarjeta.setText("");
                    new ConsultaEnSegundoPlano().execute(url2);

                    cargarList();

                }else{
                    Toast.makeText(getApplicationContext(),"Tarjeta Invalida",Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //////  FUNCION PARA LA CONSULTA DE SALDO DE CADA TARJETA BIP
    //////////////////////////////////////////////////////////////////////////////////////////

    public void onConsultar(View a) {

        data = tarjeta.getText().toString();
        url1 = "http://bip-servicio.herokuapp.com/api/v1/solicitudes.json?bip=" + data;

        if (data.length() >= 8 && data.length() <= 10) { //|| data.matches("/^\\d+$/")) {

            barra.setVisibility(View.VISIBLE);
            textSaldo.setText("Consultando Saldo");
            textSaldo.setTextSize(20);

            new TareasAsincronas().execute(url1);

        } else {

            textSaldo.setText("Tarjeta Invalida");
            textSaldo.setTextSize(20);
            status.setText("Error:");
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////// INICIO TAREAS ASINCRONAS
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public class TareasAsincronas extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... url) {

            try {
                jsonObject = JsonParser.readJsonFromUrl(url1);
                objeto = jsonObject.getString("saldoTarjeta");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return objeto;
        }

        @Override
        protected void onPostExecute(String stringFromDoInBackground) {

            // se pasa el saldo al TextView
            textSaldo.setText(stringFromDoInBackground);
            textSaldo.setTextSize(50);
            status.setText("Tu saldo es de: ");
            barra.setVisibility(View.GONE);
        }
    }

    public class ConsultaEnSegundoPlano extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected String doInBackground(String... url) {

                        try {
                            Cursor c = manager.cargarCursorTarjetas();
                            if (c != null) {
                                while (c.moveToNext()) {
                                    UpdateId = c.getString(0);
                                    UpdateNombre = c.getString(1);
                                    UpdateCodigo = c.getString(2);
                                    UpdateSaldo = c.getString(3);

                                    if (!(UpdateSaldo == null)) {

                                        String uri = url2 + UpdateCodigo;

                                        jsonObject = JsonParser.readJsonFromUrl(url2+UpdateCodigo);
                                        objeto = jsonObject.getString("saldoTarjeta");

                                        String a ="$25000";

                                        manager.actualizarSaldo(objeto, UpdateId);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }return objeto;
        }

        @Override
        protected void onPostExecute(String stringFromDoInBackground) {

            cargarList();
            //Toast.makeText(getApplicationContext(),"Sincronizacion exitosa!",Toast.LENGTH_LONG).show();
        }
    }
}
