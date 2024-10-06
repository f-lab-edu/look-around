package kky.flab.lookaround.feature.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kky.flab.lookaround.core.domain.model.Config
import kky.flab.lookaround.core.ui.component.LookaroundTopBar

@Composable
internal fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel()
) {
    val config by viewModel.configState.collectAsStateWithLifecycle()

    SettingScreen(
        config = config,
        onDarkThemeValueChanged = viewModel::setDarkMode
    )
}

@Composable
internal fun SettingScreen(
    config: Config,
    onDarkThemeValueChanged: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LookaroundTopBar(
            title = "설정"
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "다크모드",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Switch(
                checked = config.darkTheme,
                onCheckedChange = onDarkThemeValueChanged
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
internal fun SettingScreenPreview() {
    SettingScreen(
        config = Config(
            requestFineLocation = false,
            requestBackgroundLocation = false,
            requestReadStorage = false,
            darkTheme = false,
        ),
        onDarkThemeValueChanged = {}
    )
}