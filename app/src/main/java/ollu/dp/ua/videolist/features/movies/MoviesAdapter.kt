package ollu.dp.ua.videolist.features.movies

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_movie.view.*
import ollu.dp.ua.videolist.R
import ollu.dp.ua.videolist.core.extension.inflate
import ollu.dp.ua.videolist.core.extension.loadFromUrl
import ollu.dp.ua.videolist.core.navigation.Navigator
import javax.inject.Inject

class MoviesAdapter
@Inject constructor() : PagedListAdapter<Movie, MoviesAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }

    internal var clickListener: (Movie, Navigator.Extras) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.row_movie))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position), clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie?, clickListener: (Movie, Navigator.Extras) -> Unit) {
            movie?.apply {
                itemView.movieTitle.text = title
                if (!poster.isBlank()) itemView.moviePoster.loadFromUrl(poster)
                itemView.setOnClickListener { clickListener(this, Navigator.Extras(itemView.moviePoster)) }
            }
        }
    }
}
