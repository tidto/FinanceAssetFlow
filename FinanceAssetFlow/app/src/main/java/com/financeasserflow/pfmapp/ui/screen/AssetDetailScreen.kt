package com.financeasserflow.pfmapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.financeasserflow.pfmapp.data.model.AssetEntity
import com.financeasserflow.pfmapp.data.model.AssetHistoryEntity
import com.financeasserflow.pfmapp.data.model.currentValue
import com.financeasserflow.pfmapp.data.model.isInvestmentAsset
import com.financeasserflow.pfmapp.data.model.profitAmount
import com.financeasserflow.pfmapp.data.model.profitRate
import com.financeasserflow.pfmapp.viewmodel.AssetDetailUiState
import com.financeasserflow.pfmapp.viewmodel.AssetViewModel
import com.financeasserflow.pfmapp.viewmodel.toMoneyText
import com.financeasserflow.pfmapp.viewmodel.toPercentText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AssetDetailScreen(
    viewModel: AssetViewModel,
    assetId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit,
) {
    val detailState by viewModel.observeAssetDetail(assetId).collectAsStateWithLifecycle(initialValue = AssetDetailUiState())
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "자산 상세") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                detailState.asset?.let { asset ->
                    DetailHeader(asset = asset)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(modifier = Modifier.weight(1f), onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        Text(text = "수정")
                    }
                    OutlinedButton(modifier = Modifier.weight(1f), onClick = { showDeleteDialog = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Text(text = "삭제")
                    }
                }
            }

            item {
                Text(
                    text = "변경 이력",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (detailState.histories.isEmpty()) {
                item {
                    Surface {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = "아직 변경 이력이 없습니다.",
                        )
                    }
                }
            } else {
                items(detailState.histories) { history ->
                    HistoryItem(history = history)
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    if (showDeleteDialog) {
        val assetName = detailState.asset?.name ?: "이 자산"
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "삭제할까요?") },
            text = { Text(text = "'$assetName'을 삭제하면 이력을 포함한 모든 데이터가 사라집니다.") },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAsset(assetId = assetId, onDeleted = onDeleted)
                    },
                ) {
                    Text(text = "삭제")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "취소")
                }
            },
        )
    }
}

@Composable
private fun DetailHeader(asset: AssetEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = asset.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(text = asset.assetType.label) })
                AssistChip(onClick = {}, label = { Text(text = asset.category.label) })
            }
            Text(text = "현재 금액: ${asset.currentValue().toMoneyText()}")
            if (asset.memo.orEmpty().isNotBlank()) {
                Text(
                    text = "메모: ${asset.memo}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (asset.isInvestmentAsset()) {
                val profit = asset.profitAmount()
                val rate = asset.profitRate()
                Text(text = "매입금액: ${asset.principalAmount?.toMoneyText() ?: "-"}")
                Text(text = "평가금액: ${asset.valuationAmount?.toMoneyText() ?: "-"}")
                if (profit != null && rate != null) {
                    Text(
                        text = "평가손익: ${if (profit >= 0) "+" else ""}${profit.toMoneyText()} (${if (rate >= 0) "+" else ""}${rate.toPercentText()})",
                        color = if (profit >= 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(history: AssetHistoryEntity) {
    val dateText = remember(history.createdAt) {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(Date(history.createdAt))
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = history.changeType.label, fontWeight = FontWeight.SemiBold)
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            history.previousAmount?.let {
                Text(
                    text = "변경 전: ${it.toMoneyText()}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            history.newAmount?.let {
                Text(
                    text = "변경 후: ${it.toMoneyText()}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (!history.memo.isNullOrBlank()) {
                Text(
                    text = history.memo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
