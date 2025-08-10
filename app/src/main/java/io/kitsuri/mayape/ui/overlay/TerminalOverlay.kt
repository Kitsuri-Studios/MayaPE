package io.kitsuri.mayape.ui.overlay

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.kitsuri.mayape.models.TerminalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@Composable
fun TerminalOverlay(
    isVisible: Boolean,
    onClose: () -> Unit,
    viewModel: TerminalViewModel = viewModel()
) {
    val autoScrollEnabled = remember { mutableStateOf(true) }
    val autoSaveEnabled = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val logs = viewModel.logs.collectAsState().value
    val context = LocalContext.current
    val showExportDialog = remember { mutableStateOf(false) }
    val exportFileName = remember {
        mutableStateOf(
            "maya_log_${
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            }.txt"
        )
    }
    val semiDarkBlue = Color(0xFF3287F6)

    LaunchedEffect(logs) {
        if (autoScrollEnabled.value) {
            scrollState.animateScrollTo(scrollState.maxValue, tween(300))
        }
        if (autoSaveEnabled.value) {
            viewModel.exportLogs(
                context,
                exportFileName.value
            )
        }
    }


    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { it },
        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(animationSpec = tween(300)) { it },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
        ) {
            Column(
                modifier = Modifier
                    .width(760.dp)
                    .fillMaxHeight(0.95f)
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Client log",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close Terminal",
                            tint = Color.White
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.95f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                        .verticalScroll(scrollState)
                ) {
                    Column {
                        logs.forEach { log ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(200)),
                                exit = fadeOut(animationSpec = tween(200))
                            ) {
                                Text(
                                    text = log,
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Auto scroll",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Switch(
                            checked = autoScrollEnabled.value,
                            onCheckedChange = { autoScrollEnabled.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                uncheckedThumbColor = Color.White.copy(alpha = 0.3f),
                                checkedTrackColor = semiDarkBlue,
                                uncheckedTrackColor = semiDarkBlue.copy(alpha = 0.5f)
                            )
                        )
                        Button(
                            onClick = { viewModel.clearLogs() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = semiDarkBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.animateContentSize(
                                animationSpec = tween(200)
                            )
                        ) {
                            Text("Clear Terminal")
                        }
                        Button(
                            onClick = { showExportDialog.value = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = semiDarkBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.animateContentSize(
                                animationSpec = tween(200)
                            )
                        ) {
                            Text("Export Log")
                        }
                    }
                    // NEW: Auto Save Log toggle
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Auto save log",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Switch(
                            checked = autoSaveEnabled.value,
                            onCheckedChange = { autoSaveEnabled.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                uncheckedThumbColor = Color.White.copy(alpha = 0.3f),
                                checkedTrackColor = semiDarkBlue,
                                uncheckedTrackColor = semiDarkBlue.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    }

    if (showExportDialog.value) {
        AlertDialog(
            onDismissRequest = { showExportDialog.value = false },
            title = { Text("Export Logs") },
            text = {
                Column {
                    Text("Enter file name for log export:")
                    TextField(
                        value = exportFileName.value,
                        onValueChange = { exportFileName.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.exportLogs(context, exportFileName.value)
                        Toast.makeText(
                            context,
                            "Logs saved to ${exportFileName.value}",
                            Toast.LENGTH_SHORT
                        ).show()
                        showExportDialog.value = false
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = semiDarkBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showExportDialog.value = false },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = semiDarkBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
