package day10

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    // inputMatrix[yCoord][xCoord] gives the digit at input coordinates
    val inputMatrix = mutableListOf<MutableList<Int>>()

    bufferedReader.useLines { lines ->
        lines.forEachIndexed { yIndex, line ->
            line.forEach { heightNumber ->
                if (yIndex >= inputMatrix.size) {
                    inputMatrix.add(mutableListOf<Int>())
                }
                inputMatrix[yIndex].add(heightNumber.digitToInt())
            }
        }
    }

    // Recursive check finding all trails from coordinate
    fun recursiveTrailCheck(xCoord: Int, yCoord: Int, alreadyFoundEndingCoordinates: MutableSet<Pair<Int, Int>>, ignoreAlreadyFound: Boolean): Int {
        val currentDigit = inputMatrix[yCoord][xCoord]

        // Base case
        if(currentDigit == 9) {
            val baseCaseCoordinates = Pair(xCoord, yCoord)

            // Check if we want to ignore the unique constraint for puzzle 2
            if (!ignoreAlreadyFound) { // Puzzle 1 logic in here
                // Don't count the same 9 again if found from different path
                if(alreadyFoundEndingCoordinates.contains(baseCaseCoordinates)) {
                    return 0
                }

                alreadyFoundEndingCoordinates.add(baseCaseCoordinates)
                return 1
            } else { // Puzzle 2 logic
                return 1
            }

        }

        var totalTrailsFound = 0

        // Up check
        if(yCoord > 0) {
            val digitAbove = inputMatrix[yCoord - 1][xCoord]
            if (digitAbove == currentDigit + 1) {
                totalTrailsFound += recursiveTrailCheck(xCoord, yCoord-1, alreadyFoundEndingCoordinates, ignoreAlreadyFound)
            }
        }

        // Down check
        if (inputMatrix.size > (yCoord + 1)) { // Segmentation check
            val digitBelow = inputMatrix[yCoord + 1][xCoord]
            if (digitBelow == currentDigit + 1) {
                totalTrailsFound += recursiveTrailCheck(xCoord, yCoord+1, alreadyFoundEndingCoordinates, ignoreAlreadyFound)
            }
        }

        // Left check
        if(xCoord > 0) {
            val digitToTheLeft = inputMatrix[yCoord][xCoord - 1]
            if (digitToTheLeft == currentDigit + 1) {
                totalTrailsFound += recursiveTrailCheck(xCoord - 1, yCoord, alreadyFoundEndingCoordinates, ignoreAlreadyFound)
            }
        }

        // Right check
        if(inputMatrix[yCoord].size > (xCoord + 1)) {
            val digitToTheRight = inputMatrix[yCoord][xCoord + 1]
            if (digitToTheRight == currentDigit + 1) {
                totalTrailsFound += recursiveTrailCheck(xCoord + 1, yCoord, alreadyFoundEndingCoordinates, ignoreAlreadyFound)
            }
        }

        return totalTrailsFound
    }

    val resultingPuzzleOneTrails = mutableListOf<Int>()
    val resultingPuzzleTwoTrails = mutableListOf<Int>()

    inputMatrix.forEachIndexed { yIndex, line ->
        line.forEachIndexed { xIndex, height ->
            if (height == 0) {
                val alreadyFoundEndingCoordinates = mutableSetOf<Pair<Int, Int>>() // Keep track of trails ends already found in recursive check (Puzzle 1)
                resultingPuzzleOneTrails.add(recursiveTrailCheck(xIndex, yIndex, alreadyFoundEndingCoordinates, false))
                resultingPuzzleTwoTrails.add(recursiveTrailCheck(xIndex, yIndex, alreadyFoundEndingCoordinates, true)) // Ignore the unique check for puzzle 2
            }
        }
    }

    println("Sum of trails found in puzzle 1: ${resultingPuzzleOneTrails.sum()}")
    println("Sum of trails found in puzzle 2: ${resultingPuzzleTwoTrails.sum()}")
}
