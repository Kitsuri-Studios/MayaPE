package io.kitsuri.mayape.ui.components.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.activity.viewModels
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.kitsuri.mayape.models.TerminalViewModel
import io.kitsuri.mayape.utils.FileParser
import io.kitsuri.mayape.utils.LibraryUtils
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModsContent(onBackClick: () -> Unit, font: FontFamily) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val logger: TerminalViewModel = viewModel()
    // State management
    val showInvalidModDialog = remember { mutableStateOf(false) }
    var modsList by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Load mods when composable first loads
    LaunchedEffect(Unit) {
        isRefreshing = true
        LibraryUtils.refreshMods(context)
        modsList = LibraryUtils.getAllMods(context) ?: emptyMap()
        isRefreshing = false
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            handleFileSelection(
                context = context,
                uri = selectedUri,
                onInvalidMod = { showInvalidModDialog.value = true },
                onModAdded = { fileName ->
                    // Show success toast
                    Toast.makeText(context, "Mod $fileName added successfully!", Toast.LENGTH_SHORT).show()
                    logger.addLog("Main", "Mods Manager","$fileName Removed" )
                    // Refresh mod list
                    coroutineScope.launch {
                        isRefreshing = true
                        LibraryUtils.refreshMods(context)
                        modsList = LibraryUtils.getAllMods(context) ?: emptyMap()
                        isRefreshing = false
                    }
                },
                logger = logger
            )
        }
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { it / 2 },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Add Mod Button
            Button(
                onClick = { filePickerLauncher.launch("*/*") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .animateEnterExit(
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    )
            ) {
                Text("Add Mod", fontFamily = font, fontSize = 16.sp)
            }

            // Mods List
            if (isRefreshing) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                if (modsList.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No mods installed",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f),
                            fontFamily = font,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add some mods to get started!",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            fontFamily = font,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Mods list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(modsList.toList()) { (modName, isEnabled) ->
                            ModItem(
                                modName = modName,
                                isEnabled = isEnabled,
                                font = font,
                                onToggle = { enabled ->
                                    coroutineScope.launch {
                                        val success = if (enabled) {
                                            LibraryUtils.enableMod(context, modName)
                                        } else {
                                            LibraryUtils.disableMod(context, modName)
                                        }

                                        if (success) {
                                            // Update local state
                                            modsList = modsList.toMutableMap().apply {
                                                put(modName, enabled)
                                            }

                                            Toast.makeText(
                                                context,
                                                "Mod '$modName' ${if (enabled) "enabled" else "disabled"}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Failed to ${if (enabled) "enable" else "disable"} mod",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                                onDelete = {
                                    coroutineScope.launch {
                                        val success = LibraryUtils.removeMod(context, modName)
                                        if (success) {
                                            // Remove from local state
                                            modsList = modsList.toMutableMap().apply {
                                                remove(modName)
                                            }
                                            Toast.makeText(context, "Mod '$modName' deleted", Toast.LENGTH_SHORT).show()
                                            logger.addLog("Main", "Mods Manager","$modName Removed" )
                                        } else {
                                            Toast.makeText(context, "Failed to delete mod", Toast.LENGTH_SHORT).show()
                                            logger.addLog("Main", "Mods Manager","Failed to delete mod" )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Invalid mod dialog
            if (showInvalidModDialog.value) {
                AlertDialog(
                    onDismissRequest = { showInvalidModDialog.value = false },
                    title = {
                        Text(
                            text = "Invalid Mod",
                            fontFamily = font,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    },
                    text = {
                        Text(
                            text = "The selected file is not a valid mod (.so or .hxo).",
                            fontFamily = font,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { showInvalidModDialog.value = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            )
                        ) {
                            Text("OK", fontFamily = font)
                        }
                    },
                    containerColor = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun ModItem(
    modName: String,
    isEnabled: Boolean,
    font: FontFamily,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Mod name
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = modName,
                    fontFamily = font,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = if (isEnabled) "Enabled" else "Disabled",
                    fontFamily = font,
                    fontSize = 12.sp,
                    color = if (isEnabled) Color.Green.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f)
                )
            }

            // Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Enable/Disable Switch
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Green.copy(alpha = 0.6f),
                        uncheckedThumbColor = Color.White.copy(alpha = 0.8f),
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.4f)
                    )
                )

                // Delete Button
                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Mod",
                        tint = Color.Red.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Mod",
                    fontFamily = font,
                    fontSize = 20.sp,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '$modName'? This action cannot be undone.",
                    fontFamily = font,
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.8f),
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete", fontFamily = font)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancel", fontFamily = font)
                }
            },
            containerColor = Color.White.copy(alpha = 0.1f),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

fun handleFileSelection(
    context: Context,
    uri: Uri,
    onInvalidMod: () -> Unit,
    onModAdded: (String) -> Unit,
    logger: TerminalViewModel
) {

    android.util.Log.d("ModsContent", "Starting file selection handling")
    logger.addLog("Main", "File Manager","Starting file selection handling" )

    val fileParser = FileParser()
    val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    }

    if (fileName == null) {
        android.util.Log.e("ModsContent", "Could not get filename from URI")
        logger.addLog("Main", "File Manager","Could not get filename from URI" )
        onInvalidMod()
        return
    }

    android.util.Log.d("ModsContent", "Selected file: $fileName")
    logger.addLog("Main", "File Manager","Selected file: $fileName" )
    if (!fileName.endsWith(".so") && !fileName.endsWith(".hxo")) {
        android.util.Log.w("ModsContent", "Invalid file extension: $fileName")
        logger.addLog("Main", "File Manager","Invalid file extension: $fileName" )
        onInvalidMod()
        return
    }

    // Create temp file
    val tempFile = File(context.cacheDir, fileName)
    android.util.Log.d("ModsContent", "Temp file path: ${tempFile.absolutePath}")
    logger.addLog("Main", "File Manager","Temp file path: ${tempFile.absolutePath}" )
    try {
        // Copy content from URI to temp file
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                val bytesTransferred = input.copyTo(output)
                android.util.Log.d("ModsContent", "Transferred $bytesTransferred bytes to temp file")
                logger.addLog("Main", "File Manager","Transferred $bytesTransferred bytes to temp file" )
            }
        }

        if (!tempFile.exists() || tempFile.length() == 0L) {
            android.util.Log.e("ModsContent", "Temp file creation failed or is empty")
            logger.addLog("Main", "File Manager","Temp file creation failed or is empty" )
            onInvalidMod()
            return
        }

        // Validate file with FileParser
        android.util.Log.d("ModsContent", "Validating file with FileParser")
        logger.addLog("Main", "ELF Manager","Validating ELF..." )
        if (!fileParser.elfWrap(tempFile.absolutePath)) {
            android.util.Log.w("ModsContent", "File failed ELF validation")
            logger.addLog("Main", "ELF Manager","File failed ELF validation" )
            onInvalidMod()
            tempFile.delete()
            return
        }
        logger.addLog("Main", "ELF Manager","All Checks passed" )
        // Copy file to modules directory
        android.util.Log.d("ModsContent", "Copying to modules directory")
        logger.addLog("Main", "File Manager","Saving ${tempFile.absolutePath} To Modules Dir" )
        val copiedFile = LibraryUtils.copyModFile(context, tempFile, fileName)
        if (copiedFile == null) {
            android.util.Log.e("ModsContent", "Failed to copy mod file")
            logger.addLog("Main", "File Manager","Failed to save mod file" )
            onInvalidMod()
            tempFile.delete()
            return
        }

        // Update modules.json
        logger.addLog("Main", "Mod Manager","Updating modules.json" )
        android.util.Log.d("ModsContent", "Updating modules.json")
        val updateSuccess = LibraryUtils.updateModulesJson(context, fileName)
        if (!updateSuccess) {
            android.util.Log.e("ModsContent", "Failed to update modules.json")
            logger.addLog("Main", "Mod Manager","Failed to update modules.json" )
            onInvalidMod()
            tempFile.delete()
            return
        }
        logger.addLog("Main", "Mod Manager","Mod installation completed successfully" )
        android.util.Log.d("ModsContent", "Mod installation completed successfully")
        onModAdded(fileName)

    } catch (e: Exception) {
        android.util.Log.e("ModsContent", "Error handling file selection", e)
        logger.addLog("Fatal", "Mod Manager","Error handling file selection" )
        onInvalidMod()
    } finally {
        // Clean up temp file
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }
}