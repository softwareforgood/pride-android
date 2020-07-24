package com.softwareforgood.pridefestival.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.softwareforgood.pridefestival.BreadCrumbManager
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.databinding.ActivityMainBinding
import com.softwareforgood.pridefestival.ui.info.InfoActivity
import com.softwareforgood.pridefestival.util.HasComponent
import com.softwareforgood.pridefestival.util.launchActivity
import com.softwareforgood.pridefestival.util.makeActivityComponent
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasComponent<ActivityComponent> {

    @Inject
    lateinit var breadCrumbManager: BreadCrumbManager

    private val disposables = CompositeDisposable()

    private lateinit var binding: ActivityMainBinding

    override val component: ActivityComponent by lazy {
        makeActivityComponent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate() called with saveInstantSate = [%s]", savedInstanceState)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        component.inject(this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_info -> {
            launchActivity(InfoActivity::class)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
