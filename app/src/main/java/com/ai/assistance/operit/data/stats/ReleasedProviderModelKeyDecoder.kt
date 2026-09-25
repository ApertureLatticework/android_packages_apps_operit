package com.ai.assistance.operit.data.stats

import com.ai.assistance.operit.data.model.ApiProviderType

internal data class ReleasedProviderModelKey(
    val storedProviderModel: String,
    val provider: String,
    val model: String,
)

/** Decodes released `provider:model` keys stored as `provider_model`. */
internal object ReleasedProviderModelKeyDecoder {
    private val builtInProviderAliases = ApiProviderType.entries.associate { it.name to it.name }

    fun decode(
        encoded: String,
        additionalProviderAliases: Map<String, String> = emptyMap(),
    ): ReleasedProviderModelKey {
        val aliases = buildMap {
            putAll(builtInProviderAliases)
            additionalProviderAliases.forEach { (rawAlias, rawIdentity) ->
                val alias = rawAlias.trim()
                val identity = rawIdentity.trim()
                require(alias.isNotEmpty() && identity.isNotEmpty())
                val previous = put(alias, identity)
                require(previous == null || previous == identity)
            }
        }
        val known = aliases.keys.sortedByDescending(String::length)
            .firstOrNull { encoded == it || encoded.startsWith("${it}_") }
        val separator = known?.length ?: encoded.indexOf('_')
        require(separator > 0 && separator < encoded.lastIndex) {
            "released token key does not contain a provider and model: $encoded"
        }
        val providerAlias = encoded.substring(0, separator)
        val model = encoded.substring(separator + 1)
        return ReleasedProviderModelKey(
            storedProviderModel = "$providerAlias:$model",
            provider = if (known == null) providerAlias else aliases.getValue(providerAlias),
            model = model,
        )
    }

    /** Pre-provider format counted feature usage under bare function keys; they carry no
     * provider/model identity and must not decode as `provider_model`. */
    private val preProviderFunctionKeys = setOf("CHAT", "FILE_BINDING")

    /** Used by migration code for released keys from before provider/model identities existed. */
    fun decodeOrNull(
        encoded: String,
        additionalProviderAliases: Map<String, String> = emptyMap(),
    ): ReleasedProviderModelKey? =
        if (encoded in preProviderFunctionKeys) {
            null
        } else {
            try {
                decode(encoded, additionalProviderAliases)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
}
