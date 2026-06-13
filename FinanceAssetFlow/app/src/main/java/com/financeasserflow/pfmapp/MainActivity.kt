package com.financeasserflow.pfmapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.financeasserflow.pfmapp.ui.FinanceAssetFlowApp
import com.financeasserflow.pfmapp.ui.theme.FinanceAssetFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceAssetFlowTheme {
                FinanceAssetFlowApp()
            }
        }
    }
}
