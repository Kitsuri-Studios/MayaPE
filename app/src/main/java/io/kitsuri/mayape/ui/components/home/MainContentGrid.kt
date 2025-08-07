package io.kitsuri.mayape.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Rocket
import androidx.compose.material.icons.outlined.Texture
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kitsuri.mayape.R

@Composable
fun MainContentGrid(
    onModsClick: () -> Unit,
    onRealmsClick: () -> Unit,
    onTexturePacksClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    val font = FontFamily(Font(R.font.light, FontWeight.Normal))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnimatedCard(
                    icon = Icons.Outlined.Extension,
                    title = "Mods",
                    subtitle = "Browse 100s of mods",
                    modifier = Modifier.weight(1f),
                    font = font,
                    onClick = onModsClick
                )
                AnimatedCard(
                    icon = Icons.Outlined.Public,
                    title = "Realms",
                    subtitle = null,
                    modifier = Modifier.weight(1f),
                    font = font,
                    onClick = onRealmsClick
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnimatedCard(
                    icon = Icons.Outlined.Texture,
                    title = "Texture Packs",
                    subtitle = null,
                    modifier = Modifier.weight(1f),
                    font = font,
                    onClick = onTexturePacksClick
                )
                AnimatedCard(
                    icon = Icons.Outlined.BarChart,
                    title = "Statistics",
                    subtitle = null,
                    modifier = Modifier.weight(1f),
                    font = font,
                    onClick = onStatisticsClick
                )
            }
        }

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500)) + scaleIn(animationSpec = tween(500)),
            modifier = Modifier
                .weight(1f)
                .height(260.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .border(0.1.dp, Color.White.copy(0.3f), RoundedCornerShape(16.dp))
                    .clickable { /* Handle card click */ },
                color = Color.White.copy(alpha = 0.1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Rocket,
                            contentDescription = "Launch Minecraft",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Launch Minecraft",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            fontFamily = font
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { /* Handle start game */ },
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PlayArrow,
                                contentDescription = "Start Game",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Start Game",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = font
                            )
                        }
                    }
                }
            }
        }
    }
}