package learn.li.login2test.locationUtils;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import learn.li.login2test.OkHttpUtil;
import learn.li.login2test.R;
import learn.li.login2test.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link locationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link locationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class locationFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    private TextView tvResult;
    private Button btLocation;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String locationUrl = "http://192.168.50.199:8080/Mojito/user/location.do";

    public locationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment locationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static locationFragment newInstance(String param1, String param2) {
        locationFragment fragment = new locationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        tvResult = (TextView) view.findViewById(R.id.tv_result);
        btLocation = (Button) view.findViewById(R.id.bt_location);

        btLocation.setOnClickListener(this);//点击事件

        initLocation();

        return view;
    }

    /**
     * 初始化定位
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getActivity());
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
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(3000);//设置定位间隔。默认为3秒
        mOption.setNeedAddress(true);//设置是否返回逆地理地址信息。默认是ture
        mOption.setOnceLocation(false);//设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
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
                String result = Utils.getLocationStr(loc);
                tvResult.setText(result);
            } else {
                tvResult.setText("定位失败，loc is null");
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_location) {
            if (btLocation.getText().equals(
                    getResources().getString(R.string.startLocation))) {
                btLocation.setText(getResources().getString(
                        R.string.stopLocation));
                tvResult.setText("正在定位...");
                startLocation();
            } else {
                btLocation.setText(getResources().getString(
                        R.string.startLocation));
                stopLocation();
                tvResult.setText("定位停止");
            }
            //发送经纬度
            AMapLocation location = locationClient.getLastKnownLocation();
            String longitude = String.valueOf(location.getLongitude());
            String latitude = String.valueOf(location.getLatitude());
            OkHttpUtil.postLocationParams(locationUrl,longitude,latitude);
            tvResult.append("\n"+longitude+";"+latitude);
        }
    }

    /**
     * 开始定位
     */
    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     */
    private void stopLocation(){
        // 停止定位啊
        locationClient.stopLocation();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
