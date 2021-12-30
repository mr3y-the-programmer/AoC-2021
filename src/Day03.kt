fun main() {
    // First part
    val report = readInput("Day03_input")
    val columns = report[0].indices
    val gammaRate = buildString {
        for (column in columns) {
            val (zeroes, ones) = report.countBitsInColumn(column)
            val commonBit = if (zeroes > ones) "0" else "1"
            append(commonBit)
        }
    }
    val epsilonRate = gammaRate.invertBits()
    println("Power consumption: " + gammaRate.toInt(2) * epsilonRate.toInt(2))

    // second part
    println("Life Support rating: " + report.rating(RatingType.OXYGEN) * report.rating(RatingType.CO2))
}

fun List<String>.rating(type: RatingType): Int {
    var column = 0
    var candidates = this
    while (true) {
        val (zeroes, ones) = candidates.countBitsInColumn(column)
        candidates = when {
            zeroes > ones -> candidates.filter { if (type == RatingType.OXYGEN) it[column] == '0' else it[column] == '1' }
            ones > zeroes -> candidates.filter { if (type == RatingType.OXYGEN) it[column] == '1' else it[column] == '0' }
            else -> candidates.filter { if (type == RatingType.OXYGEN) it[column] == '1' else it[column] == '0' }
        }
        if (candidates.size == 1) break
        column++
    }
    return candidates.single().toInt(2)
}

enum class RatingType { OXYGEN, CO2 }

fun List<String>.countBitsInColumn(column: Int): BitCount {
    var zeroes = 0
    var ones = 0
    for (line in this) {
        if (line[column] == '0') zeroes++ else ones++
    }
    return BitCount(zeroes, ones)
}

fun String.invertBits() = this.asIterable().joinToString("") { if (it == '0') "1" else "0" }

data class BitCount(val zeroes: Int, val ones: Int)