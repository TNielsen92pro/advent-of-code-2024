package day7

// Binary 1 represents multiplication, binary 0 represents addition
fun digitToOperation(digit: Int): (Long, Int) -> Long {
    if (digit == 2) {
        return { a, b -> (a.toString() + b.toString()).toLong() }
    } else if (digit == 1){
        return { a, b -> a * b }
    } else {
        return { a, b -> a + b }
    }
}

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    var sumFirstPuzzle: Long = 0
    var sumSecondPuzzle: Long = 0

    bufferedReader.useLines { lines ->
        lines.forEach { line ->
            val lineComponents = line.trim().split("\\s+".toRegex())

            // Extract first component and remove colon to get result as an int
            val expectedResult: Long = lineComponents[0].substring(0, lineComponents[0].length - 1).toLong()

            val numbers = lineComponents.toMutableList()
            numbers.removeFirst() // Removes result component, only leaving the numbers that should be operated on

            // PUZZLE 1:

            // Extract max loop iterations by setting a binary number with the length of the number of operators, and fill it with 1's
            val maxNumberOfPossibleSequences = "1".padStart(numbers.size - 1, '1').toInt(2)

            var operationsRepresentation = 0

            while(operationsRepresentation <= maxNumberOfPossibleSequences) {
                // Turn operations representation into a binary string with leading 0's for non-used bits
                val binaryOperationsRepresentation = operationsRepresentation.toString(2).padStart(numbers.size - 1, '0')

                var currentSumOfOperations: Long = numbers[0].toLong()

                for(i in 1..<numbers.size) {
                    val currentOperationBit = binaryOperationsRepresentation[i-1]
                    val operation = digitToOperation(currentOperationBit.digitToInt())
                    val newSum: Long = operation(currentSumOfOperations, numbers[i].toInt())

                    currentSumOfOperations = newSum
                }

                if (currentSumOfOperations == expectedResult) {
                    sumFirstPuzzle+= currentSumOfOperations
                    break
                }

                operationsRepresentation++
            }

            // PUZZLE 2 (Same concept but base-3):

            // Extract max loop iterations by setting a ternary number with the length of the number of operators, and fill it with 1's
            val maxNumberOfPossibleSequencesTernary = "2".padStart(numbers.size - 1, '2').toInt(3)

            var operationsRepresentationTernary = 0

            while(operationsRepresentationTernary <= maxNumberOfPossibleSequencesTernary) {
                // Turn operations representation into a ternary string with leading 0's for non-used bits
                val ternaryOperationsRepresentation = operationsRepresentationTernary.toString(3).padStart(numbers.size - 1, '0')

                var currentSumOfOperationsTernary: Long = numbers[0].toLong()

                for(i in 1..<numbers.size) {
                    val currentOperationBit = ternaryOperationsRepresentation[i-1]
                    val operation = digitToOperation(currentOperationBit.digitToInt())
                    val newSum: Long = operation(currentSumOfOperationsTernary, numbers[i].toInt())

                    currentSumOfOperationsTernary = newSum
                }

                if (currentSumOfOperationsTernary == expectedResult) {
                    sumSecondPuzzle+= currentSumOfOperationsTernary
                    break
                }

                operationsRepresentationTernary++
            }
        }
    }

    println("Puzzle 1 sum of equations that can be made true: $sumFirstPuzzle")
    println("Puzzle 2 sum of equations that can be made true with third operation: $sumSecondPuzzle")
}
