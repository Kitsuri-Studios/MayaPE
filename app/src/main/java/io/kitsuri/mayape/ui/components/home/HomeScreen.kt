package io.kitsuri.mayape.ui.components.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.kitsuri.mayape.R


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen() {
    val currentScreen = remember { mutableStateOf(Screen.HOME) }
    val font = FontFamily(Font(R.font.light, FontWeight.Normal))

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg3),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500)) { -it },
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
        ) {
            Box {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 18.dp,
                                bottomEnd = 18.dp
                            )
                        )
                        .blur(18.dp),
                    color = Color(0x8D4B4A4A)
                ) {}

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 18.dp,
                                bottomEnd = 18.dp
                            )
                        )
                        .border(0.1.dp, Color.White.copy(0.3f), RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp)),
                    color = Color.Transparent
                ) {
                    TopBarContent(
                        isHomeScreen = currentScreen.value == Screen.HOME,
                        onBackClick = { currentScreen.value = Screen.HOME }
                    )
                }
            }
        }

        AnimatedContent(
            targetState = currentScreen.value,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { it / 2 } with
                        fadeOut(animationSpec = tween(300)) + slideOutVertically(animationSpec = tween(300)) { -it / 2 }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)
        ) { targetScreen ->
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (targetScreen) {
                    Screen.HOME -> MainContentGrid(
                        onModsClick = { currentScreen.value = Screen.MODS },
                        onRealmsClick = { currentScreen.value = Screen.REALMS },
                        onTexturePacksClick = { currentScreen.value = Screen.TEXTURE_PACKS },
                        onStatisticsClick = { currentScreen.value = Screen.STATISTICS }
                    )
                    Screen.MODS -> ModsContent(onBackClick = { currentScreen.value = Screen.HOME }, font = font)
                    Screen.REALMS -> RealmsContent(onBackClick = { currentScreen.value = Screen.HOME }, font = font)
                    Screen.TEXTURE_PACKS -> TexturePacksContent(onBackClick = { currentScreen.value = Screen.HOME }, font = font)
                    Screen.STATISTICS -> StatisticsContent(onBackClick = { currentScreen.value = Screen.HOME }, font = font)
                }
            }
        }
    }
}
