package com.financeasserflow.pfmapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.financeasserflow.pfmapp.data.model.AssetCategory
import com.financeasserflow.pfmapp.viewmodel.AssetViewModel
import com.financeasserflow.pfmapp.viewmodel.PortfolioEditUiState
import com.financeasserflow.pfmapp.viewmodel.PortfolioTargetInputItem
import com.financeasserflow.pfmapp.viewmodel.RebalancingAction
import com.financeasserflow.pfmapp.viewmodel.RebalancingItem
import com.financeasserflow.pfmapp.viewmodel.toMoneyText
import com.financeasserflow.pfmapp.viewmodel.toPercentText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: AssetViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.portfolioUiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadPortfolioForEdit()
    }

    LaunchedEffect(state.snackbarMessage) {
        val message = state.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.consumePortfolioSnackbar()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "포트폴리오 목표 설정") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                PortfolioInfoCard()
                PortfolioTargetList(
                    state = state,
                    onRatioChange = viewModel::onPortfolioTargetRatioChange,
                )
                Spacer(modifier = Modifier.height(8.dp))
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving,
                    onClick = { viewModel.savePortfolioTargets() },
                ) {
                    Text(text = if (state.isSaving) "저장 중..." else "목표 비율 저장")
                }
                if (state.rebalancing.isNotEmpty()) {
                    RebalancingSection(
                        items = state.rebalancing,
                        totalAsset = state.totalAsset,
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "목표 비율 안내",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "각 자산군의 목표 비율을 설정하세요. 현재 비중이 목표를 초과하면 대시보드에 경고가 표시됩니다. 빈칸으로 두면 해당 카테고리는 경고 없이 유지됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun PortfolioTargetList(
    state: PortfolioEditUiState,
    onRatioChange: (AssetCategory, String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "자산군별 목표 설정",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            state.items.forEachIndexed { index, item ->
                PortfolioTargetRow(
                    item = item,
                    onRatioChange = { value -> onRatioChange(item.category, value) },
                )
                if (index < state.items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }
            }
        }
    }
}

@Composable
private fun PortfolioTargetRow(
    item: PortfolioTargetInputItem,
    onRatioChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = item.category.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "현재: ${item.currentRatio.toPercentText()} (${item.currentAmount.toMoneyText()})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                modifier = Modifier.width(120.dp),
                value = item.targetRatioInput,
                onValueChange = onRatioChange,
                label = { Text("목표 %") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("%") },
            )
        }
    }
}

@Composable
private fun RebalancingSection(
    items: List<RebalancingItem>,
    totalAsset: Long,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "리밸런싱 계산기",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "목표 비율에 맞추려면 아래와 같이 조정하세요. (총자산 기준: ${totalAsset.toMoneyText()})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))

            items.forEachIndexed { index, item ->
                RebalancingRow(item = item)
                if (index < items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                }
            }
        }
    }
}

@Composable
private fun RebalancingRow(item: RebalancingItem) {
    val (actionLabel, actionColor) = when (item.action) {
        RebalancingAction.BUY -> "추가 매수" to MaterialTheme.colorScheme.primary
        RebalancingAction.SELL -> "매도 권장" to MaterialTheme.colorScheme.error
        RebalancingAction.BALANCED -> "균형 유지" to MaterialTheme.colorScheme.tertiary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.category.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "현재 ${item.currentAmount.toMoneyText()} → 목표 ${item.targetAmount.toMoneyText()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = actionColor,
            )
            if (item.action != RebalancingAction.BALANCED) {
                Text(
                    text = item.deltaAmount.toMoneyText(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = actionColor,
                )
            } else {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.titleSmall,
                    color = actionColor,
                )
            }
        }
    }
}
