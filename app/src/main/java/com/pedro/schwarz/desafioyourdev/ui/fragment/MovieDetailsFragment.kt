package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.Failure
import com.pedro.schwarz.desafioyourdev.repository.Resource
import com.pedro.schwarz.desafioyourdev.repository.Success
import com.pedro.schwarz.desafioyourdev.ui.extension.*
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

    private lateinit var movie: Movie

    private lateinit var movieDetailsOverlay: ConstraintLayout
    private lateinit var movieImage: ImageView
    private lateinit var movieAgeCard: CardView
    private lateinit var movieAge: TextView
    private lateinit var movieTitle: TextView
    private lateinit var movieHeadline: TextView
    private lateinit var movieSummary: TextView
    private lateinit var moviePublicationDate: TextView
    private lateinit var movieArticleBy: TextView
    private lateinit var movieLink: TextView
    private lateinit var moreOptionBtn: FloatingActionButton
    private lateinit var toggleFavoriteBtn: FloatingActionButton
    private lateinit var shareBtn: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = true, bottomBar = false)
        initContent(view)
        configIsLoadingListener()
        configOptionsBtns(view)
        fetchMovie()
    }

    private fun configIsLoadingListener() {
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            movieDetailsOverlay.toggleVisibility(visible = isLoading)
        })
    }

    private fun initContent(view: View) {
        movieDetailsOverlay = view.findViewById(R.id.movie_details_loading_overlay)
        movieImage = view.findViewById(R.id.movie_details_image)
        movieAgeCard = view.findViewById(R.id.movie_details_age_card)
        movieAge = view.findViewById(R.id.movie_details_age)
        movieTitle = view.findViewById(R.id.movie_details_title)
        movieHeadline = view.findViewById(R.id.movie_details_headline)
        movieSummary = view.findViewById(R.id.movie_details_summary)
        moviePublicationDate = view.findViewById(R.id.movie_details_publication_date)
        movieArticleBy = view.findViewById(R.id.movie_details_by)
        movieLink = view.findViewById(R.id.movie_details_link)
    }

    private fun configOptionsBtns(view: View) {
        moreOptionBtn = view.findViewById(R.id.movie_details_options_btn)
        toggleFavoriteBtn = view.findViewById(R.id.movie_details_toggle_favorite_btn)
        shareBtn = view.findViewById(R.id.movie_details_share_btn)
        configOptionMenuBtn()
        configIsMenuOpenListener()
    }

    private fun configIsMenuOpenListener() {
        viewModel.isMenuOpen.observe(viewLifecycleOwner, { isMenuOpen ->
            if (isMenuOpen) {
                moreOptionBtn.toggleRotateAnimation(isMenuOpen)
                toggleFavoriteBtn.toggleVisibilityAnimation(isMenuOpen)
                shareBtn.toggleVisibilityAnimation(isMenuOpen)
            } else {
                moreOptionBtn.toggleRotateAnimation(isMenuOpen)
                toggleFavoriteBtn.toggleVisibilityAnimation(isMenuOpen)
                shareBtn.toggleVisibilityAnimation(isMenuOpen)
            }
        })
    }

    private fun configOptionMenuBtn() {
        moreOptionBtn.setOnClickListener { viewModel.setIsMenuOpen = !viewModel.setIsMenuOpen }
    }

    private fun fetchMovie() {
        viewModel.setIsLoading = true
        viewModel.fetchMovie(title).observe(viewLifecycleOwner, { result: Resource<Movie> ->
            when (result) {
                is Success -> {
                    result.data?.let { this.movie = it }
                    setContent()
                }
                is Failure -> {
                    showMessage(getString(R.string.review_display_error_message))
                    controller.popBackStack()
                }
            }
            viewModel.setIsLoading = false
        })
    }

    private fun setContent() {
        if (::movie.isInitialized) {
            movieImage.apply { setImage(movie.src) }
            movieAgeCard.apply { setAgeColor(movie.mpaa_rating) }
            movieAge.apply {
                text = if (movie.mpaa_rating.isEmpty()) "N/A"
                else movie.mpaa_rating
            }
            movieTitle.text = movie.display_title
            movieHeadline.text = movie.headline
            movieSummary.text = movie.summary_short
            movieLink.setOnClickListener { goToArticle() }
            moviePublicationDate.apply { toLocaleDate(movie.publication_date) }
            movieArticleBy.text = movie.byline
            toggleFavoriteBtn.apply { setImage(movie.favorite) }
            shareBtn.setOnClickListener { shareArticle() }
            toggleFavoriteBtn.setOnClickListener { toggleMovieFavorite(movie) }
        }
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
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, movie.linkUrl)
            type = "text/plain"
        }

        val shareIntent =
            Intent.createChooser(sendIntent, getString(R.string.share_article_with_message))
        startActivity(shareIntent)
    }

    private fun goToArticle() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(movie.linkUrl)))
    }
}