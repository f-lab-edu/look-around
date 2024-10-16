package kky.flab.lookaround.feature.recording

import android.content.Intent
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.landscapist.coil.CoilImage
import kky.flab.lookaround.core.domain.model.Record
import kky.flab.lookaround.core.ui.component.LookaroundTopBar
import kky.flab.lookaround.core.ui.component.NavigationType
import kky.flab.lookaround.core.ui.util.dashedBorder
import kky.flab.lookaround.feature.recording.model.ModifyRecordEffect
import kky.flab.lookaround.feature.recording.model.ModifyRecordUiState
import kotlinx.coroutines.launch

@Composable
internal fun ModifyRecordScreen(
    id: Long,
    viewModel: ModifyRecordViewModel = hiltViewModel(),
    onComplete: () -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val contentResolver = LocalContext.current.contentResolver

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val onShowSnackBar: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getRecord(id)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                is ModifyRecordEffect.Error -> {
                    onShowSnackBar(it.message)
                }
                ModifyRecordEffect.SaveRecord -> {
                    onComplete()
                }
            }
        }
    }

    ModifyRecordScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onClose = onComplete,
        onComplete = { memo, photoUri ->
            if (photoUri != null) {
                contentResolver.takePersistableUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            viewModel.save(memo, photoUri)
        },
    )
}

@Composable
private fun ModifyRecordScreen(
    uiState: ModifyRecordUiState,
    snackbarHostState: SnackbarHostState,
    onClose: () -> Unit,
    onComplete: (String, Uri?) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            ModifyRecordUiState.Empty -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
            is ModifyRecordUiState.Result -> {
                var memoState by remember { mutableStateOf(uiState.record.memo) }

                var photoUri by remember {
                    mutableStateOf(
                        (uiState as? ModifyRecordUiState.Result)
                            ?.record
                            ?.image
                            ?.run {
                                if (isNotEmpty()) toUri()
                                else null
                            }
                    )
                }

                val photoPicker = rememberLauncherForActivityResult(
                    ActivityResultContracts.PickVisualMedia()
                ) { uri ->
                    uri?.also { photoUri = it }
                }

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
                            .height(200.dp)
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

                        if (photoUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                CoilImage(
                                    imageModel = { photoUri },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                IconButton(
                                    onClick = {
                                        photoUri = null
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_cancel_24),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .size(24.dp)
                                            .align(Alignment.TopStart)
                                    )
                                }
                            }
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
                image = "",
                path = emptyList(),
                startTimeStamp = 0,
                endTimeStamp = 0,
                distance = 0,
            )
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onClose = {},
        onComplete = { _, _ -> },
    )
}
