package com.alura.mail.mlkit

import android.util.Log
import com.alura.mail.ui.contentEmail.Suggestion
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

        Log.i("loadSmartSuggestions", "messages: $messages")

        val suggestions = mutableListOf<String>()
        val totalTexts = 3 // sempre são geradas 3 sugestões
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
                                    Log.i("generateResponse", "Suggestão: $replyText")

                                    val textTranslator = TextTranslator(fileUtil)

                                    val sourceLanguage = "en"
                                    val targetLanguage = "pt"
                                    textTranslator.textTranslate(replyText,
                                        sourceLanguage,
                                        targetLanguage,
                                        onSuccess = { translatedText ->
                                            suggestions.add(translatedText)
                                            Log.i("generateResponse", "traduzido: $translatedText")
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
                                Log.e("generateResponse", "Error idioma não suportado")
                            }


                            SmartReplySuggestionResult.STATUS_NO_REPLY -> {
                                onFailure()
                                Log.e("generateResponse", "Erro: Sem respostas")
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

    fun messageToSuggestionAction(messages: List<String>): List<Suggestion> {
        Log.i("messageToSuggestionAction", "messages: $messages")
        return messages.map { message ->
            Suggestion(message)
        }
    }

    fun messageToSuggestionActionOld(messages: List<String>): List<Suggestion> {
        val suggestions = mutableListOf<Suggestion>()
        messages.forEach { message ->
            suggestions.add(Suggestion(message))
        }
        return suggestions
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