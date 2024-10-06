package kky.flab.lookaround.feature.main

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kky.flab.lookaround.feature.main.component.MainBottomNavigation
import kky.flab.lookaround.feature.main.component.MainTabs
import kky.flab.lookaround.feature.main.navigation.MainNavHost
import kky.flab.lookaround.feature.main.service.RecordService
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    onRouteRecording: () -> Unit,
    onModifyRecord: (Long) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val onShowSnackBar: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackBarHostState.showSnackbar(
                message = message,
            )
        }
    }

    MainScreen(
        snackBarHostState = snackBarHostState,
        onShowSnackBar = onShowSnackBar,
        onRouteRecording = onRouteRecording,
        onModifyRecord = onModifyRecord,
    )
}

@Composable
private fun MainScreen(
    snackBarHostState: SnackbarHostState,
    navController: NavHostController = rememberNavController(),
    onRouteRecording: () -> Unit,
    onModifyRecord: (Long) -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    val context = LocalContext.current

    val onStartRecordingService = {
        context.startForegroundService(
            Intent(
                context,
                RecordService::class.java
            )
        )

        Unit
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            MainBottomNavigation(
                modifier = Modifier.navigationBarsPadding()
            ) {
                MainTabs.entries.forEach { tab ->
                    bottomNavigationItem(
                        iconDrawableResId = tab.iconResourceId,
                        label = tab.label,
                        selected = currentDestination?.hasRoute(tab.route::class) == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValue ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValue)
        ) {
            MainNavHost(
                navController = navController,
                onStartRecordingService = onStartRecordingService,
                onRouteRecording = onRouteRecording,
                onShowSnackBar = onShowSnackBar,
                onModifyRecord = onModifyRecord,
            )
        }
    }
}