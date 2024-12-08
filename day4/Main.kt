package day4
import kotlin.text.Regex

// The current implementation expects the input data to be fully square. Empty or shorter lines are not allowed.

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    val charMatrix = mutableListOf<String>()

    bufferedReader.useLines { lines ->
        lines.forEach { line ->
            // Add all lines to matrix. Could read the whole file at once and split the lines, but don't wanna
            charMatrix.add(line)
        }
    }

    // PUZZLE 1

    val stringToMatch = "XMAS"
    var totalMatches = 0

    // Loop through all the lines. Original indices refer to the line and column of the first letter check in the inner XMAS search
    charMatrix.forEachIndexed { originalYIndex, line ->
        // Loop through each character in the current line
        for (originalXIndex in line.indices) {
            // Assume all matches will be found before checking, set to false if any incorrect characters are found in that direction
            var rightMatchFound = true
            var leftMatchFound = true
            var upMatchFound = true
            var downMatchFound = true
            var diagonalUpRightMatchFound = true
            var diagonalUpLeftMatchFound = true
            var diagonalDownRightMatchFound = true
            var diagonalDownLeftMatchFound = true

            // Loop through 4 characters in each allowed direction from current character
            for (i in 0..3) {
                // Right way check. If rightMatchFound is false, a mismatch was already found and the check can be skipped
                if(rightMatchFound) {
                    // Current indices refers to the current XMAS search index
                    val currentXIndex = originalXIndex+i
                    val currentYIndex = originalYIndex

                    // Only need to check right-side bounds for right way text
                    if (
                        (currentXIndex >= charMatrix[originalYIndex].length) ||
                        charMatrix[currentXIndex][currentYIndex] != stringToMatch[i]
                    ){
                        rightMatchFound = false
                    }
                }

                // Left way check
                if(leftMatchFound) {
                    // Current indices refers to the current XMAS search index
                    val currentXIndex = originalXIndex-i
                    val currentYIndex = originalYIndex

                    // Only need to check left-side bounds for left way text
                    if (
                        (currentXIndex < 0) ||
                        charMatrix[currentXIndex][currentYIndex] != stringToMatch[i]
                    ){
                        leftMatchFound = false
                    }
                }

                // Up way check
                if(upMatchFound) {
                    // Current indices refers to the current XMAS search index
                    val currentXIndex = originalXIndex
                    val currentYIndex = originalYIndex-i

                    // Only need to check up-side bounds for up way text
                    if (
                        (currentYIndex < 0) ||
                        charMatrix[currentXIndex][currentYIndex] != stringToMatch[i]
                    ){
                        upMatchFound = false
                    }
                }

                // Down way check
                if(downMatchFound) {
                    // Current indices refers to the current XMAS search index
                    val currentXIndex = originalXIndex
                    val currentYIndex = originalYIndex+i

                    // Only need to check down-side bounds for down way text
                    if (
                        (currentYIndex >= charMatrix.size) ||
                        charMatrix[currentXIndex][currentYIndex] != stringToMatch[i]
                    ){
                        downMatchFound = false
                    }
                }

                // Diagonal up right check
                if(diagonalUpRightMatchFound) {
                    // Current indices refers to the current XMAS search index
                    val currentXIndex = originalXIndex+i
                    val currentYIndex = originalYIndex-i

                    // Only need to check down-side bounds for down way text
                    if (
                        (currentYIndex < 0) ||
                        (currentXIndex >= charMatrix[originalYIndex].length) ||
                        charMatrix[currentXIndex][currentYIndex] != stringToMatch[i]
                    ){
                        diagonalUpRightMatchFound = false
                    }
                }

                // Diagonal up left check
                if(diagonalUpLeftMatchFound) {
                    // Current indices refers to the current XMAS search index
                    val currentXIndex = originalXIndex-i
                    val currentYIndex = originalYIndex-i

                    // Only need to check down-side bounds for down way text
                    if (
                        (currentYIndex < 0) ||
                        (currentXIndex < 0) ||
                        charMatrix[currentXIndex][currentYIndex] != stringToMatch[i]
                    ){
                        diagonalUpLeftMatchFound = false
                    }
                }

                // Diagonal down right check
                if(diagonalDownRightMatchFound) {
                    // Current indices refers to the current XMAS search index
                    val currentXIndex = originalXIndex+i
                    val currentYIndex = originalYIndex+i

                    // Only need to check down-side bounds for down way text
                    if (
                        (currentYIndex >= charMatrix.size) ||
                        (currentXIndex >= charMatrix[originalYIndex].length) ||
                        charMatrix[currentXIndex][currentYIndex] != stringToMatch[i]
                    ){
                        diagonalDownRightMatchFound = false
                    }
                }

                // Diagonal down left check
                if(diagonalDownLeftMatchFound) {
                    // Current indices refers to the current XMAS search index
                    val currentXIndex = originalXIndex-i
                    val currentYIndex = originalYIndex+i

                    // Only need to check down-side bounds for down way text
                    if (
                        (currentYIndex >= charMatrix.size) ||
                        (currentXIndex < 0) ||
                        charMatrix[currentXIndex][currentYIndex] != stringToMatch[i]
                    ){
                        diagonalDownLeftMatchFound = false
                    }
                }
            }

            // Add all matches found with current origin character to total
            val booleans = listOf(
                rightMatchFound,
                leftMatchFound,
                upMatchFound,
                downMatchFound,
                diagonalUpRightMatchFound,
                diagonalUpLeftMatchFound,
                diagonalDownRightMatchFound,
                diagonalDownLeftMatchFound
            )
            val trueCount = booleans.count { it }
            totalMatches += trueCount
        }
    }

    println("Total matches puzzle 1: $totalMatches")

    // PUZZLE 2

    val stringToMatchSecondPuzzle = "MAS"
    var totalMatchesSecondPuzzle = 0

    // Loop through all the lines. Original indices refer to the line and column of the first letter check in the inner XMAS search
    charMatrix.forEachIndexed { originalYIndex, line ->
        // Loop through each character in the current line
        for (originalXIndex in line.indices) {

            // Use similar logic to puzzle 1 - assume all matches are found and remove one on mismatch
            var rightDownMatchFound = true
            var leftUpMatchFound = true

            // To not double-count, only check one diagonal. If string is found, check for matching strings on the other diagonal

            // First check if topleft to bottom right diagonal match is found in either direction
            for (i in 0..2) {
                // Right and down diagonal check
                val currentRightDownDiagonalXIndex = originalXIndex + i
                val currentRightDownDiagonalYIndex = originalYIndex + i

                val currentLeftUpDiagonalXIndex = originalXIndex - i
                val currentLeftUpDiagonalYIndex = originalYIndex - i

                // If false, mismatch was already found and the check can be skipped
                if (rightDownMatchFound) {
                    // Check bounds
                    if (
                        (currentRightDownDiagonalXIndex >= charMatrix[originalYIndex].length) ||
                        (currentRightDownDiagonalYIndex >= charMatrix.size) ||
                        charMatrix[currentRightDownDiagonalXIndex][currentRightDownDiagonalYIndex] != stringToMatchSecondPuzzle[i]
                    ) {
                        // Mismatch on right and down diagonal check
                        rightDownMatchFound = false
                    }
                }

                if (leftUpMatchFound) {
                    // Check bounds
                    if (
                        (currentLeftUpDiagonalXIndex < 0) ||
                        (currentLeftUpDiagonalYIndex < 0) ||
                        charMatrix[currentLeftUpDiagonalXIndex][currentLeftUpDiagonalYIndex] != stringToMatchSecondPuzzle[i]
                    ) {
                        // Mismatch on right and down diagonal check
                        leftUpMatchFound = false
                    }
                }
            }

            // If any of the original diagonal check matches are found, see if they have a corresponding X-cross MAS match
            if (rightDownMatchFound) {
                // Either 2 steps to the right and down to the left is a MAS
                if (
                    (charMatrix[originalXIndex+2][originalYIndex] == 'M') &&
                    (charMatrix[originalXIndex+1][originalYIndex+1] == 'A') &&
                    (charMatrix[originalXIndex][originalYIndex+2] == 'S')
                ) {
                    totalMatchesSecondPuzzle++
                } else if ( // Or 2 steps down and up to the right is a MAS
                    (charMatrix[originalXIndex][originalYIndex+2] == 'M') &&
                    (charMatrix[originalXIndex+1][originalYIndex+1] == 'A') &&
                    (charMatrix[originalXIndex+2][originalYIndex] == 'S')
                ) {
                    totalMatchesSecondPuzzle++
                }
            }

            if (leftUpMatchFound) {
                // Either 2 steps up down to the left is a MAS
                if (
                    (charMatrix[originalXIndex][originalYIndex-2] == 'M') &&
                    (charMatrix[originalXIndex-1][originalYIndex-1] == 'A') &&
                    (charMatrix[originalXIndex-2][originalYIndex] == 'S')
                ) {
                    totalMatchesSecondPuzzle++
                } else if ( // Or 2 steps left up to the right is a MAS
                    (charMatrix[originalXIndex-2][originalYIndex] == 'M') &&
                    (charMatrix[originalXIndex-1][originalYIndex-1] == 'A') &&
                    (charMatrix[originalXIndex][originalYIndex-2] == 'S')
                ) {
                    totalMatchesSecondPuzzle++
                }
            }
        }
    }

    println("Total matches puzzle 2: $totalMatchesSecondPuzzle")

}
