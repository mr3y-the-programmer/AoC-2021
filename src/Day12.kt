import java.util.*

fun main() {
    // part 1
    val connectedCaves = readInput("Day12_input")
    val graph = UndirectedGraph()
    connectedCaves.forEach { connection ->
        val (source, destination) = connection.trim().split("-")
        // each entry isn't necessarily ordered
        if (source == "end" || destination == "start")
            graph.addEdge(destination, source)
        else
            graph.addEdge(source, destination)
    }
    val pathsNum = graph.getNumOfDistinctPathsBetween(source = "start", destination = "end")
    println(pathsNum)
    // part 2
    println(graph.getNumOfDistinctPathsBetween(source = "start", destination = "end", canVisitSmallCavesTwice = true))
}

class UndirectedGraph {
    // adjacency list
    private val graph = hashMapOf<String, MutableList<String>>()

    private fun addVertex(vertex: String) {
        graph[vertex] = LinkedList<String>()
    }

    fun addEdge(source: String, destination: String) {
        if (!graph.containsKey(source)) addVertex(source)
        // end can only have in-going edge
        if (destination != "end" && !graph.containsKey(destination)) addVertex(destination)
        graph[source]?.add(destination)
        // start can only have out-going edge
        if (source != "start") graph[destination]?.add(source)
    }

    operator fun get(key: String) = graph[key]!!
}

fun UndirectedGraph.getNumOfDistinctPathsBetween(source: String, destination: String, canVisitSmallCavesTwice: Boolean = false): Int {
    val tempQueue: Queue<List<String>> = LinkedList()
    val paths = mutableListOf<List<String>>()

    val directAdjacentVertices = this[source]
    directAdjacentVertices.forEach { vertex ->
        tempQueue.add(listOf(source, vertex))
    }
    while (tempQueue.isNotEmpty()) {
        val frontElements = tempQueue.remove()
        this[frontElements.last()].forEach { vertex ->
            if (vertex == destination) { // we found the shortest path
                paths.add(
                    buildList {
                        addAll(frontElements)
                        add(vertex)
                    }
                )
            } else {
                if (!canVisitSmallCavesTwice) {
                    if (!(vertex[0].isLowerCase() && frontElements.contains(vertex))) {
                        tempQueue.add(
                            buildList {
                                addAll(frontElements)
                                add(vertex)
                            }
                        )
                    }
                } else {
                    if (vertex[0].isLowerCase()) {
                        if (!frontElements.contains(vertex)) {
                            tempQueue.add(
                                buildList {
                                    addAll(frontElements)
                                    add(vertex)
                                }
                            )
                        } else {
                            frontElements.filter { it[0].isLowerCase() }
                                .let { smallCaves ->
                                    val (matching, others) = smallCaves.partition { it == vertex }
                                    if (matching.count() == 1 && others.groupingBy { it }.eachCount().all { it.value <= 1 }) {
                                        tempQueue.add(
                                            buildList {
                                                addAll(frontElements)
                                                add(vertex)
                                            }
                                        )
                                    }
                                }
                        }
                    } else {
                        tempQueue.add(
                            buildList {
                                addAll(frontElements)
                                add(vertex)
                            }
                        )
                    }
                }
            }
        }
    }
    return paths.size
}