package day6
import kotlin.text.Regex

// The current implementation expects the input data to be fully square. Empty or shorter lines are not allowed.

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    bufferedReader.useLines { lines ->
        lines.forEach { line ->

        }
    }
}
