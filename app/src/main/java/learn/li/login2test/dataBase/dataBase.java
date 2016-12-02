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
    public static final String COLUMN_NAME_REALNAME = "realName";
    public static final String COLUMN_NAME_BIRTHDAY = "birthday";
    public static final String COLUMN_NAME_PHONE = "phoneNumber";
    public static final String COLUMN_NAME_PASSWORD = "password";
    public static final String COLUMN_NAME_EMAIL = "email";
    public static final String COLUMN_NAME_ID = "_id";


    public DataBase(Context context, String tableName) {
        super(context, tableName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("listItem","creating");
        String sqlItem = "create table if not exists " +TABLE_NAME_LISTITEM+"("+
                COLUMN_NAME_ID+" integer primary key autoincrement,"+
                COLUMN_NAME_ITEMNAME+" varchar(20) not null default \"0\"," +
                COLUMN_NAME_INFORMATION+" text not null default \"0\""+")";
        db.execSQL(sqlItem);

        Log.i("account","creating");
        String sqlAccount = "create table if not exists " +TABLE_NAME_ACCOUNT+"("+
                COLUMN_NAME_ID+" integer primary key autoincrement,"+
                COLUMN_NAME_REALNAME+" varchar(20) not null default \"0\"," +
                COLUMN_NAME_BIRTHDAY+" varchar(20) not null default \"1970-1-1\"," +
                COLUMN_NAME_PHONE+" varchar(20) not null default \"0\"," +
                COLUMN_NAME_EMAIL+" text not null default \"null@com\"," +
                COLUMN_NAME_PASSWORD+" text not null default \"0\""+")";
        db.execSQL(sqlAccount);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
