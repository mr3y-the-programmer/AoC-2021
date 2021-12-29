fun main() {

    // First part
    val incrementedMeasurements = readInputAsInts("Day01_input")
        .countIncrementedMeasurements()
    println(incrementedMeasurements)

    // Second part
    val incrementedSums = readInputAsInts("Day01_input")
        .windowed(3)
        .map { (a, b, c) -> a + b + c }
        .countIncrementedMeasurements()
    println(incrementedSums)
}

private fun List<Int>.countIncrementedMeasurements(): Int = windowed(2).count { (a, b) -> b > a }
