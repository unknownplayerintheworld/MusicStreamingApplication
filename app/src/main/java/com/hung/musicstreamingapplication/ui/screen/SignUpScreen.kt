package com.hung.musicstreamingapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hung.musicstreamingapplication.ui.components.CustomButton
import com.hung.musicstreamingapplication.ui.components.CustomePasswordField
import com.hung.musicstreamingapplication.ui.components.CustomeRePasswordField
import com.hung.musicstreamingapplication.ui.components.CustomeTextField

@Composable
fun SignUpAccount(
    onSignUpClick: (String,String,String) -> Unit = {_,_,_, ->},
    onSignInClick: () -> Unit = {},
    navController: NavController
) {
    val systemUiController = rememberSystemUiController()

    // Đặt màu nền của thanh trạng thái và thanh điều hướng thành trong suốt
    systemUiController.setStatusBarColor(Color.Transparent, darkIcons = true)
    systemUiController.setNavigationBarColor(Color.Transparent, darkIcons = true)

    // Ẩn thanh trạng thái và thanh điều hướng
    systemUiController.isStatusBarVisible = false
    systemUiController.isNavigationBarVisible = false

    var text by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repass by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val onPasswordChange: (String) -> Unit = { newPW -> password = newPW }
    val onTextChange: (String) -> Unit = { newText -> text = newText }
    val onEmailChange: (String) -> Unit = { newEmail -> email = newEmail }
    val onRepassChange: (String) -> Unit = { newRepass -> repass = newRepass }

    fun validateInputs() {
        emailError = if (!isValidEmail(email)) "Invalid email address" else null
        usernameError = if (!isValidUsername(text)) "Username must be 6-12 alphanumeric characters" else null
        passwordError = if (password.isEmpty()) "Password cannot be empty" else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),  // Đặt màu nền cho toàn bộ Column
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Sign In",color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable {
                    navController.navigate("login")
                })
            Spacer(modifier = Modifier.width(30.dp))
            @Suppress("DEPRECATION")
            Divider(
                modifier = Modifier
                    .height(20.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(text = "Sign Up", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 40.sp
                        )
                    ) {
                        append("Create an Account")
                    }
                },
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Don't worry!",color = MaterialTheme.colorScheme.onBackground)
            Text(text = "It will only take a moment!",color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.height(30.dp))

        CustomeTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Email",
            imageVector = Icons.Default.Email,
            errorMessage = emailError,
            onCleareErr = {
                emailError = null
            }
        )
//        emailError?.let { Text(text = it, color = Color.Red, style = TextStyle(fontSize = 12.sp)) }
        Spacer(modifier = Modifier.height(20.dp))

        CustomeTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = "Username",
            imageVector = Icons.Default.AccountCircle,
            errorMessage = usernameError,
            onCleareErr = {
                usernameError = null
            }
        )
//        usernameError?.let { Text(text = it, color = Color.Red, style = TextStyle(fontSize = 12.sp)) }
        Spacer(modifier = Modifier.height(20.dp))

        CustomePasswordField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Password",
            errorMessage = passwordError,
            onCleardErr = {
                passwordError = null
            }
        )
//        passwordError?.let { Text(text = it, color = Color.Red, style = TextStyle(fontSize = 12.sp)) }
        Spacer(modifier = Modifier.height(20.dp))

        CustomeRePasswordField(
            value = repass,
            onValueChange = onRepassChange,
            placeholder = "Confirm",
            passwordField = password
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomButton(
            onClick = {
                validateInputs()
                if (emailError == null && usernameError == null && passwordError == null) {
                    onSignUpClick(email,text,password)
                }
            },
            text = "Sign Up",
            color = MaterialTheme.colorScheme.onBackground,
            textColor = MaterialTheme.colorScheme.background,
            enabled = repass == password && emailError == null && usernameError == null && passwordError == null
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Had an account?",color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Sign In?",
            textDecoration = TextDecoration.Underline,
            color = Color.Blue,
            modifier = Modifier.clickable {
                navController.navigate("login")
            }
        )
    }
}

// Hàm kiểm tra định dạng email hợp lệ
fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[\\w+_.-]+@(.+)$".toRegex()
    return emailRegex.matches(email)
}

// Hàm kiểm tra username hợp lệ
fun isValidUsername(username: String): Boolean {
    val usernameRegex = "^[a-zA-Z0-9]{6,12}$".toRegex()
    return usernameRegex.matches(username)
}



    @Preview(showBackground = true)
    @Composable
    fun Preview() {
        SignUpAccount(navController = NavController(context = LocalContext.current))
    }