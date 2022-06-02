package com.tapblaze.pizzabus.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object JokerGirlSerializer : Serializer<JokerGirlPreferences> {
    override val defaultValue: JokerGirlPreferences
        get() = JokerGirlPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): JokerGirlPreferences {
        try {
            return JokerGirlPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto!", exception)
        }
    }

    override suspend fun writeTo(t: JokerGirlPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}