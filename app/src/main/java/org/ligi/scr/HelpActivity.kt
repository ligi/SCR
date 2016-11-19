package org.ligi.scr

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            title = "Help"
            setDisplayHomeAsUpEnabled(true)
        }

        helpText.text = Html.fromHtml(getString(R.string.help_html))
        helpText.movementMethod = LinkMovementMethod.getInstance()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
