package com.ara.memo.jackson.serialization

import com.ara.memo.jackson.model.Field
import com.ara.memo.jackson.model.Fields
import com.ara.memo.jackson.model.JsonProjection
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.stereotype.Component

@Component
class JsonProjectionSerializer(
    private val objectMapper: ObjectMapper
) : JsonSerializer<JsonProjection<*>>() {
    override fun serialize(value: JsonProjection<*>, gen: JsonGenerator, serializers: SerializerProvider) {
        val jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(value.view))
        removeFields(jsonNode, value.fields)
        gen.writeObject(jsonNode)
    }

    private fun removeFields(node: JsonNode, fields: Fields) {
        if (fields.isEmpty()) return

        if (node is ObjectNode) {
            val deleteFields = mutableSetOf<String>()
            val remainFields = mutableSetOf<Field>()
            node.fieldNames().forEach {
                val localFields = fields[it]
                if (localFields == null) deleteFields.add(it)
                else remainFields.add(localFields)
            }
            deleteFields.forEach { node.remove(it) }
            remainFields.forEach { removeFields(node[it.name], it.children) }
        } else {
            node.forEach { removeFields(it, fields) }
        }
    }
}