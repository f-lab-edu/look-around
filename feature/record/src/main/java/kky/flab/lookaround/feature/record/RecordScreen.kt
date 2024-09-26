package kky.flab.lookaround.feature.record

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kky.flab.lookaround.core.ui.component.LookaroundAlertDialog
import kky.flab.lookaround.core.ui.theme.LookaroundTheme
import kky.flab.lookaround.feature.record.component.RecordCard
import kky.flab.lookaround.feature.record.model.RecordUiModel
import kky.flab.lookaround.feature.record.model.RecordUiState
import kky.flab.lookaround.feature.recording.ModifyRecordActivity

@Composable
fun RecordScreen(
    viewModel: RecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var dialogType by remember { mutableStateOf<RecordDialogType>(RecordDialogType.Dismiss) }

    when(dialogType) {
        RecordDialogType.Dismiss -> {}
        is RecordDialogType.RecordDelete -> {
            LookaroundAlertDialog(
                text = stringResource(R.string.record_delete_message),
                onConfirm = {
                    val record = (dialogType as RecordDialogType.RecordDelete).record
                    viewModel.delete(record)
                    dialogType = RecordDialogType.Dismiss
                },
                onDismiss = {
                    dialogType = RecordDialogType.Dismiss
                }
            )
        }
    }

    RecordScreen(
        uiState,
        onModifyClick = { record ->
            context.startActivity(
                Intent(
                    context,
                    ModifyRecordActivity::class.java
                ).putExtra(ModifyRecordActivity.EXTRA_RECORD_ID, record.id)
            )
        },
        onDeleteClick = { record ->
            dialogType = RecordDialogType.RecordDelete(record)
        },
    )
}

@Composable
internal fun RecordScreen(
    uiState: RecordUiState,
    onModifyClick: (RecordUiModel) -> Unit,
    onDeleteClick: (RecordUiModel) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "내 산책",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (uiState) {
                RecordUiState.Loading -> CircularProgressIndicator()
                is RecordUiState.Result -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        )
                    ) {
                        items(uiState.records) { record ->
                            RecordCard(
                                record = record,
                                modifier = Modifier.fillMaxWidth(),
                                onModifyClick = { onModifyClick(record) },
                                onDeleteClick = { onDeleteClick(record) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
fun RecordScreenPreview() {
    LookaroundTheme {
        RecordScreen(
            RecordUiState.Result(
                listOf(
                    RecordUiModel(
                        id = 0,
                        memo = "Preview Memo",
                        imageUri = null,
                        date = "2024.01.11",
                        runTime = "24:54",
                        startTimeStamp = 0,
                        endTimeStamp = 0,
                        path = emptyList(),
                        distance = "400m"
                    )
                )
            ),
            onModifyClick = {},
            onDeleteClick = {},
        )
    }
}

internal sealed interface RecordDialogType {
    data object Dismiss : RecordDialogType

    data class RecordDelete(val record: RecordUiModel) : RecordDialogType
}
