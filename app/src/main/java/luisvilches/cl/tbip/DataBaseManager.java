package luisvilches.cl.tbip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import java.util.jar.Attributes;

/**
 * Created by vilches on 26-07-16.
 */
public class DataBaseManager {

    public static final String TABLE_NAME = "tarjetas";

    public static final String ID ="_id";
    public static final String NAME = "name";
    public static final String CODE = "code";

    public static final String CREATE_TABLE = "CREATE TABLE " +TABLE_NAME+ " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME + " TEXT NOT NULL,"
            + CODE + " TEXT NOT NULL);";

    private DbHelper helper;
    private SQLiteDatabase db;

    public DataBaseManager(Context context) {
        helper = new DbHelper(context);
        db = helper.getReadableDatabase();
    }

    private ContentValues generarContentValues(String name,String codigo){

        ContentValues valores = new ContentValues();
        valores.put(NAME,name);
        valores.put(CODE,codigo);

        return valores;
    }

    public void insertar (String name, String codigo){

        if (db==null)
            createDb();

        db.insert(TABLE_NAME,null,generarContentValues(name,codigo));
    }

    public void eliminar (String name){

        db.delete(TABLE_NAME,NAME+"?",new String[]{name});
    }

    public void eliminarMultiple(String name1,String name2){

        db.delete(TABLE_NAME,NAME+"IN (?,?)",new String[] {name1,name2});
    }

    public Cursor cargarCursorTarjetas(){

        String[] columnas = new String[] {ID,NAME,CODE};

        return db.query(TABLE_NAME,columnas,null,null,null,null,null);
    }

    public void createDb(){
        db.execSQL(DataBaseManager.CREATE_TABLE);
    }
}
