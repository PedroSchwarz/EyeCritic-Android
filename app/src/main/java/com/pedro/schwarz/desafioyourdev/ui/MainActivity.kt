package com.pedro.schwarz.desafioyourdev.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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
    private val mainBottomNav: BottomNavigationView by lazy { findViewById(R.id.main_bottom_nav) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBarWithNavController(controller, appBarConfiguration)
        mainBottomNav.setupWithNavController(controller)
        configUIComponentsListener()
    }

    private fun configUIComponentsListener() {
        appViewModel.components.observe(this, { components ->
            supportActionBar?.let {
                if (components.appBar) {
                    it.show()
                } else {
                    it.hide()
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