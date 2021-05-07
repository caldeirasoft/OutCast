package com.caldeirasoft.outcast.ui.components.bottomsheet

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.model.PreferenceItem
import com.caldeirasoft.outcast.ui.components.PodcastDefaults
import com.caldeirasoft.outcast.ui.components.PodcastThumbnail
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastActions
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastState

interface BaseBottomSheetMenuItem

object BottomSheetSeparator : BaseBottomSheetMenuItem

class BottomSheetMenuItem(
    @StringRes val titleId: Int,
    val icon: ImageVector? = null,
    val singleLineTitle: Boolean = false,
    val enabled: Boolean = true,
    val visible: Boolean = true,
    val onClick: () -> Unit = { },
) : BaseBottomSheetMenuItem