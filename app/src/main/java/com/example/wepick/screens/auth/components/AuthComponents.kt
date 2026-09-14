package com.example.wepick.screens.auth.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.FieldBeige
import com.example.wepick.ui.theme.FieldBorder
import com.example.wepick.ui.theme.InkSoft
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.TextTeal


@Composable
fun ForgotPassword(
    navController: NavController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = stringResource(id = R.string.login_forgot_password),
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = InkSoft,
            fontFamily = Nunito,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                navController.navigate(ScreenNav.ForgotPassword.route)
            }
        )
    }
}

@Composable
fun LoginDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 2.dp,
            color = Black.copy(alpha = 0.15f)
        )
        Text(
            text = stringResource(id = R.string.login_or),
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = InkSoft
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 2.dp,
            color = Black.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun FormTextFields(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    trailingIcon: (@Composable () -> Unit)? = null,
    text: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textField: String,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = modifier) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isError) AccentRed else InkSoft,
            fontFamily = Nunito,
            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp),
        )
        OutlinedTextField(
            value = value,
            readOnly = readOnly,
            onValueChange = onValueChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            shape = RoundedCornerShape(14.dp),
            isError = isError,
            minLines = minLines,
            maxLines = maxLines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FieldBeige,
                unfocusedContainerColor = FieldBeige,
                focusedBorderColor = TextTeal,
                unfocusedBorderColor = FieldBorder,
                errorBorderColor = AccentRed,
                errorTrailingIconColor = AccentRed,
                errorContainerColor = FieldBeige,
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            supportingText = supportingText,
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                color = Black,
                fontSize = 15.sp
            ),
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            placeholder = {
                Text(
                    text = textField.lowercase(),
                    color = Color(0xFFB7A574),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    fontFamily = Nunito
                )
            },

            )
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = AccentRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Nunito,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}


@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    text: String,
    textField: String,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    FormTextFields(
        modifier = modifier,
        value = value,
        onValueChanged = onValueChanged,
        text = text,
        textField = textField,
        isError = isError,
        errorText = errorText,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Mail,
                contentDescription = "Email Icon",
                tint = if (isError) AccentRed else InkSoft
            )
        }
    )
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    text: String,
    textField: String,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {

    var visiblePassword by remember { mutableStateOf(false) }
    FormTextFields(
        modifier = modifier,
        value = value,
        onValueChanged = onValueChanged,
        text = text,
        visualTransformation = if (visiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
        textField = textField,
        isError = isError,
        errorText = errorText,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = {
            val image = if (visiblePassword)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            IconButton(
                onClick = {
                    visiblePassword = !visiblePassword
                }) {
                Icon(
                    imageVector = image,
                    contentDescription = if (visiblePassword) "Hide password" else "Show Password",
                    tint = InkSoft
                )
            }
        }
    )
}