package com.alura.mail.ui.contentEmail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alura.mail.R
import com.alura.mail.extensions.toFormattedDate
import com.alura.mail.model.Email

@Composable
fun ContentEmailScreen(state: ContentEmailUiState) {
    val email = state.selectedEmail ?: return
    val textTranslateFor =
        "Traduzir do ${state.languageIdentified?.name} para ${state.localLanguage?.name}"

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        var alreadyTranslated by remember {
            mutableStateOf(false)
        }

        EmailHeader(email)
        if (state.needTranslate) {
            EmailSubHeader(
                textTranslateFor = textTranslateFor,
                alreadyTranslated = state.alreadyTranslated,
                onClick = { alreadyTranslated = !alreadyTranslated }
            )
        }
        EmailContent(email)
    }
}

@Composable
private fun EmailSubHeader(
    textTranslateFor: String,
    alreadyTranslated: Boolean,
    onClick: () -> Unit = {}
) {
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_translate),
                contentDescription = "Traduzir",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column {
                Text(
                    text = textTranslateFor,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    color = if (alreadyTranslated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inverseOnSurface,
                    fontWeight = if (alreadyTranslated) FontWeight.Normal else FontWeight.Bold
                )
                if (alreadyTranslated) {
                    Text(
                        text = "Mostrar original",
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        IconButton(
            modifier = Modifier.padding(horizontal = 4.dp),
            onClick = { /*TODO*/ }
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Mais informações",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun EmailHeader(email: Email) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = email.subject,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(email.color)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = email.user.name.first().toString(),
                        color = Color.White,
                        fontSize = 22.sp,
                    )
                }


                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = email.user.name,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = email.time.toFormattedDate(),
                        color = Color.Gray,
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    )
                }
            }

            Icon(
                Icons.Default.MoreVert, "Mais informações",
            )
        }
    }
}

@Composable
fun EmailContent(email: Email) {
    Text(
        text = email.content + email.content,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
    )
}