package learn.li.login2test.RoundIndicator;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import learn.li.login2test.R;

public class locationFragment extends Fragment implements View.OnClickListener{
    private EditText editText;
    private Button btRound;
    private RoundIndicatorView roundIndicatorView;

    public locationFragment() {
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
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        editText = (EditText) view.findViewById(R.id.etNum);
        btRound = (Button) view.findViewById(R.id.btnRound);
        roundIndicatorView = (RoundIndicatorView) view.findViewById(R.id.rdIndicator);

        btRound.setOnClickListener(this);//点击事件

        return view;
    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnRound){
            int a = Integer.valueOf(editText.getText().toString());
            roundIndicatorView.setCurrentNumAnim(a);
        }
    }
}
