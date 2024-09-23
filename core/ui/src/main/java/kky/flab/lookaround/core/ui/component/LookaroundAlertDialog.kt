package kky.flab.lookaround.core.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun LookaroundAlertDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    showDismissButton: Boolean = true,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            if (showDismissButton) {
                TextButton(
                    onClick = onDismiss,
                    content = {
                        Text("취소")
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                content = {
                    Text("확인")
                }
            )
        },
        text = { Text(text = text) },
        textContentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp
    )
}