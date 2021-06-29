package com.caldeirasoft.outcast.ui.screen.saved_episodes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.PodcastWithCount
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.EpisodesRepository
import com.caldeirasoft.outcast.ui.screen.episodelist.BaseEpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SavedEpisodesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    episodesRepository: EpisodesRepository,
    downloadRepository: DownloadRepository,
) : EpisodeListViewModel(
    episodesRepository = episodesRepository,
    downloadRepository = downloadRepository
) {
    override fun getPodcastCount(): Flow<List<PodcastWithCount>> =
        episodesRepository.getSavedEpisodesPodcastCount()

    override fun getEpisodesDataSource(): DataSource.Factory<Int, Episode> =
        episodesRepository.getSavedEpisodesDataSource()
}