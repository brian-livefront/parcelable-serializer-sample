package com.livefront.parcelableserializersample.sealedclass

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class SealedClassViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val sealedClassRoute = savedStateHandle.toRoute<SealedClassRoute>()

    private val mutableStateFlow = MutableStateFlow(
        SealedClassState(
            message = when (sealedClassRoute) {
                SealedClassRoute.Type1 -> "Type 1 Destination"
                is SealedClassRoute.Type2 -> sealedClassRoute.data
            }
        )
    )
    val stateFlow: StateFlow<SealedClassState> = mutableStateFlow.asStateFlow()
}

data class SealedClassState(
    val message: String
)
