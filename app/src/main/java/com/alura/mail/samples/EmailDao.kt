package com.alura.mail.samples

import com.alura.mail.model.Email
import com.alura.mail.model.User


class EmailDao {
    fun getEmails(): List<Email> {
        return listOfEmails.sortedByDescending { it.time }
    }

    fun getEmailById(id: String): Email? {
        return listOfEmails.firstOrNull { it.id == id }
    }

    private val listOfEmails = listOf(
        Email(
            id = "1",
            subject = "Zulu Ukuhlola okukodwa okubili",
            content = "Lena i-imeyili yokuhlola ebhalwe ngesiZulu",
            time = 1697824497961,
            color = 0xFF000000,
            user = User("Não traduzivel de teste")
        ),
        Email(
            id = "2",
            subject = "Material Design 3 in Compose",
            content = "When you introduce Compose in an existing app, you need to migrate your themes in XML to use MaterialTheme for Compose screens. This means your app's theming will have two sources of truth: the View-based theme and the Compose theme. Any changes to your styling need to be made in multiple places. Once your app is fully migrated to Compose, you can remove your XML theming.",
            time = 1697584497961,
            color = 0xFF5F96F5,
            user = User("Bob Smith")
        ),
        Email(
            id = "2-b",
            subject = "Jetpack Compose is the future",
            content = "Jetpack Compose offers an implementation of Material Design 3, the next evolution of Material Design. Material 3 includes updated theming, components and Material You personalization features like dynamic color, and is designed to be cohesive with the new visual style and system UI on Android 12 and above.",
            time = 1697464497961,
            color = 0xFFDAA844,
            user = User("Richard Hendricks")
        ),
        Email(
            id = "3",
            subject = "Como migrar temas XML para o Compose",
            content = "Um app provavelmente tem uma grande quantidade de temas e estilos para visualizações. Ao introduzir o Compose em um app já existente, é necessário migrar os temas para usar o MaterialTheme em telas do Compose. Isso significa que os temas do seu app vão ter duas fontes da verdade: o tema baseado na visualização e o tema do Compose. Qualquer mudança no estilo precisa ser feita em vários lugares.",
            time = 1697344497961,
            color = 0xFFFE8966,
            user = User("Alice Silva")
        ),
        Email(
            id = "4-a",
            subject = "ಕನ್ನಡದ ಬಗ್ಗೆ ಇನ್ನಷ್ಟು ತಿಳಿಯಿರಿ",
            content = "ಕನ್ನಡವು ಭಾರತದಿಂದ ದಕ್ಷಿಣದ ದ್ರಾವಿಡ ಭಾಷೆಯಾಗಿದೆ, ಪ್ರಸ್ತುತ ಸಕ್ರಿಯ ಸ್ಥಾನಮಾನವನ್ನು ಹೊಂದಿದೆ, ಬೆಂಗಳೂರಿನಂತಹ ನಗರಗಳಲ್ಲಿ ದೈನಂದಿನ ಸಂವಹನಕ್ಕಾಗಿ ಬಳಸಲಾಗುವ ಮುಖ್ಯ ಭಾಷೆಯಾಗಿದೆ. 2011 ರಲ್ಲಿ ಪ್ರಪಂಚದಾದ್ಯಂತ 58 ಮಿಲಿಯನ್\u200Cಗಿಂತಲೂ ಹೆಚ್ಚು ಭಾಷೆಯನ್ನು ಮಾತನಾಡುವವರು ಇದ್ದರು, ಅವರಲ್ಲಿ ಸರಿಸುಮಾರು 43 ಮಿಲಿಯನ್ ಜನರು ಸ್ಥಳೀಯ ಭಾಷಿಕರು ಮತ್ತು 15 ಮಿಲಿಯನ್ ಜನರು ಎರಡನೇ ಅಥವಾ ಮೂರನೇ ಭಾಷೆಯಾಗಿದ್ದಾರೆ.",
            time = 1697224497961,
            color = 0xFF5F96F5,
            user = User("Jane Doe")
        ),
        Email(
            id = "4",
            subject = "Présentation de Kotlin",
            content = "Grâce à l'interopérabilité de Kotlin avec Java, vous n'avez pas besoin d'adopter Kotlin du jour au lendemain. Vous pouvez avoir des projets contenant du code Kotlin et du code Java. Pour en savoir plus sur l'ajout de Kotlin à une application existante, consultez la page Ajouter Kotlin à une application existante. Si vous faites partie d'une équipe plus grande, la taille de votre organisation et de votre codebase peut nécessiter une attention particulière. Pour obtenir des conseils et d'autres informations, consultez la page Adopter Kotlin pour les grandes équipes.",
            time = 1697104497961,
            color = 0xFFF55F5F,
            user = User("Pepe Le Pew")
        ),
        Email(
            id = "5",
            subject = "Migrer des thèmes XML vers Compose",
            content = "Pour migrer votre application vers Compose, vous devez créer une version Compose de votre thème existant. Cependant, plus tôt vous créez cette version lors du processus de migration, plus vous devez gérer les thèmes XML et Compose, ce qui peut ralentir vos efforts.",
            time = 1696984497961,
            color = 0xFF5F66F5,
            user = User("Louis Vuitton")
        ),
        Email(
            id = "6",
            subject = "Урок Android с ИИ",
            content = "Привет, как дела? Мы здесь, чтобы сообщить вам, что урок Android по специальной цене. Поторопитесь и забронируйте свое место! ",
            time = 1696864497961,
            color = 0xFFF55FEE,
            user = User("Иван")
        ),
        Email(
            id = "7",
            subject = "Ein neuer Android-Kurs mit KI",
            content = "Hallo, wie geht es dir? Wir möchten dich darüber informieren, dass der Android-Kurs im Angebot ist. Schnapp dir deinen Platz jetzt! ",
            time = 1696744497961,
            color = 0xFF9B5FF5,
            user = User("Hans")
        ),
        Email(
            id = "8",
            subject = "Un nouveau cours Android avec IA",
            content = "Salut, comment ça va ? Nous sommes là pour vous informer que le cours Android est en promotion. Dépêchez-vous et réservez votre place dès maintenant ! ",
            time = 1696624497961,
            color = 0xFFF55F5F,
            user = User("Élise")
        ),
        Email(
            id = "9",
            subject = "A New Android AI Course",
            content = "Hello! We are excited to inform you about our new Android AI course. This is a great opportunity to expand your knowledge.",
            time = 1696504497961,
            color = 0xFFDAA844,
            user = User("John")
        ),

        Email(
            id = "10",
            subject = "Новый курс по искусственному интеллекту",
            content = "Здравствуйте! Мы рады сообщить вам, что у нас есть новый курс по искусственному интеллекту. Это отличная возможность расширить ваши знания.",
            time = 1696384497961,
            color = 0xFF9B5FF5,
            user = User("Андрей")
        ),
        Email(
            id = "11",
            subject = "新的人工智能Android课程",
            content = "您好！我们很高兴地告诉您，我们有一个新的人工智能Android课程。这是扩展您知识的绝佳机会。",
            time = 1696504497961,
            color = 0xFFF55F5F,
            user = User("李明")
        ),
        Email(
            id = "12",
            subject = "새로운 AI 안드로이드 코스",
            content = "안녕하세요! 우리는 새로운 AI 안드로이드 코스를 소개하게 되어 기쁩니다. 여러분의 지식을 확장하는 최고의 기회입니다.",
            time = 1696384497961,
            color = 0xFF5FD4F5,
            user = User("김지영")
        ),
        Email(
            id = "13",
            subject = "Un nuevo curso de Android con IA",
            content = "¡Hola! ¿Cómo estás? Estamos aquí para informarte que el curso de Android tiene una promoción. ¡Date prisa y asegura tu lugar ya! ",
            time = 1696264497961,
            color = 0xFF5FD4F5,
            user = User("Carlos")
        ),
        Email(
            id = "14",
            subject = "دورة جديدة في الذكاء الاصطناعي لأنظمة Android",
            content = "مرحبًا! نحن متحمسون لإبلاغك عن دورتنا الجديدة في الذكاء الاصطناعي لأنظمة Android. هذه فرصة رائعة لتوسيع معرفتك.",
            time = 1696144497961,
            color = 0xFFF55FEE,
            user = User("عبد الله")
        ),
        Email(
            id = "15",
            subject = "新しいAndroid AIコース",
            content = "こんにちは！ 新しいAndroid AIコースについてお知らせできることを嬉しく思います。これは知識を拡大する絶好の機会です。",
            time = 1696024497961,
            color = 0xFF5F96F5,
            user = User("太郎")
        ),
        Email(
            id = "16",
            subject = "Kotlin 개요",
            content = "Kotlin은 객체 지향 프로그래밍과 함수 프로그래밍을 모두 지원하는 오픈소스 정적 형식 지정 프로그래밍 언어입니다. Kotlin의 문법과 개념은 C#, 자바, Scala 등 다른 언어와 유사합니다. Kotlin은 수십 년에 걸쳐 개발되었으며 고유한 언어가 되는 것을 원치 않습니다. Kotlin에는 JVM(Kotlin/JVM), 자바스크립트(Kotlin/JS), 네이티브 코드(Kotlin/Native)를 타겟팅하는 변형이 있습니다.",
            time = 1695904497961,
            color = 0xFF9B5FF5,
            user = User("Han Tae-sul")
        ),
        Email(
            id = "17",
            subject = "Kotlin 概览",
            content = "Kotlin 是一种静态类型的开源编程语言，它既支持面向对象的编程，又支持函数式编程。Kotlin 提供的语法和概念与其他语言（包括 C#、Java 和 Scala 等等）类似。Kotlin 的目标并不是独树一帜，而是从几十年的语言发展中汲取灵感。它以变体的形式存在，这些变体以 JVM (Kotlin/JVM)、JavaScript (Kotlin/JS) 和原生代码 (Kotlin/Native) 为目标。",
            time = 1695784497961,
            color = 0xFFFE8966,
            user = User("Wang Yi")
        ),
        Email(
            id = "18",
            subject = "نظرة عامة على لغة Kotlin",
            content = "لغة Kotlin هي لغة برمجة مفتوحة المصدر ومكتوبة بشكل ثابت وتدعم كلاً من البرمجة الموجهة بالكائنات والوظائف. يقدم Kotlin بنية ومفاهيم مماثلة من لغات أخرى، بما في ذلك C# وجافا وScala وغير ذلك الكثير. ولا تهدف لغة Kotlin إلى أن تكون فريدة، بل إنها تستمد الإلهام من عقود من تطوير اللغة. تتوفّر هذه السمة في صيغ تستهدف JVM (Kotlin/JVM) وJavaScript (Kotlin/JS) وشفرة أصلية (Kotlin/Native).",
            time = 1695664497961,
            color = 0xFF5F96F5,
            user = User("Jamal Al-Fayyad")
        ),
        Email(
            id = "19",
            subject = "Kotlin'e genel bakış",
            content = "Kotlin, hem nesne odaklı hem de işlevsel programlamayı destekleyen, statik olarak yazılmış açık kaynaklı bir programlama dilidir. Kotlin; C#, Java ve Scala gibi pek çok dilden benzer söz dizimi ve kavramları sunar. Kotlin, benzersiz olmayı amaçlamaz. Onlarca yıllık dil geliştirme çalışmasından ilham alır. JVM (Kotlin/JVM), JavaScript (Kotlin/JS) ve yerel kodu (Kotlin/Native) hedefleyen varyantlarda mevcuttur.",
            time = 1695544497961,
            color = 0xFF5FF5A3,
            user = User("Zeynep Yılmaz")
        ),
        Email(
            id = "20",
            subject = "Tổng quan về Kotlin",
            content = "Kotlin là một ngôn ngữ lập trình nguồn mở, kiểu tĩnh, hỗ trợ cả lập trình chức năng lẫn hướng đối tượng. Kotlin cung cấp cú pháp và khái niệm tương tự trong các ngôn ngữ khác, bao gồm cả C#, Java và Scala cùng nhiều ngôn ngữ khác. Kotlin không phải là độc nhất – mà Kotlin lấy cảm hứng từ nhiều thập kỷ để phát triển ngôn ngữ. Mã này tồn tại trong các biến thể nhắm đến JVM (Kotlin/VM), JavaScript (Kotlin/JS) và mã gốc (Kotlin/mã gốc).",
            time = 1695424497961,
            color = 0xFF9B5FF5,
            user = User("Luan Nguyen")
        ),

        Email(
            id = "21",
            subject = "Panoramica di Kotlin",
            content = "Kotlin è un linguaggio di programmazione open source e di tipo statico che supporta la programmazione funzionale e orientata agli oggetti. Kotlin fornisce sintetizzazione e concetti simili di altri linguaggi, tra cui C#, Java e Scala, tra molti altri. L'obiettivo di Kotlin non è unico, ma si ispira da decenni di sviluppo del linguaggio. Esistono in diverse varianti che hanno come target JVM (Kotlin/JVM), JavaScript (Kotlin/JS) e codice nativo (Kotlin/Native).",
            time = 1695304497961,
            color = 0xFFF55F5F,
            user = User("Giovanni Solano")
        ),

        Email(
            id = "22",
            subject = "Descripción general de Kotlin",
            content = "Kotlin es un lenguaje de programación estático de código abierto que admite la programación funcional y orientada a objetos. Proporciona una sintaxis y conceptos similares a los de otros lenguajes, como C#, Java y Scala, entre muchos otros. No pretende ser único, sino que se inspira en décadas de desarrollo del lenguaje. Cuenta con variantes que se orientan a la JVM (Kotlin/JVM), JavaScript (Kotlin/JS) y el código nativo (Kotlin/Native).",
            time = 1695184497961,
            color = 0xFF5F96F5,
            user = User("Messi Lionel")
        ),
        Email(
            id = "23",
            subject = "XML temalarını Oluştur'a taşı",
            content = "Mevcut bir uygulamada Compose'u kullanıma sunduğunuzda, Oluştur ekranlarında MaterialTheme kullanmak için temalarınızı XML olarak taşımanız gerekir. Yani, uygulama temanız iki doğruluk kaynağına sahip olur: Görüntülemeye dayalı tema ve Oluştur teması. Stilinizde yaptığınız değişikliklerin birden çok yerde yapılması gerekir. Uygulamanız Compose'a tamamen taşındıktan sonra XML temanızı kaldırabilirsiniz.",
            time = 1626366000000L,
            color = 0xFFDAA844,
            user = User("Froid Curie")
        ),
        Email(
            id = "24",
            subject = "Νέο μάθημα Android με ΤΝ",
            content = "Γειά σας, πώς είστε; Θέλουμε να σας ενημερώσουμε ότι το μάθημα Android είναι σε προσφορά. Κλείστε τη θέση σας τώρα! ",
            time = 1694944497961,
            color = 0xFFDAA844,
            user = User("Σοφία")
        ),
        Email(
            id = "25",
            subject = "Un nuevo curso de Android con IA",
            content = "¡Hola! ¿Cómo estás? Estamos aquí para informarte que el curso de Android tiene una promoción. ¡Date prisa y asegura tu lugar ya! ",
            time = 1694824497961,
            color = 0xFF5FF5A3,
            user = User("Carlos")
        ),
        Email(
            id = "26",
            subject = "新しいAndroid AIコース",
            content = "こんにちは！ 新しいAndroid AIコースについてお知らせできることを嬉しく思います。これは知識を拡大する絶好の機会です。",
            time = 1694704497961,
            color = 0xFF5F96F5,
            user = User("太郎")
        )
    )
}