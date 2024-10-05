package kky.flab.lookaround.feature.recording

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kky.flab.lookaround.core.domain.model.Record
import kky.flab.lookaround.core.ui.component.LookaroundTopBar
import kky.flab.lookaround.core.ui.component.NavigationType
import kky.flab.lookaround.core.ui.util.dashedBorder
import kky.flab.lookaround.feature.recording.model.ModifyRecordUiState

@Composable
internal fun ModifyRecordScreen(
    id: Long,
    viewModel: ModifyRecordViewModel = hiltViewModel(),
    onComplete: () -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getRecord(id)
    }

    ModifyRecordScreen(
        uiState = uiState,
        onClose = onComplete,
        onComplete = viewModel::save,
    )
}

@Composable
private fun ModifyRecordScreen(
    uiState: ModifyRecordUiState,
    onClose: () -> Unit,
    onComplete: (String, Uri?) -> Unit,
) {
    var photoUri by remember {
        mutableStateOf<Uri?>(null)
    }

//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { result ->
//        if (result.values.all { it }) {
//            photoP
//        }
//    }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.also { photoUri = it }
    }

    Scaffold(
        topBar = {
            LookaroundTopBar(
                title = "기록하기",
                navigationType = NavigationType.Close,
                onNavigationClick = onClose,
            )
        },
        modifier = Modifier.statusBarsPadding(),
    ) { paddingValues ->
        when (uiState) {
            is ModifyRecordUiState.Result -> {
                var memoState by remember { mutableStateOf(uiState.record.memo) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clickable {
                                photoPicker.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                            .dashedBorder(color = MaterialTheme.colorScheme.primary)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = stringResource(R.string.add_photo),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 10.dp),
                            )
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth().height(150.dp)
                        ) {
                            CoilImage(
                                imageModel = { photoUri },
                            )
                        }
                    }
                    TextField(
                        value = memoState,
                        onValueChange = { memoState = it },
                        textStyle = MaterialTheme.typography.bodySmall,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.add_memo),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .weight(1.0f),
                    )
                    TextButton(
                        onClick = {
                            onComplete(memoState, photoUri)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Text(
                            text = "저장하기",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
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
private fun ModifyRecordScreenPreview() {
    ModifyRecordScreen(
        uiState = ModifyRecordUiState.Result(
            record = Record(
                id = 0,
                memo = "preview memo",
                imageUri = "",
                path = emptyList(),
                startTimeStamp = 0,
                endTimeStamp = 0,
                distance = 0,
            )
        ),
        onClose = {},
        onComplete = { _, _ -> },
    )
}