package tw.com.frankchang.houli.classno_14_databases;

import android.app.Application;

/**
 * Created by 張景翔 on 2017/4/26.
 */

public class GlobalVariable extends Application {

    private final String DB_NAME = "ClassNo_14.db";
    private final String DB_TABLENAME = "ClassNo_14";
    private final int DB_VERSION = 1;

    public String getDB_NAME(){
        return DB_NAME;
    }

    public String getDb_TableName(){
        return DB_TABLENAME;
    }

    public int getDB_VERSION(){
        return DB_VERSION;
    }
}
