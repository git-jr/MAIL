package com.alura.mail.ui.contentEmail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alura.mail.R
import com.alura.mail.extensions.toFormattedDate
import com.alura.mail.model.Email
import com.alura.mail.ui.components.LoadScreen
import com.alura.mail.ui.settings.LanguageDialog

@Composable
fun ContentEmailScreen() {
    val viewModel = hiltViewModel<ContentEmailViewModel>()
    val state by viewModel.uiState.collectAsState()

    state.selectedEmail?.let { email ->
        val textTranslateFor = stringResource(
            R.string.translate_from_to,
            state.languageIdentified?.name.toString(),
            state.localLanguage?.name.toString()
        )

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            EmailHeader(email)
            if (state.canBeTranslate) {
                AnimatedVisibility(
                    visible = state.showTranslateButton,
                ) {
                    EmailSubHeader(
                        textTranslateFor = textTranslateFor,
                        translatedState = state.translatedState,
                        onTranslate = {
                            viewModel.tryTranslateEmail()
                        },
                        onClose = {
                            viewModel.hideTranslateButton()
                        },
                    )
                }
            }
            EmailContent(email, state.rangeList)
            // Lista de chips de sugestão
            Spacer(modifier = Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp)
                ) {
                    state.suggestions.forEach { suggestion ->
                        SuggestionChip(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            onClick = {
                                if (suggestion.action == SuggestionAction.SMART_REPLY) {
                                    viewModel.addReply(suggestion.text)
                                } else {
                                    viewModel.setSelectSuggestion(suggestion)
                                }
                            },
                            label = {
                                Text(
                                    suggestion.text,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.widthIn(max = 100.dp)
                                )
                            },
                            shape = CircleShape,
                            icon = {
                                suggestion.icon?.let {
                                    Icon(
                                        painter = painterResource(id = suggestion.icon),
                                        contentDescription = suggestion.text,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                        )
                    }
                }
            }

            state.selectedSuggestion?.let { suggestion ->
                ExecuteAction(suggestion)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.showDownloadLanguageDialog) {
            LanguageDialog(
                title = "${state.languageIdentified?.name}",
                description = stringResource(R.string.download_warning_emails),
                confirmText = stringResource(R.string.baixar),
                onConfirm = {
                    viewModel.downloadLanguageModel()
                    viewModel.showDownloadLanguageDialog(false)
                },
                onDismiss = {
                    viewModel.setTranslateState(TranslatedState.NOT_TRANSLATED)
                    viewModel.showDownloadLanguageDialog(false)
                },
            )
        }
    } ?: run {
        LoadScreen()
    }
}

@Composable
private fun EmailSubHeader(
    textTranslateFor: String,
    translatedState: TranslatedState,
    onTranslate: () -> Unit = {},
    onClose: () -> Unit = {},
) {
    Column {
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
                    .clickable { onTranslate() }
                    .padding(16.dp)
                    .weight(0.8f)
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
                        color = if (translatedState == TranslatedState.TRANSLATED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inverseOnSurface,
                        fontWeight = if (translatedState == TranslatedState.TRANSLATED) FontWeight.Normal else FontWeight.Bold
                    )
                    if (translatedState == TranslatedState.TRANSLATED) {
                        Text(
                            text = stringResource(R.string.show_original),
                            fontSize = MaterialTheme.typography.labelMedium.fontSize,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                modifier = Modifier.weight(0.1f),
                onClick = { onClose() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Esconder sugestão de tradução",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (translatedState == TranslatedState.TRANSLATING) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun EmailHeader(email: Email) {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun EmailContent(email: Email, rangeList: List<IntRange> = emptyList()) {
    Text(
        text = email.subject,
        modifier = Modifier.padding(16.dp),
        fontSize = MaterialTheme.typography.titleLarge.fontSize,
    )

    if (rangeList.isNotEmpty()) {
        MountHiText(email.content, rangeList)
    } else {
        Text(
            text = email.content,
            modifier = Modifier.padding(16.dp),
            style = TextStyle(fontSize = 18.sp)
        )
    }
}


@Composable
fun MountHiText(content: String, rangeList: List<IntRange> = emptyList()) {

    val context = LocalContext.current
    var textLabel = AnnotatedString("")
    var currentRange = 0

    rangeList.forEachIndexed { index, it ->
        val normalText = content.substring(currentRange, it.first)
        val linkText = content.substring(it.first, it.last)
        val tempText = buildAnnotatedString {
            append(normalText)
            withStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline
                )
            ) { append(linkText) }

            addStringAnnotation(
                tag = index.toString(),
                annotation = linkText,
                start = it.first,
                end = it.last
            )
        }
        textLabel = textLabel.plus(tempText)
        currentRange = it.last
    }

    textLabel.spanStyles.forEachIndexed { index, spanStyle ->
        Log.e("spanStyles", "all ranges $index: ${spanStyle.start} - ${spanStyle.end}")
    }
    SelectionContainer {
        ClickableText(
            modifier = Modifier.padding(16.dp),
            style = TextStyle(
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            text = textLabel,
            onClick = { offset ->
                val tagIndex = textLabel.spanStyles.indexOfFirst { offset in it.start until it.end }

                if (tagIndex > -1) {
                    val entity = textLabel.getStringAnnotations(
                        start = 0,
                        end = Int.MAX_VALUE // o ideal seria "textLabel.length" mas ele não está pegando o tamanho completo do texto
                    )[tagIndex]
                    Log.i("spanStyles", "offset: $offset")
                    Log.e("spanStyles", "annotations: ${entity.item}")

                    copyToTransferArea(context, entity.item)
                }
            }
        )
    }

}

@Composable
fun ExecuteAction(suggestion: Suggestion) {
    // o ideial é elevar isso aqui para um local que já tenha o context
    val context = LocalContext.current
    when (suggestion.action) {
        SuggestionAction.ADDRESS -> {
            openMaps(context, suggestion.text)
        }

        SuggestionAction.DATE_TIME -> {
            addEvent(context, suggestion.text)
        }

        SuggestionAction.EMAIL -> {
            sendEmail(context, suggestion.text)
        }

        SuggestionAction.PHONE_NUMBER -> {
            callPhone(context, suggestion.text)
        }

        SuggestionAction.URL -> {
            openUrl(context, suggestion.text)
        }

        else -> {
            CopyToTransferAreaCompose(suggestion.text)
        }
    }
}


@Composable
fun CopyToTransferAreaCompose(text: String) {
    val clipboardManager = LocalClipboardManager.current
    clipboardManager.setText(AnnotatedString(text))

    val context = LocalContext.current
    Toast.makeText(context, "Copiado para área de transferência", Toast.LENGTH_SHORT).show()

//    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//    val clipData = ClipData.newPlainText("text", text)
//    clipboardManager.setPrimaryClip(clipData)
}


fun copyToTransferArea(context: Context, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", text)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(context, "Copiado para área de transferência: $text", Toast.LENGTH_SHORT).show()
}

private fun callPhone(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_CALL)
    intent.data = Uri.parse("tel:$text")
    context.startActivity(intent)
}

private fun sendEmail(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(text))
        putExtra(Intent.EXTRA_SUBJECT, "Assunto do e-mail");
        putExtra(Intent.EXTRA_TEXT, "Corpo do e-mail");
    }
    context.startActivity(intent)
}

fun addEvent(
    context: Context,
    title: String,
    location: String = "",
    begin: Long = 0,
    end: Long = 0
) {
    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, title)
        putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
    }
    context.startActivity(intent)
}


private fun openMaps(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("geo:0,0?q=$text")
    context.startActivity(intent)
}

private fun openUrl(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(text)
    context.startActivity(intent)
}

@Composable
private fun OpenUrlCompose(context: Context, text: String) {
    val uriHandler: UriHandler = LocalUriHandler.current
    uriHandler.openUri(text)
}