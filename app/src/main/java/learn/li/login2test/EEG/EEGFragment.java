package learn.li.login2test.EEG;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import learn.li.login2test.OkHttpUtil;
import learn.li.login2test.R;
import learn.li.login2test.RoundIndicator.RoundIndicatorView;
import learn.li.login2test.UIPackage.MainActivity;

public class EEGFragment extends Fragment {
    private BluetoothAdapter bluetoothAdapter;
    private TGDevice tgDevice;
    private int subjectContactQuality_last, subjectContactQuality_cnt;
    private final boolean rawEnable = true;

    private TextView tvData;
    private Button btnConnect;
    private MainActivity mainActivity;

    private boolean isGood = false;
    private RoundIndicatorView roundIndicatorView;
    private String data = "";
    private String testUrl = "http://192.168.50.183:8082/Mojito/user/dataBlueTooth.do";

    public EEGFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eeg, container, false);
        tvData = (TextView) view.findViewById(R.id.tvData);
        tvData.setText("");
        tvData.append("安卓版本:"+ Integer.valueOf(Build.VERSION.SDK)+"\n");

        subjectContactQuality_last = -1;/* start with impossible value */
        subjectContactQuality_cnt = 200; /* start over the limit, so it gets reported the 1st time */

        //check if Bluetooth is available
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            Toast.makeText(getActivity(),"蓝牙不可用", Toast.LENGTH_SHORT).show();
            return view;
        }else {
            tgDevice = new TGDevice(bluetoothAdapter, handler);
        }

        tvData.append("NeuroSky: "+TGDevice.version +" "+ TGDevice.build_title +"\n");

        roundIndicatorView = (RoundIndicatorView) view.findViewById(R.id.rdIndicator);
        mainActivity = (MainActivity) getActivity();

        tgDevice.connect(true);
        return view;
    }
    //end creat

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case TGDevice.MSG_MODEL_IDENTIFIED:
            		 /* now there is something connected,
            		 * time to set the configurations we need
            		 */
                    tvData.append("模块已识别。\n");
                    tgDevice.setBlinkDetectionEnabled(true);
                    tgDevice.setTaskDifficultyRunContinuous(true);
                    tgDevice.setTaskDifficultyEnable(true);
                    tgDevice.setTaskFamiliarityRunContinuous(true);
                    tgDevice.setTaskFamiliarityEnable(true);
                    tgDevice.setRespirationRateEnable(true);
                    // not allowed on EEG hardware, here to show the override message
                    break;

                case TGDevice.MSG_STATE_CHANGE:
                    switch (message.arg1){
                        case TGDevice.STATE_IDLE:
                            Log.i("MSG_STATE_CHANGE", message.arg1+"");
                            break;
                        case TGDevice.STATE_CONNECTING:
                            tvData.append( "连接中...\n" );
                            break;
                        case TGDevice.STATE_CONNECTED:
                            tvData.append( "已连接\n" );
                            tgDevice.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                            tvData.append( "无法连接到已配对的设备，请重启设备后再次尝试。\n" );
                            tvData.append( "蓝牙设备须先配对。\n" );
                            break;
                        case TGDevice.STATE_ERR_NO_DEVICE:
                            tvData.append( "无已配对蓝牙设备，请配对后再次尝试。\n" );
                            break;
                        case TGDevice.STATE_ERR_BT_OFF:
                            tvData.append( "蓝牙未打开，请开启蓝牙后尝试。" );
                            break;
                        case TGDevice.STATE_DISCONNECTED:
                            tvData.append( "连接断开\n" );
                    }/* end switch on msg.arg1 */
                    break;

                case TGDevice.MSG_POOR_SIGNAL:
                    /* display signal quality when there is a change of state, or every 30 reports (seconds) */
                    if (subjectContactQuality_cnt >=30 || message.arg1 != subjectContactQuality_last){
                        if (message.arg1==0){
                            tvData.append( "信号质量 佳: " + message.arg1 + "\n" );
                            isGood = false;
                        }else {
                            tvData.append( "信号质量 差: " + message.arg1 + "\n" );
                            isGood = true;
                        }

                        subjectContactQuality_cnt=0;
                        subjectContactQuality_last=message.arg1;
                    }else {
                        subjectContactQuality_cnt++;
                    }
                    break;

                case TGDevice.MSG_RAW_DATA:
                    if (isGood){
                        data = data+System.currentTimeMillis()+","+message.arg1+"\n";
                    }
                    /* Handle raw EEG/EKG data here */
                    if (data.length()>102400){
//                      byte[] tryData = data.getBytes();
                        OkHttpUtil.dataPost(testUrl, data);
//                      OkHttpUtil.dataPostTest(testUrl, tryData);
                        data="";
                        Log.i("length", data.length()+"");
                        mainActivity.sendLocation();
                    }
                    break;

                case TGDevice.MSG_BLINK:
                    tvData.append( "Blink: " + message.arg1 + "\n" );
                    break;

                case TGDevice.MSG_HEART_RATE:
                    roundIndicatorView.setCurrentNumAnim(message.arg1);
                    Log.i("view", "refresh"+message.arg1);
                    break;

                default:
                    break;
            }/* end switch on msg.what */

        }/* end handleMessage() */
    };/* end Handler */

    private byte[] bytes = {1,1,1,1,1,1,};
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            tgDevice.close();
            return true;
        }
        return getActivity().onKeyDown(keyCode, event);
    }

}
