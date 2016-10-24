package learn.li.login2test.infoFragment;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;

import learn.li.login2test.R;
import learn.li.login2test.UIPackage.MainActivity;
import learn.li.login2test.dataBase.DataBase;
import learn.li.login2test.dataBase.DataBaseUtil;

public class healthCardActivity extends ListActivity {

    private DataBase db;
    private SimpleCursorAdapter adapter = null;
    private SQLiteDatabase dbRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_card);

        db = new DataBase(this, DataBase.TABLE_NAME_LISTITEM);

        while (DataBaseUtil.isEmpty(DataBase.TABLE_NAME_LISTITEM,this)){
            initInfo();
        }
        dbRead = db.getReadableDatabase();
        adapter = new SimpleCursorAdapter(this, R.layout.list_item,
                null, new String[]{DataBase.COLUMN_NAME_ITEMNAME, DataBase.COLUMN_NAME_INFORMATION},
                new int[]{R.id.tv_itemName, R.id.tv_itemAttr}, 0);
        setListAdapter(adapter);

        refreshContactList();
    }

    private void initInfo() {
        String[] listItmeName = {"医疗状况","过敏反应","药物使用","紧急联系人","备用联系人","体重","身高"};
        String[] initAttr = {"无","无","无","110","120","60kg","170cm"};

        for (int i=0; i<listItmeName.length; i++){
            DataBaseUtil.insertInSqltoItem(DataBase.TABLE_NAME_LISTITEM, this, listItmeName[i], initAttr[i]);
        }
    }

    private void refreshContactList(){
        adapter.changeCursor(dbRead.query(DataBase.TABLE_NAME_LISTITEM, null, null, null, null, null, null));
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent=new Intent();
        //从此activity传到另一Activity
        intent.setClass(healthCardActivity.this, MainActivity.class);
        //启动另一个Activity
        healthCardActivity.this.startActivity(intent);
        healthCardActivity.this.finish();
    }

}
