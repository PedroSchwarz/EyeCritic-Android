package com.pedro.schwarz.desafioyourdev.ui.databinding.data

import androidx.lifecycle.MutableLiveData
import com.pedro.schwarz.desafioyourdev.model.Movie

data class MovieData(
    private var movie: Movie = Movie(),
    val display_title: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = movie.display_title
    },
    val mpaa_rating: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = movie.mpaa_rating
    },
    val byline: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = movie.byline
    },
    val headline: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = movie.headline
    },
    val summary_short: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = movie.summary_short
    },
    val publication_date: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = movie.publication_date
    },
    val src: MutableLiveData<String> = MutableLiveData<String>().also { it.value = movie.src },
    val linkUrl: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = movie.linkUrl
    },
    val favorite: MutableLiveData<Boolean> = MutableLiveData<Boolean>().also {
        it.value = movie.favorite
    }
) {

    fun setMovie(item: Movie) {
        this.movie = item
        this.display_title.value = this.movie.display_title
        this.mpaa_rating.value = this.movie.mpaa_rating
        this.byline.value = this.movie.byline
        this.headline.value = this.movie.headline
        this.summary_short.value = this.movie.summary_short
        this.publication_date.value = this.movie.publication_date
        this.src.value = this.movie.src
        this.linkUrl.value = this.movie.linkUrl
        this.favorite.value = this.movie.favorite
    }

    fun toMovie(): Movie? {
        return this.movie.copy(
            display_title = this.display_title.value ?: return null,
            mpaa_rating = this.mpaa_rating.value ?: return null,
            byline = this.byline.value ?: return null,
            headline = this.headline.value ?: return null,
            summary_short = this.summary_short.value ?: return null,
            publication_date = this.publication_date.value ?: return null,
            src = this.src.value ?: return null,
            linkUrl = this.linkUrl.value ?: return null,
            favorite = this.favorite.value ?: return null
        )
    }
}