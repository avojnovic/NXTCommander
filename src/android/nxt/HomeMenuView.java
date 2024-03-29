
package android.nxt;

import android.nxt.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class HomeMenuView extends View
{

	int mScreenWidth;
	int mScreenHeight;
	
	int startButtonYStart;
	int tutorialButtonYStart;
	Activity splashMenuActivity;

	Resources res;
	Bitmap ic_splash_start;
	Bitmap mBackgroundImage;

	public HomeMenuView(Context context, Activity splashMenuActivity)
	{
		super(context);
		this.splashMenuActivity = splashMenuActivity;
		res = context.getResources();
	}


	private float getStartYPos()
	{
		return mScreenHeight - ic_splash_start.getHeight() -10;
	}
	
	private float getStartXPos()
	{
		return mScreenWidth - ic_splash_start.getWidth() -10;
	}
	
	public float getStartButtonYPos(float divider)
	{
		return (ic_splash_start.getHeight() + (divider * 3));
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		canvas.drawBitmap(mBackgroundImage, 0, 0, null);			
		canvas.drawBitmap(ic_splash_start, getStartXPos(), getStartYPos() , null);				
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		mScreenHeight = h;
		mScreenWidth = w;
		setupBitmaps();
		
		invalidate();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (event.getY() > getStartYPos() && event.getY() <= getStartYPos() + ic_splash_start.getHeight()
					&& event.getX() > getStartXPos() && event.getX() <= getStartXPos() + ic_splash_start.getWidth())
			{
				Intent playGame = new Intent(splashMenuActivity.getBaseContext(), NXTCommander.class);
				splashMenuActivity.startActivity(playGame);
			}
		}
		return true;
	}

	private void setupBitmaps()
	{
		ic_splash_start = BitmapFactory.decodeResource(res, R.drawable.ic_splash_start);
		ic_splash_start = Bitmap.createScaledBitmap(ic_splash_start, ic_splash_start.getWidth(), ic_splash_start.getHeight(), true);		

		mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.background_1);
		mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, mScreenWidth, mScreenHeight, true);

	}

}
