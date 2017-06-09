package com.testBlueTooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fragment.HeartRateFragment;
import com.fragment.HumidityFragment;
import com.fragment.NoiseFragment;
import com.fragment.RayFragment;
import com.fragment.TemperatureFragment;

/**
 * Created by John on 2016/8/25.
 */
public class DataAnalysis extends Activity implements View.OnClickListener{

	private Button temper,heart,ray,humidity,noise;
	static public String wendu,shidu,zaoyin,guangzhao,xinlv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_activiry);
		temper = (Button) findViewById(R.id.bt_temperature);
		heart = (Button) findViewById(R.id.bt_heart);
		ray = (Button) findViewById(R.id.bt_ray);
		humidity = (Button) findViewById(R.id.bt_humidity);
		noise = (Button) findViewById(R.id.bt_noise);
		//新页面接收数据
		Bundle bundle = this.getIntent().getExtras();
		//接收name值
		wendu = bundle.getString("wendu");
		Log.e("wendu2",wendu);
		shidu = bundle.getString("shidu");
		zaoyin = bundle.getString("zaoyin");
		guangzhao = bundle.getString("guangzhao");
		xinlv = bundle.getString("xinlv");
		initFragment();
	}

	private void initFragment() {

		TemperatureFragment temperatureFragment = new TemperatureFragment();
		Bundle bundle = new Bundle();
		bundle.putString("wendu",wendu);
		Log.e("wendu",wendu);
		temperatureFragment.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.ll_content,temperatureFragment).commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(testBlueTooth.btAdapt.isEnabled())
		{
			testBlueTooth.btAdapt.disable();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		temper.setOnClickListener(this);
		heart.setOnClickListener(this);
		ray.setOnClickListener(this);
		humidity.setOnClickListener(this);
		noise.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.bt_temperature :
				TemperatureFragment temperatureFragment = new TemperatureFragment();
				Bundle bundle_tem = new Bundle();
				bundle_tem.putString("wendu",wendu);
				Log.e("wendu",wendu);
				temperatureFragment.setArguments(bundle_tem);
				getFragmentManager().beginTransaction().replace(R.id.ll_content,temperatureFragment).commit();
				break;
			case R.id.bt_heart :
				HeartRateFragment heartRateFragment = new HeartRateFragment();
				Bundle bundle_hea = new Bundle();
				bundle_hea.putString("xinlv",xinlv);
				Log.e("xinlv",xinlv);
				heartRateFragment.setArguments(bundle_hea);
				getFragmentManager().beginTransaction().replace(R.id.ll_content,heartRateFragment).commit();
				break;
			case R.id.bt_humidity :
				HumidityFragment humidityFragment = new HumidityFragment();
				Bundle bundle_hum = new Bundle();
				bundle_hum.putString("shidu",shidu);
				Log.e("shidu",shidu);
				humidityFragment.setArguments(bundle_hum);
				getFragmentManager().beginTransaction().replace(R.id.ll_content,humidityFragment).commit();
				break;
			case R.id.bt_noise :
				NoiseFragment noiseFragment = new NoiseFragment();
				Bundle bundle_noi = new Bundle();
				bundle_noi.putString("zaoyin",zaoyin);
				Log.e("zaoyin",zaoyin);
				noiseFragment.setArguments(bundle_noi);
				getFragmentManager().beginTransaction().replace(R.id.ll_content,noiseFragment).commit();
				break;
			case R.id.bt_ray :
				RayFragment rayFragment = new RayFragment();
				Bundle bundle_ray = new Bundle();
				bundle_ray.putString("guangzhao",guangzhao);
				Log.e("guangzhao",guangzhao);
				rayFragment.setArguments(bundle_ray);
				getFragmentManager().beginTransaction().replace(R.id.ll_content,rayFragment).commit();
				break;
		}
	}
}
