package com.lm.yandexdiskplayer.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lm.yandexapi.models.Folder
import com.lm.yandexapi.models.Song

class ControllerUiStatesImpl : ControllerUiStates {

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

    override var foldersList: List<Folder>
        get() = _foldersList
        set(value) { value.forEach { _foldersList.add(it); if(!_columnVisible)_columnVisible = true } }

    override var nowPlayingSong: Song
        get() = _nowPlayingSong
        set(value) {
            _nowPlayingSong = value
        }

    override var columnVisible: Boolean
        get() = _columnVisible
        set(value) {
            _columnVisible = value
        }

    override var timeTextProgress: String
        get() = _timeTextProgress
        set(value) {
            _timeTextProgress = value
        }

    override var isPlayingCardVisible: Boolean
        get() = _isPlayingCardVisible
        set(value) {
            _isPlayingCardVisible = value
        }

    override var playerState: PlayerState
        get() = _playerState
        set(value) {
            _playerState = value
        }

    override var timeProgress: Float
        get() = _timeProgress
        set(value) {
            _timeProgress = value
        }

    override var durationSong: String
        get() = _durationSong
        set(value) {
            _durationSong = value
        }

    override var enableNext: Boolean
        get() = _enableNext
        set(value) {
            _enableNext = value
        }

    override var enablePrev: Boolean
        get() = _enablePrev
        set(value) {
            _enablePrev = value
        }

    override var enableSlider: Boolean
        get() = _enableSlider
        set(value) {
            _enableSlider = value
        }

    override var enablePlay: Boolean
        get() = _enablePlay
        set(value) {
            _enablePlay = value
        }

    override fun muteStates() {
        _enablePrev = false
        _enableNext = false
        _enableSlider = false
        _enablePlay = false
    }

    override fun unMuteStates() {
        _enablePrev = true
        _enableNext = true
        _enableSlider = true
        _enablePlay = true
    }

    override fun clearInfo() {
        _timeProgress = 0f
        _timeTextProgress = ""
        _durationSong = ""
    }

    override fun hidePlayingCard() {
        _isPlayingCardVisible = false
    }

    override fun showPlayingCard() {
        _isPlayingCardVisible = true
    }

    override fun setSongInPlayingCard(song: Song) {
        nowPlayingSong = song
    }
}

@Composable
fun rememberPlayerUiStates(): ControllerUiStates = remember { ControllerUiStatesImpl() }