package com.alura.mail

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.alura.mail.mlkit.EntityExtractor
import com.alura.mail.ui.contentEmail.MountHiText
import com.alura.mail.ui.contentEmail.copyToTransferArea
import com.alura.mail.ui.navigation.HomeNavHost
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

                    val navController = rememberNavController()
                    HomeNavHost(navController = navController)

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

//                    if (rangeList.isNotEmpty()) {
//                        MountHiText(content = text, rangeList = rangeList)
//                    }
                }
            }
        }
    }

}

