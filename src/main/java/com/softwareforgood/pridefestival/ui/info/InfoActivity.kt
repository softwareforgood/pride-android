package com.softwareforgood.pridefestival.ui.info

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.softwareforgood.pridefestival.databinding.ActivityInfoBinding
import com.softwareforgood.pridefestival.ui.ActivityComponent
import com.softwareforgood.pridefestival.util.HasComponent
import com.softwareforgood.pridefestival.util.makeComponent

class InfoActivity : AppCompatActivity(), HasComponent<ActivityComponent> {

    override val component: ActivityComponent by lazy { makeComponent() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
