package ollu.dp.ua.videolist.features.movies

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import ollu.dp.ua.videolist.R
import ollu.dp.ua.videolist.core.interactor.GetMovies
import javax.inject.Inject

class MoviesViewModel
@Inject constructor(private val getMovies: GetMovies) : ViewModel() {

    val renderFailureInt = MutableLiveData<Int>()
    val renderFailureStr = MutableLiveData<String>()
    val movies: MutableLiveData<LiveData<PagedList<Movie>>> = MutableLiveData()
    val progressVisible = MutableLiveData<Boolean>()
    val movieListVisible = ObservableBoolean(false)
    private val handleMovieList = { movies: LiveData<PagedList<Movie>> ->
        this.movies.value = movies
        resetProgressState()
    }
    private val handleFailure = fun(error: String?) {
        when (error) {
            "NetworkConnection" -> onError(R.string.failure_network_connection)
            "ServerError" -> onError(R.string.failure_server_error)
            "ListNotAvailable" -> onError(R.string.failure_movies_list_unavailable)
            else -> onError(error)
        }
    }

    fun load() {
        setProgressState()
        loadMovies()
    }

    fun filter(query: String?) = getMovies.filterVideos(query)

    private fun onError(error: String?) {
        renderFailureStr.value = error
        errorResetProgressState()
    }

    private fun onError(error: Int) {
        renderFailureInt.value = error
        errorResetProgressState()
    }

    private fun loadMovies() = getMovies.getVideos(handleMovieList, handleFailure)

    private fun setProgressState() {
        movieListVisible.set(false)
        showProgress()
    }

    private fun resetProgressState() {
        movieListVisible.set(true)
        hideProgress()
    }

    private fun errorResetProgressState() {
        movieListVisible.set(false)
        hideProgress()
    }

    private fun showProgress() {
        progressVisible.value = true
    }

    private fun hideProgress() {
        progressVisible.value = false
    }
}
