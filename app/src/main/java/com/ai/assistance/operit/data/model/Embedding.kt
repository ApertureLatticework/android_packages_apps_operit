package com.ai.assistance.operit.data.model

import java.util.Arrays

/**
 * A wrapper class for a FloatArray to be stored as a BLOB column in the memory database.
 */
data class Embedding(val vector: FloatArray) {
    // Custom equals/hashCode to properly compare float arrays
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Embedding
        return vector.contentEquals(other.vector)
    }

    override fun hashCode(): Int {
        return vector.contentHashCode()
    }
} 