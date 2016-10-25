package learn.li.login2test.blueTooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {
    private String pin = "1234";//配对密钥
    private String deviceName = "CAR";
    public BluetoothReceiver() {
    }

    //广播接收，当远程蓝牙设备被发现时，会执行回调函数onReceive()
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();//得到action
        Log.e("action 1=", action);
        BluetoothDevice btDevice = null;//创建一个蓝牙设备
        //从Intent中获取设备对象
        btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {//发现设备
            Log.e("发现设备", "["+btDevice.getName()+"]"+":"+btDevice.getAddress());

            if (btDevice.getName() != null && btDevice.getName().contains(deviceName)) {//如果有同名设备，尝试第一个
                if (btDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.e("QWQ", "attempt to bond:"+"["+btDevice.getName()+"]");

                    try {
                        //通过工具类BluetoothUtils，调用creatBond方法
                        BluetoothUtils.createBond(btDevice.getClass(), btDevice);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else {
                Log.e("error", "Is failed");
            }
        }else if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")){
            //再次获得的action，会等于PAIRING_REQUEST
            Log.e("action 2=", action);
            if (btDevice.getName() != null && btDevice.getName().contains(deviceName)){
                Log.e("here", "OKOKOK");
                try {
                    //1.确认配对
                    BluetoothUtils.setPairingConfirmation(btDevice.getClass(), btDevice, true);
                    //2.终止有序广播
                    Log.i("order...", "isOrderedBroadcast:"+isOrderedBroadcast()
                            +",isInitialStickyBroadcast:"+isInitialStickyBroadcast());
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框
                    //3.调用setPin方法进行配对
                    boolean ret = BluetoothUtils.setPin(btDevice.getClass(), btDevice, pin);
                    Toast.makeText(context, "设备连接成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            Log.e("提示","该设备不是目标蓝牙设备");
        }
    }
}
