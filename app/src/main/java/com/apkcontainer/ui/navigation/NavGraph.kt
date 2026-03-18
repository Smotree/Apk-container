package com.apkcontainer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.apkcontainer.ui.screen.analysis.AnalysisScreen
import com.apkcontainer.ui.screen.detail.AppDetailScreen
import com.apkcontainer.ui.screen.home.HomeScreen
import com.apkcontainer.ui.screen.install.InstallScreen
import com.apkcontainer.ui.screen.network.NetworkScreen
import com.apkcontainer.ui.screen.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val INSTALL = "install"
    const val ANALYSIS = "analysis/{apkPath}"
    const val DETAIL = "detail/{appId}"
    const val NETWORK = "network"
    const val SETTINGS = "settings"

    fun analysis(apkPath: String) = "analysis/${java.net.URLEncoder.encode(apkPath, "UTF-8")}"
    fun detail(appId: Long) = "detail/$appId"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onThemeChanged: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onInstallClick = { navController.navigate(Routes.INSTALL) },
                onAppClick = { appId -> navController.navigate(Routes.detail(appId)) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onNetworkClick = { navController.navigate(Routes.NETWORK) }
            )
        }

        composable(Routes.INSTALL) {
            InstallScreen(
                onApkSelected = { path ->
                    navController.navigate(Routes.analysis(path))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.ANALYSIS,
            arguments = listOf(navArgument("apkPath") { type = NavType.StringType })
        ) { backStackEntry ->
            val apkPath = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("apkPath") ?: "",
                "UTF-8"
            )
            AnalysisScreen(
                apkPath = apkPath,
                onInstalled = { appId ->
                    navController.navigate(Routes.detail(appId)) {
                        popUpTo(Routes.HOME)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("appId") { type = NavType.LongType })
        ) {
            AppDetailScreen(
                onBack = { navController.popBackStack() },
                onDeleted = {
                    navController.popBackStack(Routes.HOME, false)
                }
            )
        }

        composable(Routes.NETWORK) {
            NetworkScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onThemeChanged = onThemeChanged
            )
        }
    }
}
