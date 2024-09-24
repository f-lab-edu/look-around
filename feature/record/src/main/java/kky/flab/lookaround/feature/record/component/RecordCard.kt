package kky.flab.lookaround.feature.record.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kky.flab.lookaround.core.ui.component.LookaroundCard
import kky.flab.lookaround.feature.record.model.RecordUiModel

@Composable
internal fun RecordCard(
    record: RecordUiModel,
    modifier: Modifier = Modifier,
    onModifyClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    LookaroundCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (record.imageUri != null) {
                CoilImage(
                    imageModel = { record.imageUri },
                    imageOptions = ImageOptions(
                        requestSize = IntSize(1000, 1000)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                )
            }
            Text(
                text = record.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = (if (record.imageUri != null) 10 else 16).dp,
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                RecordInfoContainer(
                    resourceId = kky.flab.lookaround.core.ui.R.drawable.img_stopwatch,
                    text = record.runTime,
                    modifier = Modifier.weight(1.0f)
                )
                RecordInfoContainer(
                    resourceId = kky.flab.lookaround.core.ui.R.drawable.img_person_walking,
                    text = record.distance,
                    modifier = Modifier.weight(1.0f)
                )
            }
            Text(
                text = record.memo,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 10.dp,
                )
            )
            TextButton(
                onClick = onModifyClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "수정하기",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            OutlinedButton(
                onClick = onDeleteClick,
                shape = RoundedCornerShape(10.dp),

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 6.dp)
            ) {
                Text(
                    text = "삭제하기",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
