package tungp.android.bazarbooks.utils

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonParseException
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializationContext
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(formatter.format(src))
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        return try {
            LocalDateTime.parse(json?.asString, formatter)
        } catch (e: Exception) {
            throw JsonParseException("Error parsing LocalDateTime: " + e.message, e)
        }
    }
}