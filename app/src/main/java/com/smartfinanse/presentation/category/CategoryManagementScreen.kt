package com.smartfinanse.presentation.category

import android.net.Uri
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Star as RoundedStar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import com.smartfinanse.presentation.common.SmartFinanseTopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.smartfinanse.R
import com.smartfinanse.domain.model.Category
import com.smartfinanse.presentation.common.CategoryIconRenderer
import com.smartfinanse.presentation.common.getMaterialIconByName
import com.smartfinanse.presentation.common.parseCategoryHexColor
import com.smartfinanse.domain.util.capitalizeFirst

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: CategoryManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.onImagePicked(uri)
            }
        }
    )

    uiState.showDeleteConfirmationFor?.let { category ->
        AlertDialog(
            onDismissRequest = viewModel::hideDeleteConfirmation,
            title = { Text("Usuń kategorię") },
            text = { Text("Czy na pewno chcesz usunąć kategorię ${category.name.capitalizeFirst()}?") },
            confirmButton = {
                Button(
                    onClick = viewModel::deleteCategory,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Usuń")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideDeleteConfirmation) {
                    Text("Anuluj")
                }
            }
        )
    }

    if (uiState.isSheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeSheet() },
            sheetState = sheetState
        ) {
            CategoryFormSheet(
                uiState = uiState,
                onNameChange = viewModel::updateNameInput,
                onColorChange = viewModel::updateSelectedColor,
                onIconChange = viewModel::updateSelectedIcon,
                onSave = viewModel::saveCategory,
                onPickImage = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
    }

    Scaffold(
        topBar = {
            SmartFinanseTopAppBar(
                title = { Text("Zarządzanie Kategoriami") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openSheetForAdd() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj Kategorię")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = if (uiState.managingExpenses) 0 else 1
            ) {
                Tab(
                    selected = uiState.managingExpenses,
                    onClick = { viewModel.selectExpenseTab() },
                    text = { Text(stringResource(R.string.category_tab_expenses)) }
                )
                Tab(
                    selected = !uiState.managingExpenses,
                    onClick = { viewModel.selectIncomeTab() },
                    text = { Text(stringResource(R.string.category_tab_income)) }
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Szukaj kategorii...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.filteredCategories, key = { it.id }) { category ->
                        CategoryItemCard(
                            category = category,
                            onEdit = { viewModel.openSheetForEdit(category) },
                            onDelete = { viewModel.showDeleteConfirmation(category) }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryItemCard(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIconRenderer(
                iconName = category.iconName,
                colorHex = category.colorHex,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = category.name.capitalizeFirst(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edytuj",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Usuń",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}



@Composable
private fun CategoryFormSheet(
    uiState: CategoryManagementUiState,
    onNameChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onSave: () -> Unit,
    onPickImage: () -> Unit
) {
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
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .padding(16.dp)
    ) {
        Text(
            text = if (uiState.isEditing) "Edytuj Kategorię" else "Nowa Kategoria",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.nameInput,
            onValueChange = onNameChange,
            label = { Text("Nazwa Kategorii") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
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
                        .clickable(onClick = onPickImage),
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
                        .clickable { onIconChange(iconName) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getMaterialIconByName(iconName),
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Jeśli wybrano customową ikonkę, pokazujemy jej podgląd z boku karuzeli
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
                        .clickable { onColorChange(hex) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.nameInput.isNotBlank()
        ) {
            Text("Zapisz Kategorię")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}


