package learn.li.login2test.UIPackage;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import learn.li.login2test.EEG.EEGFragment;
import learn.li.login2test.LoginActivity;
import learn.li.login2test.OkHttpUtil;
import learn.li.login2test.R;
import learn.li.login2test.dataBase.DataBase;
import learn.li.login2test.dataBase.DataBaseUtil;
import learn.li.login2test.settingPackage.settingFragment;
import learn.li.login2test.infoFragment.healthCardActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static String name="0";
//    private locationFragment locationFragment;
    private settingFragment settingFragment;
    private EEGFragment eegFragment;

    private FragmentManager fragmentManager;
    private String[] pieces;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private String locUrl = "http://debug.programmox.com:8388/Mojito/user/updateLocation.do";

    private SoundPool soundPool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保存屏幕常亮

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView mAccount = (TextView) headerView.findViewById(R.id.nav_tv_header_username);
        TextView mPhone = (TextView) headerView.findViewById(R.id.nav_tv_header_userInfo);
        String info = DataBaseUtil.readPhoneAndNameInSql(DataBase.TABLE_NAME_ACCOUNT, this);
        for (int i=0; i<3; i++){
            pieces = info.split(";");
        }
        mAccount.setText(pieces[0]);
        mPhone.setText(pieces[1]);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "您的监护人绑定ID是"+pieces[2], Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //  初始化界面管理器
        fragmentManager = getFragmentManager();
        //  初始化界面
        setTabSelection(0);
        soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        initLocation();
    }

    public void sendLocation(){
        startLocation();
    }

    /**
     * 初始化定位
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(15000);//可选，设置定位间隔。默认为15秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                String longitude = loc.getLongitude()+"";//精度
                String latitude = loc.getLatitude()+"";//纬度
                OkHttpUtil.postLocParams(locUrl, longitude, latitude);
            } else {
                Log.i("定位失败","loc is null");
            }
        }
    };

    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            stopLocation();
            destroyLocation();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "已注销", Toast.LENGTH_SHORT).show();
            DataBaseUtil.deleteInSql(DataBase.TABLE_NAME_ACCOUNT, this);
            DataBaseUtil.deleteInSql(DataBase.TABLE_NAME_LISTITEM, this);
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(i);
            eegFragment.onDetach();
            MainActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            setTabSelection(REQUEST_CODE_FIRST);
            // Handle the camera action
        }
//        else if (id == R.id.nav_gallery) {
//            setTabSelection(REQUEST_CODE_LOCATION);
//        }
        else if (id == R.id.nav_slideshow) {

            Intent intent=new Intent();
            String info = DataBaseUtil.readFirstInSql(DataBase.TABLE_NAME_ACCOUNT, this);
            for (int i=0; i<3; i++) {
                pieces = info.split(":");
            }
            intent.putExtra("name", pieces[0]);
            //从此activity传到另一Activity
            intent.setClass(MainActivity.this, healthCardActivity.class);
            //启动另一个Activity
            MainActivity.this.startActivity(intent);
            MainActivity.this.finish();
        } else if (id == R.id.nav_manage) {
            setTabSelection(REQUEST_CODE_SETTING);
        } else if (id == R.id.nav_share) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static final int REQUEST_CODE_FIRST = 0;
    private static final int REQUEST_CODE_LOCATION = 1;
    private static final int REQUEST_CODE_SETTING = 3;

    private void setTabSelection(int index) {
        //  页面切换的方法
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //  隐藏fragment，防止多个页面重叠的情况。
        hideFragments(transaction);

        switch (index) {
            //  设置选中时的图片颜色及字体颜色，若对应的fragment为空则创建，将此时的界面内容交给对应的fragment。若不为空，则将它显示出来。
            case REQUEST_CODE_FIRST:
                if (eegFragment == null){
                    eegFragment = new EEGFragment();
                    transaction.add(R.id.id_content, eegFragment);
                } else {
                    transaction.show(eegFragment);
                }
                break;

//            case REQUEST_CODE_LOCATION:
//                if (locationFragment == null){
//                    locationFragment = new locationFragment();
//                    transaction.add(R.id.id_content, locationFragment);
//                } else {
//                    transaction.show(locationFragment);
//                }
//                break;

            case REQUEST_CODE_SETTING:
                if (settingFragment == null){
                    settingFragment = new settingFragment();
                    transaction.add(R.id.id_content, settingFragment);
                } else {
                    transaction.show(settingFragment);
                }
                break;
        }
        transaction.commit();
    }

    //  将所有的fragment均设为隐藏状态。便于下一步的选择并显示。
    private void hideFragments(FragmentTransaction transaction){
        if (eegFragment != null){
            transaction.hide(eegFragment);
        }

//        if (locationFragment != null){
//            transaction.hide(locationFragment);
//        }

        if (settingFragment != null){
            transaction.hide(
                    settingFragment);
        }
    }

    public void soundWarn(){
        soundPool.load(this,R.raw.ring,1);
        soundPool.play(1,1, 1, 0, 0, 1);
    }

}
