package Day1

fun main() {
    val resource = object {}.javaClass.getResource("./data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    // First map - value: occurrences
    val firstList = sortedMapOf<Int, Int>()

    // Second map - value: occurrences
    val secondList = sortedMapOf<Int, Int>()

    bufferedReader.useLines { lines ->
        lines.forEach { line ->
            // Get the numbers from a line by splitting the spaces
            val numbers = line.trim().split("\\s+".toRegex())

            val a = numbers[0].toIntOrNull()
            val b = numbers[1].toIntOrNull()

            // Skip lines not corresponding to expected format (Could be empty last line or something)
            if (a !== null && b !== null) {
                firstList[a] = (firstList[a] ?: 0) + 1
                secondList[b] = (secondList[b] ?: 0) + 1
            }
        }
    }

    var finalDiff: Int = 0

    // Iterate through the first list in ascending key order
    firstList.forEach { (key, _) ->
        // Add the diff until 0 occurrences left for each key
        while ((firstList[key] ?: 0) > 0) {
            var secondListFirstKey = secondList.firstKey() // First key always matches the lowest value in second list
            if (secondList[secondListFirstKey] == 0) {
                // Remove key from the second list when expended
                secondList.remove(secondListFirstKey)
            }

            secondListFirstKey = secondList.firstKey() // Reinitialize in case key was removed

            // Add to final diff
            finalDiff += kotlin.math.abs(key - secondListFirstKey)

            // Subtract the used key for next loop
            secondList[secondListFirstKey] = (secondList[secondListFirstKey] ?: 0) - 1
            firstList[key] = (firstList[key] ?: 0) - 1
        }
    }

    println("Puzzle 1 final diff: $finalDiff")


}
