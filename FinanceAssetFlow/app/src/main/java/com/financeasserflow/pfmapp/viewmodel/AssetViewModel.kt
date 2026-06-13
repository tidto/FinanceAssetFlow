package com.financeasserflow.pfmapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financeasserflow.pfmapp.data.model.AssetCategory
import com.financeasserflow.pfmapp.data.model.AssetEntity
import com.financeasserflow.pfmapp.data.model.AssetHistoryEntity
import com.financeasserflow.pfmapp.data.model.AssetType
import com.financeasserflow.pfmapp.data.model.ChangeType
import com.financeasserflow.pfmapp.data.model.PortfolioTargetEntity
import com.financeasserflow.pfmapp.data.model.currentValue
import com.financeasserflow.pfmapp.data.model.isDebt
import com.financeasserflow.pfmapp.data.model.isInvestmentAsset
import com.financeasserflow.pfmapp.data.model.profitAmount
import com.financeasserflow.pfmapp.data.model.profitRate
import com.financeasserflow.pfmapp.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val repository: AssetRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _editorState = MutableStateFlow(AssetEditorUiState())
    val editorUiState = _editorState.asStateFlow()

    private val _portfolioEditState = MutableStateFlow(PortfolioEditUiState())
    val portfolioUiState = _portfolioEditState.asStateFlow()

    val dashboardUiState: StateFlow<DashboardUiState> =
        combine(
            combine(repository.observeAssets(), repository.observeTargets(), _searchQuery) { a, b, c -> Triple(a, b, c) },
            repository.observeAllHistories(),
        ) { (assets, targets, query), histories ->
            buildDashboardState(assets, targets, query, histories)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState(),
        )

    init {
        viewModelScope.launch {
            repository.ensureSeedData()
        }
    }

    fun observeAssetDetail(assetId: Long): Flow<AssetDetailUiState> {
        return combine(
            repository.observeAsset(assetId),
            repository.observeHistories(assetId),
        ) { asset, histories ->
            AssetDetailUiState(asset = asset, histories = histories)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun startNewAsset() {
        _editorState.value = AssetEditorUiState()
    }

    fun loadAsset(assetId: Long) {
        viewModelScope.launch {
            val asset = repository.getAssetOnce(assetId) ?: return@launch
            _editorState.value = AssetEditorUiState(
                assetId = asset.id,
                name = asset.name,
                assetType = asset.assetType,
                category = asset.category,
                amount = if (asset.isInvestmentAsset()) "" else asset.amount.toString(),
                principalAmount = asset.principalAmount?.toString().orEmpty(),
                valuationAmount = asset.valuationAmount?.toString().orEmpty(),
                memo = asset.memo.orEmpty(),
            )
        }
    }

    fun onNameChange(value: String) = _editorState.update { it.copy(name = value, snackbarMessage = null) }

    fun onAssetTypeChange(value: AssetType) {
        _editorState.update {
            val newCategory = if (value == AssetType.LIABILITY) {
                AssetCategory.LIABILITY
            } else if (it.category == AssetCategory.LIABILITY) {
                AssetCategory.CASH
            } else {
                it.category
            }
            it.copy(assetType = value, category = newCategory, snackbarMessage = null)
        }
    }

    fun onCategoryChange(value: AssetCategory) {
        _editorState.update {
            val newType = if (value == AssetCategory.LIABILITY) AssetType.LIABILITY else AssetType.ASSET
            it.copy(category = value, assetType = newType, snackbarMessage = null)
        }
    }

    fun onAmountChange(value: String) =
        _editorState.update { it.copy(amount = value.filter(Char::isDigit), snackbarMessage = null) }

    fun onPrincipalAmountChange(value: String) =
        _editorState.update { it.copy(principalAmount = value.filter(Char::isDigit), snackbarMessage = null) }

    fun onValuationAmountChange(value: String) =
        _editorState.update { it.copy(valuationAmount = value.filter(Char::isDigit), snackbarMessage = null) }

    fun onMemoChange(value: String) =
        _editorState.update { it.copy(memo = value, snackbarMessage = null) }

    fun saveCurrentAsset() {
        val state = _editorState.value
        val validationError = validateAssetInput(state)
        if (validationError != null) {
            _editorState.update { it.copy(snackbarMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _editorState.update { it.copy(isSaving = true, snackbarMessage = null) }
            try {
                repository.saveAsset(state.toEntity())
                _editorState.update { it.copy(isSaving = false, snackbarMessage = "저장되었습니다.") }
            } catch (throwable: Throwable) {
                _editorState.update {
                    it.copy(
                        isSaving = false,
                        snackbarMessage = throwable.message ?: "저장에 실패했습니다.",
                    )
                }
            }
        }
    }

    fun deleteAsset(assetId: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAsset(assetId)
            onDeleted()
        }
    }

    fun consumeSnackbarMessage() {
        _editorState.update { it.copy(snackbarMessage = null) }
    }

    // ── Portfolio management ─────────────────────────────────────────────────

    fun loadPortfolioForEdit() {
        viewModelScope.launch {
            _portfolioEditState.update { it.copy(isLoading = true) }
            val targets = repository.observeTargets().first()
            val assets = repository.observeAssets().first()

            val assetOnly = assets.filter { it.assetType == AssetType.ASSET }
            val totalAsset = assetOnly.sumOf { it.currentValue() }

            val items = AssetCategory.entries
                .filter { it != AssetCategory.LIABILITY }
                .map { category ->
                    val currentAmount = assetOnly
                        .filter { it.category == category }
                        .sumOf { it.currentValue() }
                    val currentRatio = if (totalAsset == 0L) {
                        0.0
                    } else {
                        currentAmount.toDouble() * 100.0 / totalAsset.toDouble()
                    }
                    val targetRatio = targets.firstOrNull { it.category == category }?.targetRatio ?: 0.0
                    PortfolioTargetInputItem(
                        category = category,
                        currentRatio = currentRatio,
                        currentAmount = currentAmount,
                        targetRatioInput = if (targetRatio == 0.0) "" else targetRatio.toInt().toString(),
                    )
                }

            _portfolioEditState.update { it.copy(items = items, isLoading = false) }
        }
    }

    fun onPortfolioTargetRatioChange(category: AssetCategory, value: String) {
        _portfolioEditState.update { state ->
            val cleaned = value.filter { it.isDigit() || it == '.' }
            state.copy(
                items = state.items.map { item ->
                    if (item.category == category) item.copy(targetRatioInput = cleaned) else item
                },
            )
        }
    }

    fun savePortfolioTargets() {
        val state = _portfolioEditState.value
        viewModelScope.launch {
            _portfolioEditState.update { it.copy(isSaving = true, snackbarMessage = null) }
            try {
                state.items.forEach { item ->
                    val ratio = item.targetRatioInput.toDoubleOrNull() ?: return@forEach
                    if (ratio >= 0.0) {
                        repository.upsertPortfolioTarget(
                            PortfolioTargetEntity(
                                category = item.category,
                                targetRatio = ratio,
                            ),
                        )
                    }
                }
                _portfolioEditState.update {
                    it.copy(isSaving = false, snackbarMessage = "목표 비율이 저장되었습니다.")
                }
            } catch (throwable: Throwable) {
                _portfolioEditState.update {
                    it.copy(
                        isSaving = false,
                        snackbarMessage = throwable.message ?: "저장에 실패했습니다.",
                    )
                }
            }
        }
    }

    fun consumePortfolioSnackbar() {
        _portfolioEditState.update { it.copy(snackbarMessage = null) }
    }

    // ── Internal helpers ─────────────────────────────────────────────────────

    private fun AssetEditorUiState.toEntity(): AssetEntity {
        val isInvestment = assetType == AssetType.ASSET && category == AssetCategory.INVESTMENT
        val amountValue = when {
            assetType == AssetType.LIABILITY -> amount.toLong()
            isInvestment -> valuationAmount.toLong()
            else -> amount.toLong()
        }

        return AssetEntity(
            id = assetId ?: 0L,
            name = name.trim(),
            assetType = assetType,
            category = category,
            amount = amountValue,
            principalAmount = if (isInvestment) principalAmount.toLong() else null,
            valuationAmount = if (isInvestment) valuationAmount.toLong() else null,
            memo = memo.trim().ifBlank { null },
        )
    }

    private fun buildDashboardState(
        assets: List<AssetEntity>,
        targets: List<PortfolioTargetEntity>,
        query: String,
        histories: List<AssetHistoryEntity> = emptyList(),
    ): DashboardUiState {
        val assetOnly = assets.filter { it.assetType == AssetType.ASSET }
        val debtOnly = assets.filter { it.assetType == AssetType.LIABILITY }

        val totalAsset = assetOnly.sumOf { it.currentValue() }
        val totalDebt = debtOnly.sumOf { it.currentValue() }
        val netWorth = totalAsset - totalDebt

        val investmentValue = assetOnly
            .filter { it.category == AssetCategory.INVESTMENT }
            .sumOf { it.currentValue() }

        val investmentRatio = if (totalAsset == 0L) {
            0.0
        } else {
            investmentValue.toDouble() * 100.0 / totalAsset.toDouble()
        }

        val investmentTargetRatio = targets.firstOrNull { it.category == AssetCategory.INVESTMENT }?.targetRatio ?: 70.0
        val warningMessage = if (investmentRatio > investmentTargetRatio) {
            "투자자산 비중이 ${investmentRatio.toPercentText()}로 목표 ${investmentTargetRatio.toPercentText()}를 초과했습니다."
        } else {
            null
        }

        val categoryBars = AssetCategory.entries
            .filter { it != AssetCategory.LIABILITY }
            .map { category ->
                val amount = assetOnly
                    .filter { it.category == category }
                    .sumOf { it.currentValue() }
                val ratio = if (totalAsset == 0L) 0.0 else amount.toDouble() * 100.0 / totalAsset.toDouble()
                CategoryBarUiState(category = category, amount = amount, ratio = ratio)
            }
            .filter { it.amount > 0L }

        val allItems = assets.map { asset ->
            val returnText = if (asset.isInvestmentAsset()) {
                val profit = asset.profitAmount()
                val rate = asset.profitRate()
                if (profit != null && rate != null) {
                    "${if (profit >= 0) "+" else ""}${profit.toMoneyText()} / ${if (rate >= 0) "+" else ""}${rate.toPercentText()}"
                } else {
                    null
                }
            } else {
                null
            }

            AssetListItemUiState(
                id = asset.id,
                name = asset.name,
                category = asset.category.label,
                type = asset.assetType,
                amountText = if (asset.isDebt()) {
                    "-${asset.currentValue().toMoneyText()}"
                } else {
                    asset.currentValue().toMoneyText()
                },
                returnText = returnText,
                warningText = if (asset.isInvestmentAsset() && (asset.profitRate() ?: 0.0) < 0) "평가손실" else null,
            )
        }

        val filteredItems = if (query.isBlank()) {
            allItems
        } else {
            allItems.filter { item ->
                item.name.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)
            }
        }

        val netWorthChart = buildNetWorthChart(histories, assets)

        return DashboardUiState(
            totalAsset = totalAsset,
            totalDebt = totalDebt,
            netWorth = netWorth,
            investmentRatio = investmentRatio,
            investmentTargetRatio = investmentTargetRatio,
            warningMessage = warningMessage,
            categoryBars = categoryBars,
            items = filteredItems,
            searchQuery = query,
            netWorthChart = netWorthChart,
        )
    }

    /**
     * 이력(histories)을 월별로 그룹화하여 순자산 추이를 계산한다.
     * 각 이력의 newAmount/previousAmount delta를 assetId별로 누적하여
     * 해당 시점의 자산 총액 변동을 월별로 집계한다.
     * 부채(DELETED changeType은 마이너스 방향)를 고려한 순자산은
     * 현재 스냅샷 기반이 아닌 이력 delta 누적으로 근사 계산한다.
     * 데이터가 없거나 1개월 이하이면 빈 리스트를 반환한다.
     */
    private fun buildNetWorthChart(
        histories: List<AssetHistoryEntity>,
        assets: List<AssetEntity>,
    ): List<NetWorthChartEntry> {
        if (histories.isEmpty()) return emptyList()

        val fmt = SimpleDateFormat("yyyy-MM", Locale.KOREA)

        val assetTypeMap = assets.associate { it.id to it.assetType }

        val monthlyDelta = mutableMapOf<String, Long>()
        histories.forEach { h ->
            val month = fmt.format(Date(h.createdAt))
            val isDebt = assetTypeMap[h.assetId]?.name == "LIABILITY"
            val delta = when (h.changeType) {
                ChangeType.CREATED -> {
                    val v = h.newAmount ?: 0L
                    if (isDebt) -v else v
                }
                ChangeType.UPDATED, ChangeType.VALUATION_UPDATED -> {
                    val prev = h.previousAmount ?: 0L
                    val next = h.newAmount ?: 0L
                    val diff = next - prev
                    if (isDebt) -diff else diff
                }
                ChangeType.DELETED -> {
                    val v = h.previousAmount ?: 0L
                    if (isDebt) v else -v
                }
            }
            monthlyDelta[month] = (monthlyDelta[month] ?: 0L) + delta
        }

        val sortedMonths = monthlyDelta.keys.sorted()
        if (sortedMonths.size < 2) {
            val singleMonth = sortedMonths.firstOrNull() ?: return emptyList()
            return listOf(NetWorthChartEntry(label = singleMonth.substring(5), netWorth = monthlyDelta[singleMonth] ?: 0L))
        }

        var cumulative = 0L
        return sortedMonths.map { month ->
            cumulative += monthlyDelta[month] ?: 0L
            NetWorthChartEntry(
                label = month.substring(5),
                netWorth = cumulative,
            )
        }
    }
}
