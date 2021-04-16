package com.pedro.schwarz.desafioyourdev.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.AppViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val controller by lazy { findNavController(R.id.nav_host) }
    private val appBarConfiguration by lazy {
        AppBarConfiguration.Builder(
            R.id.movieListFragment,
            R.id.favoriteMovieListFragment,
            R.id.searchMovieFragment,
        ).build()
    }
    private val appViewModel by viewModel<AppViewModel>()
    private val appBar: AppBarLayout by lazy { findViewById(R.id.appbar) }
    private val mainToolbar: MaterialToolbar by lazy { findViewById(R.id.main_toolbar) }
    private val mainBottomNav: BottomNavigationView by lazy { findViewById(R.id.main_bottom_nav) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        setupActionBarWithNavController(controller, appBarConfiguration)
        mainBottomNav.setupWithNavController(controller)
        configUIComponentsListener()
    }

    private fun configUIComponentsListener() {
        appViewModel.components.observe(this, { components ->
            supportActionBar?.let {
                if (components.appBar) {
                    it.show()
                    appBar.visibility = View.VISIBLE
                } else {
                    it.hide()
                    appBar.visibility = View.GONE
                }
            }
            if (components.bottomBar) {
                mainBottomNav.visibility = View.VISIBLE
            } else {
                mainBottomNav.visibility = View.GONE
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return controller.navigateUp() || super.onSupportNavigateUp()
    }
}