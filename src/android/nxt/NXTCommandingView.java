package android.nxt;

import java.util.List;

import android.nxt.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class NXTCommandingView extends SurfaceView implements SurfaceHolder.Callback
{
	private static final int SHORT_PRESS_MAX_DURATION = 750;

	class NXTCommandingThread extends Thread
	{

		private static final int REDRAW_SCHED = 100;
		private int ICON_MAX_SIZE;

		private int GOAL_HEIGHT;
		private int GOAL_WIDTH;
		private static final int HAPTIC_FEEDBACK_LENGTH = 30;
		boolean mInGoal = true;

		Vibrator mHapticFeedback;

		private Bitmap mBackgroundImage;

		private Drawable mIconOrange;

		private Drawable mIconWhite;

		private Bitmap mActionButton;

		private Bitmap mActionDownButton;

		private int mCanvasHeight = 1;

		private int mCanvasWidth = 1;

		private long mLastTime;

		private boolean mRun = false;

		private SurfaceHolder mSurfaceHolder;

		private float mX;

		private float mY;

		private int mGrowAdjust;

		long mElapsedSinceDraw = 0;

		long mElapsedSinceNXTCommand = 0;

		int mAvCount = 0;

		long mNextPulse = 0;

		Drawable mPulsingTiltIcon;

		boolean mActionPressed = false;

		boolean mToNXT = false;

		float mNumAcX;
		float mNumAcY;

		private float xX0 = 0;
		private float xX1 = 0;
		private float xY0 = 0;
		private float xY1 = 0;

		private float yX0 = 0;
		private float yX1 = 0;
		private float yY0 = 0;
		private float yY1 = 0;
		public boolean longPressCancel;

		public NXTCommandingThread(SurfaceHolder surfaceHolder, Context context, Vibrator vibrator, Handler handler)
		{

			mHapticFeedback = vibrator;
			mSurfaceHolder = surfaceHolder;

			Resources res = context.getResources();
			mIconOrange = context.getResources().getDrawable(R.drawable.dot);

			mIconWhite = context.getResources().getDrawable(R.drawable.dot);

			mActionButton = BitmapFactory.decodeResource(res, R.drawable.action_btn_up);
			mActionDownButton = BitmapFactory.decodeResource(res, R.drawable.action_btn_down);
			mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.background_2);
		}

		private int calcNextPulse()
		{

			int xDistanceFromGoal = 0;
			int yDistanceFromGoal = 0;

			if (mX > mCanvasWidth / 2)
			{
				xDistanceFromGoal = (int) ((mX - (mCanvasWidth / 2)) - (GOAL_WIDTH / 2));

			}
			else
			{
				xDistanceFromGoal = (int) ((mCanvasWidth / 2) - mX) - (GOAL_WIDTH / 2);
			}
			xDistanceFromGoal += ICON_MAX_SIZE / 2;

			if (mY > ((mCanvasHeight - mActionButton.getHeight()) / 2))
			{
				yDistanceFromGoal = (int) ((mY - ((mCanvasHeight - mActionButton.getHeight()) / 2)) - (GOAL_WIDTH / 2));

			}
			else
			{
				yDistanceFromGoal = (int) (((mCanvasHeight - mActionButton.getHeight()) / 2) - mY - (GOAL_WIDTH / 2));

			}
			yDistanceFromGoal += ICON_MAX_SIZE / 2;

			double mOneSideGameWidth = (mCanvasWidth - GOAL_WIDTH) / 2;//

			double mOneSideGameHeight = ((mCanvasHeight - mActionButton.getHeight()) / 2) - (GOAL_WIDTH / 2);

			double mPercentToXEdge = (xDistanceFromGoal / (mOneSideGameWidth)) * 100;
			double mPercentToYEdge = (yDistanceFromGoal / mOneSideGameHeight) * 100;

			float closeEdge = (float) (mPercentToXEdge > mPercentToYEdge ? mPercentToXEdge : mPercentToYEdge);
			return (int) (800 - ((closeEdge * 8)));
		}

		private void doDraw(Canvas mCanvas)
		{

			if (!mActivity.isConnected())
			{

				mCanvas.drawBitmap(mBackgroundImage, 0, 0, null);

				mCanvas.drawBitmap(mActionDownButton, 0, mCanvasHeight - mActionButton.getHeight(), null);

			}
			else
			{

				mGrowAdjust = ICON_MAX_SIZE;
				mCanvas.drawBitmap(mBackgroundImage, 0, 0, null);

				mCanvas.drawBitmap(mActionPressed ? mActionDownButton : mActionButton, 0, mCanvasHeight - mActionButton.getHeight(), null);
				mActionPressed = false;

				if (mX + ICON_MAX_SIZE / 2 >= mCanvasWidth)
				{

					mX = mCanvasWidth - (ICON_MAX_SIZE / 2);
				}
				else if (mX - (ICON_MAX_SIZE / 2) < 0)
				{
					mX = ICON_MAX_SIZE / 2;
				}

				if (mY + ICON_MAX_SIZE / 2 >= (mCanvasHeight - mActionButton.getHeight()))
				{

					mY = mCanvasHeight - mActionButton.getHeight() - ICON_MAX_SIZE / 2;
				}
				else if (mY - ICON_MAX_SIZE / 2 < 0)
				{
					mY = ICON_MAX_SIZE / 2;
				}

				if (mLastTime > mNextPulse)
				{

					mPulsingTiltIcon = mPulsingTiltIcon == mIconOrange ? mIconWhite : mIconOrange;

					mNextPulse = mPulsingTiltIcon == mIconOrange ? mLastTime + calcNextPulse() : mLastTime + 90;
				}

				mPulsingTiltIcon.setBounds((int) mX - (mGrowAdjust / 2), (int) mY - (mGrowAdjust / 2), ((int) mX + mGrowAdjust / 2), ((int) mY + mGrowAdjust / 2));
				mPulsingTiltIcon.draw(mCanvas);

			}

		}

		public void doStart()
		{
			synchronized (mSurfaceHolder)
			{

				mX = mCanvasWidth / 2;
				mY = (mCanvasHeight - mActionButton.getHeight()) / 2;
			}
		}

		public void pause()
		{
			thread.setRunning(false);
			synchronized (mSurfaceHolder)
			{

			}
			boolean retry = true;
			getThread().setRunning(false);
			while (retry)
			{
				try
				{
					getThread().join();
					retry = false;
				}
				catch (InterruptedException e)
				{
				}
			}

		}

		public synchronized void restoreState(Bundle savedState)
		{
			synchronized (mSurfaceHolder)
			{

			}
		}

		@Override
		public void run()
		{

			while (mRun)
			{

				try
				{
					Thread.sleep(30);
				}
				catch (InterruptedException e)
				{
				}

				updateTime();
				updateMoveIndicator(mAccelX, mAccelY);
				doActionButtonFeedback();

				if (mElapsedSinceDraw > REDRAW_SCHED)
				{

					if (mElapsedSinceNXTCommand > NXTCommander.UPDATE_TIME)
					{

						doMotorMovement(-mNumAcY, -mNumAcX);
					}

					lockCanvasAndDraw();

				}
			}
		}

		private void doActionButtonFeedback()
		{
			if ((mLastTime - mTimeActionDown) > SHORT_PRESS_MAX_DURATION && longPressCancel != true)
			{
				vibrate();

				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e)
				{

					e.printStackTrace();
				}
				vibrate();
				longPressCancel = true;
			}

		}

		public void lockCanvasAndDraw()
		{
			Canvas c = null;
			try
			{
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder)
				{
					doDraw(c);

				}
			}
			finally
			{
				if (c != null)
				{

					mSurfaceHolder.unlockCanvasAndPost(c);

					mElapsedSinceDraw = 0;

				}
			}
		}

		public Bundle saveState(Bundle map)
		{
			synchronized (mSurfaceHolder)
			{
				if (map != null)
				{

				}
			}
			return map;
		}

		public void setRunning(boolean b)
		{
			mRun = b;
		}

		public void doMotorMovement(float pitch, float roll)
		{

			int left = 0;
			int right = 0;

			if ((Math.abs(pitch) > 10.0) || (Math.abs(roll) > 10.0))
			{

				if (pitch > 33.3)
				{
					pitch = (float) 33.3;
				}
				else if (pitch < -33.3)
				{
					pitch = (float) -33.3;
				}

				if (roll > 33.3)
				{
					roll = (float) 33.3;
				}
				else if (roll < -33.3)
				{
					roll = (float) -33.3;
				}

				if (Math.abs(pitch) > 10.0)
				{
					left = (int) Math.round(3.3 * pitch * (1.0 + roll / 60.0));
					right = (int) Math.round(3.3 * pitch * (1.0 - roll / 60.0));
				}
				else
				{
					left = (int) Math.round(3.3 * roll - Math.signum(roll) * 3.3 * Math.abs(pitch));
					right = -left;
				}

				if (left > 100)
					left = 100;
				else if (left < -100)
					left = -100;

				if (right > 100)
					right = 100;
				else if (right < -100)
					right = -100;

			}

			mActivity.updateMotorControl(left, right);
		}

		public void setState(int mode)
		{
			synchronized (mSurfaceHolder)
			{
				setState(mode, null);
			}
		}

		public void setState(int mode, CharSequence message)
		{

			synchronized (mSurfaceHolder)
			{

			}

		}

		public void setSurfaceSize(int width, int height)
		{

			synchronized (mSurfaceHolder)
			{
				mCanvasWidth = width;
				mCanvasHeight = height;
				float mAHeight = mActionButton.getHeight();
				float mAWidth = mActionButton.getWidth();
				mActionButton = Bitmap.createScaledBitmap(mActionButton, width, (Math.round((width * (mAHeight / mAWidth)))), true);
				mActionDownButton = Bitmap.createScaledBitmap(mActionDownButton, width, (Math.round((width * (mAHeight / mAWidth)))), true);

				mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);

				int temp_ratio = mCanvasWidth / 64;
				GOAL_WIDTH = mCanvasWidth / temp_ratio;

				ICON_MAX_SIZE = (GOAL_WIDTH / 8) * 6;

				temp_ratio = mCanvasHeight / 64;
				GOAL_HEIGHT = mCanvasHeight / temp_ratio;

			}
		}

		public void unpause()
		{

			synchronized (mSurfaceHolder)
			{
				mLastTime = System.currentTimeMillis() + 100;
			}

		}

		private void updateTime()
		{
			long now = System.currentTimeMillis();

			if (mLastTime > now)
				return;

			long elapsed = now - mLastTime;

			mElapsedSinceDraw += elapsed;
			mElapsedSinceNXTCommand += elapsed;
			mLastTime = now;

		}

		public void vibrate()
		{
			mHapticFeedback.vibrate(HAPTIC_FEEDBACK_LENGTH);

		}

		void updateMoveIndicator(float mAcX, float mAcY)
		{

			xX1 = xX0;
			xX0 = mAcX;
			xY1 = xY0;
			xY0 = (float) 0.07296293 * xX0 + (float) 0.07296293 * xX1 + (float) 0.8540807 * xY1;
			mAcX = xY0;

			yX1 = yX0;
			yX0 = mAcY;
			yY1 = yY0;
			yY0 = (float) 0.07296293 * yX0 + (float) 0.07296293 * yX1 + (float) 0.8540807 * yY1;
			mAcY = yY0;

			mX = ((mCanvasWidth / 2)) + (int) ((mAcX / 10) * (mCanvasWidth / 10));

			mNumAcX = mAcX;

			mY = (((mCanvasHeight - mActionButton.getHeight()) / 2)) + (int) ((mAcY / 10) * ((mCanvasHeight - mActionButton.getHeight()) / 10));

			mNumAcY = mAcY;

		}

	}

	private static final String TAG = NXTCommandingView.class.getName();;

	private NXTCommander mActivity;

	private NXTCommandingThread thread;

	private SensorManager mSensorManager;

	private float mAccelX = 0;
	private float mAccelY = 0;

	long mTimeActionDown = 0;

	private final SensorEventListener mSensorAccelerometer = new SensorEventListener()
	{
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			mAccelX = 0 - event.values[2];
			mAccelY = 0 - event.values[1];
		}

	};

	Context context;

	public NXTCommandingView(Context context, NXTCommander uiActivity)
	{
		super(context);

		mActivity = uiActivity;
		mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);

		SurfaceHolder holder = getHolder();
		holder.setKeepScreenOn(true);
		holder.addCallback(this);
		this.context = context;

		thread = new NXTCommandingThread(holder, context, (Vibrator) uiActivity.getSystemService(Context.VIBRATOR_SERVICE), new Handler()
		{
			@Override
			public void handleMessage(Message m)
			{

			}
		});

		setFocusable(true);
	}

	public NXTCommandingThread getThread()
	{
		return thread;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getY() > this.getHeight() - getThread().mActionButton.getHeight())
		{

			switch (event.getAction())
			{

				case MotionEvent.ACTION_DOWN:
					mTimeActionDown = System.currentTimeMillis();
					getThread().longPressCancel = false;
					break;

				case MotionEvent.ACTION_UP:
					long mTimeActionUp = System.currentTimeMillis();

					if (mTimeActionUp - mTimeActionDown < SHORT_PRESS_MAX_DURATION)
					{
						getThread().longPressCancel = true;
						mActivity.actionButtonPressed();
					}

					break;
			}
		}
		return true;
	}

	public void registerListener()
	{
		List<Sensor> sensorList;

		sensorList = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		mSensorManager.registerListener(mSensorAccelerometer, sensorList.get(0), SensorManager.SENSOR_DELAY_GAME);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		getThread().setSurfaceSize(width, height);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{

		if (getThread().getState() != Thread.State.TERMINATED)
		{

			getThread().setRunning(true);

			getThread().start();
		}
		else
		{
			thread = new NXTCommandingThread(holder, context, (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE), new Handler()
			{
				@Override
				public void handleMessage(Message m)
				{

				}
			});

			getThread().setRunning(true);
			getThread().start();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.i(TAG, "surfaceDestroyed");
		boolean retry = true;
		getThread().setRunning(false);
		while (retry)
		{
			try
			{
				getThread().join();
				retry = false;
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	public void unregisterListener()
	{
		mSensorManager.unregisterListener(mSensorAccelerometer);

	}

}
