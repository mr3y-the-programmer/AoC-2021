import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    // part 1
    val horizontalPositions = readInput("Day07_input")
        .joinToString("")
        .trim()
        .split(",")
        .map { it.toInt() }

    val sorted = horizontalPositions.sorted()
    val medianPosition = horizontalPositions.size / 2
    println(horizontalPositions.sumOf { abs(sorted[medianPosition] - it) })

    // part 2
    val mean = horizontalPositions.sum().toDouble() / horizontalPositions.size
    val meanFloor = floor(mean)
    val meanCeil = ceil(mean)
    val roundedLow = horizontalPositions.solve(minOf(meanFloor, meanCeil))
    val roundedHigh = horizontalPositions.solve(maxOf(meanFloor, meanCeil))
    println(minOf(roundedLow, roundedHigh).toInt())
}

fun Double.gauss() = this * (this + 1) / 2

fun List<Int>.solve(roundedMean: Double) = this.sumOf { abs(roundedMean - it).gauss() }