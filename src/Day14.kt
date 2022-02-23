fun main() {
    // part 1
    val (polymerTemplate, insertionRules) = readInput("Day14_input").let { it.take(1).single() to it.drop(2) }
    val polymer = polymerTemplate.polymerize(insertionRules, 10)
    val (max, min) = polymer.groupingBy { it }.eachCount().values.let { it.maxOf { it } to it.minOf { it } }
    println(max - min)
}

private fun String.polymerize(rules: List<String>, steps: Int): String {
    var tempPolymer = this
    repeat(steps) {
        val newPolymer = StringBuilder((tempPolymer.length * 2) - 1)
        for (i in 0 until tempPolymer.lastIndex) {
            newPolymer.append(tempPolymer[i])
            val insertionChar = rules.first { rule -> rule.take(2) == "${tempPolymer[i]}${tempPolymer[i + 1]}" }.last()
            newPolymer.append(insertionChar)
            if (i == tempPolymer.lastIndex - 1) newPolymer.append(tempPolymer[tempPolymer.lastIndex])
        }
        tempPolymer = newPolymer.toString()
    }
    return tempPolymer
}