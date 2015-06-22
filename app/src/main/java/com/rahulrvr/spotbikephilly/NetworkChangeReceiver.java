package com.rahulrvr.spotbikephilly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 *Network change listener
 *
 */
public class NetworkChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);


        if(context != null && connectivityManager.getActiveNetworkInfo() == null) {
            new MaterialDialog.Builder(context)
                    .positiveColorRes(R.color.primary)
                    .title(context.getString(R.string.error_title))
                    .content(context.getString(R.string.no_connectivity_message))
                    .positiveText(R.string.action_ok)
                    .show();
        }
    }
}
