/*
Copyright 2009-2016 Urban Airship Inc. All rights reserved.


Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE URBAN AIRSHIP INC ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL URBAN AIRSHIP INC OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.urbanairship.rileyrw;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

public class SampleApplication extends Application {

    private static final String FIRST_RUN_KEY = "first_run";

    @Override
    public void onCreate() {
        super.onCreate();

        /*
          Optionally, customize your config at runtime:

          AirshipConfigOptions options = new AirshipConfigOptions.Builder()
                .setInProduction(!BuildConfig.DEBUG)
                .setDevelopmentAppKey("Your Development App Key")
                .setDevelopmentAppSecret("Your Development App Secret")
                .setProductionAppKey("Your Production App Key")
                .setProductionAppSecret("Your Production App Secret")
                .build();

          UAirship.takeOff(this, options);
         */

        UAirship.takeOff(this, new UAirship.OnReadyCallback() {
            @Override
            public void onAirshipReady(UAirship airship) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean isFirstRun = preferences.getBoolean(FIRST_RUN_KEY, true);
                if (isFirstRun) {
                    preferences.edit().putBoolean(FIRST_RUN_KEY, false).apply();
                }

                configureAirship(airship, isFirstRun);
            }
        });
    }

    /**
     * Called when UAirship is finished taking off.
     * @param airship The UAirship singleton.
     * @param isFirstRun Flag indicating if this is a first app run.
     */
    private void configureAirship(UAirship airship, boolean isFirstRun) {
        // Customize the notification factory with the
        DefaultNotificationFactory factory = new DefaultNotificationFactory(this);
        factory.setColor(ContextCompat.getColor(this, R.color.color_primary));
        factory.setSmallIconId(R.drawable.ic_notification);

        // Set the custom factory
        airship.getPushManager().setNotificationFactory(factory);

        // Enable user notifications by default on first run
        if (isFirstRun) {
            airship.getPushManager().setUserNotificationsEnabled(true);
        }
    }
}
