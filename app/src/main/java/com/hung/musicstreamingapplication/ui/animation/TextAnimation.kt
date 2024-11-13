package com.hung.musicstreamingapplication.ui.animation

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hung.musicstreamingapplication.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun JourneyItemText() {
   val infinitiveAnimation = rememberInfiniteTransition(label = "")
    val fontProvider = remember {
        GoogleFont.Provider(
            providerAuthority = "com.google.android.gms.fonts",
            providerPackage = "com.google.android.gms",
            certificates = R.array.com_google_android_gms_fonts_certs
        )
    }
    val fontName = GoogleFont("Anton")
    val fontFamily = FontFamily(
        Font(googleFont = fontName, fontProvider = fontProvider)
    )

    val color by infinitiveAnimation.animateColor(
        initialValue = Color.Gray,
        targetValue = Color.White,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val shimmerAlpha by infinitiveAnimation.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Text(
        text = stringResource(R.string.music_newest),
        color = color.copy(alpha = shimmerAlpha),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = fontFamily
    )
//    val colorAnimatables = remember {
//        text.map {
//            Animatable(Color.Gray)
//        }
//    }
//    LaunchedEffect(Unit) {
//        while (true) {
//            for (i in text.indices) {
//                // Đặt màu của ký tự hiện tại sang màu trắng
//                val currentAnimatable = colorAnimatables[i]
//                currentAnimatable.animateTo(Color.White)
//
//                // Nếu có ký tự kế tiếp, dần chuyển từ ký tự hiện tại sang ký tự tiếp theo
//                if (i + 1 < text.length) {
//                    val nextAnimatable = colorAnimatables[i + 1]
//
//                    // Chuyển tiếp dần màu từ ký tự hiện tại sang ký tự kế tiếp
//                    launch {
//                        // Từ từ chuyển ký tự tiếp theo từ xám sang trắng đồng thời chuyển ký tự hiện tại lại xám
////                        nextAnimatable.animateTo(Color.White, animationSpec = tween(500))
//                        currentAnimatable.animateTo(Color.Gray, animationSpec = tween(500))
//                    }
//                } else {
//                    // Nếu là ký tự cuối cùng, đưa nó về xám trước khi lặp lại
//                    currentAnimatable.animateTo(Color.Gray, animationSpec = tween(500))
//                }
//                // Thời gian chờ trước khi chuyển sang ký tự tiếp theo
//                delay(500)
//            }
//        }
//    }
//    Row(
//    ){
//        text.forEachIndexed {
//                index,char ->
//            Text(
//                text = char.toString(),
//                fontWeight = FontWeight.ExtraBold,
//                fontFamily = FontFamily.SansSerif,
//                color = colorAnimatables[index].value,
//                fontSize = 18.sp,
//                modifier = Modifier.padding(end = 2.dp)
//            )
//        }
//    }
}

@Preview
@Composable
fun PreviewTest() {
    Column(
        Modifier.fillMaxSize()
            .background(Color.Black)
    ) {
        JourneyItemText()
    }
}