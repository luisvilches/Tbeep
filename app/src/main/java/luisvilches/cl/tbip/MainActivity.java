package luisvilches.cl.tbip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textSaldo,status;
    EditText tarjeta;
    String objeto,url1,data,RegDelete,idDelete,UpdateSaldo,UpdateId,UpdateNombre,UpdateCodigo,saldoActualizado,obj;
    JSONObject jsonObject;
    ProgressBar barra;
    DataBaseManager manager;
    Cursor cursor;
    ListView list;;
    SimpleCursorAdapter adapter;
    String[] from;
    int[] to;
    private JSONArray jsonArray;
    Context context;
    String url2 = "http://bip-servicio.herokuapp.com/api/v1/solicitudes.json?bip=";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tarjeta = (EditText) findViewById(R.id.editText);
        textSaldo = (TextView) findViewById(R.id.textSaldo);
        status = (TextView) findViewById(R.id.tuSaldo);
        barra = (ProgressBar) findViewById(R.id.barra);

        comprobarConexion();

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

        textSaldo.setText("Bienvenido!");
        textSaldo.setTextSize(30);

        manager = new DataBaseManager(this);

        list = (ListView) findViewById(R.id.ListTarjetas);
        new ConsultaEnSegundoPlano().execute(url2);
        cargarList();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

              // Cursor c = (Cursor) parent.getItemAtPosition(i);
               //String text = c.getString(1);

              // Toast.makeText(getApplicationContext(), text,Toast.LENGTH_LONG).show();
            }

        });

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

    public void cargarList2(){

        from = new String[]{manager.NAME, manager.SALDO};
        to = new int[]{R.id.textNombre, R.id.textNumero};
        cursor = manager.cargarCursorTarjetas();

        adapter = new SimpleCursorAdapter(this,R.layout.itemlist,cursor,from,to,0);
        list.setAdapter(adapter);
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
            Toast.makeText(this, "Sin Coneccion a Internet", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SinConexion.class);
            startActivity(intent);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    ////// ENLACE A LA VISTA DE MAPAS
    //////////////////////////////////////////////////////////////////////////////////////

    public void onMaps(View v) {

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void onActivitySave(View b) {

        Intent intent = new Intent(this, IngresarBip.class);
        startActivity(intent);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
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

                String name = nombreTarjeta.getText().toString();
                String codigo = numeroTarjeta.getText().toString();

                String saldo = "$0";

                //DataBaseManager manager = new DataBaseManager(this);
                manager.insertar(nombre,numero);

                nombreTarjeta.setText("");
                numeroTarjeta.setText("");
                new ConsultaEnSegundoPlano().execute(url2);

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
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
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
        ;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://luisvilches.cl.tbip/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://luisvilches.cl.tbip/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

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

                                    //Toast.makeText(getApplicationContext(), UpdateId, Toast.LENGTH_LONG).show();

                                    if (!(UpdateSaldo == null)) {

                                       // Toast.makeText(getApplicationContext(), UpdateSaldo, Toast.LENGTH_LONG).show();

                                        String uri = url2 + UpdateCodigo;

                                        jsonObject = JsonParser.readJsonFromUrl(url2+UpdateCodigo);
                                        objeto = jsonObject.getString("saldoTarjeta");

                                        String a ="$25000";

                                        manager.actualizarSaldo(objeto, UpdateId);
                                        //c.close();
                                        //cargarList();
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

            //manager.actualizarSaldo(stringFromDoInBackground,UpdateId);
            //manager.close();

            cargarList();
            Toast.makeText(getApplicationContext(),"Actualizacion de saldos exitosa!",Toast.LENGTH_LONG).show();


        }
    }
}
