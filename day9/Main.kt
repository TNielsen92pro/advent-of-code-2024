package day9

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/exampledata/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    val diskRepresentation = StringBuilder()
    var firstEmptySpaceIndex = 999999999
    var lastDiskSpaceIndex = 0

    bufferedReader.useLines { lines ->
        lines.forEach { line ->
            // Assume the ID's cycle from 0-9.
            // Only one line in the program but 20k letters

            line.forEachIndexed { index, spaceIndicatorNumber ->
                repeat(spaceIndicatorNumber.digitToInt()) {
                    // Every other indicatorNumber indicates disk space. Index / 2 is used for incrementing id's
                    if (index % 2 == 0) {
                        diskRepresentation.append(((index / 2) % 10).toString()) // Repeat ID's from 0 to 9
                    } else {
                        diskRepresentation.append('.') // Repeat ID's from 0 to 9

                        // When first empty space is added, save its index for later usage
                        if (firstEmptySpaceIndex == 999999999) {
                            firstEmptySpaceIndex = diskRepresentation.length - 1
                        }
                    }
                }
            }
        }
    }

    // Setting last disk space index is easier outside of above loop
    var i = diskRepresentation.length - 1
    while(lastDiskSpaceIndex == 0) {
        if (diskRepresentation[i] == '.') {
            i--
        } else {
            lastDiskSpaceIndex = i
        }
    }

    // Move last disk space id's to first empty space in the diskRepresentation until all possible digits have been moved
    while (firstEmptySpaceIndex < lastDiskSpaceIndex) {
        diskRepresentation[firstEmptySpaceIndex] = diskRepresentation[lastDiskSpaceIndex]
        diskRepresentation[lastDiskSpaceIndex] = '.'

        // Find next first empty space index, skip all others
        while (diskRepresentation[firstEmptySpaceIndex] != '.') {
            firstEmptySpaceIndex++
        }

        // Find new last digit, skip all empty disk spaces
        while (diskRepresentation[lastDiskSpaceIndex] == '.') {
            lastDiskSpaceIndex--
        }
    }

    var sumOfIdsMultipliedByIndex = 0

    // Make final calculations, break as soon as first empty space is encountered (no more trailing file digits)
    for ((index, char) in diskRepresentation.withIndex()) {
        if (char == '.') {
            break
        } else {
            sumOfIdsMultipliedByIndex+= (index * char.digitToInt())
        }
    }

    // TODO: Too low result on real input data. Could it be the id's looping? Try a solution where the ID's to beyond 9
    println(sumOfIdsMultipliedByIndex)
}
