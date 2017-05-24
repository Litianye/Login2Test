package learn.li.login2test.EEG;

import android.bluetooth.BluetoothAdapter;
import android.media.SoundPool;
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
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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

    private Button btnState;
    private MainActivity mainActivity;
    private int lastHeartRate;
    private String checkNum;

    private boolean isGood = false;
    private RoundIndicatorView roundIndicatorView;
    private String data = "";
    private String dataUrl = "http://debug.programmox.com:8388/Mojito/user/dataBlueTooth.do";
    private String heartUrl = "http://debug.programmox.com:8388/Mojito/user/updateHeartRate.do";

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
        btnState = (Button) view.findViewById(R.id.btnState);

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
//                    tvData.append("模块已识别。\n");
                    Log.i("EEG", "模块已识别。\n");
                    btnState.setText("模块已识别");
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
                            Log.i("EEG", "连接中...\n");
                            btnState.setText("连接中...");
                            break;
                        case TGDevice.STATE_CONNECTED:
                            Log.i("EEG", "已连接\n" );
                            btnState.setText("已连接");
                            tgDevice.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                            Log.i("EEG", "无法连接到已配对的设备，请重启设备后再次尝试。\n" );
                            Log.i("EEG", "蓝牙设备须先配对。\n" );
                            btnState.setText("无法连接");
                            break;
                        case TGDevice.STATE_ERR_NO_DEVICE:
                            Log.i("EEG", "无已配对蓝牙设备，请配对后再次尝试。\n");
                            btnState.setText("无法连接");
                            break;
                        case TGDevice.STATE_ERR_BT_OFF:
                            Log.i("EEG", "蓝牙未打开，请开启蓝牙后尝试。" );
                            btnState.setText("无法连接");
                            break;
                        case TGDevice.STATE_DISCONNECTED:
                            Log.i("EEG", "连接断开\n" );
                            btnState.setText("连接断开");
                    }/* end switch on msg.arg1 */
                    break;

                case TGDevice.MSG_POOR_SIGNAL:
                    /* display signal quality when there is a change of state, or every 30 reports (seconds) */
                    if (subjectContactQuality_cnt >=30 || message.arg1 != subjectContactQuality_last){
                        if (message.arg1==0){
                            Log.i("EEG", message.arg1+"\n" );
                            isGood = false;
                            Log.i("isGood", String.valueOf(isGood));
                        }else {
                            Log.i("EEG", message.arg1+"\n" );
                            isGood = true;
                            Log.i("isGood", String.valueOf(isGood));
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
                        OkHttpUtil.dataPost(dataUrl, data);
                        data="";
                        Log.i("length", data.length()+"");
                        OkHttpUtil.heartRatePost(heartUrl, String.valueOf(lastHeartRate));
                        Log.i("view", "refresh"+lastHeartRate);
                        mainActivity.sendLocation();
                        btnState.setText(setState(OkHttpUtil.getDataStr()));
                    }
                    break;

                case TGDevice.MSG_BLINK:
//                    tvData.append( "Blink: " + message.arg1 + "\n" );
                    Log.i("Blink:", message.arg1+"\n" );
                    break;

                case TGDevice.MSG_HEART_RATE:
                    lastHeartRate = message.arg1;
                    roundIndicatorView.setCurrentNumAnim(lastHeartRate);
                    break;

                default:
                    break;
            }/* end switch on msg.what */

        }/* end handleMessage() */
    };/* end Handler */

    @Override
    public void onDetach() {
        super.onDetach();
        tgDevice.close();
    }

    private String setState(String dateResponse){
        Log.e("dataStr", dateResponse);
        try {
            JSONTokener jsonInfo = new JSONTokener(dateResponse);
            Log.i("json", jsonInfo.toString());
            JSONObject info = (JSONObject) jsonInfo.nextValue();
            checkNum = info.getString("error");
        } catch (JSONException e){
            e.printStackTrace();
        }
        switch (checkNum){
            case "0":
                return "身体状况正常";
            case "1":
                return "身体状况异常";
            case "2":
                mainActivity.soundWarn();
                return "危险！";
            default:
                return "身体状况正常";
        }
//        switch (dateResponse){
//            case "{\"error\":\"0\"}":
//                return "身体状况正常";
//            case "{\"error\":\"1\"}":
//                return "身体状况异常";
//            case "{\"error\":\"2\"}":
//                soundPool.load(mainActivity,R.raw.ring,1);
//                soundPool.play(1,1, 1, 0, 0, 1);
//                return "危险！";
//            default:
//                return "身体状况正常";
//        }
    }
}
