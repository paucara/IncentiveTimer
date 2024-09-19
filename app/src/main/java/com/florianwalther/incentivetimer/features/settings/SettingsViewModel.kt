package com.florianwalther.incentivetimer.features.settings

import androidx.lifecycle.*
import com.florianwalther.incentivetimer.data.datastore.DefaultPreferencesManager
import com.florianwalther.incentivetimer.data.datastore.ThemeSelection
import com.florianwalther.incentivetimer.features.settings.model.SettingsScreenState
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: DefaultPreferencesManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), SettingsScreenActions {

    private val appPreferences = preferencesManager.appPreferences

    private val timerPreferences = preferencesManager.timerPreferences

    private val showThemeDialog =
        savedStateHandle.getLiveData("showThemeDialog", false)

    private val showPomodoroLengthDialog =
        savedStateHandle.getLiveData("showPomodoroLengthDialog", false)

    private val showShortBreakLengthDialog =
        savedStateHandle.getLiveData("showShortBreakLengthDialog", false)

    private val showLongBreakLengthDialog =
        savedStateHandle.getLiveData("showLongBreakLengthDialog", false)

    private val showPomodorosPerSetDialog =
        savedStateHandle.getLiveData("showPomodorosPerSetDialog", false)

    private val showAppInstructionsDialog =
        savedStateHandle.getLiveData("showAppInstructionsDialog", false)

    private val showRestartPomodoroDialog = /*:D*/
        savedStateHandle.getLiveData("showRestartPomodoroDialog", false)

    //Problamente haya otra forma de combinar los flows
    val screenState = combineTuple(
        appPreferences,
        timerPreferences,
        showThemeDialog.asFlow(),
        showPomodoroLengthDialog.asFlow(),
        showShortBreakLengthDialog.asFlow(),
        showLongBreakLengthDialog.asFlow(),
        showPomodorosPerSetDialog.asFlow(),
        showAppInstructionsDialog.asFlow(),
        showRestartPomodoroDialog.asFlow() /*:D*/
    ).map { (
                appPreferences,
                timerPreferences,
                showThemeDialog,
                showPomodoroLengthDialog,
                showShortBreakLengthDialog,
                showLongBreakLengthDialog,
                showPomodorosPerSetDialog,
                showAppInstructionsDialog,
                showRestartPomodoroDialog
            ) ->
        SettingsScreenState(
            appPreferences = appPreferences,
            timerPreferences = timerPreferences,
            showThemeDialog = showThemeDialog,
            showPomodoroLengthDialog = showPomodoroLengthDialog,
            showShortBreakLengthDialog = showShortBreakLengthDialog,
            showLongBreakLengthDialog = showLongBreakLengthDialog,
            showPomodorosPerSetDialog = showPomodorosPerSetDialog,
            showAppInstructionsDialog = showAppInstructionsDialog,
            showRestartPomodoroDialog = showRestartPomodoroDialog
        )
    }.asLiveData()

    override fun onThemePreferenceClicked() {
        showThemeDialog.value = true
    }

    override fun onThemeSelected(theme: ThemeSelection) {
        showThemeDialog.value = false
        viewModelScope.launch {
            preferencesManager.updateSelectedTheme(theme)
        }
    }

    override fun onThemeDialogDismissed() {
        showThemeDialog.value = false
    }

    override fun onPomodoroLengthPreferenceClicked() {
        showPomodoroLengthDialog.value = true
    }

    override fun onPomodoroLengthSet(lengthInMinutes: Int) {
        viewModelScope.launch {
            preferencesManager.updatePomodoroLength(lengthInMinutes)
            showPomodoroLengthDialog.value = false
            showRestartPomodoroDialog.value = true /* activa el dialogo de reinicio del pomodoro */
        }
    }

    override fun onPomodoroLengthDialogDismissed() {
        showPomodoroLengthDialog.value = false
    }

    override fun onShortBreakLengthPreferenceClicked() {
        showShortBreakLengthDialog.value = true
    }

    override fun onShortBreakLengthSet(lengthInMinutes: Int) {
        viewModelScope.launch {
            preferencesManager.updateShortBreakLength(lengthInMinutes)
            showShortBreakLengthDialog.value = false
        }
    }

    override fun onShortBreakLengthDialogDismissed() {
        showShortBreakLengthDialog.value = false
    }

    override fun onLongBreakLengthPreferenceClicked() {
        showLongBreakLengthDialog.value = true
    }

    override fun onLongBreakLengthSet(lengthInMinutes: Int) {
        viewModelScope.launch {
            preferencesManager.updateLongBreakLength(lengthInMinutes)
            showLongBreakLengthDialog.value = false
        }
    }

    override fun onLongBreakLengthDialogDismissed() {
        showLongBreakLengthDialog.value = false
    }

    override fun onPomodorosPerSetPreferenceClicked() {
        showPomodorosPerSetDialog.value = true
    }

    override fun onPomodorosPerSetSet(amount: Int) {
        viewModelScope.launch {
            preferencesManager.updatePomodorosPerSet(amount)
            showPomodorosPerSetDialog.value = false
        }
    }

    override fun onAutoStartNextTimerCheckedChanged(checked: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateAutoStartNextTimer(autoStartNextTimer = checked)
        }
    }

    override fun onPomodorosPerSetDialogDismissed() {
        showPomodorosPerSetDialog.value = false
    }

    override fun onShowAppInstructionsClicked() {
        showAppInstructionsDialog.value = true
    }

    override fun onAppInstructionsDialogDismissed() {
        showAppInstructionsDialog.value = false
    }

    override fun onRestartPomodoroClicked() {
        showRestartPomodoroDialog.value = false
    }
}