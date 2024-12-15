package day9

// Only one line in the program but 20k chars

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    val diskRepresentation = mutableListOf<Int?>()
    var firstEmptySpaceIndex = 999999999
    var lastDiskSpaceIndex = 0


    // Map of {numberOfEmptySpaces -> [orderedStartIndex1, orderedStartIndex1, +...]} for quick lookups
    val emptySpaceMapper = mutableMapOf<Int, MutableList<Int>>()

    bufferedReader.useLines { lines ->
        lines.forEach { line ->

            var currentEmptySpaceCounter = 0

            line.forEachIndexed { index, spaceIndicatorNumber ->
                repeat(spaceIndicatorNumber.digitToInt()) { // Do not enter loop if digit is 0
                    // Every other indicatorNumber indicates disk space. Index / 2 is used for incrementing id's
                    if (index % 2 == 0) {
                        // If we are coming from empty space, that space has been counted. Add to emptySpaceHash
                        if (currentEmptySpaceCounter != 0) {
                            val indexList = emptySpaceMapper.getOrPut(currentEmptySpaceCounter) { mutableListOf() }
                            indexList.add(diskRepresentation.size - currentEmptySpaceCounter) // Add the index where the empty space begins
                            currentEmptySpaceCounter = 0 // Reset empty space counter
                        }

                        diskRepresentation.add(index / 2)
                    } else {
                        diskRepresentation.add(null)

                        // When first empty space is added, save its index for later usage
                        if (firstEmptySpaceIndex == 999999999) {
                            firstEmptySpaceIndex = diskRepresentation.size - 1
                        }

                        currentEmptySpaceCounter += 1
                        // Increment the number of empty spaces on this part of disk
                    }
                }
            }

            if (currentEmptySpaceCounter != 0) {
                val indexList = emptySpaceMapper.getOrPut(currentEmptySpaceCounter) { mutableListOf() }
                indexList.add(diskRepresentation.size - currentEmptySpaceCounter) // Add the index where the empty space begins
            }

        }
    }

    // Setting last disk space index is easier outside of above loop
    var i = diskRepresentation.size - 1
    while(lastDiskSpaceIndex == 0) {
        if (diskRepresentation[i] == null) {
            i--
        } else {
            lastDiskSpaceIndex = i
        }
    }

    // PUZZLE 1

    val firstPuzzleDiskRepresentation = diskRepresentation.toMutableList()

    // Move last disk space id's to first empty space in the diskRepresentation until all possible digits have been moved
    while (firstEmptySpaceIndex < lastDiskSpaceIndex) {
        firstPuzzleDiskRepresentation[firstEmptySpaceIndex] = firstPuzzleDiskRepresentation[lastDiskSpaceIndex]
        firstPuzzleDiskRepresentation[lastDiskSpaceIndex] = null

        // Find next first empty space index, skip all others
        while (firstPuzzleDiskRepresentation[firstEmptySpaceIndex] != null) {
            firstEmptySpaceIndex++
        }

        // Find new last digit, skip all empty disk spaces
        while (firstPuzzleDiskRepresentation[lastDiskSpaceIndex] == null) {
            lastDiskSpaceIndex--
        }
    }

    var sumOfIdsMultipliedByIndex: Long = 0

    // Make final calculations, break as soon as first empty space is encountered (no more trailing file digits)
    for ((index, id) in firstPuzzleDiskRepresentation.withIndex()) {
        if (id == null) {
            break
        } else {
            sumOfIdsMultipliedByIndex+= (index * id)
        }
    }

    // Empty up some space
    firstPuzzleDiskRepresentation.clear()

    // PUZZLE 2

    var currentFileSizeCounter = 0

    var currentFileId: Int? = diskRepresentation.last() // Keeps track of current file ID for when we switch files without empty space inbetween

    // Loop through disk representation reversely to start finding files to move
    for ((index, id) in diskRepresentation.asReversed().withIndex()) {
        val realIndex = diskRepresentation.size - 1 - index // Even through file is reversed, index starts at 0. This makes index start from the end

        // Not counting first digit, but this has no effect on final result since first file is never moved
        if (id != null && currentFileId == id) { // Keep counting file size until encountering empty space or a new file
            currentFileSizeCounter += 1 // Count file size when file is being iterated
        } else {
            if (currentFileSizeCounter != 0) { // If fileSizeCounter is not empty, we just came from a file
                val currentFileStartIndex = realIndex + 1 // Current file starts on the index right next to this empty space

                val fileIdToMove = diskRepresentation[currentFileStartIndex]

                var smallestIndexOfAllSpacesBigEnough: Int? = null
                var sizeOfEarliestEmptySpace = 0

                // Go through all possible file sizes and get the smallest index
                for(emptyFileSize in currentFileSizeCounter..emptySpaceMapper.keys.max()) { // Search through empty spaces big enough to fit file

                    val currentFileSizeIndexList = emptySpaceMapper[emptyFileSize]

                    // If there are empty spaces of this size
                    if (currentFileSizeIndexList != null && currentFileSizeIndexList.size > 0) {
                        val matchingEmptySpaceStartIndex = currentFileSizeIndexList[0]

                        // Find the earliest index of all possible empty spaces
                        if (smallestIndexOfAllSpacesBigEnough == null || (matchingEmptySpaceStartIndex < smallestIndexOfAllSpacesBigEnough)) {
                            smallestIndexOfAllSpacesBigEnough = matchingEmptySpaceStartIndex
                            sizeOfEarliestEmptySpace = emptyFileSize
                        }
                    }
                }

                if (smallestIndexOfAllSpacesBigEnough != null) {
                    // Only move file if free space exists to the left of file
                    if (smallestIndexOfAllSpacesBigEnough <= currentFileStartIndex) {
                        // Match found!
                        // (Earliest possible empty space of proper size and starting to the left of current file)

                        for (writeIndexCounter in 0..<currentFileSizeCounter) {
                            diskRepresentation[smallestIndexOfAllSpacesBigEnough + writeIndexCounter] = fileIdToMove // Writing file
                            diskRepresentation[currentFileStartIndex + writeIndexCounter] = null // Erasing moved file
                        }

                        val remainingEmptySpace = sizeOfEarliestEmptySpace - currentFileSizeCounter

                        val startIndexForRemainingEmptySpace = smallestIndexOfAllSpacesBigEnough + currentFileSizeCounter

                        // Loop through list for remainingEmptySpace from emptySpaceMap and add remainingEmptySpace in proper position
                        val remainingEmptySpaceList = emptySpaceMapper.getOrPut(remainingEmptySpace) { mutableListOf() }
                        if (remainingEmptySpaceList.size == 0) {
                            remainingEmptySpaceList.add(startIndexForRemainingEmptySpace)
                        } else {
                            for ((localIndex, startIndexOfEmptySpace) in remainingEmptySpaceList.withIndex()) {
                                if (startIndexOfEmptySpace > startIndexForRemainingEmptySpace) { // If next empty space in ordered index list is placed ahead of this empty space, insert here (Keeps list sorted)
                                    remainingEmptySpaceList.add(localIndex, startIndexForRemainingEmptySpace)
                                    break
                                }

                                if (localIndex == remainingEmptySpaceList.size - 1) { // If no empty space ahead of the remaining space, place ahead of all other empty spaces of this size
                                    remainingEmptySpaceList.add(startIndexForRemainingEmptySpace)
                                    break // Need break here since it adds an index to the list and therefore increasing the list size so that the loop keeps on going
                                }
                            }
                        }

                        // Remove old space indication used in hash
                        emptySpaceMapper[sizeOfEarliestEmptySpace]?.removeAt(0)
                    } else {
                        // If the smallest index of this free space size is not to the left of file, no occurrence of the free space size will be. Clear the whole list.
                        emptySpaceMapper[sizeOfEarliestEmptySpace]?.clear()
                    }
                }

                currentFileSizeCounter = 0
            }

            // Need to fetch current id explicitly since id parameter may be overwritten if match was found above (in edge cases where file moves exactly one step to the left of its original position)
            val currentId = diskRepresentation[realIndex]

            currentFileId = currentId

            if (currentId != null) {
                currentFileSizeCounter += 1
            }
        }
    }

    var secondPuzzleSum: Long = 0

    // Make final calculations for puzzle 2. Do not break since we may have trailing big files that didn't find any free space
    for ((index, id) in diskRepresentation.withIndex()) {
        if (id != null) {
            val nextProductToAdd: Long = index.toLong() * id.toLong()
            secondPuzzleSum+= nextProductToAdd
        }
    }

    println("Second puzzle final sum: $secondPuzzleSum")
}
