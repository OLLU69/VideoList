package ollu.dp.ua.videolist.features.movies

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_movies.*
import ollu.dp.ua.videolist.R
import ollu.dp.ua.videolist.appComponent
import ollu.dp.ua.videolist.core.extension.observe
import ollu.dp.ua.videolist.core.extension.viewModel
import ollu.dp.ua.videolist.core.navigation.Navigator
import ollu.dp.ua.videolist.core.platform.BaseFragment
import ollu.dp.ua.videolist.databinding.FragmentMoviesBinding
import javax.inject.Inject

class MoviesFragment : BaseFragment<FragmentMoviesBinding>() {

    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var moviesAdapter: MoviesAdapter

    private lateinit var moviesViewModel: MoviesViewModel

    override fun layoutId() = R.layout.fragment_movies

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appComponent()?.inject(this)
        moviesViewModel = activity!!.viewModel(viewModelFactory) {
            observe(movies, ::observeMovies)
            observe(renderFailureStr, { renderFailure(it) })
            observe(renderFailureInt, { renderFailure(it) })
            observe(progressVisible, { if (it == true) showProgress() else hideProgress() })
        }
        binding.vm = moviesViewModel
        initializeView()
        moviesViewModel.load()
    }

    private fun observeMovies(liveData: LiveData<PagedList<Movie>>?) {
        observe(liveData ?: return, ::renderMoviesList)
    }

    private fun initializeView() {
        movieList.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        movieList.adapter = moviesAdapter
        moviesAdapter.clickListener = { movie, navigationExtras ->
            navigator.showMovieDetails(activity!!, movie, navigationExtras)
        }
    }

    private fun renderMoviesList(movies: PagedList<Movie>?) {
        moviesAdapter.submitList(movies)
    }

    private fun renderFailure(message: String?) {
        if (message.isNullOrBlank()) {
            notifyWithAction(R.string.unknown_error, R.string.action_refresh, moviesViewModel::load)
            return
        }
        notifyWithAction(message, R.string.action_refresh, moviesViewModel::load)
    }

    private fun renderFailure(@StringRes message: Int?) {
        message ?: let {
            notifyWithAction(R.string.unknown_error, R.string.action_refresh, moviesViewModel::load)
            return
        }
        notifyWithAction(message, R.string.action_refresh, moviesViewModel::load)
    }

    fun filter(query: String?) {
        moviesViewModel.filter(query)
    }
}
