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

public class HumidityFragment extends Fragment {
	TextView textView_wet,textView_tip;
	ImageView imageView_wet;
	static double wet;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
View view=inflater.inflate(R.layout.fragment_humidity,container,false);

		textView_wet=(TextView)view.findViewById(R.id.much_wet);
		imageView_wet=(ImageView)view.findViewById(R.id.wet_pic);
		textView_tip=(TextView)view.findViewById(R.id.tip_wet);
		//接受从Activity中传过来的值：
		Bundle bundle=getArguments();
		if(bundle!=null) {
			String s = bundle.getString("shidu");
			textView_wet.setText(s);
			wet = Double.parseDouble(s);
		}
		if (wet<25){
			imageView_wet.setImageResource(R.drawable.wet1);
			textView_tip.setText("空气太干燥了喔~~要记得给身体补点水哟~！");
		}
		else if(wet>25&&wet<=55){
			imageView_wet.setImageResource(R.drawable.wet2);
			textView_tip.setText("咦~还有点干燥哟~~小要提醒您记得适量补充水份喔~");
		}else if (wet>55&&wet<=65){
			imageView_wet.setImageResource(R.drawable.wet3);
			textView_tip.setText("哇~这个湿度好舒服哟~~");
		}else if (wet>65&&wet<=80){
			imageView_wet.setImageResource(R.drawable.wet4);
			textView_tip.setText("开始变潮湿了哟~~要注意在房间放点干燥剂，也可以多利用机器除湿哟~~");
		}else if (wet>80){
			imageView_wet.setImageResource(R.drawable.wet5);
			textView_tip.setText("这样子的环境一定要多做点除湿的措施哟~~太潮湿了，也要及时把布制品拿去除湿，去霉哟~~~！");
		}
		return view;
	}
}
