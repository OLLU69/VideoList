package ollu.dp.ua.videolist.features.movies

import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.fragment_movie_details.*
import kotlinx.android.synthetic.main.toolbar.*
import ollu.dp.ua.videolist.R
import ollu.dp.ua.videolist.appComponent
import ollu.dp.ua.videolist.core.extension.*
import ollu.dp.ua.videolist.core.platform.BaseActivity
import ollu.dp.ua.videolist.core.platform.BaseFragment
import ollu.dp.ua.videolist.databinding.FragmentMovieDetailsBinding
import ollu.dp.ua.videolist.features.movies.MovieDetailsActivity.Companion.MOVIE_ID
import ollu.dp.ua.videolist.features.movies.MovieDetailsActivity.Companion.POSTER
import javax.inject.Inject

const val TAG = "MovieDetailsFragment"

class MovieDetailsFragment : BaseFragment<FragmentMovieDetailsBinding>() {

    companion object {

        fun forMovie(bundle: Bundle): MovieDetailsFragment {
            return MovieDetailsFragment().apply {
                arguments = bundle
            }
        }
    }

    @Inject
    lateinit var movieDetailsAnimator: MovieDetailsAnimator

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel

    override fun layoutId() = R.layout.fragment_movie_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appComponent()?.inject(this)
        activity?.let { movieDetailsAnimator.postponeEnterTransition(it) }
        movieDetailsViewModel = viewModel(viewModelFactory) {
            observe(toolbarTitle, { activity!!.toolbar.title = it })
            observe(onMovieDetailsReady, { renderMovieDetails() })
            observe(loadPosterUrlAndPostponeEnterTransition,
                { url -> activity?.also { moviePoster.loadUrlAndPostponeEnterTransition(url ?: return@observe, it) } })
            observe(notify, { notify(it ?: return@observe) })
            observe(onError, { handleFailure(it) })
        }
        binding.vm = movieDetailsViewModel
        if (firstTimeCreated(savedInstanceState)) {
            movieDetailsViewModel.loadMovieDetails(arguments!!.getInt(MOVIE_ID))
        } else {
            movieDetailsAnimator.scaleUpView(moviePlay)
            movieDetailsAnimator.cancelTransition(moviePoster)
            moviePoster.loadFromUrl((arguments!!.getString(POSTER) ?: ""))
        }
    }

    override fun onBackPressed() {
        movieDetailsAnimator.fadeInvisible(scrollView, movieDetails)
        if (moviePlay.isVisible()) movieDetailsAnimator.scaleDownView(moviePlay)
        else movieDetailsAnimator.cancelTransition(moviePoster)
    }

    private fun renderMovieDetails() {
        Log.d(TAG, "renderMovieDetails()")
        movieDetailsAnimator.fadeVisible(scrollView, movieDetails)
        movieDetailsAnimator.scaleUpView(moviePlay)
    }

    private fun handleFailure(failure: Int?) {
        failure ?: return
        activity?.setResult(failure)
        (activity as? BaseActivity)?.onBackPressed()
    }
}
