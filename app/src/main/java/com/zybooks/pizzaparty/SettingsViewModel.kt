package com.zybooks.pizzaparty

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Switch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.ui.text.style.LineBreak
import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import java.util.Locale

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    // Dark Mode State
    private val _isDarkMode = mutableStateOf(false)
    val isDarkMode: State<Boolean> = _isDarkMode

    // Language Selection State
    private val _selectedLanguage = mutableStateOf("English") // Default
    val selectedLanguage: State<String> = _selectedLanguage

    // Toggle Dark Mode
    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Set Language and update locale
    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        updateLocale(language)
    }

    // Method to update app locale
    private fun updateLocale(language: String) {
        val locale = when (language) {
            "German" -> Locale.GERMAN
            "Japanese" -> Locale.JAPANESE
            "French" -> Locale.FRENCH
            "Korean" -> Locale.KOREAN
            else -> Locale.ENGLISH
        }

        // Update configuration
        Locale.setDefault(locale)
        val config = Configuration(getApplication<Application>().resources.configuration)
        config.setLocale(locale)
        getApplication<Application>().resources.updateConfiguration(
            config,
            getApplication<Application>().resources.displayMetrics
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel = viewModel()) {
    val isDarkMode by settingsViewModel.isDarkMode
    val selectedLanguage by settingsViewModel.selectedLanguage
    var showLanguageSelector = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context.applicationContext as Application)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dark Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.dark_mode), modifier = Modifier.weight(1f))
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { settingsViewModel.toggleDarkMode() })
            }
            // Language Selection Button
            OutlinedCard(
                onClick = { showLanguageSelector.value = true },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.language, selectedLanguage), modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            }

        }
    }

    // Language Selection Bottom Sheet
    if (showLanguageSelector.value) {
        LanguageSelectionSheet(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { newLanguage ->
                settingsViewModel.setLanguage(newLanguage)
                showLanguageSelector.value = false
            },
            onDismiss = { showLanguageSelector.value = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionSheet(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf("English", "German", "Japanese", "French", "Korean")

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.select_language), style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(languages) { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = language,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (language == selectedLanguage) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

