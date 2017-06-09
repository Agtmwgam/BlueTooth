package com.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.testBlueTooth.R;

/**
 * Created by John on 2016/8/25.
 */
public class NoiseFragment extends Fragment{
	TextView textView_noi,textView_tip;
	ImageView imageView_noi;
	static double noi;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_noise,container,false);
		textView_noi=(TextView)view.findViewById(R.id.much_noi);
		imageView_noi=(ImageView)view.findViewById(R.id.noi_pic);
		textView_tip=(TextView)view.findViewById(R.id.tip_noi);

		//接受从Activity中传过来的值：
		Bundle bundle=getArguments();
		if(bundle!=null) {
			String s = bundle.getString("zaoyin");
			textView_noi.setText(s);
			noi = Double.parseDouble(s);
		}
		if (noi<25){
			imageView_noi.setImageResource(R.drawable.noi1);
			textView_tip.setText("刚好能感受到声音~");
		}
		else if(noi>25&&noi<=30){
			imageView_noi.setImageResource(R.drawable.noi2);
			textView_tip.setText("安安静静的，这环境不错~~赞赞哒！");
		}else if (noi>40&&noi<=60){
			imageView_noi.setImageResource(R.drawable.noi3);
			textView_tip.setText("喃喃细语的声音哟~~小要嗅到了作为单身狗的伤害");
		}else if (noi>60&&noi<=200){
			imageView_noi.setImageResource(R.drawable.noi4);
			textView_tip.setText("这种交谈最舒服了，无吵无闹，最正常，嘻嘻~~");
		}else if (noi>200){
			imageView_noi.setImageResource(R.drawable.noi5);
			textView_tip.setText("吵吵闹闹的~~~！");
		}
		return view;
	}
}