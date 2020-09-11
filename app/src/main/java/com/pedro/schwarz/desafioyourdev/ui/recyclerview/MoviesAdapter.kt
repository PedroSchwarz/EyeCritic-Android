package com.pedro.schwarz.desafioyourdev.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.desafioyourdev.databinding.ItemMovieBinding
import com.pedro.schwarz.desafioyourdev.model.Movie

class MoviesAdapter(
    var onToggleFavorite: (movie: Movie) -> Unit = {},
    var onItemClick: (title: String) -> Unit = {}
) : ListAdapter<Movie, MoviesAdapter.ViewHolder>(MovieCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var movie: Movie

        init {
            binding.onItemClick = View.OnClickListener { onItemClick(movie.display_title) }
            binding.onToggleFavorite = View.OnClickListener { onToggleFavorite(movie) }
        }

        fun bind(item: Movie) {
            this.movie = item
            binding.movie = this.movie
        }
    }
}

object MovieCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
}