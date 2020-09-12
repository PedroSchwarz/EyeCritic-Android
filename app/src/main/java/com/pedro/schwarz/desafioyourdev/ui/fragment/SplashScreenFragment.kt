package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.AppViewModel
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.Components
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class SplashScreenFragment : Fragment() {
    private val controller by lazy { findNavController() }
    private val appViewModel by inject<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = false, bottomBar = false)

        CoroutineScope(Dispatchers.IO).launch {
            delay(1500)
            withContext(Dispatchers.Main) {
                val directions =
                    SplashScreenFragmentDirections.actionSplashScreenFragmentToMovieListFragment()
                controller.navigate(directions)
            }
        }
    }
}