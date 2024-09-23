package kky.flab.lookaround.feature.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kky.flab.lookaround.core.ui.component.LookaroundCard
import kky.flab.lookaround.feature.home.R

@Composable
fun RecordingStateCard(
    recording: Boolean,
    onStartWalking: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LookaroundCard(
        color = MaterialTheme.colorScheme.primary,
        onClick = onStartWalking,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (recording) stringResource(R.string.status_on_message)
                else stringResource(R.string.status_off_message),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = if (recording) stringResource(R.string.status_on_sub_message)
                else stringResource(R.string.status_off_sub_message),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
fun RecordingStateCardPreview() {
    Column {
        RecordingStateCard(recording = true, onStartWalking = {})
        Spacer(modifier = Modifier.height(10.dp))
        RecordingStateCard(recording = false, onStartWalking = {})
    }
}
