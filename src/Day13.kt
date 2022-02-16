import kotlin.math.max

fun main() {
    val (dots, instructions) = readInput("Day13_input").splitOnBlankLine()
    val paper = createPaperFilledWithDots(dots)
    val foldedPaper = paper.foldBasedOnInstructions(instructions)
    foldedPaper.forEach { println(it.toList()) }
}

private fun Array<Array<Char>>.foldBasedOnInstructions(instructions: List<String>): Array<Array<Char>> {
    var tempPaper = this
    instructions.forEach { instruction ->
        val foldLine = instruction.substringAfter('=').toInt()
        val isVerticalFold = instruction.substringBefore('=').last() == 'y'
        if (isVerticalFold) {
            var (upsideRow, downsideRow) = foldLine - 1 to foldLine + 1
            while (upsideRow >= 0 && downsideRow <= tempPaper.lastIndex) {
                tempPaper[downsideRow].forEachIndexed { index, dot ->
                    tempPaper[upsideRow][index] = if (dot != '.') dot else tempPaper[upsideRow][index]
                }
                upsideRow--
                downsideRow++
            }
            val upsidePart = tempPaper.take(foldLine)
            println(upsidePart.sumOf { row -> row.count { it == '#' } })
            tempPaper = upsidePart.toTypedArray()
        } else {
            var (leftSideColumn, rightSideColumn) = foldLine - 1 to foldLine + 1
            while (leftSideColumn >= 0 && rightSideColumn <= tempPaper[0].lastIndex) {
                (0..tempPaper.lastIndex).forEach {
                    val row = tempPaper[it]
                    row[leftSideColumn] = if (row[rightSideColumn] != '.') row[rightSideColumn] else row[leftSideColumn]
                }
                leftSideColumn--
                rightSideColumn++
            }
            val leftSidePart = tempPaper.map { it.take(foldLine) }
            println(leftSidePart.sumOf { row -> row.count { it == '#' } })
            tempPaper = leftSidePart.map { it.toTypedArray() }.toTypedArray()
        }
    }
    return tempPaper
}

private fun createPaperFilledWithDots(dots: List<String>): Array<Array<Char>> {
    var bottomMostPosition = -1
    var rightMostPosition = -1
    dots.forEach { dot ->
        val (x, y) = dot.split(',').map { it.toInt() }
        bottomMostPosition = max(bottomMostPosition, y)
        rightMostPosition = max(rightMostPosition, x)
    }
    val paper = Array(bottomMostPosition + 1) { Array(rightMostPosition + 1) { '.' } }
    dots.forEach { dot ->
        val (column, row) = dot.split(',').map { it.toInt() }
        paper[row][column] = '#'
    }
    return paper
}

private fun List<String>.splitOnBlankLine(): Pair<List<String>, List<String>> {
    val first = takeWhile { it.isNotBlank() || it.isNotEmpty() }
    return Pair(first, drop(first.size + 1))
}