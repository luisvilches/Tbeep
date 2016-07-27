package luisvilches.cl.tbip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class NotConexion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_conexion);
    }

    public void onReintentar(View view){
        if (Utils.isHayConexion(this)){

            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

        }else{

            Toast.makeText(this, "Sin Coneccion a Internet", Toast.LENGTH_SHORT).show();

        }
    }
}
