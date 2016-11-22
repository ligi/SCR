package org.ligi.scr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import retrofit2.Call;
import retrofit2.Callback;

abstract class DefaultRetrofitCallback<T> implements Callback<T> {

    private final boolean fatal;
    private final Activity activity;

    DefaultRetrofitCallback(boolean fatal, Activity activity) {
        this.fatal = fatal;
        this.activity = activity;
    }

    @Override
    public void onFailure(final Call<T> call, final Throwable t) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(android.R.string.dialog_alert_title);
        builder.setMessage(t.getMessage());

        if (fatal) {
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, final int i) {
                    dialogInterface.dismiss();
                    activity.finish();
                }
            });
        } else {
            builder.setPositiveButton(android.R.string.ok, null);
        }

        builder.show();
    }

}
