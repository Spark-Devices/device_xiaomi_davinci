/*
 * Copyright (C) 2021 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.keyhandler;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;

public class KeyHandler implements DeviceKeyHandler {

    private static final boolean DEBUG = true;
    private static final String TAG = "KeyHandler";
    private static final int KEYCODE_FOD = 338;

    public KeyHandler(Context context) {
        if (DEBUG)
            Log.i(TAG, "KeyHandler constructor called");
    }

    public KeyEvent handleKeyEvent(KeyEvent event) {
        int scanCode = event.getScanCode();

        if (DEBUG)
            Log.i(TAG, "handleKeyEvent=" + scanCode);

        switch (scanCode) {
            case KEYCODE_FOD:
                return event;
            default:
                return event;
        }
    }
}
