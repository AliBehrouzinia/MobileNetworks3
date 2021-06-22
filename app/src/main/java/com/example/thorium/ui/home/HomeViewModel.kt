package com.example.thorium.ui.home

import androidx.lifecycle.*
import com.example.common.entity.CellLogRequest
import com.example.common.entity.Tracking
import com.example.thorium.util.SingleLiveEvent
import com.example.usecase.interactor.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startNewTrackingUseCase: StartNewTrackingUseCase,
    private val saveCellLogUseCase: SaveCellLogUseCase,
    private val stopActiveTrackingUseCase: StopActiveTrackingUseCase,
    private val getActiveTrackingUseCase: GetActiveTrackingUseCase,
    private val isThereActiveTrackingUseCase: IsThereActiveTrackingUseCase
) : ViewModel() {

    private val _alert: SingleLiveEvent<String> = SingleLiveEvent()
    val alert: LiveData<String> = _alert

    private val _activeTracking: MutableLiveData<Tracking> = MutableLiveData()
    val activeTracking = _activeTracking

    private val _displayedTracking: MutableLiveData<Tracking> = MutableLiveData()
    val displayedTracking: LiveData<Tracking> = _displayedTracking

    val isThereActiveTracking: LiveData<Boolean> = isThereActiveTrackingUseCase().asLiveData()

    private fun runUseCase(successMessage: String, useCase:suspend () -> Unit) {
        viewModelScope.launch {
            try {
                useCase()
                _alert.value = successMessage
            } catch (e: Exception) {
                _alert.value = e.message
            }
        }
    }

    private fun onStartTrackingClicked() {
        runUseCase(successMessage = "Successfully started new tracking") {
            startNewTrackingUseCase()
        }
    }

    private fun onStopTrackingClicked() {
        runUseCase(successMessage = "Successfully stopped active tracking") {
            stopActiveTrackingUseCase()
        }
    }

    fun onSaveCellLogClicked(cellLogRequest: CellLogRequest) {
        runUseCase(successMessage = "Successfully saved cell log") {
            saveCellLogUseCase(cellLogRequest)
            _activeTracking.value = getActiveTrackingUseCase()
        }
    }

    fun onStartStopTrackingClicked() {
        if (isThereActiveTracking.value!!) {
            onStopTrackingClicked()
        } else {
            onStartTrackingClicked()
        }
    }
}