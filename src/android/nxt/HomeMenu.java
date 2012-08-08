

package android.nxt;

import android.nxt.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class HomeMenu extends Activity
{
	public static final int MENU_QUIT = Menu.FIRST;
	public static final String MINDDROID_PREFS = "Mprefs";

	public static void quitApplication()
	{
		if (NXTCommander.isBtOnByUs())
		{
			BluetoothAdapter.getDefaultAdapter().disable();
			NXTCommander.setBtOnByUs(false);
		}
		
		splashMenu.finish();
	}

	private View splashMenuView;

	private static Activity splashMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		splashMenuView = new HomeMenuView(getApplicationContext(), this);
		setContentView(splashMenuView);
		splashMenu = this;
	}

	@Override
	protected void onDestroy()
	{

		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		if (NXTCommander.isBtOnByUs())
		{
			BluetoothAdapter.getDefaultAdapter().disable();
			NXTCommander.setBtOnByUs(false);
		}
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, MENU_QUIT, 1, getResources().getString(R.string.quit))
				.setIcon(R.drawable.ic_menu_exit);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{			
			case MENU_QUIT:
				finish();
				return true;
		}
		return false;
	}

}
