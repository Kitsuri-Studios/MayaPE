package io.kitsuri.mayape.ui.components.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kitsuri.mayape.R

@Composable
fun ControlPanel(
    authState: LoginState,
    userCode: String?,
    onDismiss: () -> Unit,
    onLogin: () -> Unit,
    onCopyCode: () -> Unit,
    isCompact: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isCompact) 16.dp else 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Maya PE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 20.dp))

        when (authState) {
            LoginState.Initial -> {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "",
                    modifier = Modifier.scale(1.5f).padding(top = 20.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth(0.30f)
                        .height(if (isCompact) 44.dp else 48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0078D4)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "Microsoft Login",
                        fontSize = if (isCompact) 13.sp else 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
            LoginState.Loading -> {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 48.dp else 64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0078D4).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF0078D4),
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(if (isCompact) 24.dp else 32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 20.dp))
                Text(
                    text = if (isCompact) "Connecting..." else "Connecting to Microsoft...",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = if (isCompact) 14.sp else 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                if (!isCompact) {
                    Text(
                        text = "Please wait",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            LoginState.AwaitingAuth, is LoginState.Error, LoginState.Success -> {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "",
                    modifier = Modifier
                        .scale(1.5f)
                        .padding(top = 40.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (authState) {
                        LoginState.AwaitingAuth -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF0078D4)
                            )
                            Text(
                                text = "Loading...",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        is LoginState.Error -> {
                            Icon(
                                imageVector = ImageVector.vectorResource(ir.alirezaivaz.tablericons.R.drawable.ic_exclamation_circle),
                                contentDescription = "Error",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFFE53E3E)
                            )
                            Text(
                                text = authState.message,
                                fontSize = 12.sp,
                                color = Color(0xFFE53E3E),
                                modifier = Modifier.padding(start = 8.dp),
                                maxLines = 2
                            )
                        }
                        LoginState.Success -> {
                            Icon(
                                imageVector = ImageVector.vectorResource(ir.alirezaivaz.tablericons.R.drawable.ic_circle_check),
                                contentDescription = "Success",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF38A169)
                            )
                            Text(
                                text = "Success",
                                fontSize = 12.sp,
                                color = Color(0xFF38A169),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}