package com.lm.yandexdiskplayer.player

import android.support.v4.media.MediaBrowserCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lm.yandexapi.models.Folder
import com.lm.yandexapi.models.Song
import kotlin.random.Random

class ControllerUiStates : MediaBrowserCompat.SubscriptionCallback() {

    private var _nowPlayingSong by mutableStateOf(Song())

    private var _isPlayingCardVisible by mutableStateOf(false)

    private var _playerState by mutableStateOf(PlayerState.NULL)

    private var _enableNext by mutableStateOf(true)

    private var _enablePrev by mutableStateOf(true)

    private var _enableSlider by mutableStateOf(true)

    private var _enablePlay by mutableStateOf(true)

    private var _timeProgress by mutableStateOf(0f)

    private var _timeTextProgress by mutableStateOf("")

    private var _durationSong by mutableStateOf("")

    private var _columnVisible by mutableStateOf(false)

    private var _foldersList: SnapshotStateList<Folder> = mutableStateListOf()

    var folderList: List<Folder>
        get() = _foldersList
        set(value) {
            value.forEach { _foldersList.add(it); if (!_columnVisible) _columnVisible = true }
        }

    var nowPlayingSong: Song
        get() = _nowPlayingSong
        set(value) {
            _nowPlayingSong = value
        }

    var columnVisible: Boolean
        get() = _columnVisible
        set(value) {
            _columnVisible = value
        }

    var timeTextProgress: String
        get() = _timeTextProgress
        set(value) {
            _timeTextProgress = value
        }

    var isPlayingCardVisible: Boolean
        get() = _isPlayingCardVisible
        set(value) {
            _isPlayingCardVisible = value
        }

    var playerState: PlayerState
        get() = _playerState
        set(value) {
            _playerState = value
        }

    var timeProgress: Float
        get() = _timeProgress
        set(value) {
            _timeProgress = value
        }

    var durationSong: String
        get() = _durationSong
        set(value) {
            _durationSong = value
        }

    var enableNext: Boolean
        get() = _enableNext
        set(value) {
            _enableNext = value
        }

    var enablePrev: Boolean
        get() = _enablePrev
        set(value) {
            _enablePrev = value
        }

    var enableSlider: Boolean
        get() = _enableSlider
        set(value) {
            _enableSlider = value
        }

    var enablePlay: Boolean
        get() = _enablePlay
        set(value) {
            _enablePlay = value
        }

    fun muteStates() {
        _enablePrev = false
        _enableNext = false
        _enableSlider = false
        _enablePlay = false
    }

    fun unMuteStates() {
        _enablePrev = true
        _enableNext = true
        _enableSlider = true
        _enablePlay = true
    }

    fun clearInfo() {
        _timeProgress = 0f
        _timeTextProgress = ""
        _durationSong = ""
    }

    fun hidePlayingCard() {
        _isPlayingCardVisible = false
    }

    fun showPlayingCard() {
        _isPlayingCardVisible = true
    }

    fun setSongInPlayingCard(song: Song) {
        nowPlayingSong = song
    }

    override fun onChildrenLoaded(
        parentId: String,
        children: MutableList<MediaBrowserCompat.MediaItem>
    ) {
        if (parentId == "root") {
            val map = children.map {
                Song(it.description.title.toString(),
                    "", it.mediaId.toString(), it.description.subtitle.toString()) }
                .groupBy { it.path.substringBefore(it.name) }
            folderList = map.keys.map { Folder(it, "",
                Random(67465465).nextInt().toString(), (map[it] ?: emptyList())
                    .sortedBy { song -> song.name })
            }.sortedBy { it.path }
        }
        super.onChildrenLoaded(parentId, children)
    }
}

