package com.caldeirasoft.outcast.ui.screen.inbox

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
import com.caldeirasoft.outcast.ui.screen.episodelist.base.*
import com.caldeirasoft.outcast.ui.util.isDateTheSame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    episodesRepository: EpisodesRepository,
    downloadRepository: DownloadRepository,
) : EpisodeListViewModel(
    episodesRepository = episodesRepository,
    downloadRepository = downloadRepository
) {
    override fun getPodcastCount(): Flow<List<PodcastWithCount>> =
        episodesRepository.getInboxEpisodesPodcastCount()

    override fun getEpisodesDataSource(): DataSource.Factory<Int, Episode> =
        episodesRepository.getInboxEpisodesDataSource()

    override fun Flow<PagingData<EpisodeUiModel.EpisodeItem>>.insertDateSeparators(): Flow<PagingData<EpisodeUiModel>> =
        map {
            it.insertSeparators { before, after ->
                if (after == null) {
                    // end of the list
                    return@insertSeparators null
                }

                val releaseDate = after.episode.releaseDateTime
                if (before == null) {
                    // we're at the beginning of the lis
                    return@insertSeparators EpisodeUiModel.SeparatorItem(releaseDate)
                }
                // check between 2 items
                if (before.episode.releaseDateTime.isDateTheSame(after.episode.releaseDateTime)) {
                    EpisodeUiModel.SeparatorItem(releaseDate)
                }
                else {
                    // no separator
                    null
                }
            }
        }
}