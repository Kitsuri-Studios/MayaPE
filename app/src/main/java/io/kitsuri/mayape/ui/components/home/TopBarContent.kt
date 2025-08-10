package io.kitsuri.mayape.ui.components.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kitsuri.mayape.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TopBarContent(
    isHomeScreen: Boolean,
    onBackClick: () -> Unit,
    onTerminalClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val font = FontFamily(Font(R.font.light, FontWeight.Normal))
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MayaPE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                fontFamily = font
            )
            Text(
                text = "by Kitsuri Studios",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 7.sp,
                fontFamily = font,
                modifier = Modifier.offset(y = (-5).dp)
            )
        }

        AnimatedContent(
            targetState = isHomeScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) + scaleIn(animationSpec = tween(200), initialScale = 0.8f) with
                        fadeOut(animationSpec = tween(200)) + scaleOut(animationSpec = tween(200), targetScale = 0.8f)
            },
            label = "Top Bar Icons Transition"
        ) { homeScreen ->
            if (homeScreen) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedIcon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = "Help",
                        onClick = { /* Handle help click */ }
                    )
                    AnimatedIcon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = "Download",
                        onClick = { /* Handle download click */ }
                    )
                    AnimatedIcon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        onClick = onSettingsClick
                    )
                    AnimatedIcon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(38.dp)
                            .padding(start = 8.dp),
                        onClick = { /* Handle profile click */ }
                    )
                    AnimatedIcon(
                        imageVector = Icons.Outlined.Terminal,
                        contentDescription = "Terminal",
                        onClick = onTerminalClick
                    )

                }
            } else {
                AnimatedIcon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    onClick = onBackClick
                )
            }
        }
    }
}