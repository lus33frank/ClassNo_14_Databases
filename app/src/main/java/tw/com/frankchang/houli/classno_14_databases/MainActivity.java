package tw.com.frankchang.houli.classno_14_databases;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //畫面元件屬性
    private TextView textView_Show;
    private EditText etInput_Id;
    private EditText etInput_Name;
    private EditText etInput_Phone;
    private EditText etInput_Address;
    private Button button_Previous;
    private Button button_Next;
    private ListView listView;

    private GlobalVariable globalVariable = new GlobalVariable();
    private SQLiteDatabase db_SQLite;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findviewer();

        //第一種方式 SQLiteOpenHelper
        //  data/data/<Package Name>/databases/
        setSQLite_DB1();

        //第二種方式 Context
        //  data/data/<Package Name>/databases/
        //setSQLite_DB2();

        //第三種方式 SQLiteDatabase
        //若要使用SD Card要記得加上權限
        //setSQLite_DB3();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cursor != null){
            cursor.close();
        }
        db_SQLite.close();
    }

    private void findviewer() {
        textView_Show = (TextView) findViewById(R.id.textView);

        etInput_Id = (EditText) findViewById(R.id.editText);
        etInput_Name = (EditText) findViewById(R.id.editText2);
        etInput_Phone = (EditText) findViewById(R.id.editText3);
        etInput_Address = (EditText) findViewById(R.id.editText4);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(listviewListener);

        button_Previous = (Button) findViewById(R.id.button5);
        button_Previous.setOnClickListener(previousListener);
        button_Previous.setEnabled(false);

        button_Next = (Button) findViewById(R.id.button6);
        button_Next.setOnClickListener(nextListener);
        button_Next.setEnabled(false);
    }

    //設定資料庫連線（第一種）
    private void setSQLite_DB1(){
        DBHelper dbHelper = new DBHelper(this, globalVariable.getDB_NAME(),
                null, globalVariable.getDB_VERSION());
        db_SQLite = dbHelper.getWritableDatabase();
    }

    //設定資料庫連線（第二種）
    private void setSQLite_DB2(){
        db_SQLite = openOrCreateDatabase(globalVariable.getDB_NAME(), MODE_PRIVATE, null);

        String strSQL = "CREATE TABLE if not exists " + globalVariable.getDb_TableName() + " (";
        strSQL += "id INTEGER PRIMARY KEY NOT NULL, ";
        strSQL += "name VARCHAR(45), ";
        strSQL += "phone VARCHAR(20), ";
        strSQL += "address VARCHAR(60))";
        db_SQLite.execSQL(strSQL);
    }

    //設定資料庫連線（第三種）
    private void setSQLite_DB3(){
        //指定路徑
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        //指定檔案
        File file = new File(path, globalVariable.getDB_NAME());

        //設定建TABLE指令
        String strSQL = "CREATE TABLE if not exists " + globalVariable.getDb_TableName() + " (";
        strSQL += "id INTEGER PRIMARY KEY NOT NULL, ";
        strSQL += "name VARCHAR(45), ";
        strSQL += "phone VARCHAR(20), ";
        strSQL += "address VARCHAR(60))";

        //檢查資料庫檔案是否存
        if(!file.exists()){
            //從 assets 資料夾，讀資料庫檔並寫到目的資料夾
            try {
                //讀
                InputStream is = getAssets().open(globalVariable.getDB_NAME());
                byte[] buffer_DB = new byte[is.available()];
                is.read(buffer_DB);
                is.close();

                //寫
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(buffer_DB);
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            //從 assets 資料夾，謮入 sql 指令字串的文字檔，在建立空資料表後再執行 sql 語法
            try {
                InputStream is = getAssets().open("sql.txt");    //SQL指令預儲檔
                byte[] buffer_SQL = new byte[is.available()];
                is.read(buffer_SQL);
                is.close();

                //開啟資料庫
                db_SQLite = SQLiteDatabase.openOrCreateDatabase(file, null);
                //建立TABLE
                db_SQLite.execSQL(strSQL);

                //建立預設資料
                String[] sqls = new String(buffer_SQL).split("\n");
                for(int i = 0; i < sqls.length; i++){
                    db_SQLite.execSQL(sqls[i]);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //開啟資料庫
        db_SQLite = SQLiteDatabase.openOrCreateDatabase(file,null);
        //建立TABLE
        db_SQLite.execSQL(strSQL);
    }

    //新增
    public void addOnClick(View view) {
        //宣告
        String addMessage;
        ContentValues values = new ContentValues();

        //取值放入
        values.put("id", Integer.parseInt(etInput_Id.getText().toString()));
        values.put("name", etInput_Name.getText().toString());
        values.put("phone", etInput_Phone.getText().toString());
        values.put("address", etInput_Address.getText().toString());

        //新增到資料表
        long row = db_SQLite.insert(globalVariable.getDb_TableName(), null, values);

        //訊息內容
        addMessage = getString(R.string.MainActivity_add);
        addMessage += String.valueOf(row);
        addMessage += getString(R.string.MainActivity_count);
        //跳訊息
        Toast.makeText(this, addMessage, Toast.LENGTH_SHORT).show();
    }

    //修改
    public void updateOnClick(View view) {
        //宣告
        String addMessage;
        ContentValues values = new ContentValues();

        //取值放入
        values.put("id", Integer.parseInt(etInput_Id.getText().toString()));
        values.put("name", etInput_Name.getText().toString());
        values.put("phone", etInput_Phone.getText().toString());
        values.put("address", etInput_Address.getText().toString());

        //修改資料到資料表
        long rows = db_SQLite.update(globalVariable.getDb_TableName(), values, "id = ?",
                new String[]{etInput_Id.getText().toString()});

        //訊息內容
        addMessage = getString(R.string.MainActivity_update);
        addMessage += String.valueOf(rows);
        addMessage += getString(R.string.MainActivity_count);
        //跳訊息
        Toast.makeText(this, addMessage, Toast.LENGTH_SHORT).show();
    }

    //刪除
    public void deleteOnClick(View view) {
        //宣告
        String addMessage;

        long rows = db_SQLite.delete(globalVariable.getDb_TableName(), "id = ?",
                new String[]{etInput_Id.getText().toString()});

        //訊息內容
        addMessage = getString(R.string.MainActivity_delete);
        addMessage += String.valueOf(rows);
        addMessage += getString(R.string.MainActivity_count);
        //跳訊息
        Toast.makeText(this, addMessage, Toast.LENGTH_SHORT).show();
    }

    //查詢
    public void queryOnClick(View view) {
        cursor = db_SQLite.query(globalVariable.getDb_TableName(), null, null, null, null, null, "id ASC");
        if (cursor != null){
            button_Previous.setEnabled(true);
            button_Next.setEnabled(true);
        }
        setDataToListView();
    }

    private void setDataToListView() {
        if (cursor != null){
            ArrayList<HashMap<String,Object>> data = new ArrayList<>();
            while (cursor.moveToNext()){
                HashMap<String,Object> items=new HashMap<>();
                items.put("Id", cursor.getString(cursor.getColumnIndex("id")));
                items.put("Name", cursor.getString(cursor.getColumnIndex("name")));
                items.put("Phone", cursor.getString(cursor.getColumnIndex("phone")));
                items.put("Address", cursor.getString(cursor.getColumnIndex("address")));

                data.add(items);
            }

            SimpleAdapter simpleAdapter=new SimpleAdapter(
                    this,
                    data,
                    R.layout.view_listview_item,
                    new String[]{"Id", "Name", "Phone", "Address"},
                    new int[]{R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5}
            );

            listView.setAdapter(simpleAdapter);
        }
    }

    private AdapterView.OnItemClickListener listviewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            etInput_Id.setText(((TextView)view.findViewById(R.id.textView2)).getText());
            etInput_Name.setText(((TextView)view.findViewById(R.id.textView3)).getText());
            etInput_Phone.setText(((TextView)view.findViewById(R.id.textView4)).getText());
            etInput_Address.setText(((TextView)view.findViewById(R.id.textView5)).getText());
        }
    };

    //上一筆
    private View.OnClickListener previousListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cursor.isFirst()){
                Toast.makeText(MainActivity.this, R.string.MainActivity_First, Toast.LENGTH_SHORT).show();
                return;
            }

            if (cursor.moveToPrevious()){
                etInput_Id.setText(cursor.getString(cursor.getColumnIndex("id")));
                etInput_Name.setText(cursor.getString(cursor.getColumnIndex("name")));
                etInput_Phone.setText(cursor.getString(cursor.getColumnIndex("phone")));
                etInput_Address.setText(cursor.getString(cursor.getColumnIndex("address")));
            }
        }
    };

    //下一筆
    private View.OnClickListener nextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cursor.isLast()){
                Toast.makeText(MainActivity.this, R.string.MainActivity_Last, Toast.LENGTH_SHORT).show();
                return;
            }

            if (cursor.moveToNext()){
                etInput_Id.setText(cursor.getString(cursor.getColumnIndex("id")));
                etInput_Name.setText(cursor.getString(cursor.getColumnIndex("name")));
                etInput_Phone.setText(cursor.getString(cursor.getColumnIndex("phone")));
                etInput_Address.setText(cursor.getString(cursor.getColumnIndex("address")));
            }
        }
    };
}
