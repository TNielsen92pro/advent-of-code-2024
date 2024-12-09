package day6

// IMPROTANT:
// This code takes a couple of seconds to run because of heavy simulations. Don't be impatient.

enum class Direction {
    NORTH, SOUTH, EAST, WEST
}

val nextDirectionHashMap = hashMapOf<Direction, Direction>(
    Direction.NORTH to Direction.EAST,
    Direction.EAST to Direction.SOUTH,
    Direction.SOUTH to Direction.WEST,
    Direction.WEST to Direction.NORTH,
)

class Guard {
    var position: MutableMap<Char, Int> = mutableMapOf('x' to 0, 'y' to 0)
    var facingDirection: Direction = Direction.NORTH
    fun changeDirection() {
        facingDirection = nextDirectionHashMap[facingDirection]!!
    }
    fun getNextPosition(): MutableMap<Char, Int> {
        val currentPosition = position.toMutableMap()
        if (facingDirection == Direction.NORTH) {
            currentPosition['y'] = (currentPosition['y'] ?: 0) - 1
        }
        if (facingDirection == Direction.EAST) {
            currentPosition['x'] = (currentPosition['x'] ?: 0) + 1
        }
        if (facingDirection == Direction.SOUTH) {
            currentPosition['y'] = (currentPosition['y'] ?: 0) + 1
        }
        if (facingDirection == Direction.WEST) {
            currentPosition['x'] = (currentPosition['x'] ?: 0) - 1
        }
        return currentPosition
    }
    fun goToNextPosition() {
        if (facingDirection == Direction.NORTH) {
            position['y'] = (position['y'] ?: 0) - 1
        }
        if (facingDirection == Direction.EAST) {
            position['x'] = (position['x'] ?: 0) + 1
        }
        if (facingDirection == Direction.SOUTH) {
            position['y'] = (position['y'] ?: 0) + 1
        }
        if (facingDirection == Direction.WEST) {
            position['x'] = (position['x'] ?: 0) - 1
        }
    }
}

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/data/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    val inputMatrix = mutableListOf<String>()

    val guard = Guard()
    val guardStartPosition = mutableMapOf('x' to 0, 'y' to 0)

    bufferedReader.useLines { lines ->
        lines.forEachIndexed { yPosition, line ->
            // Load all the input into a matrix
            inputMatrix.add(line)

            if (line.contains("^")) {
                val guardXPosition = line.indexOf("^")

                guardStartPosition['x'] = guardXPosition
                guardStartPosition['y'] = yPosition

                // toMutableMap makes sure it copies by value
                guard.position = guardStartPosition.toMutableMap()
            }
        }
    }

    // For puzzle 2, also store the direction of each position visited
    val visitedPositions = mutableSetOf(mapOf('x' to guard.position['x'], 'y' to guard.position['y']))

    // While guard is within bounds
    while(
        guard.position['x']!! > 0 &&
        guard.position['x']!! < (inputMatrix[0].length - 1) &&
        guard.position['y']!! > 0 &&
        guard.position['y']!! < (inputMatrix.size - 1)
    ) {
        val nextPosition = guard.getNextPosition()
        if(inputMatrix[nextPosition['y']!!][nextPosition['x']!!] == '#') { // Weird that first coord is Y coord in inputMatrix
            guard.changeDirection()
        } else {
            guard.goToNextPosition()
            visitedPositions.add(nextPosition)
        }
    }

    println("Puzzle 1 number of visited positions: ${visitedPositions.size}")

    // Puzzle 2:
    // Loop through all the positions, and make a full simulation with an obstacle at each position.
    // In the simulations, also store the corresponding direction with each visitedPosition for the conditional check below.
    // Condition 1: If guard ends up in a visitedPosition with the same direction as once before, a loop has been found. Increment puzzle2 results and skip to next iteration.
    // Condition 2: If guard ends up outside bounds, move to next iteration.

    fun isLoop(testMatrix: List<String>): Boolean {
        val freshGuard = Guard()
        freshGuard.position = guardStartPosition.toMutableMap()

        // For puzzle 2, also store the direction of each position visited
        val visitedPositionsWithDirection = mutableSetOf(mapOf('x' to freshGuard.position['x'], 'y' to freshGuard.position['y'], 'd' to freshGuard.facingDirection))

        // While guard is within bounds
        while(
            freshGuard.position['x']!! > 0 &&
            freshGuard.position['x']!! < (testMatrix[0].length - 1) &&
            freshGuard.position['y']!! > 0 &&
            freshGuard.position['y']!! < (testMatrix.size - 1)
        ) {


            val nextPosition = freshGuard.getNextPosition()
            if(testMatrix[nextPosition['y']!!][nextPosition['x']!!] == '#') {
                freshGuard.changeDirection()
            } else {
                freshGuard.goToNextPosition()


                val currentPositionWithDirection = mapOf(
                    'x' to freshGuard.position['x'],
                    'y' to freshGuard.position['y'],
                    'd' to freshGuard.facingDirection
                )

                if(visitedPositionsWithDirection.contains(currentPositionWithDirection)) {
                    return true
                }

                visitedPositionsWithDirection.add(mapOf('x' to freshGuard.position['x'], 'y' to freshGuard.position['y'], 'd' to freshGuard.facingDirection))
            }
        }

        return false
    }

    var totalObstaclePlacesCreatingLoop = 0

    for(matrixYCoord in 0..inputMatrix.lastIndex) { // Matrix Y coordinate
        for(matrixXCoord in 0..<inputMatrix[0].length) { // Matrix Y coordinate
            // Only run simulation if not on an obstacle or where the guard starts
            if(inputMatrix[matrixYCoord][matrixXCoord] == '.') { // Again, weird mix-up of Y coordinate being first but that's okay
                // Copy current line and replace current coordinate with an obstacle
                val newLine = inputMatrix[matrixYCoord].substring(0, matrixXCoord) + '#' + inputMatrix[matrixYCoord].substring(matrixXCoord + 1)

                // Change line in inputmatrix copy to the new line with obstacle
                val newInputMatrix = inputMatrix.toMutableList()
                newInputMatrix[matrixYCoord] = newLine

                if(isLoop(newInputMatrix)) {
                    totalObstaclePlacesCreatingLoop++
                }
            }
        }
    }

    println("Puzzle 2 number of possible obstacle placements that creates a loop: $totalObstaclePlacesCreatingLoop")


}
