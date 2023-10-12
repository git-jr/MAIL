package com.alura.mail.dao

import com.alura.mail.ui.home.Email
import com.alura.mail.ui.home.User

class EmailDao {
    fun getEmails(): List<Email> {
        val emails = mutableListOf<Email>()
        repeat(100) {
            emails.add(
                Email(
                    it,
                    "Alura $it",
                    "Curso de Kotlin",
                    "Olá, tudo bem? Estamos aqui para te avisar que o curso de Android está com uma promoção imperdível. Corra e garanta já a sua vaga!",
                    User(
                        "Alura",
                        "https://www.alura.com.br/assets/img/alura-logo-white.1570550707.svg"
                    ),
                ),
            )
        }
        return emails
    }
}