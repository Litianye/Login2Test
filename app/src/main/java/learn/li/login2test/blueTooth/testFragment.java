package learn.li.login2test.blueTooth;

import android.bluetooth.BluetoothAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import learn.li.login2test.R;

public class testFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Button btnAutoPair, btnAlarm;
    private TextView txBluetooth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        btnAutoPair = (Button) view.findViewById(R.id.btnPair);
        btnAlarm = (Button) view.findViewById(R.id.btnAlarm);
        btnAutoPair.setOnClickListener(this);
        btnAlarm.setOnClickListener(this);
        return view;
    }

    public void onClick(View arg0){
        if (arg0.getId() == R.id.btnPair){
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();//异步的，不会等待结果，直接返回。
                Log.i("开启","OK");
            }else{
                bluetoothAdapter.startDiscovery();
                Log.i("搜索","OK");
            }
        }else if(arg0.getId() == R.id.btnAlarm) {
            Toast.makeText(getActivity(), "已发送", Toast.LENGTH_SHORT).show();
        }
    }

}
