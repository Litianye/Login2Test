package learn.li.login2test.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by 李天烨 on 2016/9/13.
 */
public class DataBaseUtil {

    //保存到数据库
    public static void insertInSqltoItem(String tableName, Context context, String attr1, String attr2){
        DataBase db = new DataBase(context, tableName);
        //取得一个可写的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        //像ContentValues中存放数据
        values.put(DataBase.COLUMN_NAME_ITEMNAME, attr1);
        values.put(DataBase.COLUMN_NAME_INFORMATION, attr2);
        //数据库执行插入命令
        dbS.insert(tableName, null, values);

        Log.i("insert","query-->"+tableName+":"+attr1+";"+attr2);
    }


    //更新到数据库
    public static void updateInsqltoItem(String tableName, Context context, String attr, String type){
        DataBase db = new DataBase(context, tableName);
        //取得一个只读的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataBase.COLUMN_NAME_INFORMATION, attr);
        dbS.update(tableName, values, DataBase.COLUMN_NAME_ID+"=?", new String[]{""+type});
        Log.i("update","query-->"+type+":"+attr);
    }

    //保存到数据库
    public static void insertInSqltoAccount(String tableName, Context context, String attr1, String attr2){
        DataBase db = new DataBase(context, tableName);
        //取得一个可写的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        //像ContentValues中存放数据
        values.put(DataBase.COLUMN_NAME_PHONE, attr1);
        values.put(DataBase.COLUMN_NAME_PASSWORD, attr2);
        //数据库执行插入命令
        dbS.insert(tableName, null, values);

        Log.i("insert","query-->"+tableName+":"+attr1+";"+attr2);
    }

    //查询数据库
    public static String searchInSql(String tableName, Context context, String account){
        String password = "";
        DataBase db = new DataBase(context, tableName);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getReadableDatabase();

        //查询并获得游标
        Cursor c = dbS.query(tableName, new String[]{"account","password"}, "account=?", new String[]{account}, null, null, null, null);
        //利用游标遍历所有数据对象
        while(c.moveToNext()){
            password = c.getString(c.getColumnIndex("password"));
            //日志打印输出
            Log.i("search","query-->"+account+":"+password);
        }
        return account+":"+password;
    }

    //查询数据库第一条
    public static String readFirstInSql(String tableName, Context context){
        String account_out = "";
        String password_out = "";
        DataBase db = new DataBase(context, tableName);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getReadableDatabase();


        Cursor c = dbS.query(tableName, null, null, null, null, null, null);//查询并获得游标
        c.moveToFirst();
        account_out = c.getString(c.getColumnIndex("account"));
        password_out = c.getString(c.getColumnIndex("password"));
        //日志打印输出
        Log.i("readFirst", "query-->" + account_out+":"+password_out);
        return account_out + ":" + password_out;
    }

    //删除数据
    public static void deleteInSql(String tableName, Context context, String account){
        DataBase db = new DataBase(context, tableName);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        dbS.delete(tableName, "account=?", new String[]{account});
        Log.i("delete","query-->"+account);
    }

    //判断空否
    public static boolean isEmpty(String tableName, Context context){
        DataBase db = new DataBase(context, tableName);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getReadableDatabase();
        Cursor c = dbS.query(tableName,null,null,null,null,null,null);//查询并获得游标

        if (c.moveToFirst()){
            Log.i("isEmpty","query-->"+"false");
            return false;
        }else {
            Log.i("isEmpty","query-->"+"true");
            return true;
        }
    }

}
