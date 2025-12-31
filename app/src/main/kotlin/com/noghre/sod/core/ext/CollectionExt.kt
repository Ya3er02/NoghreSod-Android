package com.noghre.sod.core.ext

/**
 * Extension functions for collections.
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */

/**
 * Safely get an item at index or return default value.
 */
fun <T> List<T>.getOrNull(index: Int, default: T): T {
    return if (index in indices) this[index] else default
}

/**
 * Check if list is not empty and not null.
 */
fun <T> List<T>?.isNotEmptyOrNull(): Boolean = !this.isNullOrEmpty()

/**
 * Partition list by predicate returning Pair<matching, notMatching>.
 */
fun <T> List<T>.partitionBy(predicate: (T) -> Boolean): Pair<List<T>, List<T>> {
    return partition(predicate)
}

/**
 * Find first index matching predicate or -1.
 */
fun <T> List<T>.findIndexOrNull(predicate: (T) -> Boolean): Int? {
    return indexOfFirst(predicate).takeIf { it != -1 }
}

/**
 * Group by key and return map of lists.
 */
fun <T, K> List<T>.groupByKey(keySelector: (T) -> K): Map<K, List<T>> {
    return groupBy(keySelector)
}

/**
 * Chunk list into sublists of size n.
 */
fun <T> List<T>.chunked(size: Int): List<List<T>> {
    require(size > 0) { "Size must be > 0" }
    return if (isEmpty()) emptyList() else windowed(size, size, partialWindows = true)
}

/**
 * Flatten nested lists.
 */
fun <T> List<List<T>>.flatten(): List<T> {
    return flatMap { it }
}

/**
 * Transform and filter in one pass.
 */
fun <T, R> List<T>.mapNotNull(transform: (T) -> R?): List<R> {
    return mapNotNull(transform)
}

/**
 * Get distinct items by key.
 */
fun <T, K> List<T>.distinctBy(selector: (T) -> K): List<T> {
    return distinctBy(selector)
}

/**
 * Get first n items.
 */
fun <T> List<T>.firstN(n: Int): List<T> {
    return take(n)
}

/**
 * Get last n items.
 */
fun <T> List<T>.lastN(n: Int): List<T> {
    return takeLast(n)
}

/**
 * Check if all items are unique.
 */
fun <T> List<T>.isUnique(): Boolean {
    return size == toSet().size
}

/**
 * Swap elements at two indices.
 */
fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}

/**
 * Rotate list left by n positions.
 */
fun <T> List<T>.rotateLeft(n: Int): List<T> {
    if (isEmpty() || n == 0) return this
    val rotation = n % size
    return drop(rotation) + take(rotation)
}

/**
 * Rotate list right by n positions.
 */
fun <T> List<T>.rotateRight(n: Int): List<T> {
    if (isEmpty() || n == 0) return this
    val rotation = n % size
    return takeLast(rotation) + dropLast(rotation)
}

/**
 * Get random item from list.
 */
fun <T> List<T>.random(): T? = if (isNotEmpty()) get((Math.random() * size).toInt()) else null

/**
 * Shuffle list immutably.
 */
fun <T> List<T>.shuffled(): List<T> = toMutableList().apply { shuffle() }

/**
 * Map extension for null-safe value getting.
 */
fun <K, V> Map<K, V>?.getValueOrNull(key: K): V? {
    return this?.get(key)
}

/**
 * Create immutable map from varargs.
 */
fun <K, V> mapOf(vararg pairs: Pair<K, V>): Map<K, V> {
    return mapOf(*pairs)
}
