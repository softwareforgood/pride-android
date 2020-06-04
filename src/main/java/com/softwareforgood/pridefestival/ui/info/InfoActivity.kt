package com.softwareforgood.pridefestival.ui.info

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.ui.ActivityComponent
import com.softwareforgood.pridefestival.util.HasComponent
import com.softwareforgood.pridefestival.util.makeComponent
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity(), HasComponent<ActivityComponent> {

    override val component: ActivityComponent by lazy { makeComponent() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
