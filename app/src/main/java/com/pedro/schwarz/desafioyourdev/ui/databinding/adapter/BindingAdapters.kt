package com.pedro.schwarz.desafioyourdev.ui.databinding.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pedro.schwarz.desafioyourdev.ui.extension.*
import com.pedro.schwarz.desafioyourdev.ui.extension.setRatingColor

@BindingAdapter("setIsRefreshing")
fun SwipeRefreshLayout.setIsRefreshing(isRefreshing: Boolean) {
    this.isRefreshing = isRefreshing
}

@BindingAdapter("setRatingColor")
fun CardView.setRatingColor(rating: String?) {
    rating?.let { setRatingColor(rating) }
}

@BindingAdapter("setFavoriteStateIndicator")
fun FloatingActionButton.setFavoriteStateIndicator(favorite: Boolean) {
    setFavoriteStateImage(favorite)
}

@BindingAdapter("src", "thumbnail")
fun ImageView.setImage(imageUrl: String, thumbnail: Boolean) {
    setImageSrc(imageUrl, thumbnail)
}

@BindingAdapter("setToLocaleDate")
fun TextView.setToLocaleDate(date: String?) {
    date?.let {
        if (date.isNotEmpty()) {
            toLocaleDate(date)
        }
    }
}

@BindingAdapter("toggleRotate")
fun FloatingActionButton.toggleRotate(isMenuOpen: Boolean) {
    toggleRotateAnimation(isMenuOpen)
}

@BindingAdapter("toggleVisibility")
fun FloatingActionButton.toggleVisibility(isMenuOpen: Boolean) {
    toggleVisibilityAnimation(isMenuOpen)
}