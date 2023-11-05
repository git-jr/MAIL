package com.alura.mail.mlkit

import android.util.Log
import com.alura.mail.util.FileUtil
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage

class ResponseGenerator(private val fileUtil: FileUtil) {
    fun generateResponse(
        messages: List<Message>,
        onSuccess: (List<String>) -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val suggestions = mutableListOf<String>()
        val totalTexts = messages.size
        var textsTranslated = 0

        val smartReplyGenerator = SmartReply.getClient()
        generateTextMessage(
            fakeConversation = messages,
            onSuccess = { translatedList ->
                smartReplyGenerator.suggestReplies(translatedList)
                    .addOnSuccessListener { result ->
                        when (result.status) {
                            SmartReplySuggestionResult.STATUS_SUCCESS -> {
                                for (suggestion in result.suggestions) {
                                    val replyText = suggestion.text
                                    Log.i("Respondedor", "Suggestão: $replyText")

                                    val textTranslator = TextTranslator(fileUtil)

                                    val sourceLanguage = "en"
                                    val targetLanguage = "pt"
                                    textTranslator.textTranslate(replyText,
                                        sourceLanguage,
                                        targetLanguage,
                                        onSuccess = { translatedText ->
                                            Log.i("Respondedor", "traduzdio: $translatedText")
                                            textsTranslated++
                                            if (textsTranslated == totalTexts) {
                                                onSuccess(suggestions)
                                            }
                                        }
                                    )
                                }
                            }

                            SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE -> {
                                onFailure()
                                Log.e("Respondedor", "Error idioma não suportado")
                            }


                            SmartReplySuggestionResult.STATUS_NO_REPLY -> {
                                onFailure()
                                Log.e("Respondedor", "Erro: Sem respostas")
                            }
                        }
                    }
                    .addOnFailureListener {
                        // Task failed with an exception
                        // ...
                        Log.e("Respondedor", "Error", it)
                    }
            }
        )
    }

    private fun generateTextMessage(
        fakeConversation: List<Message>,
        onSuccess: (List<TextMessage>) -> Unit = {}
    ) {
        val textMessageList = mutableListOf<TextMessage>()
        val textTranslator = TextTranslator(fileUtil)

        val sourceLanguage = "pt"
        val targetLanguage = "en"

        val totalTexts = fakeConversation.size
        var textsTranslated = 0

        fakeConversation.forEach { (message, isLocalUser) ->
            textTranslator.textTranslate(message, sourceLanguage, targetLanguage,
                onSuccess = { translatedText ->
                    if (isLocalUser) {
                        Log.i("Respondedor", "time: ${System.currentTimeMillis()}")
                        textMessageList.add(
                            TextMessage.createForLocalUser(
                                translatedText, System.currentTimeMillis()
                            )
                        )
                    } else {
                        Log.i("Respondedor", "time: ${System.currentTimeMillis()}")
                        textMessageList.add(
                            TextMessage.createForRemoteUser(
                                /* messageText = */ translatedText, /* timestampMillis = */
                                System.currentTimeMillis(), /* remoteUserId = */
                                "userId"
                            )
                        )
                    }
                    textsTranslated++
                    if (textsTranslated == totalTexts) {
                        onSuccess(textMessageList)
                    }
                }
            )
        }
    }


//    fun generateResponseOld() {
//
////        val fakeConversation = listOf(
////            Pair("Oi, tudo bem?", true),
////            Pair("Tudo bem e você?", false),
////            Pair("Tudo ótimo", true),
////        )
//
////        val fakeConversation = listOf(
////            Pair("Hi, how are you?", true),
////            Pair("I'm fine, and you?", false),
////            Pair("I'm fine too", true),
////        )
//
//        val smartReplyGenerator = SmartReply.getClient()
//
//        val fakeConversation = listOf(
//            Pair("Estamos entrando contato referente a vaga de desenvolvedor Android", false),
//        )
//
//        generateTextMessage(
//            fakeConversation = fakeConversation,
//            onSuccess = { translatedList ->
//                smartReplyGenerator.suggestReplies(translatedList)
//                    .addOnSuccessListener { result ->
//                        when (result.status) {
//                            SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE -> {
//                                Log.e("Respondedor", "Error idioma não suportado")
//                            }
//
//                            SmartReplySuggestionResult.STATUS_SUCCESS -> {
//                                for (suggestion in result.suggestions) {
//                                    val replyText = suggestion.text
//                                    Log.i("Respondedor", "Suggestão: $replyText")
//
//                                    val textTranslator = TextTranslator(fileUtil)
//
//                                    val sourceLanguage = "en"
//                                    val targetLanguage = "pt"
//                                    textTranslator.textTranslate(replyText,
//                                        sourceLanguage,
//                                        targetLanguage,
//                                        onSuccess = { translatedText ->
//                                            Log.i("Respondedor", "traduzdio: $translatedText")
//                                        }
//                                    )
//                                }
//                            }
//
//                            SmartReplySuggestionResult.STATUS_NO_REPLY -> {
//                                Log.e("Respondedor", "Erro: Sem respostas")
//                            }
//                        }
//                    }
//                    .addOnFailureListener {
//                        // Task failed with an exception
//                        // ...
//                        Log.e("Respondedor", "Error", it)
//                    }
//            }
//        )
//    }

    fun conversationSample() {
        val conversation = mutableListOf<TextMessage>()
        conversation.add(
            TextMessage.createForLocalUser(
                "are you on your way?", System.currentTimeMillis()
            )
        )

        conversation.add(
            TextMessage.createForRemoteUser(
                "Running late, sorry!", System.currentTimeMillis(), "userId"
            )
        )
    }

}

data class Message(
    val content: String,
    val isLocalUser: Boolean,
)


// trechos uteis
//val fakeConversation = listOf(
//    Pair("Tudo certo para amanhã as 10:00?", false),
//    Pair("Sim, te encontro aonde?", true),
//)

//val fakeConversation = listOf(
//    Pair("Bom dia Ana, segue a documentação em anexo", false),
//)

//val fakeConversation = listOf(
//    Pair("Bom dia Carlos, pode passar no departamento de recursos humanos para pegar seu crachá", true),
//    Pair("Ok, muito obrigado", false),
//)

// Gera só emojis
//val fakeConversation = listOf(
//    Pair("Feliz aniversário João", false),
//    Pair("Muuito obrigado Maria!", true),
//)

// Esse é top
//val fakeConversation = listOf(
//    Pair("Feliz halloween 🎃, preprados para gostosuras e travessuras?", false),
//)

// Boa também, mostrar que o false ou true, altera as resposta
//val fakeConversation = listOf(
//    Pair("Promoção de black friday, tudo pela metade do dobro, bora gastar 🤑", false)
//)

//// mais ou menos:
//val fakeConversation = listOf(
//    Pair("Lançamento do novo curso de IA para Android 🚀", false),
//    Pair("Venha conferir!", false)
//)


//
//val fakeConversation = listOf(
//    Pair("Estamos entrando contato referente a vaga de desenvolvedor Android", false),
//)