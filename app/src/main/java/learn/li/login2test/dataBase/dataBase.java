package learn.li.login2test.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 李天烨 on 2016/10/23.
 */
public class DataBase extends SQLiteOpenHelper {
    public static final String TABLE_NAME_LISTITEM = "listItem";
    public static final String TABLE_NAME_ACCOUNT = "account";
    public static final String COLUMN_NAME_ITEMNAME = "itemName";
    public static final String COLUMN_NAME_INFORMATION = "information";
    public static final String COLUMN_NAME_ID = "_id";

    public DataBase(Context context, String tableName) {
        super(context, tableName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("listItem","creating");
        String sql = "create table if not exists " +TABLE_NAME_LISTITEM+"("+
                COLUMN_NAME_ID+" integer primary key autoincrement,"+
                COLUMN_NAME_ITEMNAME+" varchar(20) not null default \"0\"," +
                COLUMN_NAME_INFORMATION+" text not null default \"0\""+")";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
