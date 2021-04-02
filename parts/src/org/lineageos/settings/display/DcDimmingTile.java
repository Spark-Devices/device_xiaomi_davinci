/*
 * Copyright (C) 2017-2021 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.display;

import static org.lineageos.settings.display.DcDimmingService.MODE_AUTO_OFF;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import org.lineageos.settings.display.DcDimmingService;
import org.lineageos.settings.R;

public class DcDimmingTile extends TileService {

	private static final boolean DEBUG = false;
	private static final String TAG = "DcDimmingTile";
	private static final String DC_DIMMING_AUTO_MODE = "dc_dimming_auto_mode";
	private static final String DC_DIMMING_STATE = "dc_dimming_state";
	private static final String DC_DIMMING_SUPPORTED = "dc_dimming_supported";

	private String dcEnabled, dcDisabled, dcSchedule;
	private DcDimmingService mService;
	private Intent intent;
	private Context mContext;

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
			IBinder service) {
			DcDimmingService.LocalBinder binder = (DcDimmingService.LocalBinder) service;
			mService = binder.getService();
			if (DEBUG) Log.d(TAG, "DcDimmingService Connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			if (DEBUG) Log.d(TAG, "DcDimmingService Disconnected");
		}
	};

	@Override
	public void onClick() {
		if (DEBUG) Log.d(TAG, "Tile click");
		if (!mService.isDcDimmingOn()) {
			updateTile(true);
			mService.setDcDimming(true);
		} else {
			updateTile(false);
			mService.setDcDimming(false);
		}
		super.onClick();
	}

	@Override
	public void onStartListening() {
		if (DEBUG) Log.d(TAG, "Supported: " + isSupported() + ", DC Dimming On: " + isDcDimmingOn());
		mContext = getApplicationContext();
		intent = new Intent(mContext, DcDimmingService.class);
		mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		dcEnabled = mContext.getString(R.string.dc_dimming_summary_on);
		dcDisabled = mContext.getString(R.string.dc_dimming_summary_off);
		dcSchedule = mContext.getString(R.string.dc_dimming_auto_mode_title);
		updateTile(isDcDimmingOn());
		super.onStartListening();
	}

	@Override
	public void onStopListening() {
		if (DEBUG) Log.d(TAG, "onStopListening");
		mContext.unbindService(mConnection);
		super.onStopListening();
	}

	private boolean isSupported() {
		return Settings.System.getIntForUser(getContentResolver(),
			DC_DIMMING_SUPPORTED, 0,
			UserHandle.USER_CURRENT) == 1;
	}

	private boolean isDcDimmingOn() {
		return Settings.System.getIntForUser(getContentResolver(),
			DC_DIMMING_STATE, 0,
			UserHandle.USER_CURRENT) == 1;
	}

	private int getAutoMode() {
		return Settings.System.getIntForUser(getContentResolver(),
			DC_DIMMING_AUTO_MODE, 0,
			UserHandle.USER_CURRENT);
	}

	private void updateTile(boolean enabled) {
		final Tile tile = getQsTile();
		if (!isSupported()) {
			tile.setState(Tile.STATE_UNAVAILABLE);
		} else {
			tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
			if (getAutoMode() == MODE_AUTO_OFF) {
				tile.setSubtitle(enabled ? dcEnabled : dcDisabled);
			} else tile.setSubtitle(enabled ? dcSchedule : dcDisabled);
		}
		tile.updateTile();
	}
}