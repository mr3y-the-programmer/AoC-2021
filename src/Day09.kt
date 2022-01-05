fun main() {
    // part 1
    val input = readInput("Day09_input").trim()
    val pointsMap = Array(input.size) { Array(input[0].length) { Point.RegularPoint (0, 0, 0) } }
    input.forEachIndexed { lineIndex, line ->
        line.forEachIndexed { charIndex, char ->
            pointsMap[lineIndex][charIndex] = Point.RegularPoint(lineIndex, charIndex, char.digitToInt())
        }
    }
    val lowPoints = pointsMap.extractLowPoints()
    println(lowPoints.sumOf { it.riskLevel })
    // part 2
    val largestBasinsSizes = pointsMap.generateBasinsAtLowPoints(lowPoints)
        .sortedByDescending { it.points.size }
        .take(3)
        .map { it.points.size }
        .reduce { acc, i -> acc * i }
    println(largestBasinsSizes)
}

private fun Array<Array<Point.RegularPoint>>.generateBasinsAtLowPoints(lowPoints: List<Point.LowPoint>): List<Basin> {
    val basins = mutableListOf<Basin>()
    for (lowPoint in lowPoints) {
        val points = getAdjacentPointsRecursively(lowPoint.rowIndex, lowPoint.columnIndex) + lowPoint
        basins.add(Basin(points))
    }
    return basins
}

// TODO: there is a bug here in distinction by indexes, so it is not working + it is too complex approach as you can see
private fun Array<Array<Point.RegularPoint>>.getAdjacentPointsRecursively(
    rowIndex: Int,
    columnIndex: Int,
    initialPoints: MutableList<Point.RegularPoint?> = mutableListOf(),
    direction: Direction = Direction.Default
): List<Point.RegularPoint> {
    if (rowIndex < 0 || rowIndex > lastIndex || columnIndex < 0 || columnIndex > this[0].lastIndex || this.getOrNull(rowIndex)?.getOrNull(columnIndex)?.height == 9) return emptyList()
    val copy = initialPoints
    if (direction.canGoLeft && !initialPoints.any { it?.rowIndex == rowIndex && it.columnIndex == columnIndex - 1 }) {
        initialPoints += this[rowIndex].getOrNull(columnIndex - 1)?.let {
            if (it.height == 9) null else Point.RegularPoint(rowIndex, columnIndex - 1, it.height, isChecked = true)
        }
    }
    if (direction.canGoUp && !initialPoints.any { it?.rowIndex == rowIndex - 1 && it.columnIndex == columnIndex }) {
        initialPoints += this.getOrNull(rowIndex - 1)?.get(columnIndex)?.let {
            if (it.height == 9) null else Point.RegularPoint(rowIndex - 1, columnIndex, it.height, isChecked = true)
        }
    }
    if (direction.canGoRight && !initialPoints.any { it?.rowIndex == rowIndex && it.columnIndex == columnIndex + 1 }) {
        initialPoints += this[rowIndex].getOrNull(columnIndex + 1)?.let {
            if (it.height == 9) null else Point.RegularPoint(rowIndex, columnIndex + 1, it.height, isChecked = true)
        }
    }
    if (direction.canGoDown && !initialPoints.any { it?.rowIndex == rowIndex + 1 && it.columnIndex == columnIndex }) {
        initialPoints += this.getOrNull(rowIndex + 1)?.get(columnIndex)?.let {
            if (it.height == 9) null else Point.RegularPoint(rowIndex + 1, columnIndex, it.height, isChecked = true)
        }
    }

    // add borders of borders
    val newDirection = Direction(canGoLeft = true, canGoUp = true, canGoRight = true, canGoDown = true)
    if (direction.canGoLeft && copy != initialPoints)
        getAdjacentPointsRecursively(rowIndex, columnIndex - 1, initialPoints, newDirection.copy(canGoRight = false))

    if (direction.canGoUp && copy != initialPoints)
        getAdjacentPointsRecursively(rowIndex - 1, columnIndex, initialPoints, newDirection.copy(canGoDown = false))

    if (direction.canGoRight && copy != initialPoints)
        getAdjacentPointsRecursively(rowIndex, columnIndex + 1, initialPoints, newDirection.copy(canGoLeft = false))

    if (direction.canGoDown && copy != initialPoints)
        getAdjacentPointsRecursively(rowIndex + 1, columnIndex, initialPoints, newDirection.copy(canGoUp = false))

    return initialPoints.filterNotNull()
}

data class Direction(val canGoLeft: Boolean, val canGoUp: Boolean, val canGoRight: Boolean, val canGoDown: Boolean) {
    companion object {
        val Default = Direction(canGoLeft = true, canGoUp = true, canGoRight = true, canGoDown = true)
    }
}

private fun Array<Array<Point.RegularPoint>>.extractLowPoints(): List<Point.LowPoint> {
    val lowPoints = mutableListOf<Point.LowPoint>()
    forEachIndexed { rowIndex, row ->
        row.forEachIndexed { pointIndex, currentPoint ->
            val pointBorders = listOfNotNull(
                row.getOrNull(pointIndex - 1), // left
                this.getOrNull(rowIndex - 1)?.get(pointIndex), // up
                this.getOrNull(rowIndex + 1)?.get(pointIndex), // down
                row.getOrNull(pointIndex + 1), // right
            )
            if (pointBorders.all { it.height > currentPoint.height }) lowPoints.add(Point.LowPoint(rowIndex, pointIndex, currentPoint.height))
        }
    }
    return lowPoints
}

sealed class Point {
    abstract val rowIndex: Int
    abstract val columnIndex: Int
    abstract val height: Int

    data class RegularPoint(override val rowIndex: Int, override val columnIndex: Int, override val height: Int, val isChecked: Boolean = false): Point()
    data class LowPoint(override val rowIndex: Int, override val columnIndex: Int, override val height: Int): Point() {
        val riskLevel
            get() = height + 1
    }
}

data class Basin(val points: List<Point>)