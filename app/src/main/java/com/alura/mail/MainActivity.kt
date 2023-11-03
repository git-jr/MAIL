package com.alura.mail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alura.mail.mlkit.EntityExtractor
import com.alura.mail.ui.contentEmail.CopyToTransferAreaCompose
import com.alura.mail.ui.contentEmail.copyToTransferArea
import com.alura.mail.ui.theme.MAILTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAILTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //TestesDeSelecao()

//                    val navController = rememberNavController()
//                    HomeNavHost(navController = navController)

                    val text1 = "My flight is LX373, please pick me up at 8am tomorrow."
                    val text =
                        "Fala aí John, tudo bem? Esse é o local que falei: 221B Baker Street, Reino Unido " +
                                "Vejo você lá às 16:19, qualquer coisa pode ligar nesse número: 4002-8922 ou manda msg" +
                                "por aqui: teste@gmail.com. Na dúvida, dá uma olhada no https://www.alura.com.br/ " +
                                "O valor valor do metro aqui é £ 2,78 que dá uns R$ 1.500 reais kkkkk" +
                                "Ah mais uma coisa, não esquece meus US$100, meu IBAN é: GB15MIDL40051512345678"

                    val entityExtraction = EntityExtractor()
                    entityExtraction.extract(
                        text = text,
                        onSuccess = {
                            Log.e("entityExtractor", "Tamanho: ${it.size}")
                        }
                    )

                    var rangeList by remember {
                        mutableStateOf(emptyList<IntRange>())
                    }

                    entityExtraction.gentRanges(
                        text = text,
                        onSuccess = {
                            rangeList = it
                            Log.e("entityExtractor", "Tamanho: ${it.size}")
                        }
                    )

                    if (rangeList.isNotEmpty()) {
                        MountHiText(content = text, rangeList = rangeList)
                    }

//                    TestesDeSelecao()

                }
            }
        }
    }

    @Composable
    private fun TestesDeSelecao() {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val string = buildAnnotatedString {
                append("Texto normal")
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        fontWeight = FontWeight.Bold,
                    )
                ) {
                    append("link de algo ")
                }
                append("outro normal ")
                addStringAnnotation(
                    tag = "TAG 1",
                    annotation = "https://developer.android.com/jetpack/compose",
                    start = 0,
                    end = 10
                )
                addStringAnnotation(
                    tag = "TAG 22222222222222",
                    annotation = "https://developer.android.com/jetpack/compose",
                    start = 15,
                    end = 50
                )
                //                            addUrlAnnotation(
                //                                start = 6,
                //                                end = 21,
                //                                urlAnnotation = UrlAnnotation("https://developer.android.com/jetpack/compose")
                //                            )
                withStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Jetpack Compose Sublinhado")
                }
            }

            SelectionContainer {
                ClickableText(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    style = TextStyle(fontSize = 22.sp),
                    text = string,
                    onClick = {
                        string.getStringAnnotations(it, it)
                            .firstOrNull()?.let { annotation ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Clicado: ${annotation.tag} ${annotation.item}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                )
            }
        }
    }
}


@Composable
fun AnnotatedClickableText() {
    val annotatedText = buildAnnotatedString {
        append("Click ")

        // We attach this *URL* annotation to the following content
        // until `pop()` is called
        pushStringAnnotation(
            tag = "URL",
            annotation = "https://developer.android.com"
        )
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("here")
        }

        pop()
    }
    val context = LocalContext.current
    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            // We check if there is an *URL* annotation attached to the text
            // at the clicked position
            annotatedText.getStringAnnotations(
                tag = "URL", start = offset,
                end = offset
            ).firstOrNull()?.let { annotation ->
                // If yes, we log its value
                Toast.makeText(context, "Clicado", Toast.LENGTH_SHORT).show()
                Log.d("Clicked URL", annotation.item)
            }
        }
    )
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
            ) {
                append(linkText)
            }

            addStringAnnotation(
                tag = "TAG $index",
                annotation = linkText,
                start = it.first,
                end = it.last
            )
        }
        textLabel = textLabel.plus(tempText)
        currentRange = it.last
    }

    textLabel.spanStyles.forEachIndexed { index, spanStyle ->
        Log.e("spanStyles", "spanStyles $index: ${spanStyle.start} - ${spanStyle.end}")
    }

    SelectionContainer {
        ClickableText(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            style = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground),
            text = textLabel,
            onClick = { offset ->
                Log.i("spanStyles", "offset: $offset")
                Log.i("spanStyles", "last index: ${textLabel.length}")

                val indexTag = textLabel.spanStyles.indexOfFirst { offset in it.start until it.end }

                if (indexTag > -1) {
                    val entity = textLabel.getStringAnnotations(
                        start = 0,
                        end = Int.MAX_VALUE // o idela seria "textLabel.length" mas ele não está pegando o tamanho completo do texto
                    )[indexTag]
                    Log.e("spanStyles", "annotations: ${entity.item}")

                    copyToTransferArea(
                        context = context,
                        text = entity.item
                    )
                }
            }
        )
    }

}

@Composable
fun MountHiTextOld(content: String, rangeList: List<IntRange> = emptyList()) {

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
            ) {
                append(linkText)
            }

            addStringAnnotation(
                tag = "TAG $index",
                annotation = linkText,
                start = it.first,
                end = it.last
            )
        }
        textLabel = textLabel.plus(tempText)
        currentRange = it.last
    }


    SelectionContainer {
        ClickableText(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            style = TextStyle(fontSize = 18.sp),
            text = textLabel,
            onClick = { offset ->
                // Obtém todas as anotações na posição do clique
                val annotations = textLabel.getStringAnnotations(0, 1000)
                if (annotations.isNotEmpty()) {
                    Log.e("Ranges", "Ranges ${annotations.size}")
                    annotations.forEachIndexed { index, range ->

                        Log.e("Ranges", "TAG ${annotations[index].item}")
                        Log.e("Ranges", "Range $index: ${range.start} - ${range.end}")
                    }
                }


                val tag = annotations.find { it.tag != null }?.tag
                if (tag != null) {
                    Log.d("ClickedTag", tag)
                }

                Log.d("offset", "offset: $offset")
                Log.d("offset", "Tamanho do texto: ${textLabel.text.length}")
                Log.d(
                    "Clicked URL",
                    "Todos as tags: ${textLabel.getStringAnnotations(0, 1000)}"
                )

                textLabel.getStringAnnotations(offset, textLabel.length).firstOrNull().let {
                    Log.d("Unique", "Pimeira tag: ${it?.tag}")
                    Log.d("Unique", "Item: ${it?.item}")
                }

                val textoDoClique =
                    textLabel.getStringAnnotations(offset, offset).firstOrNull()
                        ?.let { annotation ->
                            // If yes, we log its value
                            Log.d("Agora vai", annotation.item)
                            Log.d("Agora vai", annotation.tag)
                            Log.d("Agora vai", "range: ${annotation.start} - ${annotation.end}")

                        }

                Log.e("textoDoClique", "textoDoClique: $textoDoClique")

                Toast.makeText(
                    context,
                    "Clicado: $textoDoClique",
                    Toast.LENGTH_SHORT
                ).show()

//                textLabel.getStringAnnotations(it, it)
//                    .firstOrNull()?.let { annotation ->
//                        Toast.makeText(
//                            context,
//                            "Clicado: ${annotation.tag} ${annotation.item}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
            }
        )
    }

}