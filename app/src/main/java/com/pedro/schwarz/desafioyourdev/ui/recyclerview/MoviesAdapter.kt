package com.pedro.schwarz.desafioyourdev.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.desafioyourdev.databinding.ItemMovieBinding
import com.pedro.schwarz.desafioyourdev.model.Movie

class MoviesAdapter(
    val hasToggle: Boolean,
    var onToggleFavorite: (movie: Movie) -> Unit = {},
    var onItemClick: (title: String, itemView: View) -> Unit = { _, _ -> }
) : PagedListAdapter<Movie, MoviesAdapter.ViewHolder>(MovieCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var movie: Movie

        init {
            binding.onItemClick =
                View.OnClickListener { onItemClick(movie.display_title, binding.itemMovieBody) }
            binding.onToggleFavorite = View.OnClickListener { onToggleFavorite(movie) }
        }

        fun bind(item: Movie) {
            this.movie = item
            binding.movie = this.movie
            binding.itemMovieBody.transitionName = item.display_title
            binding.hasToggle = hasToggle
        }
    }
}

object MovieCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
}