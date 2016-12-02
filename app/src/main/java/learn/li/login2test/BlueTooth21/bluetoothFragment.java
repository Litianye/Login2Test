package learn.li.login2test.BlueTooth21;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import learn.li.login2test.OkHttpUtil;
import learn.li.login2test.R;

public class bluetoothFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Button btnScan;
    private TextView tvBluetoothData;
    private ListView lvDevices;
    private ArrayAdapter<String> adtDevices;
    private List<String> lstDevices = new ArrayList<String>();
    private BluetoothAdapter btAdapt;
    public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private BluetoothSocket btSocket = null;
    private Boolean bConnect = false;
    private String strAddress;
    private CharSequence strName="Dess";

    private String testUrl = "http://192.168.50.197:8082/Mojito/user/dataBlueTooth.do";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        tvBluetoothData = (TextView) view.findViewById(R.id.tvBluetoothData);
        lvDevices = (ListView) view.findViewById(R.id.lvDevices);
        adtDevices = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, lstDevices);
        lvDevices.setAdapter(adtDevices);
        lvDevices.setOnItemClickListener(new ItemClickEvent());
        btnScan = (Button)view.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new ClickEvent());

        btAdapt = BluetoothAdapter.getDefaultAdapter();

        addPairedDevice();

        return view;
    }

    private void sendBroadcast(){
        // 注册Receiver来获取蓝牙设备相关的结果
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND); // 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(searchDevices, intent);
    }

    class ItemClickEvent implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (btAdapt.getState() != BluetoothAdapter.STATE_ON) {// 如果蓝牙还没开启
                Toast.makeText(getActivity(), "请开启蓝牙", Toast.LENGTH_SHORT).show();
                return;
            }
            if (btAdapt.isDiscovering()){
                btAdapt.cancelDiscovery();
            }
            String str = lstDevices.get(position);
            if (str==null || str.equals("")){
                return;
            }
            String[] values = str.split("\\|");
            strName = values[0];
            strAddress = values[1];
            Log.i("Address", values[1]);
            IntentFilter intent = new IntentFilter();
            intent.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            getActivity().registerReceiver(connectDevices, intent);

            mHandler.sendEmptyMessageDelayed(Common.MESSAGE_CONNECT, 1000);
        }
    }

    // 按钮事件
    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sendBroadcast();
            if (v == btnScan){// 搜索设备
                if (btAdapt == null) {
                    Toast.makeText(getActivity(), "您的机器上没有发现蓝牙适配器，本程序将不能运行!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (btAdapt.getState() != BluetoothAdapter.STATE_ON) {// 如果蓝牙还没开启
                    Toast.makeText(getActivity(), "请先开启蓝牙", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!btAdapt.isDiscovering()) {
                    lstDevices.clear();
                    addPairedDevice();
                    btnScan.setClickable(false);
                    btAdapt.startDiscovery();
                }

            }
        }
    }

    // 增加配对设备
    private void addPairedDevice() {
        Set<BluetoothDevice> pairedDevices = btAdapt.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().contains(strName)){
                    String str = device.getName() + "|" + device.getAddress();
                    Log.i("Device.Name:", device.getName()+device.getName().contains(strName));
                    lstDevices.add(str);
                    adtDevices.notifyDataSetChanged();
                }
            }
        }
    }

    // Hander
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Common.MESSAGE_CONNECT:
                    new Thread(new Runnable() {
                        public void run() {
                            InputStream tmpIn;
                            OutputStream tmpOut;
                            try {

                                UUID uuid = UUID.fromString(SPP_UUID);
                                BluetoothDevice btDev = btAdapt
                                        .getRemoteDevice(strAddress);
                                btSocket = btDev.createInsecureRfcommSocketToServiceRecord(uuid);
                                //.createRfcommSocketToServiceRecord(uuid);
                                btSocket.connect();
                                tmpIn = btSocket.getInputStream();
                                tmpOut = btSocket.getOutputStream();
                            } catch (Exception e) {
                                Log.d(Common.TAG, "Error connected to: "
                                        + strAddress);
                                bConnect = false;
                                mmInStream = null;
                                mmOutStream = null;
                                btSocket = null;
                                e.printStackTrace();
                                mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_LOST);
                                return;
                            }
                            mmInStream = tmpIn;
                            mmOutStream = tmpOut;
                            mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_SUCCEED);
                        }

                    }).start();
                    break;
                case Common.MESSAGE_CONNECT_SUCCEED:
                    addLog("连接成功");
                    bConnect = true;
                    new Thread(new Runnable() {
                        public void run() {
                            byte[] bufRecv = new byte[102400+1024];
                            int nRecv = 0;
                            while (bConnect) {
                                try {
                                    nRecv = mmInStream.read(bufRecv);
                                    while (nRecv < 102400) {
                                        Thread.sleep(100);
                                        Log.i("nRecv", nRecv+"");
                                        nRecv = nRecv+mmInStream.read(bufRecv);
                                    }

                                    byte[] nPacket = new byte[nRecv+1024];
                                    System.arraycopy(bufRecv, 0, nPacket, 0, nRecv);
                                    OkHttpUtil.dataPostTest(testUrl,nPacket);
                                    mHandler.obtainMessage(Common.MESSAGE_RECV,
                                            nRecv, -1, nPacket).sendToTarget();
                                    Thread.sleep(100);
                                } catch (Exception e) {
                                    Log.e(Common.TAG, "Recv thread:" + e.getMessage());
                                    mHandler.sendEmptyMessage(Common.MESSAGE_EXCEPTION_RECV);
                                    break;
                                }
                            }
                            Log.e(Common.TAG, "Exit while");
                        }
                    }).start();
                    break;
                case Common.MESSAGE_EXCEPTION_RECV:
                case Common.MESSAGE_CONNECT_LOST:
                    addLog("连接异常，请退出本界面后重新连接");
                    try {
                        if (mmInStream != null)
                            mmInStream.close();
                        if (mmOutStream != null)
                            mmOutStream.close();
                        if (btSocket != null)
                            btSocket.close();
                    } catch (IOException e) {
                        Log.e(Common.TAG, "Close Error");
                        e.printStackTrace();
                    } finally {
                        mmInStream = null;
                        mmOutStream = null;
                        btSocket = null;
                        bConnect = false;
                    }
                    break;
                case Common.MESSAGE_WRITE:

                    break;
                case Common.MESSAGE_READ:

                    break;
                case Common.MESSAGE_RECV:
                    byte[] bBuf = (byte[]) msg.obj;
                    addLog("接收数据: " + bytesToString(bBuf, msg.arg1));
                    break;
                case Common.MESSAGE_TOAST:
                    Toast.makeText(getActivity(),
                            msg.getData().getString(Common.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void addLog(String str) {
        tvBluetoothData.append(str + "\n");
    }

    public static String bytesToString(byte[] b, int length) {
        StringBuffer result = new StringBuffer("");
        for (int i = 0; i < length; i++) {
            result.append((char) (b[i]));
        }

        return result.toString();
    }

    private BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) { //found device
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String str = device.getName() + "|" + device.getAddress();

                if (lstDevices.indexOf(str) == -1)// 防止重复添加
                    lstDevices.add(str); // 获取设备名称和mac地址
                if (lstDevices.indexOf("null|" + device.getAddress()) != -1)
                    lstDevices.set(lstDevices.indexOf("null|" + device.getAddress()), str);
                adtDevices.notifyDataSetChanged();

            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                btnScan.setText("正在扫描");
                btnScan.setTextColor(Color.RED);
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                btnScan.setText("扫描设备");
                btnScan.setTextColor(Color.BLACK);
                Toast.makeText(getActivity(), "扫描完成，点击列表中的设备来尝试连接", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private BroadcastReceiver connectDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(Common.TAG, "Receiver:" + action);
            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {

            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
