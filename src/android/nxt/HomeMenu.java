/**
 * Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 * This file is part of MINDdroid.
 *
 * MINDdroid is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * MINDdroid is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * MINDdroid. If not, see <http://www.gnu.org/licenses/>.
 **/

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
		// TODO Auto-generated method stub
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
