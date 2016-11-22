package org.ligi.scr

import android.app.Activity
import android.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback

internal abstract class DefaultRetrofitCallback<T>(private val fatal: Boolean, private val activity: Activity) : Callback<T> {

    override fun onFailure(call: Call<T>, t: Throwable) {

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(android.R.string.dialog_alert_title)
        builder.setMessage(t.message)

        if (fatal) {
            builder.setPositiveButton(android.R.string.ok) { dialogInterface, i ->
                dialogInterface.dismiss()
                activity.finish()
            }
        } else {
            builder.setPositiveButton(android.R.string.ok, null)
        }

        builder.show()
    }

}
