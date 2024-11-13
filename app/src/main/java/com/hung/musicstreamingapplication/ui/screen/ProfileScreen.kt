package com.hung.musicstreamingapplication.ui.screen

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.activity.SignActivity
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel

@Composable
fun ProfileScreen(
    musicVM: MusicViewModel,
    navController: NavHostController,
    loginVM: LoginViewModel
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val userid by loginVM._currentUserId.collectAsState()
    val currentUser by loginVM.currentUser.collectAsState()
    LaunchedEffect(currentUser){
        userid?.let { loginVM.getUser(it) }
        if(currentUser == null){
            val intent = Intent(context, SignActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    loginVM.logout()
                    // Handle logout logic here
                    val intent = Intent(context,SignActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        )
    }

    // Modal for Delete confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Confirm Delete") },
            text = { Text("Are you sure you want to delete your account?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    // Handle delete logic here
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No")
                }
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        )
    }
    Column(
        Modifier
            .fillMaxWidth()
            .height(1200.dp)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
        ){
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically

            ){
                Text(text = stringResource(id = R.string.personal), color = MaterialTheme.colorScheme.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp))
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Mic", modifier = Modifier.size(25.dp), tint = MaterialTheme.colorScheme.onBackground)
            }
            IconButton(onClick = {
                navController.navigate("search")
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search",tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(25.dp))
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
                LocalContext.current).data(if(
                    !currentUser?.profilePicture.isNullOrEmpty()
                ){
                    currentUser?.profilePicture
                }else{
                    R.drawable.default_user_avt
                }).crossfade(true).build()),
                contentDescription = "journey",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(90.dp)))
            Spacer(modifier = Modifier.width(15.dp))
            currentUser?.let{ Text(text = it.username, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground) }
        }
        Row(
            Modifier.fillMaxWidth()
                .padding(top = 10.dp)
        ){
            Text(modifier = Modifier.padding(horizontal = 15.dp),text = stringResource(id = R.string.upgrade),color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Column(
            Modifier
                .width(400.dp)
                .padding(vertical = 10.dp, horizontal = 15.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row{
                    Text(
                        "MUSA",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Box(
                        Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            "Plus",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "More")
            }
            Text(stringResource(id = R.string.price_options_1),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp))
            Text(text = stringResource(R.string.MUSA_plus_description), fontWeight = FontWeight.Light, fontSize = 15.sp,color = MaterialTheme.colorScheme.onBackground
            , modifier = Modifier.padding(horizontal = 15.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Column(
                    modifier = Modifier
                        .width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(painter = painterResource(id = R.drawable.cancel_ads), contentDescription = "cancelADS")
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Column(Modifier.width(85.dp)) {
                        Text(text = stringResource(id = R.string.cancel_ads_text), fontSize = 12.sp,color = MaterialTheme.colorScheme.onBackground)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier
                        .width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_arrow_circle_down_24), contentDescription = "download")
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Column(Modifier.width(85.dp)) {
                        Text(text = stringResource(id = R.string.download_music_text), fontSize = 12.sp,color = MaterialTheme.colorScheme.onBackground)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier
                        .width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_playlist_add_check_24), contentDescription = "playlist unlimited")
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Column(
                        Modifier.width(85.dp)
                    ) {
                        Text(text = stringResource(id = R.string.playlist_unlimited_text), fontSize = 12.sp,color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth()
        ){
            Text(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),text = stringResource(id = R.string.setting),color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ){
            Icon(painter = painterResource(id = R.drawable.play_circle_outline_24), contentDescription = "player")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.player), fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ){
            Icon(painter = painterResource(id = R.drawable.baseline_download_24), contentDescription = "download")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.download_text), fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ){
            Icon(painter = painterResource(id = R.drawable.library_music_24), contentDescription = "library")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.library), fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ){
            Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "notification")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.notification), fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ){
            Icon(painter = painterResource(id = R.drawable.outline_bedtime_24), contentDescription = "UI")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.interface_ui), fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Row{
                Icon(imageVector = Icons.Outlined.Info, contentDescription = "version")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.version),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(text = stringResource(id = R.string.version_number), fontSize = 12.sp,color = Color.Gray.copy(alpha = 0.8f), fontWeight = FontWeight.Light)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ){
            Icon(painter = painterResource(id = R.drawable.baseline_outlined_flag_24), contentDescription = "support")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.support_and_report), fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    showDeleteDialog = true
                }
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ){
            Icon(painter = painterResource(id = R.drawable.baseline_do_not_disturb_alt_24), contentDescription = "support")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.remove_acc), fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    showLogoutDialog = true
                }
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ){
            Icon(painter = painterResource(id = R.drawable.baseline_logout_24), contentDescription = "logout")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.logout), fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Spacer(modifier = Modifier.height(300.dp))
    }
}