package com.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.testBlueTooth.DataAnalysis;
import com.testBlueTooth.R;

/**
 * Created by John on 2016/8/25.
 */
public class TemperatureFragment extends Fragment {

	TextView textView_tem,textView_tip;
	ImageView imageView_tem;
	static double wendu;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_temperature,container,false);
		textView_tem=(TextView)view.findViewById(R.id.much_tem);
		imageView_tem=(ImageView)view.findViewById(R.id.tem_pic);
		textView_tip=(TextView)view.findViewById(R.id.tip_tem);


		//接受从Activity中传过来的值：
		Bundle bundle=getArguments();
		if(bundle!=null) {
			String s = bundle.getString("wendu");
            textView_tem.setText(s);
			wendu = Double.parseDouble(s);
		}
		if (wendu<10.0){
			imageView_tem.setImageResource(R.drawable.tem1);
			textView_tip.setText("哎呀~~好冻啊！小要提醒您现在容易冻伤，出现冻疮，要记得穿棉衣保暖喔~~！");
		}
		else if(wendu>11&&wendu<=18){
			imageView_tem.setImageResource(R.drawable.tem2);
			textView_tip.setText("凉风习习~对比起高温的天气，的确很舒服~可小要有个温馨提醒哟：凉风一起，容易感冒，要记得多穿几件衣服保暖哟~");
		}else if (wendu>18&&wendu<=25){
			imageView_tem.setImageResource(R.drawable.tem3);
			textView_tip.setText("不冷不热，温度刚好");
		}else if (wendu>25&&wendu<=31){
			imageView_tem.setImageResource(R.drawable.tem4);
			textView_tip.setText("人体感知的最佳舒适度，身体内的毛细血管舒张平衡，感觉非常舒适");
		}else if (wendu>31&&wendu<=36){
			imageView_tem.setImageResource(R.drawable.tem5);
			textView_tip.setText("人体会开始发热、情绪产生波动。此时您需要采取一些措施，调节体温哟~");
		}else if (wendu>=37){
			imageView_tem.setImageResource(R.drawable.tem6);
			textView_tip.setText("此时温度高于人体温度，小要提醒您要做好防暑措施，条件允许的话，要做好一些应急的降温措施，千万别中暑哟~~");
		}
		return view;
	}
}
