fun main() {

    var x = 0
    var y = 0
    var aim = 0
    readInput("Day02_input")
        .forEach {
            val command = it.split(" ")
            when {
                command[0] == "forward" -> {
                    x += command[1].toInt()
                    y += aim * command[1].toInt()
                }
                command[0] == "up" -> aim -= command[1].toInt()
                command[0] == "down" -> aim += command[1].toInt()
            }
        }
    println(x * y)
}