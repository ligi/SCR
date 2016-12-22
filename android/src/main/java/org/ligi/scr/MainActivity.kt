package org.ligi.scr

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import info.metadude.java.library.halfnarp.ApiModule
import info.metadude.java.library.halfnarp.model.CreateTalkPreferencesSuccessResponse
import info.metadude.java.library.halfnarp.model.GetTalksResponse
import info.metadude.java.library.halfnarp.model.TalkIds
import info.metadude.java.library.halfnarp.model.UpdateTalkPreferencesSuccessResponse
import kotlinx.android.synthetic.main.activity_main.*
import net.steamcrafted.loadtoast.LoadToast
import org.ligi.kaxt.startActivityFromClass
import org.ligi.kaxt.startActivityFromURL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var adapter: EventViewHolderAdapter? = null

    var currentTab = 1
    var groups: Map<Int, MutableList<GetTalksResponse>>? = null

    inner class MyItemTouchHelper : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) =
                makeMovementFlags(0, when (currentTab) {
                    0 -> ItemTouchHelper.RIGHT
                    2 -> ItemTouchHelper.LEFT
                    else -> ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                })

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?)
                = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val offset = when (direction) {
                ItemTouchHelper.RIGHT -> 1
                else -> -1
            }

            move(offset, viewHolder.adapterPosition)
        }
    }

    private fun move(offset: Int, index: Int) {

        val elementAt = adapter?.talkResponses?.elementAt(index)

        adapter?.talkResponses?.removeAt(index)
        groups?.get(currentTab)?.removeAt(index)
        groups?.get(currentTab + offset).let {
            it?.add(elementAt!!)
        }

        adapter?.notifyItemRemoved(index)
        adapter?.notifyDataSetChanged()
        setTabNames()

        State.noIDs.clear()
        State.noIDs.addAll(groups!!.get(0)!!.map { it.eventId.toString() })

        State.yesIDs.clear()
        State.yesIDs.addAll(groups!!.get(2)!!.map { it.eventId.toString() })

        setStateChanged()
    }

    fun setTabNames() {
        noTab.text = "NO (" + groups!![0]!!.size + ")"
        maybeTab.text = "MAYBE (" + groups!![1]!!.size + ")"
        yesTab.text = "YES (" + groups!![2]!!.size + ")"
    }

    val noTab by lazy { tabLayout.newTab().setTag(0) }
    val yesTab by lazy { tabLayout.newTab().setTag(2) }
    val maybeTab by lazy { tabLayout.newTab().setTag(1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val layoutManager = StaggeredGridLayoutManager(resources.getInteger(R.integer.rows), OrientationHelper.VERTICAL)
        trackRecycler.layoutManager = layoutManager

        collapse_toolbar.isTitleEnabled = false

        tabLayout.addTab(noTab)
        tabLayout.addTab(maybeTab)
        tabLayout.addTab(yesTab)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTab = tab.tag as Int
                setCurrentAdapter()
            }

        })
        maybeTab.select()
        fab.setOnClickListener {
            //submitVotes()
            transferToSchedule()
        }

        val itemTouchHelper = ItemTouchHelper(MyItemTouchHelper())

        itemTouchHelper.attachToRecyclerView(trackRecycler)

        loadData()
    }

    private fun submitVotes() {

        val uuidOrNull = State.lastUUID

        setStateSaved()

        if (uuidOrNull == null) {

            val loadToast = LoadToast(this).setText("Initial upload").show()

            Handler().postDelayed({

                ApiModule.getTalkPreferencesService().createTalkPreferences(getTalkIds(2)).enqueue(object : Callback<CreateTalkPreferencesSuccessResponse> {
                    override fun onResponse(call: Call<CreateTalkPreferencesSuccessResponse>, response: Response<CreateTalkPreferencesSuccessResponse>) {
                        State.lastUUID = response.body().uid
                        loadToast.success()
                    }

                    override fun onFailure(call: Call<CreateTalkPreferencesSuccessResponse>, t: Throwable?) {
                        loadToast.error()
                        setStateChanged()
                    }

                })
            }, 1500)

        } else {
            val loadToast = LoadToast(this).setText("Uploading new selection").show()

            Handler().postDelayed({
                ApiModule.getTalkPreferencesService().updateTalkPreferences(uuidOrNull, getTalkIds(2)).enqueue(object : Callback<UpdateTalkPreferencesSuccessResponse> {
                    override fun onResponse(call: Call<UpdateTalkPreferencesSuccessResponse>?, response: Response<UpdateTalkPreferencesSuccessResponse>?) {
                        loadToast.success()
                    }

                    override fun onFailure(call: Call<UpdateTalkPreferencesSuccessResponse>?, t: Throwable?) {
                        loadToast.error()
                        setStateChanged()
                    }

                })
            }, 1500)
        }
    }

    private fun transferToSchedule() {
        val sendIntent = Intent()

        val res = getTalkIds(2).sortedTalkIds.joinToString(",")
        sendIntent.putExtra("CSV", res)

        sendIntent.setClassName("org.ligi.fahrplan", "org.ligi.fahrplan.TalkPrefsImportActivity")

        try {
            startActivity(sendIntent)
        } catch (e: ActivityNotFoundException) {
            AlertDialog.Builder(this).setMessage("Please install the latest version of 33c3 Fahrplan")
                    .setPositiveButton(android.R.string.ok, { dialogInterface: DialogInterface, i: Int ->
                        startActivityFromURL("https://play.google.com/store/apps/details?id=org.ligi.fahrplan")
                    })
                    .setNegativeButton(android.R.string.cancel, { dialogInterface: DialogInterface, i: Int ->
                    })
                    .show()
        }
    }

    private fun getTalkIds(i: Int) = TalkIds().apply {
        add(groups!![i]!!.map { it.eventId })
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
                ViewCompat.setBackground(trackRecycler, BitmapDrawable(resources, bitmap))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // form follows function
        }

        trackRecycler.layoutManager.scrollToPosition(State.lastPos)

        if (State.lastUpdateSaved) {
            fab.hide()
        } else {
            fab.show()
        }
    }

    override fun onPause() {
        State.lastPos = (trackRecycler.layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(null)[0]

        super.onPause()
    }


    private fun loadData() {
        val service = ApiModule.getTalkPreferencesService()
        service.talks.enqueue(object : DefaultRetrofitCallback<List<GetTalksResponse>>(true, this) {
            override fun onResponse(call: Call<List<GetTalksResponse>>?, response: Response<List<GetTalksResponse>>) {
                val body = response.body()

                val _groups = body.groupBy {
                    when {
                        State.yesIDs.contains(it.eventId.toString()) -> 2
                        State.noIDs.contains(it.eventId.toString()) -> 0
                        else -> 1
                    }
                }
                groups = (0..2).associate { it to if (_groups.containsKey(it)) _groups[it]!!.toMutableList() else mutableListOf() }

                setCurrentAdapter()
                setTabNames()
                AlertDialog.Builder(this@MainActivity).setMessage("The Halfnarp is over - you can now transfer your selection to the schedule app")
                        .setPositiveButton("transfer",{ dialogInterface: DialogInterface, i: Int ->
                            transferToSchedule()
                        })
                        .setNegativeButton(android.R.string.cancel,{ dialogInterface: DialogInterface, i: Int -> })
                        .show()
            }
        })
    }

    private fun setCurrentAdapter() {
        if (groups != null)
            groups?.get(currentTab).let {
                val newList = it ?: mutableListOf()
                if (adapter == null) {
                    adapter = EventViewHolderAdapter(newList, { offset: Int, index: Int -> move(offset, index) })
                    trackRecycler.adapter = adapter
                } else {
                    (adapter as EventViewHolderAdapter).apply {
                        talkResponses.clear()
                        talkResponses.addAll(newList)
                        notifyDataSetChanged()
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> this.startActivityFromClass(ListActivity::class.java)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setStateChanged() {
        fab.show()
        State.lastUpdateSaved = false
    }

    private fun setStateSaved() {
        fab.hide()
        State.lastUpdateSaved = true
    }
}