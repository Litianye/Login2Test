package learn.li.login2test;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import learn.li.login2test.dataBase.DataBase;
import learn.li.login2test.dataBase.DataBaseUtil;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private String register = "http://192.168.50.199:8080/Mojito/user/register.do";

    private EditText tvRealName, tvPhone, tvPassword, tvEmail ;
    private Button btRegister;
    private String realName, phone, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

    }

    private void initViews () {
        tvRealName = (EditText) findViewById(R.id.register_name);
        tvPhone = (EditText) findViewById(R.id.register_phone);
        tvPassword = (EditText) findViewById(R.id.register_password);
        tvEmail = (EditText) findViewById(R.id.register_email);

        btRegister = (Button) findViewById(R.id.btn_register);
        btRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register:
                if (isCompleted()){
                    DataBaseUtil.insertInSqlToAccount(DataBase.TABLE_NAME_ACCOUNT,
                            this, realName, phone, password, email);
                    Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();
                    //从此activity传到另一Activity
                    intent.setClass(RegisterActivity.this, LoginActivity.class);
                    //启动另一个Activity
                    RegisterActivity.this.startActivity(intent);
                    RegisterActivity.this.finish();
                }
                break;
//            case R.id.register_birthday:
//                new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        tvBirthday.setText(String.format("%d-%d-%d",year,monthOfYear+1,dayOfMonth));
//                    }
//                },2000,1,2).show();
            default:
                break;
        }
    }

    private boolean isCompleted() {
        realName = tvRealName.getText().toString();
        phone = tvPhone.getText().toString();
        password = tvPassword.getText().toString();
        email = tvEmail .getText().toString();

        if ((realName.length()>1)&&(phone.length()==11)&&(password.length()>2)&&email.contains("@")){
            return true;
        }else {
            Toast.makeText(this, "请填写有效信息", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
