package com.hch.businquiry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.hch.businquiry.utils.PermisitionUtils;

/**
 * 
 * @author yechao
 * @功能说明 自定义TabHost
 *
 */
public class MainActivity extends FragmentActivity {
	// 定义FragmentTabHost对象
	private FragmentTabHost mTabHost;

	// 定义一个布局
	private LayoutInflater layoutInflater;

	// 定义数组来存放Fragment界面
	private Class fragmentArray[] = {
			FragmentCategory.class,
			FragmentNearby.class ,
			FragmentUser.class,
			};

	// 定义数组来存放按钮图片
	private int mImageViewArray[] = {
			R.drawable.main_tab_item_category, R.drawable.main_tab_item_setting,
			R.drawable.main_tab_item_user };

	// Tab选项卡的文字
	private String mTextviewArray[] = { "搜索", "附近", "我的"};
	private static final String LTAG = MainActivity.class.getSimpleName();

	private SDKReceiver mReceiver;
	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver
	{
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(LTAG, "action: " + s);

			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				showLongToast("key 验证出错! 错误码 :" + intent.getIntExtra
						(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
						+  " ; 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
				showLongToast("key 验证成功! 功能可以正常使用");
//				text.setTextColor(Color.YELLOW);
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				showLongToast("网络出错");
			}
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();

		sdkRegisterReceiver();
	}

	/**
	 *  注册 SDK 广播监听者
	 */
	private void sdkRegisterReceiver(){
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
	}
	/**
	 * 初始化组件
	 */
	private void initView() {
		PermisitionUtils permisitionUtils = new PermisitionUtils(this, this);
		permisitionUtils.setSinglePermisition(PermisitionUtils.IPermisitionType.LOCATION);
		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);

		// 实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		// 得到fragment的个数
		int count = fragmentArray.length;

		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
					.setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			// 设置Tab按钮的背景
			mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.main_tab_item_bg);

		}
		mTabHost.getTabWidget().setDividerDrawable(R.color.transparent);
	}

	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);

		return view;
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
	}
	public void showLongToast(String str){
		Toast.makeText(this,str,Toast.LENGTH_LONG).show();
	}
	public void showShortToast(String str){
		Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
	};
}
