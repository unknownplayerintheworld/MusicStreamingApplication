package com.hung.musicstreamingapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.hung.musicstreamingapplication.R

@Composable
fun IconWithBadge(
    imageVector: ImageVector? = null,
    painter: Painter? = null,
    contentDescription: String,
    badgeCount: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if(painter != null){
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }else{
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Badge
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd) // Đặt badge ở góc trên bên phải
                    .background(MaterialTheme.colorScheme.background, shape = CircleShape),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun Prev() {
    Row(
        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
    ) {
        IconWithBadge(painter = painterResource(id = R.drawable.baseline_comment_24), contentDescription = "content", badgeCount = 2)
    }
}