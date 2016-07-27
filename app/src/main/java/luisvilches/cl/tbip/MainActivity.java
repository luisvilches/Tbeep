package luisvilches.cl.tbip;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView textSaldo;
    TextView status;
    EditText tarjeta;
    String objeto;
    JSONObject jsonObject;
    String data;
    ProgressBar barra;
    DataBaseManager manager;
    Cursor cursor;
    ListView list;
    SimpleCursorAdapter adapter;
    String[] from;
    int[] to;
    String saldo;

    //URL DE CONSULTA
    String url1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tarjeta = (EditText) findViewById(R.id.editText);
        textSaldo = (TextView) findViewById(R.id.textSaldo);
        status = (TextView) findViewById(R.id.tuSaldo);
        barra = (ProgressBar) findViewById(R.id.barra);

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


        if (!Utils.isHayConexion(this)){
            Toast.makeText(this, "Sin Coneccion a Internet", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,NotConexion.class);
            startActivity(intent);
        }
        cargarList();


    }

    public void cargarList(){

        from = new String[]{manager.NAME, manager.CODE};
        to = new int[]{android.R.id.text1, android.R.id.text2};

        cursor = manager.cargarCursorTarjetas();
        adapter = new SimpleCursorAdapter(this,android.R.layout.two_line_list_item,cursor,from,to,0);
        list.setAdapter(adapter);

    }


    // ENLACE A LA VISTA DE MAPAS
    public void onMaps(View v) {

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void onActivitySave(View b){

        Intent intent = new Intent(this, IngresarBip.class);
        startActivity(intent);
    }

    //  CONSULTA DE SALDO BIP
    public void onConsultar(View a) {

        data = tarjeta.getText().toString();
        url1 = "http://bip-servicio.herokuapp.com/api/v1/solicitudes.json?bip=" + data;

        if (data.length() >= 8 && data.length() <= 10 ) { //|| data.matches("/^\\d+$/")) {

            barra.setVisibility(View.VISIBLE);
            textSaldo.setText("Consultando Saldo");
            textSaldo.setTextSize(20);

            new TareasAsincronas().execute(url1);

        } else {
            textSaldo.setText("Tarjeta Invalida");
            textSaldo.setTextSize(20);
            status.setText("Error:");
        } ;
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
}
