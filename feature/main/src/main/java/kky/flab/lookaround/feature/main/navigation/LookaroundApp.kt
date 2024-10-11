package kky.flab.lookaround.feature.main.navigation

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.util.Consumer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kky.flab.lookaround.core.ui_navigation.route.AppRoute
import kky.flab.lookaround.feature.main.LocalActivity
import kky.flab.lookaround.feature.main.MainScreen
import kky.flab.lookaround.feature.recording.navigation.recordingGraph

@Composable
fun LookaroundApp() {
    val navController = rememberNavController()
    val activity = LocalActivity.current

    DisposableEffect(activity, navController) {
        val onNewIntentConsumer = Consumer<Intent> {
            val askFinish =
                it.data?.getQueryParameter("askFinish")?.toBoolean()
                ?: false
            navController.navigate(
                AppRoute.Recording(
                    askFinish = askFinish
                )
            )
        }

        activity.addOnNewIntentListener(onNewIntentConsumer)

        onDispose {
            activity.removeOnNewIntentListener(onNewIntentConsumer)
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Main,
    ) {
        composable<AppRoute.Main> {
            MainScreen(
                onRouteRecording = {
                    navController.navigate(AppRoute.Recording(askFinish = false))
                },
                onModifyRecord = { recordId ->
                    navController.navigate(AppRoute.ModifyRecord(id = recordId))
                },
            )
        }

        recordingGraph(
            onClose = { navController.popBackStack() },
            onFinishRecord = { recordId ->
                navController.navigate(
                    AppRoute.ModifyRecord(recordId)
                ) {
                    popUpTo<AppRoute.Main>()
                }
            }
        )
    }
}