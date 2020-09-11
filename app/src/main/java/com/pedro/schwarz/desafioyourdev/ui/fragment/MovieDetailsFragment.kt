package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.databinding.FragmentMovieDetailsBinding
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.Failure
import com.pedro.schwarz.desafioyourdev.repository.Resource
import com.pedro.schwarz.desafioyourdev.repository.Success
import com.pedro.schwarz.desafioyourdev.ui.databinding.data.MovieData
import com.pedro.schwarz.desafioyourdev.ui.extension.showMessage
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.AppViewModel
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.Components
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.MovieDetailsViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieDetailsFragment : Fragment() {

    private val controller by lazy { findNavController() }
    private val arguments by navArgs<MovieDetailsFragmentArgs>()
    private val title by lazy { arguments.title }
    private val viewModel by viewModel<MovieDetailsViewModel>()
    private val appViewModel by sharedViewModel<AppViewModel>()

    private val movieData = MovieData()

    private lateinit var moreOptionBtn: FloatingActionButton
    private lateinit var toggleFavoriteBtn: FloatingActionButton
    private lateinit var shareBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchMovie()
    }

    private fun fetchMovie() {
        viewModel.setIsLoading = true
        viewModel.fetchMovie(title).observe(this, { result: Resource<Movie> ->
            when (result) {
                is Success -> {
                    result.data?.let { this.movieData.setMovie(it) }
                }
                is Failure -> {
                    showMessage(getString(R.string.review_display_error_message))
                    controller.popBackStack()
                }
            }
            viewModel.setIsLoading = false
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.movie = movieData
        binding.onGoToArticle = View.OnClickListener { goToArticle() }
        binding.onShareArticle = View.OnClickListener { shareArticle() }
        binding.onToggleFavorite =
            View.OnClickListener { movieData.toMovie()?.let { toggleMovieFavorite(it) } }
        binding.onToggleMenu =
            View.OnClickListener { viewModel.setIsMenuOpen = !viewModel.setIsMenuOpen }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = true, bottomBar = false)
    }

    private fun toggleMovieFavorite(movie: Movie) {
        viewModel.toggleMovieFavorite(movie).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Success -> {
                    showMessage(getString(R.string.added_to_favorites_message))
                }
                is Failure -> {
                    result.error?.let { showMessage(it) }
                }
            }
        })
    }

    private fun shareArticle() {
        movieData.toMovie()?.let {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, it.linkUrl)
                type = "text/plain"
            }

            val shareIntent =
                Intent.createChooser(sendIntent, getString(R.string.share_article_with_message))
            startActivity(shareIntent)
        }
    }

    private fun goToArticle() {
        movieData.toMovie()?.let {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.linkUrl)))
        }
    }
}