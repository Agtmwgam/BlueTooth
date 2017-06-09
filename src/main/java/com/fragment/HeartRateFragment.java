package com.fragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.testBlueTooth.R;


public class HeartRateFragment extends Fragment{
    TextView   textView_hea,textView_tip;
    ImageView imageView_hea;
    static double hea;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heartrate, container, false);
        textView_hea = (TextView)view.findViewById(R.id.much_hea);
        textView_tip = (TextView)view.findViewById(R.id.tip_hea);
        imageView_hea = (ImageView)view.findViewById(R.id.hea_pic);
        //接受从Activity中传过来的值：
        Bundle bundle=getArguments();
        if(bundle!=null){
            String s=bundle.getString("xinlv");
            textView_hea.setText(s);
            hea=Double.parseDouble(s);
        }
        if (hea<60){
            imageView_hea.setImageResource(R.drawable.hea1);
            textView_tip.setText("心率过缓，小要提醒您要注意休息哟~~如若觉得身体不适要尽快就医哟~!毕竟身体是最重要的呀~");
        }
        else if(hea>60&&hea<=100){
            imageView_hea.setImageResource(R.drawable.hea2);
            textView_tip.setText("心率正常，小要要恭喜您了呢~您现在心率很正常哟~~！");
        }else if (hea>100){
            imageView_hea.setImageResource(R.drawable.hea3);
            textView_tip.setText("心率过快，小要提醒您要注意休息哟~~如若觉得身体不适要尽快就医哟~!毕竟身体是最重要的呀~");
        }
        return view;
    }
}