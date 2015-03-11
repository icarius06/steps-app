package com.onaio.steps.activityHandler;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.onaio.steps.R;
import com.onaio.steps.SettingsActivity;

public class SettingActivityHandler implements IActivityHandler {
    private static final int IDENTIFIER = 1;
    public static final String PHONE_ID = "phoneId";

    @Override
    public boolean shouldOpen(int menu_id) {
        return menu_id == R.id.action_settings;
    }

    @Override
    public boolean open(ListActivity activity) {
        Intent intent = new Intent(activity.getBaseContext(), SettingsActivity.class);
        activity.startActivityForResult(intent, IDENTIFIER);
        return true;
    }

    @Override
    public boolean canHandleResult(int requestCode) {
        return requestCode == IDENTIFIER;
    }

    @Override
    public void handleResult(ListActivity activity, Intent data, int resultCode) {
        if (resultCode == activity.RESULT_OK)
            handleSuccess(activity, data);
        else
            savePhoneIdErrorHandler();
    }

    private void handleSuccess(ListActivity activity, Intent data) {
        SharedPreferences.Editor editor = dataStoreEditor(activity);
        String phoneId = data.getStringExtra(PHONE_ID);
        editor.putString(PHONE_ID, phoneId);
        if (!editor.commit())
            savePhoneIdErrorHandler();
    }

    private void savePhoneIdErrorHandler() {
        //TODO: toast message for save phone id failure
    }

    private SharedPreferences dataStore(ListActivity activity) {
        return activity.getPreferences(activity.MODE_PRIVATE);
    }

    private SharedPreferences.Editor dataStoreEditor(ListActivity activity) {
        return dataStore(activity).edit();
    }
}