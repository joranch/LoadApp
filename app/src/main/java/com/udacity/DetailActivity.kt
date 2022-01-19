package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val status = intent?.getStringExtra(INTENT_EXTRA_STATUS_KEY)
        val filename = intent?.getStringExtra(INTENT_EXTRA_FILE_KEY)

        status_text.text = status ?: ""
        filename_text.text = filename ?: ""

        close_button.setOnClickListener {
            finish()
        }
    }

}
