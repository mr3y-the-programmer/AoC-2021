import java.util.*

fun main() {
    val allLines = readInput("Day10_input")
    // part 1
    val corruptedLines = allLines.filter { line -> line.syntaxErrorScore() != null }
    println(corruptedLines.sumOf { it.syntaxErrorScore()!! })
    // part 2
    val incompleteLinesScores = (allLines - corruptedLines.toSet())
        .map { line -> line.autoCompletionScore() }
        .sorted()
    println(incompleteLinesScores[incompleteLinesScores.size / 2])
}

private fun String.autoCompletionScore(): Long {
    var score = 0L
    val validPairs = hashMapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
    val missingSymbolScore = hashMapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)
    val syntaxStack = Stack<Char>()
    // filter the complete pairs
    forEach { symbol -> if (validPairs.containsKey(symbol)) syntaxStack.push(symbol) else syntaxStack.pop() }
    // compute score for missing pairs
    for (i in syntaxStack.lastIndex downTo 0) {
        val openingChar = syntaxStack[i]
        score *= 5
        score += missingSymbolScore[validPairs[openingChar]]!!
    }
    return score
}

// Calculate syntax error score for each corrupted line or return null if the line isn't corrupted
private fun String.syntaxErrorScore(): Int? {
    val corruptedSymbolScore = hashMapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
    val validPairs = hashMapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
    val syntaxStack = Stack<Char>()
    forEach { symbol ->
        if (validPairs.containsKey(symbol)) {
            syntaxStack.push(symbol)
        } else {
            val openingChar = syntaxStack.pop()
            if (validPairs[openingChar] != symbol) return corruptedSymbolScore[symbol]!!
        }
    }
    return null
}
