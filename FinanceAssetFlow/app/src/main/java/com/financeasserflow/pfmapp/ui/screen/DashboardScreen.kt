package com.financeasserflow.pfmapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.financeasserflow.pfmapp.data.model.AssetCategory
import com.financeasserflow.pfmapp.ui.components.AssetCard
import com.financeasserflow.pfmapp.viewmodel.AssetViewModel
import com.financeasserflow.pfmapp.viewmodel.DashboardUiState
import com.financeasserflow.pfmapp.viewmodel.toMoneyText
import com.financeasserflow.pfmapp.viewmodel.toPercentText

@Composable
fun DashboardScreen(
    viewModel: AssetViewModel,
    onAddAsset: () -> Unit,
    onOpenAsset: (Long) -> Unit,
    onOpenPortfolio: () -> Unit,
) {
    val state by viewModel.dashboardUiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var searchVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "FinanceAssetFlow") },
                actions = {
                    IconButton(onClick = { searchVisible = !searchVisible }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "검색")
                    }
                    IconButton(onClick = onOpenPortfolio) {
                        Icon(imageVector = Icons.Default.PieChart, contentDescription = "포트폴리오 목표 설정")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAsset) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "자산 추가")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                AnimatedVisibility(visible = searchVisible) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        value = searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        label = { Text("자산 검색") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.clearSearch() }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "검색 초기화")
                                }
                            }
                        },
                    )
                }
            }

            item {
                SummarySection(state = state)
            }

            if (state.warningMessage != null) {
                item {
                    WarningBanner(message = state.warningMessage!!)
                }
            }

            item {
                CategoryBarSection(state = state)
            }

            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "자산 목록" else "검색 결과 (${state.items.size}건)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (state.items.isEmpty()) {
                item {
                    EmptyState(isSearch = searchQuery.isNotBlank())
                }
            } else {
                items(state.items, key = { it.id }) { item ->
                    AssetCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        item = item,
                        onClick = { onOpenAsset(item.id) },
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(76.dp))
            }
        }
    }
}

@Composable
private fun SummarySection(state: DashboardUiState) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "순자산", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = state.netWorth.toMoneyText(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(label = "총자산", value = state.totalAsset.toMoneyText(), modifier = Modifier.weight(1f))
            MetricCard(label = "총부채", value = state.totalDebt.toMoneyText(), modifier = Modifier.weight(1f))
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "투자 비중", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = state.investmentRatio.toPercentText(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "목표 ${state.investmentTargetRatio.toPercentText()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WarningBanner(message: String) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun CategoryBarSection(state: DashboardUiState) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "자산 비중", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))

            state.categoryBars.forEach { bar ->
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "${bar.category.label}  ${bar.ratio.toPercentText()}")
                        Text(text = bar.amount.toMoneyText())
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        val color = when (bar.category) {
                            AssetCategory.CASH -> MaterialTheme.colorScheme.secondary
                            AssetCategory.SAVINGS -> MaterialTheme.colorScheme.primary
                            AssetCategory.INVESTMENT -> MaterialTheme.colorScheme.tertiary
                            AssetCategory.LIABILITY -> MaterialTheme.colorScheme.error
                            AssetCategory.ETC -> MaterialTheme.colorScheme.outline
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((bar.ratio / 100.0).toFloat().coerceIn(0f, 1f))
                                .height(10.dp)
                                .background(color),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(isSearch: Boolean) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = if (isSearch) {
                "검색 결과가 없습니다."
            } else {
                "등록된 자산이 없습니다. 오른쪽 아래 + 버튼으로 첫 자산을 추가해 보세요."
            },
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
