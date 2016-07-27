package luisvilches.cl.tbip;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class IngresarBip extends AppCompatActivity {

    EditText nombre;
    EditText numero;
    String name;
    String codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_bip);

        Button btnSave = (Button)findViewById(R.id.save);
        nombre = (EditText)findViewById(R.id.editText3);
        numero = (EditText) findViewById(R.id.editText2);
    }

    public void onSave(View s){

        name = nombre.getText().toString();
        codigo = numero.getText().toString();

        DataBaseManager manager = new DataBaseManager(this);
        manager.insertar(name,codigo);

        nombre.setText("");
        numero.setText("");
        Toast.makeText(this, "Se cargaron los datos de tu Tarjeta Bip",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
