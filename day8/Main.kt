package day8

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    // Stores antenna positions as {antennaType1 -> [(xPos1, yPos1), (xPos2, yPos2), ...], antennaType2 -> [...]...}
    val antennaPositions = mutableMapOf<Char, MutableList<Pair<Int, Int>>>()

    // The anti thingies stored in a set to ensure uniqueness
    val antiThingsFirstPuzzle = mutableSetOf<Pair<Int, Int>>()
    val antiThingsSecondPuzzle = mutableSetOf<Pair<Int, Int>>()

    var xMax = 0
    var yMax = 0

    bufferedReader.useLines { lines ->
        lines.forEachIndexed { yPosition, line ->

            xMax = line.length
            yMax++

            for (i in line.indices) {
                if(line[i] != '.') {
                    // Add coordinates of this antenna to the list of coordinates for this antenna type. xPos is i, yPos is yPosition
                    antennaPositions.getOrPut(line[i]) { mutableListOf<Pair<Int, Int>>() }.add(Pair(i, yPosition))
                }
            }



            // Idea:
            // Lookup each antenna. If any other antennas of the same type is found, jump past that antenna as long as the distance to that antenna, and add an antinode.
            // This way all possible nodes should be found, and no duplicates (considering one single antenna type).
            // For other antenna types, same concept but make sure not to add an antinode that has already been added (Just make a Set of it).
            // Remember that antinodes can appear on top of antennas.
        }
    }

    for (key in antennaPositions.keys) {
        val positions = antennaPositions[key]!! // List of positions for this key

        for (position in positions) {
            // For each antenna (position), send ray through all other antennas of this type (positions)
            for (targetPosition in positions) {
                if (targetPosition != position) { // Don't compare the same antenna to itself
                    // Calculate the x/y coord difference between the two antennas
                    val distanceVector = Pair(targetPosition.first - position.first, targetPosition.second - position.second)
                    val antiThingPlacement = Pair(position.first + (distanceVector.first * 2), (position.second + (distanceVector.second * 2)))

                    // When there is more than 1 antenna of a type, there will always be an antiThing on their positions
                    antiThingsSecondPuzzle.add(position)
                    antiThingsSecondPuzzle.add(targetPosition)

                    var iterativeAntiThingPlacement = Pair<Int, Int>(antiThingPlacement.first, antiThingPlacement.second)

                    while ((iterativeAntiThingPlacement.first + (distanceVector.first) in 0..<xMax) && (iterativeAntiThingPlacement.second + (distanceVector.second) in 0..<yMax)) {
                        iterativeAntiThingPlacement = Pair<Int, Int>(iterativeAntiThingPlacement.first + (distanceVector.first), iterativeAntiThingPlacement.second + (distanceVector.second))
                        antiThingsSecondPuzzle.add(iterativeAntiThingPlacement)
                    }

                    // Check if antiThing is within bounds
                    if ((antiThingPlacement.first in 0..<xMax) && (antiThingPlacement.second in 0..<yMax)) {
                        antiThingsFirstPuzzle.add(antiThingPlacement)
                        antiThingsSecondPuzzle.add(antiThingPlacement)
                    }
                }
            }
        }
    }

    println("antiThings first puzzle: ${antiThingsFirstPuzzle.size}")
    println("antiThings second puzzle: ${antiThingsSecondPuzzle.size}")
}
