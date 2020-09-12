package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.databinding.FragmentSearchMovieBinding
import com.pedro.schwarz.desafioyourdev.repository.Failure
import com.pedro.schwarz.desafioyourdev.repository.Success
import com.pedro.schwarz.desafioyourdev.ui.extension.setContent
import com.pedro.schwarz.desafioyourdev.ui.extension.showMessage
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.MoviesAdapter
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.AppViewModel
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.Components
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.SearchMovieViewModel
import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchMovieFragment : Fragment() {
    private val controller by lazy { findNavController() }
    private val viewModel by viewModel<SearchMovieViewModel>()
    private val appViewModel by sharedViewModel<AppViewModel>()
    private val moviesAdapter by inject<MoviesAdapter> { parametersOf(false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setIsEmpty = true
        configMovieClick()
    }

    private fun configMovieClick() {
        moviesAdapter.onItemClick = { title ->
            goToMovieDetails(title)
        }
    }

    private fun goToMovieDetails(title: String) {
        val action =
            SearchMovieFragmentDirections.actionSearchMovieFragmentToMovieDetailsFragment(title)
        controller.navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchMovieBinding.inflate(inflater, container, false)
        setBinding(binding)
        return binding.root
    }

    private fun setBinding(binding: FragmentSearchMovieBinding) {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        configList(binding)
        handleOnSearch(binding)
    }

    private fun handleOnSearch(binding: FragmentSearchMovieBinding) {
        binding.onSearch = View.OnClickListener { validateTitle(binding.searchMovieSearch) }
    }

    private fun configList(binding: FragmentSearchMovieBinding) {
        binding.searchMovieList.apply {
            setContent(false, StaggeredGridLayoutManager.VERTICAL, false, moviesAdapter)
            itemAnimator = FlipInBottomXAnimator().apply { addDuration = 300 }
        }
    }

    private fun validateTitle(titleField: TextInputLayout) {
        titleField.editText?.let {
            val title = it.text.toString()
            if (title.isEmpty()) {
                showMessage(getString(R.string.search_movie_empty_title_message))
            } else {
                fetchMoviesByTitle(title)
            }
        }
    }

    private fun fetchMoviesByTitle(title: String) {
        viewModel.fetchMoviesByTitle(title).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Success -> {
                    result.data?.let { viewModel.setIsEmpty = it.isEmpty() }
                    moviesAdapter.submitList(result.data)
                }
                is Failure -> {
                    result.error?.let { showMessage(it) }
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = true, bottomBar = true)
    }
}