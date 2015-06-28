package ke.qq.tzparallaxlistview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;

public class ParallaxListView extends ListView {

	protected static final String TAG = ParallaxListView.class.getName();

	private ImageView mImageView;
	// 定义ImageView的最大可拉伸的高度
	private int mDrawableMaxHeight = -1;
	// 定义ImageView初始加载的高度
	private int mImageViewHeight = -1;
	// 定义ImageView的默认高度
	private int mDefaultImageViewHeight = 0;

	public ParallaxListView(Context context) {
		super(context);
		init(context);
	}

	public ParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ParallaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public void setParallaxImageView(ImageView iv) {
		mImageView = iv;
		mImageView.setScaleType(ScaleType.CENTER_CROP);
	}

	// 设置缩放级别---控制图片的最大拉伸高度
	// 在界面加载完毕的时候调用
	public void setViewsBounds(double zoomRatio) {
		// mDrawableMaxHeight=zoomRatio*mDefaultImageViewHeight;
		if (mDrawableMaxHeight == -1) {

			mImageViewHeight = mImageView.getHeight();
			if (mImageViewHeight < 0) {

				mImageViewHeight = mDefaultImageViewHeight;
			}
		}
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	private void init(Context context) {
		setVerticalScrollBarEnabled(false);
		mDefaultImageViewHeight = 449;

	}

	
	//内部滑动时调用
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// 监听listView的滑动
		super.onScrollChanged(l, t, oldl, oldt);
		// 如何控制图片见效的高度？---监听listView的头部滑出去的距离
		View header = (View) mImageView.getParent();

		// 得到头部划出去的距离----<0
		if (header.getTop() < 0 && mImageView.getHeight() > mImageViewHeight) {

			mImageView.getLayoutParams().height = Math.max(
					mImageView.getHeight() - Math.abs(header.getTop()), mDefaultImageViewHeight);
			header.layout(header.getLeft(), 0, header.getRight(), header.getHeight());

			// 调整ImageView所在容器的高度
			mImageView.requestLayout();

		}
	}

	
	//在划出屏幕时调用
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
			int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY,
			boolean isTouchEvent) {

		// 当listView，ScrollView滑动过头的时候回调的方法
		// 不断地控制ImageView的高度
		boolean isCollapse = resizeOverScrollby(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

		return isCollapse ? true : super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}

	/**
	 * 处理listView，ScrollView滑动过头
	 * @param deltaX
	 * @param deltaY
	 * @param scrollX
	 * @param scrollY
	 * @param scrollRangeX
	 * @param scrollRangeY
	 * @param maxOverScrollX
	 * @param maxOverScrollY
	 * @param isTouchEvent
	 * @return
	 */
	private boolean resizeOverScrollby(int deltaX, int deltaY, int scrollX, int scrollY,
			int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY,
			boolean isTouchEvent) {
		if (deltaY < 0) {
			mImageView.getLayoutParams().height = Math.max(
					mImageView.getHeight() + Math.abs(deltaY), mDefaultImageViewHeight);
			// 重新调整ImageView的宽高
			mImageView.requestLayout();
		} else {

			if (mImageView.getHeight() > mDefaultImageViewHeight)
				mImageView.getLayoutParams().height = mImageView.getHeight() - Math.abs(deltaY);
			// 重新调整ImageView的宽高
			mImageView.requestLayout();
		}

		return true;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		//松开手时，展示动画效果
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			ResetAnimation anim = new ResetAnimation(mImageView, mDefaultImageViewHeight);

			anim.setDuration(500);
			mImageView.startAnimation(anim);
		}

		return super.onTouchEvent(ev);
	}

	//自定义动画效果
	public class ResetAnimation extends Animation {

		private ImageView mView;
		// private int targetHeight;
		private int originalHeith;
		private int extraHeight;

		public ResetAnimation(ImageView mView, int targetHeight) {
			// 图片动画地减小高度
			this.mView = mView;
			// this.targetHeight = targetHeight;
			this.originalHeith = mView.getHeight();

			extraHeight = originalHeith - targetHeight;
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);

			//interpolatedTime的值为0.0~1.0f
			mView.getLayoutParams().height = (int) (originalHeith - extraHeight * interpolatedTime);
			mView.requestLayout();
		}
	}

}
