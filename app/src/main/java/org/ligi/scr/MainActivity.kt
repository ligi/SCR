package org.ligi.scr

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.squareup.otto.Subscribe
import info.metadude.java.library.halfnarp.ApiModule
import info.metadude.java.library.halfnarp.model.CreateTalkPreferencesSuccessResponse
import info.metadude.java.library.halfnarp.model.GetTalksResponse
import info.metadude.java.library.halfnarp.model.UpdateTalkPreferencesSuccessResponse
import kotlinx.android.synthetic.main.activity_main.*
import net.steamcrafted.loadtoast.LoadToast
import org.ligi.axt.AXT
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit

class MainActivity : AppCompatActivity() {

    internal val KEY_LAST_POSITION = "last_scroll_position"
    internal val KEY_LAST_UPDATE_SAVED = "last_update_saved"

    private var adapter: EventViewHolderAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setIcon(R.drawable.logo)
            subtitle = "Schedule Conflict Resolver"
        }

        fab.setOnClickListener {
            val uuidOrNull = prefs.getString("uuid", null)

            setStateSaved()

            if (uuidOrNull == null) {

                val loadToast = LoadToast(this).setText("Initial upload").show()

                Handler().postDelayed({
                    ApiModule.getTalkPreferencesService().createTalkPreferences(App.talkIds).enqueue(object : Callback<CreateTalkPreferencesSuccessResponse> {
                        override fun onResponse(response: Response<CreateTalkPreferencesSuccessResponse>, retrofit: Retrofit) {
                            prefs.edit().putString("uuid", response.body().uid).commit()
                            loadToast.success()
                        }

                        override fun onFailure(t: Throwable) {
                            loadToast.error()
                            setStateChanged()
                        }
                    })
                }, 1500)

            } else {
                val loadToast = LoadToast(this).setText("Uploading new selection").show()

                Handler().postDelayed({
                    ApiModule.getTalkPreferencesService().updateTalkPreferences(uuidOrNull, App.talkIds).enqueue(object : Callback<UpdateTalkPreferencesSuccessResponse> {
                        override fun onResponse(updateTalkPreferencesSuccessResponse: Response<UpdateTalkPreferencesSuccessResponse>, retrofit: Retrofit) {
                            loadToast.success()
                        }

                        override fun onFailure(t: Throwable) {
                            loadToast.error()
                            setStateChanged()
                        }
                    })
                }, 1500)
            }
        }

        val layoutManager = StaggeredGridLayoutManager(resources.getInteger(R.integer.rows), OrientationHelper.VERTICAL)
        trackRecycler.layoutManager = layoutManager

        loadData()
    }

    override fun onResume() {
        super.onResume()

        try {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                trackRecycler.setBackgroundResource(R.drawable.bg_src)
            } else {
                val matrix = Matrix()
                matrix.postRotate(90f)
                val source = BitmapFactory.decodeResource(resources, R.drawable.bg_src)
                val bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
                ViewCompat.setBackground(trackRecycler,BitmapDrawable(resources, bitmap))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // form follows function
        }

        App.bus.register(this)
        App.talkIds.load()

        trackRecycler.layoutManager.scrollToPosition(sharedPrefs.getInt(KEY_LAST_POSITION, 0))

        if (prefs.getBoolean(KEY_LAST_UPDATE_SAVED, false)) {
            fab.hide()
        } else {
            fab.show()
        }
    }

    private val sharedPrefs: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)

    override fun onPause() {
        App.bus.unregister(this)
        App.talkIds.save()

        val lastFirstVisiblePosition = (trackRecycler.layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(null)[0]
        sharedPrefs.edit().putInt(KEY_LAST_POSITION, lastFirstVisiblePosition).apply()

        super.onPause()
    }


    private fun loadData() {
        val service = ApiModule.getTalkPreferencesService()
        service.talks.enqueue(object : DefaultRetrofitCallback<List<GetTalksResponse>>(true, this) {
            override fun onResponse(response: Response<List<GetTalksResponse>>, retrofit: Retrofit) {
                adapter = EventViewHolderAdapter(response.body())
                trackRecycler.adapter = adapter
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> AXT.at(this).startCommonIntent().activityFromClass(HelpActivity::class.java)
        }
        return super.onOptionsItemSelected(item)
    }

    private val prefs: SharedPreferences
        get() = getSharedPreferences("prefs", Context.MODE_PRIVATE)

    @Subscribe
    fun onEvent(scopeChangeEvent: TalkIdsChangeEvent) {
        setStateChanged()
    }

    private fun setStateChanged() {
        fab.show()
        prefs.edit().putBoolean(KEY_LAST_UPDATE_SAVED, false).apply()
    }

    private fun setStateSaved() {
        fab.hide()
        prefs.edit().putBoolean(KEY_LAST_UPDATE_SAVED, true).apply()
    }
}