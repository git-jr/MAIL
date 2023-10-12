package com.alura.mail.dao

import com.alura.mail.ui.home.Email
import com.alura.mail.ui.home.User
import com.alura.mail.ui.theme.backgroundProfileColors
import kotlinx.datetime.Clock


class EmailDao {
    fun getEmails(): List<Email> {
        val emails = mutableListOf<Email>()

        // gera 20 emails da ultima semana
        repeat(20) { index ->
            val currentTime = generateRandomDateInMillis(1)
            emails.add(
                Email(
                    id = index,
                    subject = "Curso de Kotlin $index",
                    content = "Olá, tudo bem? Estamos aqui para te avisar que o curso de Android está com uma promoção imperdível. Corra e garanta já a sua vaga!",
                    time = currentTime,
                    color = backgroundProfileColors.random(),
                    user = User(
                        "Alura",
                        "https://www.alura.com.br/assets/img/alura-logo-white.1570550707.svg"
                    ),
                ),
            )
        }

        // gera 20 emails do ano passado
        repeat(20) {
            val index = it + 20
            val currentTime = generateRandomDateInMillis(52)
            emails.add(
                Email(
                    id = index,
                    subject = "Curso de Kotlin $index",
                    content = "Olá, tudo bem? Estamos aqui para te avisar que o curso de Android está com uma promoção imperdível. Corra e garanta já a sua vaga!",
                    time = currentTime,
                    color = backgroundProfileColors.random(),
                    user = User(
                        "Alura",
                        "https://www.alura.com.br/assets/img/alura-logo-white.1570550707.svg"
                    ),
                ),
            )
        }

        // gera 20 emails de 2 anos atrás
        repeat(20) {
            val index = it + 40
            val currentTime = generateRandomDateInMillis(104)
            emails.add(
                Email(
                    id = index,
                    subject = "Curso de Kotlin $index",
                    content = "Olá, tudo bem? Estamos aqui para te avisar que o curso de Android está com uma promoção imperdível. Corra e garanta já a sua vaga!",
                    time = currentTime,
                    color = backgroundProfileColors.random(),
                    user = User(
                        "Alura",
                        "https://www.alura.com.br/assets/img/alura-logo-white.1570550707.svg"
                    ),
                ),
            )
        }

        return emails.sortedByDescending { it.time }
    }
}

fun generateRandomDateInMillis(weeksAgo: Int): Long {
    val currentDate = Clock.System.now().toEpochMilliseconds()

    val weeksAgoInMillis = weeksAgo.toLong() * 7 * 24 * 60 * 60 * 1000
    val startTime = currentDate - weeksAgoInMillis

    return (startTime until currentDate).random()
}


//private fun generateRandomDateInMillisJava(weeksAgo: Int = 150): Long {
//    val currentDate = System.currentTimeMillis()
//
//    val startTime = Calendar.getInstance().apply {
//        timeInMillis = System.currentTimeMillis()
//        add(Calendar.WEEK_OF_MONTH, -weeksAgo)
//    }.timeInMillis
//
//    return (startTime until currentDate).random()
//}