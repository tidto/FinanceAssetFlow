package com.financeasserflow.pfmapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

private val LightColors = lightColorScheme(
    primary = FinancePrimary,
    secondary = FinanceSecondary,
    tertiary = FinanceTertiary,
    surface = FinanceSurface,
)

private val DarkColors = darkColorScheme(
    primary = FinancePrimary,
    secondary = FinanceSecondary,
    tertiary = FinanceTertiary,
    surface = FinanceSurfaceDark,
)

@Composable
fun FinanceAssetFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
