package com.example.wepick.screens.profile_screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wepick.R
import com.example.wepick.screens.profile_screens.components.BackButton
import com.example.wepick.screens.profile_screens.components.LabelText
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DarkButtonPurple
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.TextTeal

private data class FaqItem(val question: String, val answer: String)

@Composable
fun HelpScreen(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val contactEmail = stringResource(R.string.help_contact_email)

    val faqItems = listOf(
        FaqItem(stringResource(R.string.help_faq_q1), stringResource(R.string.help_faq_a1)),
        FaqItem(stringResource(R.string.help_faq_q2), stringResource(R.string.help_faq_a2)),
        FaqItem(stringResource(R.string.help_faq_q3), stringResource(R.string.help_faq_a3)),
        FaqItem(stringResource(R.string.help_faq_q4), stringResource(R.string.help_faq_a4)),
        FaqItem(stringResource(R.string.help_faq_q5), stringResource(R.string.help_faq_a5)),
        FaqItem(stringResource(R.string.help_faq_q6), stringResource(R.string.help_faq_a6)),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardYellow),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BackButton(
                    navController
                )

                LabelText("HELP CENTER") // TODO: ADD IN R.STRING

                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.help_faq_title),
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DarkButtonPurple
                )

                Spacer(Modifier.height(8.dp))

                faqItems.forEach { item ->
                    FaqEntry(item.question, item.answer)
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.help_contact_title),
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DarkButtonPurple
                )

                Spacer(Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$contactEmail")
                            }
                            try {
                                context.startActivity(emailIntent)
                            } catch (e: ActivityNotFoundException) {
                                // no email client installed
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null,
                            tint = TextTeal
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = stringResource(R.string.help_contact_button),
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextTeal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqEntry(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextTeal,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextTeal
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = answer,
                        fontFamily = Nunito,
                        fontSize = 13.sp,
                        color = Black.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}