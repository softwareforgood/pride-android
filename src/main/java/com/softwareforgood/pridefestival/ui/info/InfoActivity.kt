package com.softwareforgood.pridefestival.ui.info

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.softwareforgood.pridefestival.databinding.ActivityInfoBinding
import com.softwareforgood.pridefestival.util.HasComponent
import com.softwareforgood.pridefestival.util.makeActivityComponent

class InfoActivity : AppCompatActivity(), HasComponent<InfoComponent> {

    private lateinit var binding: ActivityInfoBinding

    override val component: InfoComponent by lazy {
        makeActivityComponent()
            .infoComponentBuilder
            .activityInfoBinding(binding)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
