package kky.flab.lookaround.feature.recording.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RecordingInfoRow(
    text: String,
    image: Painter,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 6.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
internal fun RecordingInfoRowPreview() {
    Column {
        RecordingInfoRow(
            text = "00:00",
            image = painterResource(kky.flab.lookaround.core.ui.R.drawable.img_stopwatch)
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordingInfoRow(
            text = "100m",
            image = painterResource(kky.flab.lookaround.core.ui.R.drawable.img_person_walking)
        )
    }
}
