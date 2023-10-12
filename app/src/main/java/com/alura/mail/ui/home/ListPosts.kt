package com.alura.mail.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alura.mail.dao.EmailDao
import com.alura.mail.extensions.toFormattedDate


@Composable
fun ListPosts(emails: List<Email>) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
    ) {
        items(emails) { email ->
            EmailItem(email)
        }
    }
}


@Composable
fun EmailItem(email: Email) {
    ListItem(
        headlineContent = {
            Text(
                text = email.user.name,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
            )
        },
        supportingContent = {
            Text(
                text = email.subject,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = email.content,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },

        leadingContent = {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color(email.color)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = email.user.name.first().toString(),
                    color = Color.White,
                    fontSize = 22.sp,
                )
            }

        },
        trailingContent = {
            Column {
                Text(text = email.time.toFormattedDate())
                Spacer(modifier = Modifier.height(32.dp))
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
        )
    )
}


@Preview
@Composable
fun EmailItemPreview() {
    EmailItem(
        email = EmailDao().getEmails().first()
    )
}

