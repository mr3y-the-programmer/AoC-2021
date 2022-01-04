fun main() {
    // part 1
    val predictableDigitsInstances = readInput("Day08_input")
        .map { it.substringAfter("|").trim() }
        .sumOf { it.split(" ").count { digit -> digit.isPredictableDigit() } }
    println(predictableDigitsInstances)
    // part 2
    val outputSum = readInput("Day08_input")
        .sumOf {
            val decoder = it.substringBefore("|").trim().toDecoder()
            it.substringAfter("|")
                .trim()
                .split(" ")
                .joinToString("") { encodedDigit -> decoder.decodeDecimalDigitFor(encodedDigit).toString() }
                .toInt()
        }
    println(outputSum)
}

private fun String.toDecoder(): Decoder {
    val allEncodedDigits = split(" ")
    val internalMapping = hashMapOf<String, Int>()
    allEncodedDigits
        .filter { it.isPredictableDigit() }
        .forEach { encoded ->
            when (encoded.length) {
                2 -> internalMapping[encoded] = 1
                3 -> internalMapping[encoded] = 7
                4 -> internalMapping[encoded] = 4
                7 -> internalMapping[encoded] = 8
            }
        }
    // now we can predict the other digits
    allEncodedDigits
        .filterNot { it.isPredictableDigit() }
        .forEach { encoded ->
            val one = internalMapping.entries.single { it.value == 1 }.key
            val four = internalMapping.entries.single { it.value == 4 }.key
            if (encoded.length == 5) { // 2, 3, 5
                when {
                    one.asIterable().subtract(encoded.asIterable().toSet()).isEmpty() -> internalMapping[encoded] = 3
                    four.asIterable().subtract(encoded.asIterable().toSet()).size == 1 -> internalMapping[encoded] = 5
                    else -> internalMapping[encoded] = 2
                }
            } else {
                when {
                    four.asIterable().subtract(encoded.asIterable().toSet()).isEmpty() -> internalMapping[encoded] = 9
                    one.asIterable().subtract(encoded.asIterable().toSet()).isEmpty() -> internalMapping[encoded] = 0
                    else -> internalMapping[encoded] = 6
                }
            }
        }
    return Decoder(internalMapping)
}

class Decoder(private val mapping: Map<String, Int>) {

    fun decodeDecimalDigitFor(encoded: String): Int {
        return mapping.filter { (encodedKey, _) ->
            encodedKey.asIterable().subtract(encoded.asIterable().toSet()).isEmpty()
                    && encoded.asIterable().subtract(encodedKey.asIterable().toSet()).isEmpty()
        }.values.single()
    }
}

// we can only predict 1, 4, 7, 8 using the length of the digit
private fun String.isPredictableDigit() = length == 2 || length == 3 || length == 4 || length == 7
