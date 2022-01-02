fun main() {
    val (input, boards) = readInput("Day04_input").let {
        it.take(2).trim().single().split(",") to it.drop(2).trim().chunked(5).map { it.toBoard() }
    }
    var first: Int? = null
    var markedBoards = boards
    for (i in input) {
        val number = i.toInt()
        markedBoards = markedBoards.markNumber(number)
        for (board in markedBoards) {
            if (board.hasAnyCompleteRowsOrColumns()) {
                val unMarkedSum = board.getSumOfUnmarkedNumbers()
                first = unMarkedSum * number
                break
            }
        }
        if (first != null) break
    }
    println(first)
}

private fun Board.getSumOfUnmarkedNumbers(): Int {
    var sum = 0
    sum += row1.filterNot { it.isMarked }.sumOf { it.item }
    sum += row2.filterNot { it.isMarked }.sumOf { it.item }
    sum += row3.filterNot { it.isMarked }.sumOf { it.item }
    sum += row4.filterNot { it.isMarked }.sumOf { it.item }
    sum += row5.filterNot { it.isMarked }.sumOf { it.item }
    return sum
}

private fun Board.hasAnyCompleteRowsOrColumns(): Boolean {
    if (row1.all { it.isMarked } || row2.all { it.isMarked }
        || row3.all { it.isMarked } || row4.all { it.isMarked }
        || row5.all { it.isMarked }) return true
    (0..4).forEach {
        if (row1[it].isMarked && row2[it].isMarked
            && row3[it].isMarked && row4[it].isMarked
            && row5[it].isMarked) return true
    }
    return false
}

private fun List<Board>.markNumber(num: Int) = map {
    Board(
        row1 = it.row1.markNumber(num),
        row2 = it.row2.markNumber(num),
        row3 = it.row3.markNumber(num),
        row4 = it.row4.markNumber(num),
        row5 = it.row5.markNumber(num)
    )
}

private fun Array<BoardItem>.markNumber(num: Int) =
    if (any { it.item == num }) {
        val targetIndex = indexOfFirst { it.item == num }
        this[targetIndex] = BoardItem(num, isMarked = true)
        this
    } else
        this

private fun String.toBoardRow() = split(" ").trim().map { BoardItem(it.toInt()) }.toTypedArray()

private fun List<String>.toBoard() =
    Board(
        row1 = this[0].toBoardRow(),
        row2 = this[1].toBoardRow(),
        row3 = this[2].toBoardRow(),
        row4 = this[3].toBoardRow(),
        row5 = this[4].toBoardRow()
    )

data class Board(
    val row1: Array<BoardItem>,
    val row2: Array<BoardItem>,
    val row3: Array<BoardItem>,
    val row4: Array<BoardItem>,
    val row5: Array<BoardItem>,
) {
    override fun toString(): String {
        return """
            ${row1[0].item} ${row1[1].item} ${row1[2].item} ${row1[3].item} ${row1[4].item},
            ${row2[0].item} ${row2[1].item} ${row2[2].item} ${row2[3].item} ${row2[4].item},
            ${row3[0].item} ${row3[1].item} ${row3[2].item} ${row3[3].item} ${row3[4].item},
            ${row4[0].item} ${row4[1].item} ${row4[2].item} ${row4[3].item} ${row4[4].item},
            ${row5[0].item} ${row5[1].item} ${row5[2].item} ${row5[3].item} ${row5[4].item}
            
            
        """.trimIndent()
    }
}

data class BoardItem(
    val item: Int,
    val isMarked: Boolean = false
)
