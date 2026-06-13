package com.financeasserflow.pfmapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.financeasserflow.pfmapp.data.model.AssetCategory
import com.financeasserflow.pfmapp.data.model.AssetType
import com.financeasserflow.pfmapp.viewmodel.AssetEditorUiState
import com.financeasserflow.pfmapp.viewmodel.AssetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetEntryScreen(
    viewModel: AssetViewModel,
    assetId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val state by viewModel.editorUiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(assetId) {
        if (assetId == null) {
            viewModel.startNewAsset()
        } else {
            viewModel.loadAsset(assetId)
        }
    }

    LaunchedEffect(state.snackbarMessage) {
        val message = state.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.consumeSnackbarMessage()
        if (message == "저장되었습니다.") {
            onSaved()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = if (assetId == null) "자산 등록" else "자산 수정") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TypeDropdown(
                label = "자산/부채",
                value = state.assetType,
                options = AssetType.entries,
                onValueChange = viewModel::onAssetTypeChange,
            )

            CategoryDropdown(
                label = "분류",
                value = state.category,
                options = if (state.assetType == AssetType.LIABILITY) {
                    listOf(AssetCategory.LIABILITY)
                } else {
                    AssetCategory.entries.filter { it != AssetCategory.LIABILITY }
                },
                onValueChange = viewModel::onCategoryChange,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("자산명") },
                singleLine = true,
                isError = state.snackbarMessage?.contains("자산명") == true,
            )

            if (state.assetType == AssetType.ASSET && state.category == AssetCategory.INVESTMENT) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.principalAmount,
                    onValueChange = viewModel::onPrincipalAmountChange,
                    label = { Text("매입금액") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = state.snackbarMessage?.contains("매입금액") == true,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.valuationAmount,
                    onValueChange = viewModel::onValuationAmountChange,
                    label = { Text("현재 평가금액") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = state.snackbarMessage?.contains("평가금액") == true,
                )
            } else {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.amount,
                    onValueChange = viewModel::onAmountChange,
                    label = { Text(if (state.assetType == AssetType.LIABILITY) "부채 금액" else "금액") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = state.snackbarMessage?.contains("금액") == true,
                )
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                value = state.memo,
                onValueChange = viewModel::onMemoChange,
                label = { Text("메모") },
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving,
                onClick = { viewModel.saveCurrentAsset() },
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Text(text = if (state.isSaving) "저장 중..." else "저장")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> TypeDropdown(
    label: String,
    value: T,
    options: List<T>,
    onValueChange: (T) -> Unit,
) where T : Enum<T> {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = valueDisplay(value),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(valueDisplay(option)) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    label: String,
    value: AssetCategory,
    options: List<AssetCategory>,
    onValueChange: (AssetCategory) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = value.label,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun <T> valueDisplay(value: T): String where T : Enum<T> {
    return when (value) {
        is AssetType -> value.label
        else -> value.name
    }
}
