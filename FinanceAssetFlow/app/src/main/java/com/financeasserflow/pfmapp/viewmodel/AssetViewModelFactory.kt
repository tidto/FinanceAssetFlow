package com.financeasserflow.pfmapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.financeasserflow.pfmapp.data.repository.AssetRepository

class AssetViewModelFactory(
    private val repository: AssetRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AssetViewModel(repository) as T
    }
}

