package learn.li.login2test.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 李天烨 on 2016/9/13.
 */
public class DataBaseUtil{

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

    //更新到account数据库
    public static void updateBirthInsqltoAccount(String tableName, Context context, String birthday){
        DataBase db = new DataBase(context, tableName);
        //取得一个只读的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataBase.COLUMN_NAME_BIRTHDAY, birthday);
        dbS.update(tableName, values, DataBase.COLUMN_NAME_REALNAME, null);
        Log.i("update","query-->"+birthday+";");
    }

    //保存到数据库
    public static void insertInSqlToAccount(String tableName, Context context, String realName,
                                            String phone, String password, String email, String custodyCode){
        DataBase db = new DataBase(context, tableName);
        //取得一个可写的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        //像ContentValues中存放数据
        values.put(DataBase.COLUMN_NAME_REALNAME,realName);
        values.put(DataBase.COLUMN_NAME_PHONE, phone);
        values.put(DataBase.COLUMN_NAME_PASSWORD, password);
        values.put(DataBase.COLUMN_NAME_EMAIL, email);
        values.put(DataBase.COLUMN_NAME_CUSTODYCODE, custodyCode);
        //数据库执行插入命令
        dbS.insert(tableName, null, values);

        Log.i("insert","query-->"+tableName+":"+realName+":"+phone+";"+password+";"+email+";"+custodyCode);
    }

    //保存到数据库
    public static void insertBasicInSqlToAccount(String tableName, Context context,
                                            String realName, String phone, String password){
        DataBase db = new DataBase(context, tableName);
        //取得一个可写的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        //像ContentValues中存放数据
        values.put(DataBase.COLUMN_NAME_REALNAME,realName);
        values.put(DataBase.COLUMN_NAME_PHONE, phone);
        values.put(DataBase.COLUMN_NAME_PASSWORD, password);
        //数据库执行插入命令
        dbS.insert(tableName, null, values);
        Log.i("insert","query-->"+tableName+":"+realName+":"+phone+";"+password);
    }

    //查询数据库
    public static String readPhoneAndNameInSql(String tableName, Context context){
        String phoneNumber = "";
        String realName = "";
        String custodyCode = "";
        DataBase db = new DataBase(context, tableName);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getReadableDatabase();

        Cursor c = dbS.query(tableName, null, null, null, null, null, null);//查询并获得游标
        c.moveToFirst();
        phoneNumber = c.getString(c.getColumnIndex("phoneNumber"));
        realName = c.getString(c.getColumnIndex("realName"));
        custodyCode = c.getString(c.getColumnIndex("custodyCode"));

        return realName+";"+phoneNumber+";"+custodyCode;
    }

    //查询数据库第一条
    public static String readFirstInSql(String tableName, Context context){
        String name_out = "";
        String phone_out = "";
        String password_out = "";
        String birthday_out = "";
        String id_out ="";
        DataBase db = new DataBase(context, tableName);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getReadableDatabase();


        Cursor c = dbS.query(tableName, null, null, null, null, null, null);//查询并获得游标
        c.moveToFirst();
        id_out =c.getString(c.getColumnIndex("_id"));
        name_out = c.getString(c.getColumnIndex("realName"));
        phone_out = c.getString(c.getColumnIndex("phoneNumber"));
        password_out = c.getString(c.getColumnIndex("password"));
        birthday_out = c.getString(c.getColumnIndex("birthday"));
        //日志打印输出
        Log.i("readFirst", "query-->" + id_out +";"+ name_out+":"+ phone_out+":"+ password_out+":"+birthday_out);
        return name_out+":"+ phone_out+":"+ password_out;
    }

    //删除数据
    public static void deleteInSql(String tableName, Context context){
        DataBase db = new DataBase(context, tableName);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        dbS.delete(tableName, null, null);
        Log.i("delete","query-->");
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
