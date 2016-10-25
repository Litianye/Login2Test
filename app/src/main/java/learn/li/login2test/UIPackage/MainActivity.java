package learn.li.login2test.UIPackage;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import android.widget.TextView;

import learn.li.login2test.R;
import learn.li.login2test.blueTooth.testFragment;
import learn.li.login2test.locationUtils.locationFragment;
import learn.li.login2test.settingPackage.settingFragment;
import learn.li.login2test.infoFragment.healthCardActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static String name="0", phone="0";
    private locationFragment locationFragment;
    private settingFragment settingFragment;
    private learn.li.login2test.blueTooth.testFragment testFragment;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent=getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        Log.i("name", name);
        Log.i("phone", phone);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "您的监护人绑定ID是59670160001", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView mAccount = (TextView) headerView.findViewById(R.id.nav_tv_header_username);
        TextView mEmail = (TextView) headerView.findViewById(R.id.nav_tv_header_userInfo);
        mAccount.setText(name);
        mEmail.setText(phone);

        //  初始化界面管理器
        fragmentManager = getFragmentManager();
        //  初始化界面
        setTabSelection(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        } else if (id == R.id.nav_gallery) {
            setTabSelection(REQUEST_CODE_LOCATION);
        } else if (id == R.id.nav_slideshow) {

            Intent intent=new Intent();

            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
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
                if (testFragment == null){
                    testFragment = new testFragment();
                    transaction.add(R.id.id_content, testFragment);
                } else {
                    transaction.show(testFragment);
                }
                break;

            case REQUEST_CODE_LOCATION:
                if (locationFragment == null){
                    locationFragment = new locationFragment();
                    transaction.add(R.id.id_content, locationFragment);
                } else {
                    transaction.show(locationFragment);
                }
                break;

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
        if (testFragment != null){
            transaction.hide(testFragment);
        }

        if (locationFragment != null){
            transaction.hide(locationFragment);
        }

        if (settingFragment != null){
            transaction.hide(
                    settingFragment);
        }
    }

}
