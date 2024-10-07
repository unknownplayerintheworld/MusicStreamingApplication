package com.hung.musicstreamingapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hung.musicstreamingapplication.R

@Composable
fun CustomeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    imageVector: ImageVector,
    errorMessage: String? = null,
    onCleareErr:() -> Unit
) {

    TextField(
        value = value,
        onValueChange = onValueChange,
        maxLines = 1,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray.copy(alpha = 0.5f),
                style = TextStyle(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 23.sp,
                fontFamily = FontFamily.SansSerif
            )
        },
        textStyle = TextStyle(textAlign = TextAlign.Center),
        trailingIcon = {
            if (errorMessage != null && errorMessage != "") {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            onCleareErr()
                        }
                )
            }
            else {
                Icon(imageVector = imageVector, contentDescription = "Username")
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent, // Ẩn đường viền khi được focus
            unfocusedIndicatorColor = Color.Transparent // Ẩn đường viền khi không được focus
        ),
        modifier = Modifier
            .padding(10.dp, 10.dp, 10.dp, 0.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clip(RoundedCornerShape(10.dp))
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onCleareErr()
                }
            },
        shape = RoundedCornerShape(20.dp)
    )
    errorMessage?.let {
        Text(
            text = it,
            color = Color.Red,
            style = TextStyle(fontSize = 12.sp)
        )

    }
    if(errorMessage==null || errorMessage == ""){
        Spacer(modifier = Modifier.height(14.dp))
    }
}
@Composable
fun CustomePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessage: String? = null,
    onCleardErr:() -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val visualTransformation = if (passwordVisible) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }
    val iconResId = if (passwordVisible) {
        R.drawable.ic_visibility // Đổi thành ID của icon hiện mật khẩu
    } else {
        R.drawable.baseline_visibility_off_24 // Đổi thành ID của icon ẩn mật khẩu
    }
    TextField(
        value = value,
        onValueChange = onValueChange,
        maxLines = 1,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray.copy(alpha = 0.5f),
                style = TextStyle(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 23.sp,
                fontFamily = FontFamily.SansSerif
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent, // Ẩn đường viền khi được focus
            unfocusedIndicatorColor = Color.Transparent // Ẩn đường viền khi không được focus
        ),
        textStyle = TextStyle(textAlign = TextAlign.Center),
        visualTransformation = visualTransformation,
        modifier = Modifier
            .padding(10.dp,10.dp,10.dp,0.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onCleardErr()
                }
            },
        shape = RoundedCornerShape(20.dp),
        trailingIcon = {
            if (errorMessage != null && errorMessage != "") {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            onCleardErr()
                        }
                )
            }
            else {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        }
    )
    errorMessage?.let {
        Text(
            text = it,
            color = Color.Red,
            style = TextStyle(fontSize = 12.sp)
        )
    }
    if(errorMessage==null || errorMessage == ""){
        Spacer(modifier = Modifier.height(14.dp))
    }
}
@Composable
fun CustomeRePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    passwordField: String
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isPassword = value == passwordField

    val visualTransformation = if (passwordVisible) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }
    TextField(
        value = value,
        onValueChange = onValueChange,
        maxLines = 1,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray.copy(alpha = 0.5f),
                style = TextStyle(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 23.sp,
                fontFamily = FontFamily.SansSerif
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent, // Ẩn đường viền khi được focus
            unfocusedIndicatorColor = Color.Transparent // Ẩn đường viền khi không được focus
        ),
        textStyle = TextStyle(textAlign = TextAlign.Center),
        visualTransformation = visualTransformation,
        modifier = Modifier
            .padding(5.dp,10.dp,10.dp,0.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp),
        trailingIcon = {
            IconButton(onClick = {   }) {
                Icon(imageVector = if(isPassword) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = if(isPassword) "Correct" else "Incorrect",
                    tint = if(isPassword) Color.Green else Color.Red)
            }
        }
    )
}
@Preview
@Composable
fun PreviewCustomTF() {
    // Create a mutable state to hold the text value
    var text by remember { mutableStateOf("Username") }

    // Define a function to update the text value
    val onTextChange: (String) -> Unit = { newText -> text = newText }
    CustomeTextField(value = "Username", onValueChange = onTextChange, placeholder = "Username",Icons.Default.AccountCircle, onCleareErr = {})
}

@Preview
@Composable
fun PreviewCustomPF() {
    // Create a mutable state to hold the text value
    var text by remember { mutableStateOf("Username") }

    // Define a function to update the text value
    val onTextChange: (String) -> Unit = { newText -> text = newText }
    CustomePasswordField(value = "Password", onValueChange = onTextChange, placeholder = "Password", onCleardErr = {})
}

@Preview
@Composable
fun PreviewRepass() {
    // Create a mutable state to hold the text value
    var text by remember { mutableStateOf("Password") }

    // Define a function to update the text value
    val onTextChange: (String) -> Unit = { newText -> text = newText }
    CustomeRePasswordField(value = "Password", onValueChange = onTextChange, placeholder = "Confirm", passwordField = "Password")
}