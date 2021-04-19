package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.InboxRepository
import com.caldeirasoft.outcast.db.Episode
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityScoped
class FetchInboxUseCase @Inject constructor(
    val inboxRepository: InboxRepository,
) : FlowUseCaseWithoutParams<List<Episode>> {
    override fun execute(): Flow<List<Episode>> = inboxRepository.fetchEpisodes()
}