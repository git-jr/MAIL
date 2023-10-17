package com.alura.mail.dao

import com.alura.mail.model.Email
import com.alura.mail.model.User
import kotlinx.datetime.Clock


class EmailDao {
    fun getEmails(): List<Email> {
        return listOfEmails
    }

    fun getEmailById(id: String): Email? {
        return listOfEmails.firstOrNull { it.id == id }
    }
}

fun generateRandomDateInMillis(weeksAgo: Int): Long {
    val currentDate = Clock.System.now().toEpochMilliseconds()

    val weeksAgoInMillis = weeksAgo.toLong() * 7 * 24 * 60 * 60 * 1000
    val startTime = currentDate - weeksAgoInMillis

    return (startTime until currentDate).random()
}

val listOfEmails = listOf(
    Email(
        id = "1",
        subject = "Um novo curso de Android com IA",
        content = "Olá, tudo bem? \n\nEstamos aqui para te avisar que o curso de Android está com uma promoção imperdível. \nCorra e garanta já a sua vaga! \n\n",
        time = generateRandomDateInMillis(1),
        color = 0xFFFE8966,
        user = User(
            "Ana",
            ""
        ),
    ),
    Email(
        id = "2",
        subject = "Un nouveau cours Android avec IA",
        content = "Salut, comment ça va ? \n\nNous sommes là pour vous informer que le cours Android est en promotion. \nDépêchez-vous et réservez votre place dès maintenant ! \n\n",
        time = 1619382000000L,
        color = 0xFF5F96F5,
        user = User(
            "Élise",
            ""
        ),
    ),
    Email(
        id = "3",
        subject = "Урок Android с ИИ",
        content = "Привет, как дела? \n\nМы здесь, чтобы сообщить вам, что урок Android по специальной цене. \nПоторопитесь и забронируйте свое место! \n\n",
        time = 1642086000000L,
        color = 0xFFF55FEE,
        user = User(
            "Иван",
            ""
        ),
    ),
    Email(
        id = "4",
        subject = "Ein neuer Android-Kurs mit KI",
        content = "Hallo, wie geht es dir? \n\nWir möchten dich darüber informieren, dass der Android-Kurs im Angebot ist. \nSchnapp dir deinen Platz jetzt! \n\n",
        time = 1633206000000L,
        color = 0xFF9B5FF5,
        user = User(
            "Hans",
            ""
        ),
    ),
    Email(
        id = "5",
        subject = "Un nouveau cours Android avec IA",
        content = "Salut, comment ça va ? \n\nNous sommes là pour vous informer que le cours Android est en promotion. \nDépêchez-vous et réservez votre place dès maintenant ! \n\n",
        time = 1606863600000L,
        color = 0xFFF55F5F,
        user = User(
            "Élise",
            ""
        ),
    ),
    Email(
        id = "6",
        subject = "A New Android AI Course",
        content = "Hello! We are excited to inform you about our new Android AI course. This is a great opportunity to expand your knowledge.",
        time = 1617385200000L,
        color = 0xFFDAA844,
        user = User(
            "John",
            ""
        ),
    ),
    Email(
        id = "7",
        subject = "Νέο μάθημα Android με ΤΝ",
        content = "Γειά σας, πώς είστε; \n\nΘέλουμε να σας ενημερώσουμε ότι το μάθημα Android είναι σε προσφορά. \nΚλείστε τη θέση σας τώρα! \n\n",
        time = 1628238000000L,
        color = 0xFFDAA844,
        user = User(
            "Σοφία",
            ""
        ),
    ),
    Email(
        id = "8",
        subject = "Un nuevo curso de Android con IA",
        content = "¡Hola! ¿Cómo estás? \n\nEstamos aquí para informarte que el curso de Android tiene una promoción. \n¡Date prisa y asegura tu lugar ya! \n\n",
        time = 1608591600000L,
        color = 0xFF5FF5A3,
        user = User(
            "Carlos",
            ""
        ),
    ),
    Email(
        id = "9",
        subject = "新しいAndroid AIコース",
        content = "こんにちは！ 新しいAndroid AIコースについてお知らせできることを嬉しく思います。これは知識を拡大する絶好の機会です。",
        time = 1626366000000L,
        color = 0xFF5F96F5,
        user = User(
            "太郎",
            ""
        ),
    ),
    Email(
        id = "10",
        subject = "Новый курс по искусственному интеллекту",
        content = "Здравствуйте! Мы рады сообщить вам, что у нас есть новый курс по искусственному интеллекту. Это отличная возможность расширить ваши знания.",
        time = 1623078000000L,
        color = 0xFF9B5FF5,
        user = User(
            "Андрей",
            ""
        ),
    ),
    Email(
        id = "11",
        subject = "新的人工智能Android课程",
        content = "您好！我们很高兴地告诉您，我们有一个新的人工智能Android课程。这是扩展您知识的绝佳机会。",
        time = 1635242400000L,
        color = 0xFFF55F5F,
        user = User(
            "李明",
            ""
        ),
    ),
    Email(
        id = "12",
        subject = "새로운 AI 안드로이드 코스",
        content = "안녕하세요! 우리는 새로운 AI 안드로이드 코스를 소개하게 되어 기쁩니다. 여러분의 지식을 확장하는 최고의 기회입니다.",
        time = 1606928400000L,
        color = 0xFF5FD4F5,
        user = User(
            "김지영",
            ""
        ),
    ),
    Email(
        id = "13",
        subject = "Un nuevo curso de Android con IA",
        content = "¡Hola! ¿Cómo estás? \n\nEstamos aquí para informarte que el curso de Android tiene una promoción. \n¡Date prisa y asegura tu lugar ya! \n\n",
        time = 1616151600000L,
        color = 0xFF5FD4F5,
        user = User(
            "Carlos",
            ""
        ),
    ),
    Email(
        id = "14",
        subject = "دورة جديدة في الذكاء الاصطناعي لأنظمة Android",
        content = "مرحبًا! نحن متحمسون لإبلاغك عن دورتنا الجديدة في الذكاء الاصطناعي لأنظمة Android. هذه فرصة رائعة لتوسيع معرفتك.",
        time = 1643631600000L,
        color = 0xFFF55FEE,
        user = User(
            "عبد الله",
            ""
        ),
    ),
    Email(
        id = "15",
        subject = "新しいAndroid AIコース",
        content = "こんにちは！ 新しいAndroid AIコースについてお知らせできることを嬉しく思います。これは知識を拡大する絶好の機会です。",
        time = 1626366000000L,
        color = 0xFF5F96F5,
        user = User(
            "太郎",
            ""
        ),
    )
)





