package day3
import kotlin.text.Regex

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    // Match full mul(x,y) regex, or "don't" or "do" (never both, prioritizes "don't")
    val regex = Regex("mul\\(\\d{1,3},\\d{1,3}\\)|don't|do(?!n't)")

    var firstPuzzleTotal = 0
    var secondPuzzleTotal = 0

    // Controls whether to ignore matched multiplications or not
    var enabled = true

    bufferedReader.useLines { lines ->
        lines.forEach { line ->
            // Find all occurrences of regex function
            val occurrences = regex.findAll(line)

            for(match in occurrences) {
                val matchedString = match.value

                val dontRegex = Regex("don't")
                val doRegex = Regex("do")

                if(dontRegex.find(matchedString)?.value != null) {
                    enabled = false
                } else if(doRegex.find(matchedString)?.value != null) {
                    enabled = true
                }

                val firstNumberRegex = Regex("\\((\\d+)")
                val secondNumberRegex = Regex(",(\\d+)")

                // match.value extracts the sequence value of current match. groupValues.get(1) gets only the digit value from number regex above (parenthesis part).
                val firstNumber = firstNumberRegex.find(matchedString)?.groupValues?.get(1)?.toIntOrNull()
                val secondNumber = secondNumberRegex.find(matchedString)?.groupValues?.get(1)?.toIntOrNull()

                if (firstNumber != null && secondNumber != null) {
                    val calculatedMultiple = (firstNumber * secondNumber)

                    // Always add calculated to first puzzle result
                    firstPuzzleTotal += calculatedMultiple

                    // Second puzzle demands only adding when enabled
                    if(enabled) secondPuzzleTotal += calculatedMultiple
                }
            }
        }
    }

    println("Total for puzzle 1: $firstPuzzleTotal")
    println("Total for puzzle 1: $secondPuzzleTotal")
}
