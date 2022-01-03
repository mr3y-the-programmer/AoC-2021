import java.util.*
import kotlin.collections.ArrayList

fun main() {
    val fishes = readInput("Day06_input")
        .joinToString("")
        .trim()
        .split(",")
        .map { it.toInt() }

    val lanternfish = LongArray(9).toCollection( ArrayList() )

    // count the occurrences
    fishes.forEach { lanternfish[it]++ }

    for (day in 1..80) { // for part 2, swap 80 with 256
        // every iteration:
        // shift each element to the previous position, if the element at the first position (index 0) then shift it to the end of list
        Collections.rotate(lanternfish, -1)
        lanternfish[6] += lanternfish[8]
    }

    println(lanternfish.sum())
}
