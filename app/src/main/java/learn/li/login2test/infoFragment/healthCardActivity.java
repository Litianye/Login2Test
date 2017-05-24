package learn.li.login2test.infoFragment;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import learn.li.login2test.OkHttpUtil;
import learn.li.login2test.R;
import learn.li.login2test.UIPackage.MainActivity;
import learn.li.login2test.dataBase.DataBase;
import learn.li.login2test.dataBase.DataBaseUtil;

public class healthCardActivity extends ListActivity {

    private DataBase db;
    private SimpleCursorAdapter adapter = null;
    private SQLiteDatabase dbRead;
    private String isAllergy, bloodType;
    private String name;
    private TextView tvNameHeader, tvBirthday;
    private String healInfoUrl = "http://debug.programmox.com:8388/Mojito/user/finishedMR.do";

    public static final String[] ITEMNAME = {"医疗状况","医疗记录","药物使用","紧急联系人","备用联系人","体重","身高","过敏反应","血型"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_card);

        Intent intent=getIntent();
        name = intent.getStringExtra("name");
//        phone = intent.getStringExtra("birthday");

        initView();
        tvNameHeader.setText(name);
//        tvPhone.setText(phone);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_healthCard_upload);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHealthInfo();
                Snackbar.make(view, "上传完成", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initView() {
        tvNameHeader = (TextView) findViewById(R.id.tv_healthCardName);
        tvBirthday = (TextView) findViewById(R.id.tv_healthCardBirthday);

        tvBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(healthCardActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String temp = String.format("%d-%d-%d",year,monthOfYear+1,dayOfMonth);
                        tvBirthday.setText(temp);
                        DataBaseUtil.updateBirthInsqltoAccount(DataBase.TABLE_NAME_ACCOUNT, healthCardActivity.this, temp);
                    }
                },1970,1,1).show();

            }
        });
    }

    private void initInfo() {
        String[] initAttr = {"无","无","无","110","120","60kg","170cm","无","A"};

        for (int i=0; i<ITEMNAME.length; i++){
            DataBaseUtil.insertInSqltoItem(DataBase.TABLE_NAME_LISTITEM, this, ITEMNAME[i], initAttr[i]);
        }
    }

    private void getHealthInfo(){
        Cursor c = adapter.getCursor();
        String[] healthInfo=new String[ITEMNAME.length];
        for (int i=0; i<ITEMNAME.length; i++){
            c.moveToPosition(i);
            healthInfo[i]=c.getString(c.getColumnIndex(DataBase.COLUMN_NAME_INFORMATION));
            Log.i("healInfo", healthInfo[i]);
        }
        //          "医疗状况","医疗记录","药物使用","紧急联系人","备用联系人","体重","身高","过敏反应","血型"
//        String url, String name, String medicalStatus, String medicalNote, String drugUse,
//                String contactsName1, String contactsNumber1, String contactsName2, String contactNumber2,
//                String weight, String stature, String irritability, String bloodType
        OkHttpUtil.healthInfoPost(healInfoUrl, name, healthInfo[0], healthInfo[1], healthInfo[2],
                "紧急联系人",  healthInfo[3], "备用联系人", healthInfo[4],healthInfo[5], healthInfo[6], healthInfo[7], healthInfo[8]);
    }

    private void refreshContactList() {
        adapter.changeCursor(dbRead.query(DataBase.TABLE_NAME_LISTITEM, null, null, null, null, null, null));
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = adapter.getCursor();
        c.moveToPosition(position);

        showDialog(c.getString(c.getColumnIndex(DataBase.COLUMN_NAME_ITEMNAME))
                , c.getInt(c.getColumnIndex(DataBase.COLUMN_NAME_ID)));

        super.onListItemClick(l,v,position,id);
    }

    private void showDialog(String title, final int _id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        builder.setTitle(title);
        switch (_id){
            case 1:
            case 2:
            case 3:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(editText);
                break;
            case 8:
                builder.setSingleChoiceItems(new String[]{"有", "无"}, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0){
                                    isAllergy = "有";
                                }else {
                                    isAllergy = "无";
                                }
                            }
                    });
                break;
            case 4:
            case 5:
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(editText);
                break;
            case 6:
            case 7:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(editText);
                break;
            case 9:
                builder.setSingleChoiceItems(new String[]{"A", "B", "AB", "O"}, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i){
                                    case 0:
                                        bloodType="A";
                                        break;
                                    case 1:
                                        bloodType="B";
                                        break;
                                    case 2:
                                        bloodType="AB";
                                        break;
                                    case 3:
                                        bloodType="O";
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                break;
            default:
                break;
        }
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (_id){
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        DataBaseUtil.updateInsqltoItem(DataBase.TABLE_NAME_LISTITEM,
                                healthCardActivity.this,
                                editText.getText().toString(), String.valueOf(_id));
                        break;
                    case 8:
                        DataBaseUtil.updateInsqltoItem(DataBase.TABLE_NAME_LISTITEM,
                                healthCardActivity.this,
                                isAllergy, String.valueOf(_id));
                        break;
                    case 6:
                        DataBaseUtil.updateInsqltoItem(DataBase.TABLE_NAME_LISTITEM,
                                healthCardActivity.this,
                                editText.getText().toString(), String.valueOf(_id));
                        break;
                    case 7:
                        DataBaseUtil.updateInsqltoItem(DataBase.TABLE_NAME_LISTITEM,
                                healthCardActivity.this,
                                editText.getText().toString(), String.valueOf(_id));
                        break;
                    case 9:
                        DataBaseUtil.updateInsqltoItem(DataBase.TABLE_NAME_LISTITEM,
                                healthCardActivity.this,
                                bloodType, String.valueOf(_id));
                        break;
                    default:
                        break;
                }
                refreshContactList();
            }
        });
        builder.show();
        Log.i("_id", String.valueOf(_id));
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent=new Intent();
//        intent.putExtra("name", name);
//        intent.putExtra("phone", phone);
        //从此activity传到另一Activity
        intent.setClass(healthCardActivity.this, MainActivity.class);
        //启动另一个Activity
        healthCardActivity.this.startActivity(intent);
        healthCardActivity.this.finish();
        db.close();
    }

}
