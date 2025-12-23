package com.shortspark.emaliestates.util.helpers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

object MessageSerializer : KSerializer<List<String>> {
    private val listSerializer = ListSerializer(String.serializer())

    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: List<String>) {
        listSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): List<String> {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This serializer can only be used with JSON.")
        val jsonElement = jsonDecoder.decodeJsonElement()

        return when (jsonElement) {
            // If the JSON field is a single string, wrap it in a list
            is JsonPrimitive -> listOf(jsonElement.content)
            // If it's an array, decode it as a list of strings
            is JsonArray -> jsonDecoder.json.decodeFromJsonElement(listSerializer, jsonElement)
            else -> throw IllegalStateException("Unexpected JSON element type for message field.")
        }
    }
}