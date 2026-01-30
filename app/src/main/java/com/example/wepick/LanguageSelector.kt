package com.example.wepick

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wepick.R
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.Muted
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White

@Composable
fun LanguageSelector(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage

    val languages = listOf("EN" to "en", "UA" to "uk", "RU" to "ru")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.select_lang).uppercase(),
            fontFamily = PressStart2P,
            fontSize = 8.sp,
            color = Muted,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            languages.forEach { (label, code) ->
                val isSelected = currentLang == code

                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 35.dp)
                        .background(
                            if (isSelected) AccentRed else White,
                            RoundedCornerShape(8.dp)
                        )
                        .border(2.dp, Black, RoundedCornerShape(8.dp))
                        .clickable {
                            if (!isSelected) viewModel.setLanguage(code, context)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontFamily = PressStart2P,
                        fontSize = 10.sp,
                        color = if (isSelected) White else Black
                    )
                }
            }
        }
    }
}