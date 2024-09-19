package kky.flab.lookaround.feature.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kky.flab.lookaround.core.ui.component.LookaroundAlertDialog
import kky.flab.lookaround.feature.home.R

@Composable
fun LocationPermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    LookaroundAlertDialog(
        text = stringResource(R.string.request_location_permission_message),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

@Composable
fun StartRecordDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    LookaroundAlertDialog(
        text = stringResource(R.string.start_record_message),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

enum class DialogType {
    Dismiss,
    LoadWeatherPermission,
    StartRecord,
}