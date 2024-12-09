package daytemplate

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    bufferedReader.useLines { lines ->
        lines.forEach { line ->

        }
    }
}
