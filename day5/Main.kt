package day5
import kotlin.text.Regex

// The current implementation expects the input data to be fully square. Empty or shorter lines are not allowed.

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    // The hashmaps are a big confusing, should probably be the other way around logically

    // biggerThanHashMap[biggerNumber] = [smallerNumber1, smallerNumber2, ...]
    val biggerThanHashMap = mutableMapOf<Int, MutableList<Int>>()

    // lesserThanHashMap[smallerNumber] = [biggerNumber1, biggerNumber2, ...]
    val lesserThanHashMap = mutableMapOf<Int, MutableList<Int>>()

    var orderingInputFinished = false

    var totalCorrectlyOrderedLines = 0
    var totalSumOfMiddleNumbers = 0

    var incorrectlyOrderedLines = mutableListOf<List<Int>>()

    bufferedReader.useLines { lines ->
        lines.forEach { line ->
            // Number orders and update output is separated by an empty line
            if (line.trim().isEmpty()) {
                orderingInputFinished = true
            } else {
                if (orderingInputFinished) {
                    // Assume correctly ordered line initially, falsify if error is found
                    var currentLineIsCorrectlyOrdered = true

                    // Expects are numbers to be proper ints
                    val numbers = line.split(",").map { it.toInt() }
                    numbers.forEachIndexed {index, number ->

                        // This left number check is not needed since it will have always been checked in the previous loop

                        /*if (index > 0) { // Check that left number is not included in biggerThan hashmap for current number check
                            val lesserNumber = numbers[index-1]
                            val biggerNumbers = lesserThanHashMap[number]

                            if (biggerNumbers != null && biggerNumbers.contains(lesserNumber)) {
                                currentLineIsCorrectlyOrdered = false
                            }
                        }*/

                        if (index < numbers.size - 1) { // Check that right number is not included in lesserThan hashmap for current number check
                            val biggerNumber = numbers[index+1]
                            val lesserNumbers = biggerThanHashMap[number]

                            if (lesserNumbers != null && lesserNumbers.contains(biggerNumber)) {
                                currentLineIsCorrectlyOrdered = false
                            }
                        }
                    }

                    if (currentLineIsCorrectlyOrdered) {
                        totalCorrectlyOrderedLines++

                        val middleNumber = numbers[numbers.size / 2] // Slightly haxy way to access middle number index
                        totalSumOfMiddleNumbers+=middleNumber
                    } else {
                        incorrectlyOrderedLines.add(numbers)
                    }
                } else {
                    val lesserDigit = line.substring(0, 2).toIntOrNull()
                    val biggerDigit = line.substring(3, 5).toIntOrNull()

                    if (lesserDigit != null && biggerDigit != null) {
                        // Add biggerDigit to lesserDigit index in lesserThan map (handles first addition as well as consecutive ones)
                        lesserThanHashMap.getOrPut(lesserDigit) { mutableListOf() }.add(biggerDigit)
                        biggerThanHashMap.getOrPut(biggerDigit) { mutableListOf() }.add(lesserDigit)
                    }
                }
            }
        }
    }

    // Puzzle 2 begins here

    fun correctFaultyNumberSequenceAndReturnMiddleValue(numbers: List<Int>): Int {
        numbers.forEachIndexed {index, number ->
            if (index < numbers.size - 1) { // Check that right number is not included in lesserThan hashmap for current number check
                val biggerNumber = numbers[index+1]
                val lesserNumbers = biggerThanHashMap[number]

                if (lesserNumbers != null && lesserNumbers.contains(biggerNumber)) {
                    // Copy the list of numbers and swap the faulty combination
                    val adjustedNumbersCopy = numbers.toMutableList().apply {
                        val temp = this[index]
                        this[index] = this[index+1]
                        this[index+1] = temp
                    }

                    // Run the function with the adjusted list recursively until it is correctly ordered
                    return correctFaultyNumberSequenceAndReturnMiddleValue(adjustedNumbersCopy)
                }
            }
        }

        // Base case - line is now correct, return middle number
        return numbers[numbers.size / 2]
    }

    var totalSumOfMiddleNumbersForCorrectedLines = 0

    incorrectlyOrderedLines.forEach{numbers ->
        // LETS GO RECURSIVE!
        totalSumOfMiddleNumbersForCorrectedLines += correctFaultyNumberSequenceAndReturnMiddleValue(numbers)
    }

    println("Total correct lines: $totalCorrectlyOrderedLines")
    println("Total sum of middle digits from correct lines (Puzzle 1): $totalSumOfMiddleNumbers")
    println("Total sum of middle digits from corrected faulty lines (Puzzle 2): $totalSumOfMiddleNumbersForCorrectedLines")
}
