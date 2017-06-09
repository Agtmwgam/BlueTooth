package com.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.testBlueTooth.R;

/**
 * Created by John on 2016/8/25.
 */
public class RayFragment extends Fragment {
	 TextView textView_ray,textView_tip;
    ImageView imageView_ray;
	static double ray;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_ray,container,false);

        textView_ray=(TextView)view.findViewById(R.id.much_ray);
        imageView_ray=(ImageView)view.findViewById(R.id.ray_pic);
		textView_tip=(TextView)view.findViewById(R.id.tip_ray);
		/*textView_ray.setText("ray");*/

		//接受从Activity中传过来的值：
		Bundle bundle=getArguments();
		if(bundle!=null) {
			String s = bundle.getString("guangzhao");
			textView_ray.setText(s);
			ray = Double.parseDouble(s);
		}
		if (ray<2){
            imageView_ray.setImageResource(R.drawable.ray1);
            textView_tip.setText("小要听说这个时候的紫外线等级为1，紫外线照射强度最弱，对人体而言是安全的，是不需要采取防护措施的喔~~！");
		}
		else if(ray>3&&ray<=4){
			imageView_ray.setImageResource(R.drawable.ray2);
            textView_tip.setText("小要查了一下，现在的紫外线指数为2，紫外线照射强度弱，对人体而言是处于正常的紫外线之下，不过小要还是要提醒您外出的话可是要戴防护帽或太阳镜的喔~~");
		}else if (ray>5&&ray<=6){
			imageView_ray.setImageResource(R.drawable.ray3);
            textView_tip.setText("现在的紫外线指数为3，紫外线照射强度中等，此时需要注意，外出除了戴防护帽和太阳镜以外，应涂擦SPF指数不低于15的防晒霜~~");
		}else if (ray>7&&ray<=9){
			imageView_ray.setImageResource(R.drawable.ray4);
            textView_tip.setText("紫外线指数为4，紫外线照射强度强，对人体影响较强，在白天10：00——16：00避免外出活动，外出时尽可能在遮荫处,不然就要晒伤了~~！！~");
		}else if (ray>10){
			imageView_ray.setImageResource(R.drawable.ray5);
            textView_tip.setText("这个时候的紫外线指数为5，紫外线照射强度很强，容易紫外线中毒，小要强烈建议您不要外出，如果迫不得已必须外出时，一定要采取一定的防护措施！！！~！");
		}
		return view;
	}
}
