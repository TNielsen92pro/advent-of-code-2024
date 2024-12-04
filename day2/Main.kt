package day2
import kotlin.math.abs

// Return the operation of the first two numbers. The same operation must apply to the full number sequence
fun getOperation(first: Int, second: Int): (Int, Int) -> Boolean {
    if((first == second) || (abs(first - second) > 3)) {
        return {_, _ -> false} // First two numbers are already invalid, only return false
    } else if (first < second) {
        return ::firstLessThanSecond
    } else {
        return ::firstIsMoreThanSecond
    }
}

fun firstLessThanSecond(first: Int, second: Int): Boolean {
    return (first < second) && (abs(first - second) <= 3)
}

fun firstIsMoreThanSecond(first: Int, second: Int): Boolean {
    return (first > second) && (abs(first - second) <= 3)
}

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    var safeReportsFirstPuzzle: Int = 0
    var extraSafeReportsSecondPuzzle: Int = 0

    bufferedReader.useLines { lines ->
        lines.forEach { line ->
            // Get the numbers from a line by splitting the spaces
            val numbers = line.trim().split("\\s+".toRegex())

            val first: Int? = numbers[0].toIntOrNull()
            val second: Int? = numbers[1].toIntOrNull()

            // Skip lines not corresponding to expected format (Could be empty last line or something)
            if (first !== null && second !== null) {
                // Get the operation between first and second number, the same operation should be used on all consecutive numbers
                val operation = getOperation(first, second)

                // Check how many faulty lines found
                var faultyLines = 0

                for (i in 1..<numbers.size) {
                    val firstLevel = numbers[i-1].toInt()
                    val secondLevel = numbers[i].toInt()

                    if(!operation(firstLevel, secondLevel)) {
                        faultyLines++ // If any 2 numbers in the line does not fulfill operations requirements, it is faulty
                    }
                }

                // Count line if not faulty
                if (faultyLines == 0) {
                    safeReportsFirstPuzzle++
                }

                // If faulty combination, it may be safe with a level removed. Time for a NESTED LOOP!!!
                if (faultyLines > 0) {
                    for (i in numbers.indices) {
                        // Keep track of if we found a sequence that is safe without current index
                        var safeWithoutIndex = true

                        // Copy numbers list and remove current index
                        val copiedListWithoutCurrentIndex = numbers.filterIndexed { index, _ -> index != i }

                        // Reset operation in case the index removal changes combination of first 2 levels
                        val newOperation = getOperation(copiedListWithoutCurrentIndex[0].toInt(), copiedListWithoutCurrentIndex[1].toInt())

                        for (j in 1..<copiedListWithoutCurrentIndex.size) {
                            val firstLevel = copiedListWithoutCurrentIndex[j-1].toInt()
                            val secondLevel = copiedListWithoutCurrentIndex[j].toInt()

                            if(!newOperation(firstLevel, secondLevel)) {
                                safeWithoutIndex = false
                            }
                        }

                        // If a level sequence works without one of the indexes, add to second puzzle result and break
                        if (safeWithoutIndex) {
                            extraSafeReportsSecondPuzzle++
                            break
                        }
                    }
                }
            }
        }
    }

    println("Number of safe reports for puzzle 1: $safeReportsFirstPuzzle")
    println("Number of safe reports for puzzle 2: ${safeReportsFirstPuzzle+extraSafeReportsSecondPuzzle}")
}
