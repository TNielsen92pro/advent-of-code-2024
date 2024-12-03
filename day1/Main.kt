package day1

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
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

    var puzzle2FinalDiff: Int = 0

    // Iterate through the first list
    firstList.forEach { (key, value) -> 
        // This is what the puzzle asks for from each entry in the first list
        val keyTimesOccurrencesInSecondList = key * (secondList[key] ?: 0)

        // Since the hashmap tells us how many times the key will appear in the first list, we can add all the occurences together by multiplying by the value in the hashmap right away
        val keyTimesOccurrencesInSecondListTimesOccurencesInFirstList = keyTimesOccurrencesInSecondList * value

        // Then we add them all together for the result
        puzzle2FinalDiff += keyTimesOccurrencesInSecondListTimesOccurencesInFirstList
    }

    var puzzle1FinalDiff: Int = 0

    // Iterate through the first list in ascending key order
    firstList.forEach { (key, _) ->
        // Add the diff until 0 occurrences left in first list (and second since they're the same size)
        while ((firstList[key] ?: 0) > 0) {
            var secondListFirstKey = secondList.firstKey() // First key always matches the lowest value in second list
            if (secondList[secondListFirstKey] == 0) {
                // Remove key from the second list when expended
                secondList.remove(secondListFirstKey)
            }

            secondListFirstKey = secondList.firstKey() // Reinitialize in case key was removed

            // Add to final diff
            puzzle1FinalDiff += kotlin.math.abs(key - secondListFirstKey)

            // Subtract the used key for next loop
            secondList[secondListFirstKey] = (secondList[secondListFirstKey] ?: 0) - 1
            firstList[key] = (firstList[key] ?: 0) - 1
        }
    }

    println("Puzzle 1 final diff: $puzzle1FinalDiff")
    println("Puzzle 2 final diff: $puzzle2FinalDiff")

}
