package ru.kheynov.secretsanta

import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp

private const val ONESIGNAL_APP_ID = "e281e33f-9662-49c7-ad62-71bad853fc3a"

@HiltAndroidApp
class SantaApplication : android.app.Application(){
    override fun onCreate() {
        super.onCreate()
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.promptForPushNotifications();
        val clientId = "8f337"
        OneSignal.setExternalUserId(clientId)
    }
}