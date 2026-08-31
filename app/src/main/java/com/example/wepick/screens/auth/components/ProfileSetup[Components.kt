package com.example.wepick.screens.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wepick.R
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DarkButtonPurple
import com.example.wepick.ui.theme.FieldBorder
import com.example.wepick.ui.theme.InkSoft
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.PressStart2P


@Composable
fun ProfileAvatar(
    photoUrl: String?,
    isImageUploading: Boolean,
    onAddClick: () -> Unit,
) {
    Box(
        modifier = Modifier.size(88.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFE49A), Color(0xFFFFC94D))
                    )
                )
                .border(3.dp, DarkButtonPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isImageUploading) {
                CircularProgressIndicator(
                    color = DarkButtonPurple,
                    modifier = Modifier.size(30.dp)
                )
            } else if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "?",
                    style = TextStyle(
                        fontFamily = PressStart2P,
                        fontSize = 32.sp,
                        color = DarkButtonPurple,
                        fontWeight = FontWeight.Black
                    )
                )
            }
        }

        if (!isImageUploading) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(DarkButtonPurple)
                    .border(2.dp, CardYellow, CircleShape)
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Photo",
                    tint = CardYellow,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun LockedEmailField(
    email: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.profile_setup_display_email_label),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = InkSoft,
            fontFamily = Nunito,
            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = {},
            enabled = false,
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = Color(0x0F241436),
                disabledBorderColor = FieldBorder,
                disabledTextColor = InkSoft,
                disabledPlaceholderColor = InkSoft
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = InkSoft,
                    modifier = Modifier.size(16.dp)
                )
            },
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        )
    }
}
