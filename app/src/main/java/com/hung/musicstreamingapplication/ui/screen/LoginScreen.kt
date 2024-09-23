package com.hung.musicstreamingapplication.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.presentation.sign_in.SignInState
import com.hung.musicstreamingapplication.ui.components.CustomButton
import com.hung.musicstreamingapplication.ui.components.CustomePasswordField
import com.hung.musicstreamingapplication.ui.components.CustomeTextField


@Composable
fun SignInWithGoogleBar(
    onSignUpClick: () -> Unit = {},
    onSignInWithGG: () -> Unit,
    onSignInClick: (String,String) -> Unit = {_,_ ->},
    navController: NavController,
    state: SignInState
) {
    val systemUiController = rememberSystemUiController()

    // Đặt màu nền của thanh trạng thái và thanh điều hướng thành trong suốt
    systemUiController.setStatusBarColor(Color.Transparent, darkIcons = true)
    systemUiController.setNavigationBarColor(Color.Transparent, darkIcons = true)

    // Ẩn thanh trạng thái và thanh điều hướng
    systemUiController.isStatusBarVisible = false
    systemUiController.isNavigationBarVisible = false

    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    var text by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val onPasswordChange: (String) -> Unit = { newPW -> password = newPW }
    val onTextChange: (String) -> Unit = { newText -> text = newText }

    fun validateInputs() {
        usernameError = if (text.isBlank()) "Username cannot be empty" else null
        passwordError = if (password.isBlank()) "Password cannot be empty" else null
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
            Text(text = "Sign In", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(30.dp))
            Divider(
                modifier = Modifier
                    .height(20.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(text = "Sign Up",color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                })
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
                        append("Welcome to MUSA")
                    }
                },
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Countless Exciting Things Await!",color = MaterialTheme.colorScheme.onBackground)
            Text(text = "Let's check it out!",color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.height(30.dp))

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

        Spacer(modifier = Modifier.height(30.dp))

        CustomButton(
            onClick = {
                validateInputs()
                if (usernameError == null && passwordError == null) {
                    // Perform login action
                    onSignInClick(text,password)
                }
            },
            text = "Login",
            color = MaterialTheme.colorScheme.onBackground,
            textColor = MaterialTheme.colorScheme.background,
            enabled = true
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Forgot your password?",
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .width(70.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "or",color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.width(10.dp))
            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .width(70.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .width(260.dp)
                .height(60.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(30.dp)
                )
                .clickable {
                    onSignInWithGG()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.icongoogle),
                contentDescription = "Google Logo"
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Sign In With Google",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Don't have an account?",color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "SignUp?",
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navController.navigate("signup")
            }
        )
    }
}



    @Preview(showBackground = true)
    @Composable
    fun PreviewLogin() {
//        SignInWithGoogleBar(navController = NavController(LocalContext.current))
    }