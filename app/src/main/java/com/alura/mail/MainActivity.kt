package com.alura.mail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.alura.mail.mlkit.Message
import com.alura.mail.mlkit.ResponseGenerator
import com.alura.mail.ui.navigation.HomeNavHost
import com.alura.mail.ui.theme.MAILTheme
import com.alura.mail.util.FileUtil
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

                    val fakeConversation = listOf(
                        Message(
                            "Feliz halloween 🎃, preprados para gostosuras e travessuras?",
                            false
                        ),
                    )

                    ResponseGenerator(FileUtil(this)).generateResponse(fakeConversation)
                }
            }
        }
    }

}

