package com.pedro.schwarz.desafioyourdev.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.ui.extension.setAgeColor
import com.pedro.schwarz.desafioyourdev.ui.extension.setImage

class MoviesAdapter(
    var onToggleFavorite: (movie: Movie) -> Unit = {},
    var onItemClick: (title: String) -> Unit = {}
) : ListAdapter<Movie, MoviesAdapter.ViewHolder>(MovieCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var movie: Movie

        fun bind(item: Movie) {
            this.movie = item
            setContent()
        }

        private fun setContent() {
            if (::movie.isInitialized) {
                itemView.findViewById<CardView>(R.id.item_movie_body)
                    .setOnClickListener { onItemClick(movie.display_title) }
                itemView.findViewById<TextView>(R.id.item_movie_title).text = movie.display_title
                itemView.findViewById<CardView>(R.id.item_movie_age_card).apply {
                    setAgeColor(movie.mpaa_rating)
                }
                itemView.findViewById<TextView>(R.id.item_movie_age).apply {
                    text = if (movie.mpaa_rating.isEmpty()) "N/A"
                    else movie.mpaa_rating
                }
                itemView.findViewById<TextView>(R.id.item_movie_summary).text = movie.summary_short
                itemView.findViewById<ImageView>(R.id.item_movie_image)
                    .apply { setImage(movie.src, thumbnail = true) }
                itemView.findViewById<FloatingActionButton>(R.id.item_movie_toggle_favorite_btn)
                    .apply {
                        setImage(movie.favorite)
                        setOnClickListener { onToggleFavorite(movie) }
                    }
            }
        }
    }
}

object MovieCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
}