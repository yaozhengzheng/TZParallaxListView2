package ke.qq.tzparallaxlistview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private ParallaxListView pListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		View headerView = View.inflate(this, R.layout.header_view, null);
		ImageView ivBackground = (ImageView) headerView.findViewById(R.id.ivBackground);
		pListView = (ParallaxListView) findViewById(R.id.listView);
		pListView.addHeaderView(headerView);

		pListView.setParallaxImageView(ivBackground);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, new String[] { "星期一  上班", "星期二  打豆豆",
						"星期三  看星星", "星期四    学习", "星期五 放假", "星期六  拍拖", "星期天  看电影","...." });

		pListView.setAdapter(adapter);
	}

	
	//在view加载完成时设定缩放级别
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus) {
			pListView.setViewsBounds(2);
		}

	}
}
