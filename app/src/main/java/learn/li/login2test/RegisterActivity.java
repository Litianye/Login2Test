package learn.li.login2test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import learn.li.login2test.dataBase.DataBase;
import learn.li.login2test.dataBase.DataBaseUtil;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private String registerUrl = "http://192.168.0.176:8080/Mojito/user/register.do";

    private EditText tvRealName, tvPhone, tvPassword, tvEmail ;
    private Button btRegister;
    private String realName, phone, password, email;
    private String rCheckNum, custodyCode, message;

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
                    String registerInfo = null;
                    try {
                        registerInfo = OkHttpUtil.RegisterPostParams(registerUrl,phone, password, realName, email);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONTokener jsonInfo = new JSONTokener(registerInfo);
                        Log.i("registerInfo", registerInfo);
                        JSONObject info = (JSONObject) jsonInfo.nextValue();
                        if (info.getString("custodyCode") != null){
                            custodyCode = info.getString("custodyCode");
                        }else if (info.getString("message") != null){
                            message = info.getString("message");
                        }
                        rCheckNum = info.getString("error");
                        Log.i("rCheckNum", rCheckNum);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                    switch (rCheckNum){
                        case "0":
                            DataBaseUtil.insertInSqlToAccount(DataBase.TABLE_NAME_ACCOUNT,
                                    this, realName, phone, password, email, custodyCode);
                            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent();
                            //从此activity传到另一Activity
                            intent.setClass(RegisterActivity.this, LoginActivity.class);
                            //启动另一个Activity
                            RegisterActivity.this.startActivity(intent);
                            RegisterActivity.this.finish();
                            break;
                        case "1":
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            break;
                        case "2":
                            Toast.makeText(this, "服务器内部问题", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }

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
