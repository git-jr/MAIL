package com.alura.mail.mlkit

import android.util.Log
import com.alura.mail.R
import com.alura.mail.ui.contentEmail.Suggestion
import com.alura.mail.ui.contentEmail.SuggestionAction
import com.google.mlkit.nl.entityextraction.DateTimeEntity
import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityAnnotation
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import com.google.mlkit.nl.entityextraction.FlightNumberEntity
import com.google.mlkit.nl.entityextraction.MoneyEntity

class EntityExtractor {

    fun extractSuggestion(
        text: String,
        onSuccess: (List<EntityInfo>) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val entityExtractor =
            EntityExtraction.getClient(
                EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH).build()
            )
        val params = EntityExtractionParams.Builder(text).build()

        entityExtractor
            .downloadModelIfNeeded()
            .addOnSuccessListener { _ ->
                /* Model downloading succeeded, you can call extraction API here. */
                entityExtractor
                    .annotate(params)
                    .addOnSuccessListener { entityAnnotations: MutableList<EntityAnnotation> ->

                        val entityInfoList = mutableListOf<EntityInfo>()
                        // Annotation process was successful, you can parse the EntityAnnotations list here.
                        for (entityAnnotation in entityAnnotations) {
                            val entityText = text.substring(
                                entityAnnotation.start,
                                entityAnnotation.end
                            )

                            entityInfoList.add(
                                EntityInfo(
                                    entityText = entityText,
                                    action = getSuggestionActionByEntity(entityAnnotation.entities.first().type)
                                )
                            )
                        }
                        onSuccess(entityInfoList)
                    }
                    .addOnFailureListener {
                        Log.e("entityExtractor", "Falha na indetificação $it")
                    }
            }
            .addOnFailureListener { _ -> /* Model downloading failed. */
                Log.e("entityExtractor", "Falha no download do modelo")
            }

    }

    fun gentRanges(
        text: String,
        onSuccess: (List<IntRange>) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val entityExtractor =
            EntityExtraction.getClient(
                EntityExtractorOptions.Builder(EntityExtractorOptions.PORTUGUESE).build()
            )
        val params = EntityExtractionParams.Builder(text).build()

        entityExtractor
            .downloadModelIfNeeded()
            .addOnSuccessListener { _ ->
                /* Model downloading succeeded, you can call extraction API here. */
                entityExtractor
                    .annotate(params)
                    .addOnSuccessListener { entityAnnotations: MutableList<EntityAnnotation> ->

                        onSuccess(entityAnnotations.map { it.start..it.end })
                        // Annotation process was successful, you can parse the EntityAnnotations list here.
                    }
                    .addOnFailureListener {
                        Log.e("entityExtractor", "Falha na indetificação $it")
                    }
            }
            .addOnFailureListener { _ -> /* Model downloading failed. */
                Log.e("entityExtractor", "Falha no download do modelo")
            }

    }

    fun entityToSuggestionAction(entities: List<EntityInfo>): List<Suggestion> {
        return entities.map { entity ->
            val suggestion = Suggestion(text = entity.entityText, action = entity.action)
            when (entity.action) {
                SuggestionAction.DATE_TIME -> suggestion.copy(icon = R.drawable.ic_date_time)
                SuggestionAction.PHONE_NUMBER -> suggestion.copy(icon = R.drawable.ic_call)
                SuggestionAction.ADDRESS -> suggestion.copy(icon = R.drawable.ic_location)
                SuggestionAction.EMAIL -> suggestion.copy(icon = R.drawable.ic_email)
                SuggestionAction.URL -> suggestion.copy(icon = R.drawable.ic_link)

                else -> suggestion.copy(icon = R.drawable.ic_copy)
            }
        }
    }


    private fun getSuggestionActionByEntity(entityType: Int): SuggestionAction {
        return when (entityType) {
            1 -> SuggestionAction.ADDRESS
            2 -> SuggestionAction.DATE_TIME
            3 -> SuggestionAction.EMAIL
            4 -> SuggestionAction.FLIGHT_NUMBER
            5 -> SuggestionAction.IBAN
            6 -> SuggestionAction.ISBN
            7 -> SuggestionAction.PAYMENT_CARD_NUMBER
            8 -> SuggestionAction.PHONE_NUMBER
            9 -> SuggestionAction.TRACKING_NUMBER
            10 -> SuggestionAction.URL
            11 -> SuggestionAction.MONEY
            else -> SuggestionAction.SMART_REPLY
        }
    }

    fun extract(
        text: String,
        onSuccess: (List<Entity>) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val entityExtractor =
            EntityExtraction.getClient(
                EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH).build()
            )
        val params = EntityExtractionParams.Builder(text).build()

        entityExtractor
            .downloadModelIfNeeded()
            .addOnSuccessListener { _ ->
                /* Model downloading succeeded, you can call extraction API here. */
                entityExtractor
                    .annotate(params)
                    .addOnSuccessListener { entityAnnotations: MutableList<EntityAnnotation> ->
                        onSuccess(entityAnnotations.flatMap { it.entities })
                        // Annotation process was successful, you can parse the EntityAnnotations list here.
                        for (entityAnnotation in entityAnnotations) {
                            val entities: List<Entity> = entityAnnotation.entities

                            val entityText = text.substring(
                                entityAnnotation.start,
                                entityAnnotation.end
                            )

                            for (entity in entities) {
                                Log.d(
                                    "entityExtractor",
                                    "Nome entidade ${entityType[entity.type]} - ${entity.type}"
                                )
                                when (entity) {
                                    is DateTimeEntity -> {
                                        Log.d(
                                            "entityExtractor",
                                            "Data: Granularidade: ${entity.dateTimeGranularity} TimeStamp: ${entity.timestampMillis}"
                                        )
                                    }

                                    is MoneyEntity -> {
                                        Log.d(
                                            "entityExtractor",
                                            "Dinheiros: Tipo moeda: ${entity.unnormalizedCurrency} Parte inteira: ${entity.integerPart} Fração: ${entity.fractionalPart}"
                                        )
                                    }

                                    is FlightNumberEntity -> {
                                        Log.d(
                                            "entityExtractor",
                                            "Airline Code: ${entity.airlineCode}"
                                        )
                                        Log.d(
                                            "entityExtractor",
                                            "Flight number: ${entity.flightNumber}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.e("entityExtractor", "Falha na indetificação $it")
                    }
            }
            .addOnFailureListener { _ -> /* Model downloading failed. */
                Log.e("entityExtractor", "Falha no download do modelo")
            }

    }

}


val entityType = mapOf(
    1 to "Endereço",
    2 to "Data e Hora",
    3 to "E-mail",
    4 to "Número de Voo",
    5 to "IBAN",
    6 to "ISBN",
    7 to "Cartão de Pagamento",
    8 to "Telefone",
    9 to "Número de Rastreamento",
    10 to "URL",
    11 to "Dinheiro"
)


data class EntityInfo(
    val entityText: String,
    val action: SuggestionAction = SuggestionAction.SMART_REPLY,
)