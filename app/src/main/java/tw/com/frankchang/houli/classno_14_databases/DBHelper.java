package tw.com.frankchang.houli.classno_14_databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 張景翔 on 2017/4/26.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSQL;
        GlobalVariable globalVariable = new GlobalVariable();

        strSQL = "CREATE TABLE " + globalVariable.getDb_TableName() + " (";
        strSQL += "id INTEGER PRIMARY KEY NOT NULL, ";
        strSQL += "name VARCHAR(45), ";
        strSQL += "phone VARCHAR(20), ";
        strSQL += "address VARCHAR(60))";
        db.execSQL(strSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
