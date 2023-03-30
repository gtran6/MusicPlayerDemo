package com.plcoding.spotifycloneyt.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import com.bumptech.glide.RequestManager
import com.plcoding.spotifycloneyt.R
import com.plcoding.spotifycloneyt.adapter.SwipeSongAdapter
import com.plcoding.spotifycloneyt.data.entity.Song
import com.plcoding.spotifycloneyt.exoplayer.toSong
import com.plcoding.spotifycloneyt.other.Status
import com.plcoding.spotifycloneyt.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var currPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeToObservers()

        vpSong.adapter = swipeSongAdapter
    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) {
            vpSong.currentItem = newItemIndex
            currPlayingSong = song
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(this) {
            it?.let {  result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data.let {  songs ->
                            swipeSongAdapter.songs = songs!!
                            if (songs.isNotEmpty()) {
                                glide.load((currPlayingSong ?: songs[0]).imageURL).into(ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(currPlayingSong ?: return@observe)
                        }
                    }
                    Status.ERROR -> Unit
                    Status.LOADING -> Unit
                }
            }
        }

        mainViewModel.currPlayingSong.observe(this) {
            if (it == null) return@observe

            currPlayingSong = it.toSong()
            glide.load(currPlayingSong?.imageURL).into(ivCurSongImage)
            switchViewPagerToCurrentSong(currPlayingSong ?: return@observe)
        }

        mainViewModel.playbackState.observe(this) {
            playbackState = it

        }
    }
}