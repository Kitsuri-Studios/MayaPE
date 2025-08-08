package io.kitsuri.mayape.ui.components.misc

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kitsuri.elfcheck.FileParser
import io.kitsuri.mayape.utils.FileUtils

@Composable
fun ElfFileCheckerUI() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val filePath = FileUtils.uriToFilePath(context, it)
            if (filePath != null) {
                val parser = FileParser()
                if (parser.elfWrap(filePath)) {
                    dialogMessage = "Valid ELF file"
                } else {
                    dialogMessage = "Invalid ELF file"
                }
                showDialog = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { filePickerLauncher.launch("*/*") }) {
            Text("Select File to Check")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("ELF Check Result") },
            text = { Text(dialogMessage) }
        )
    }
}