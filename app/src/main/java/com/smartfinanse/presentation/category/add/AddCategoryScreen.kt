package com.smartfinanse.presentation.category.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartfinanse.presentation.common.CategoryIconRenderer
import com.smartfinanse.presentation.common.SmartFinanseTopAppBar
import com.smartfinanse.presentation.common.getMaterialIconByName
import com.smartfinanse.presentation.common.parseCategoryHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    onNavigateBackWithResult: (Long?) -> Unit,
    viewModel: AddCategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess && uiState.savedCategoryId != null) {
            onNavigateBackWithResult(uiState.savedCategoryId)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.onImagePicked(uri)
            }
        }
    )

    Scaffold(
        topBar = {
            SmartFinanseTopAppBar(
                title = { Text("Nowa Kategoria") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBackWithResult(null) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wróć"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val presetColors = listOf(
            "#FF9800", "#F44336", "#E91E63", "#9C27B0", "#673AB7",
            "#3F51B5", "#2196F3", "#00BCD4", "#009688", "#4CAF50", "#8BC34A"
        )
        val presetIcons = listOf(
            "ic_food", "ic_transport", "ic_health", "ic_bills", "ic_entertainment", "ic_other", "ic_shopping", "ic_home", "ic_work"
        )
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Stwórz kategorię",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.nameInput,
                onValueChange = viewModel::updateNameInput,
                label = { Text("Nazwa Kategorii") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.error != null,
                supportingText = {
                    uiState.error?.let { Text(it) }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Wybierz ikonę lub wgraj własną", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Wgraj własną")
                    }
                }
                items(presetIcons) { iconName ->
                    val isSelected = uiState.selectedIconName == iconName
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                else MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { viewModel.updateSelectedIcon(iconName) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getMaterialIconByName(iconName),
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (uiState.selectedIconName.startsWith("file://") || uiState.selectedIconName.startsWith("content://")) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            CategoryIconRenderer(
                                iconName = uiState.selectedIconName,
                                colorHex = uiState.selectedColorHex,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Wybierz kolor", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presetColors) { hex ->
                    val isSelected = uiState.selectedColorHex == hex
                    val fallback = MaterialTheme.colorScheme.onSurfaceVariant
                    val color = parseCategoryHexColor(hex, fallback)
                    
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, CircleShape)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { viewModel.updateSelectedColor(hex) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = viewModel::saveCategory,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Zapisz Kategorię")
                }
            }
        }
    }
}
