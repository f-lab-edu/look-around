package kky.flab.lookaround.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kky.flab.lookaround.core.ui_navigation.AppNavHost
import kky.flab.lookaround.core.ui_navigation.route.MainRoute
import kky.flab.lookaround.feature.main.component.MainBottomNavigation
import kky.flab.lookaround.feature.main.component.MainTabs
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val snackBarHostState = remember { SnackbarHostState() }

    var currentTab by remember { mutableStateOf(MainTabs.Home) }
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val onShowSnackBar: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackBarHostState.showSnackbar(
                message = message,
            )
        }
    }

    MainScreen(
        currentTab = currentTab,
        navController = navController,
        snackBarHostState = snackBarHostState,
        onShowSnackBar = onShowSnackBar,
        onTabChanged = { tab -> currentTab = tab }
    )
}

@Composable
internal fun MainScreen(
    currentTab: MainTabs,
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    onShowSnackBar: (String) -> Unit,
    onTabChanged: (MainTabs) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = {
            MainBottomNavigation(
                modifier = Modifier.navigationBarsPadding()
            ) {
                MainTabs.entries.forEach { tab ->
                    bottomNavigationItem(
                        iconDrawableResId = tab.iconResourceId,
                        label = tab.label,
                        selected = currentTab == tab,
                        onClick = {
                            when(tab) {
                                MainTabs.Home -> {
                                    navController.navigate(MainRoute.Home)
                                    onTabChanged(tab)
                                }
                                MainTabs.Record -> {
                                    navController.navigate(MainRoute.Record)
                                    onTabChanged(tab)
                                }
                                MainTabs.Setting -> {
                                    navController.navigate(MainRoute.Setting)
                                    onTabChanged(tab)
                                }
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
            AppNavHost(
                navController = navController,
                onShowSnackBar = onShowSnackBar
            )
        }
    }
}