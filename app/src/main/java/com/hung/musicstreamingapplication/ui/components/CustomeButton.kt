package com.hung.musicstreamingapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    onClick:() -> Unit,
    text: String,
    textColor: Color = Color.Black,
    modifier: Modifier = Modifier,
    color: Color,
    enabled: Boolean
) {
    Button(
        enabled = enabled,
        onClick = { onClick() },
        modifier = modifier
            .height(50.dp)
            .width(200.dp),
        colors = ButtonDefaults.buttonColors(color),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp, hoveredElevation = 16.dp),
        shape = CircleShape
        ) {
            Text(text = text, color = textColor)
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewButton(){
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Gray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CustomButton(onClick = { /*TODO*/ }, text = "Button", color = Color.Black, textColor = Color.Black,enabled = true)
    }
}