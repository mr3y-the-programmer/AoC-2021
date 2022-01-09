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

// ugly solution but it works
private fun Array<Array<Point.RegularPoint>>.generateBasinsAtLowPoints(lowPoints: List<Point.LowPoint>): List<Basin> {
    val basins = mutableListOf<Basin>()
    for (lowPoint in lowPoints) {
        val points = getAdjacentPointsRecursively(lowPoint.rowIndex, lowPoint.columnIndex) + lowPoint
        basins.add(Basin(points))
    }
    return basins
}

private fun Array<Array<Point.RegularPoint>>.getAdjacentPointsRecursively(
    rowIndex: Int,
    columnIndex: Int,
    initialPoints: MutableList<Point.RegularPoint?> = mutableListOf(),
    direction: Direction = Direction.Default
): List<Point.RegularPoint> {
    if (rowIndex < 0 || rowIndex > lastIndex || columnIndex < 0 || columnIndex > this[0].lastIndex || this.getOrNull(rowIndex)?.getOrNull(columnIndex)?.height == 9) return emptyList()
    if (direction.canGoLeft) {
        var checked = false
        initialPoints += this[rowIndex].getOrNull(columnIndex - 1)?.let {
            if (it.height == 9 || it.isChecked)
                null
            else {
                checked = true
                Point.RegularPoint(rowIndex, columnIndex - 1, it.height)
            }
        }
        if (checked) this[rowIndex][columnIndex - 1] = this[rowIndex][columnIndex - 1].copy(isChecked = true)
    }
    if (direction.canGoUp) {
        var checked = false
        initialPoints += this.getOrNull(rowIndex - 1)?.get(columnIndex)?.let {
            if (it.height == 9 || it.isChecked)
                null
            else {
                checked = true
                Point.RegularPoint(rowIndex - 1, columnIndex, it.height)
            }
        }
        if (checked) this[rowIndex - 1][columnIndex] = this[rowIndex - 1][columnIndex].copy(isChecked = true)
    }
    if (direction.canGoRight) {
        var checked = false
        initialPoints += this[rowIndex].getOrNull(columnIndex + 1)?.let {
            if (it.height == 9 || it.isChecked)
                null
            else {
                checked = true
                Point.RegularPoint(rowIndex, columnIndex + 1, it.height)
            }
        }
        if (checked) this[rowIndex][columnIndex + 1] = this[rowIndex][columnIndex + 1].copy(isChecked = true)
    }
    if (direction.canGoDown) {
        var checked = false
        initialPoints += this.getOrNull(rowIndex + 1)?.get(columnIndex)?.let {
            if (it.height == 9 || it.isChecked)
                null
            else {
                checked = true
                Point.RegularPoint(rowIndex + 1, columnIndex, it.height)
            }
        }
        if (checked) this[rowIndex + 1][columnIndex] = this[rowIndex + 1][columnIndex].copy(isChecked = true)
    }

    // add borders of borders
    if (direction.canGoLeft) {
        val newCopy = this.map { it.sliceArray(0 until columnIndex) }.toTypedArray()
        newCopy.getAdjacentPointsRecursively(rowIndex, columnIndex - 1, initialPoints, direction.copy(canGoRight = false))
        newCopy.forEachIndexed { i, row ->
            row.forEachIndexed { j, regularPoint ->
                if (regularPoint.isChecked) this[i][j] = regularPoint
            }
        }
    }

    if (direction.canGoUp) {
        val newCopy = this.sliceArray(0 until rowIndex)
        newCopy.getAdjacentPointsRecursively(rowIndex - 1, columnIndex, initialPoints, direction.copy(canGoDown = false))
        newCopy.forEachIndexed { i, row ->
            row.forEachIndexed { j, regularPoint ->
                if (regularPoint.isChecked) this[i][j] = regularPoint
            }
        }
    }

    if (direction.canGoRight && columnIndex + 1 <= this[0].lastIndex) {
        val newCopy = this.map { it.sliceArray((columnIndex + 1)..it.lastIndex) }.toTypedArray()
        newCopy.getAdjacentPointsRecursively(rowIndex, 0, initialPoints, direction.copy(canGoLeft = false))
        newCopy.forEachIndexed { i, row ->
            row.forEachIndexed { j, regularPoint ->
                if (regularPoint.isChecked) this[i][j + columnIndex + 1] = regularPoint
            }
        }
    }

    if (direction.canGoDown && rowIndex + 1 <= this.lastIndex) {
        val newCopy = this.sliceArray((rowIndex + 1)..this.lastIndex)
        newCopy.getAdjacentPointsRecursively(0, columnIndex, initialPoints, direction.copy(canGoUp = false))
        newCopy.forEachIndexed { i, row ->
            row.forEachIndexed { j, regularPoint ->
                if (regularPoint.isChecked) this[i + rowIndex + 1][j] = regularPoint
            }
        }
    }

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