package com.financeasserflow.pfmapp.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.financeasserflow.pfmapp.ui.screen.AssetDetailScreen
import com.financeasserflow.pfmapp.ui.screen.AssetEntryScreen
import com.financeasserflow.pfmapp.ui.screen.DashboardScreen
import com.financeasserflow.pfmapp.ui.screen.PortfolioScreen
import com.financeasserflow.pfmapp.viewmodel.AssetViewModel

private object Routes {
    const val DASHBOARD = "dashboard"
    const val ENTRY = "entry"
    const val DETAIL = "detail"
    const val PORTFOLIO = "portfolio"
}

@Composable
fun FinanceAssetFlowApp() {
    val viewModel: AssetViewModel = hiltViewModel()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD,
    ) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                viewModel = viewModel,
                onAddAsset = {
                    viewModel.startNewAsset()
                    navController.navigate("${Routes.ENTRY}?assetId=-1")
                },
                onOpenAsset = { assetId ->
                    navController.navigate("${Routes.DETAIL}/$assetId")
                },
                onOpenPortfolio = {
                    navController.navigate(Routes.PORTFOLIO)
                },
            )
        }

        composable(
            route = "${Routes.ENTRY}?assetId={assetId}",
            arguments = listOf(navArgument("assetId") {
                type = NavType.LongType
                defaultValue = -1L
            }),
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getLong("assetId")?.takeIf { it > 0L }
            AssetEntryScreen(
                viewModel = viewModel,
                assetId = assetId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable(
            route = "${Routes.DETAIL}/{assetId}",
            arguments = listOf(navArgument("assetId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getLong("assetId") ?: return@composable
            AssetDetailScreen(
                viewModel = viewModel,
                assetId = assetId,
                onBack = { navController.popBackStack() },
                onEdit = {
                    navController.navigate("${Routes.ENTRY}?assetId=$assetId")
                },
                onDeleted = {
                    navController.popBackStack(Routes.DASHBOARD, inclusive = false)
                },
            )
        }

        composable(Routes.PORTFOLIO) {
            PortfolioScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
