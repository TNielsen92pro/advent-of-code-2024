package day11

fun main() {
    val resource = object {}.javaClass.getResource("./puzzle1/exampledata/input") // Data path to input data
        ?: error("File not found")

    val bufferedReader = resource.openStream().bufferedReader()

    // TODO: FUCK. Det är binärträd som gäller eller hur?
    // TODO: Noder är stenar. Löv är nästa stenar.
    // TODO: Varje nod innehåller hur många löv / subnoder den har.
    // TODO: Varje gång an lägger till en nod går man upp i träder och incrementar alla parent nodes.
    // TODO: Tree height är antalet blinkningar som gjorts. Hur store:as detta?
    // TODO: Hur hittar man snabbt svaret på antal noder x antal blinkningar nedåt i trädet, från godtycklig nod?
    // TODO: Detta tar endast tree-height i tidskomplexitet eftersom det inte branchas ut när man går uppåt. Borde va feasible.
    // TODO: Ingen nod kan förekomma 2 gånger utan då skickas man till den existerande noden (id hantering?)
    // TODO: Måste ha en sorterad lista över alla existerande noder och vilka de pekar på för att snabbt kolla upp om den redan finns. (Denna lista representerar hela trädet?)
    // TODO: Varje nod bör ha stoneNumber som huvud-id för snabb lookup, och innehålla Left, Right, Parent, TotalChildren, ChildrenHeight. Typ?

    /*
    * Puzzle 2 thoughts:
    * Store each iteration and how many stones they return in memory, and use that to lookup resulting stones instead of searching recursively.
    * If not found in lookup, runt that stone through recursive checks and add to lookup table until it is found.
    * Finding a stone in the lookup table should immediately stop the search and return that number.
    *
    * We need to store initialStoneNumber, iterations, resultingStoneNumber in the table. We should always look to find the matching
    * initialStoneNumber with the MOST amount of iterations, and skip all those iterations immediately.
    * In other words, for each recursive call, store every step and the number of iterations from all previous steps somehow (if iterations > 1 probably) in the table
    * Until a match is found or max number of blinks reached
    *
    * Maybe this can be optimized by using ID's for each recursive search, so we don't have to keep sending all new stones down the line.
    * E.g. store the ID in a lookup table and for each recursive call with that ID, add an entry with +1 iterations from the last one and new result.
    *
    * If we stop the search as soon as we find a match we will not have duplicates, so memory should not be an issue
    *
    * For each recursive "thread":
    * Use a stoneNumberToResult iteration map for this particular thread (represents a thread id).
    * For each iteration, store current stone number with iteration 0 and increase all other stone number iterations by 0 and add the current result.
    * Send all these into the main lookup table before next iteration.
    * As soon as a lookup result is found, Exit recursive check and destroy this threads iteration map as they are all now in the lookup table.
    *
    * */

    // TODO: Keep the iterations sorted by key for quicker lookup
    // TODO: Need to store several resultingStoneNumbers since the result will contain more stones than original
    // TODO: Just make resultStoneNum an array of strings? Should be enough
    // Map of {stoneNum -> (iterations -> [...resultingStoneNumbers...])}
    val stoneLookupTable: MutableMap<String, MutableMap<Int, MutableList<String>>> = mutableMapOf()

    fun blinkRecursively(stone: String, blinks: Int, maxBlinks: Int, buffer: MutableMap<String, MutableMap<String, Any>>): Long {
        if(blinks == maxBlinks) {
            return 1L // Reached max blinks, this stone is in the final result
        }

        var strippedStone = stone

        // Strip leading 0's from stone
        while (strippedStone.length > 1 && strippedStone[0] == '0') {
            strippedStone = strippedStone.substring(1, strippedStone.length)
        }

        // Check if stone result is in lookup table. If found, lower iterations by max amount
        val stoneLookup = stoneLookupTable[strippedStone]
        if (stoneLookup != null) {
            // Stone found. Look for max iterations match.

            val blinksLeft = maxBlinks - blinks
            val lookedUpIterations = stoneLookup.keys // Always sorted in ascending order
            for (iterations in lookedUpIterations.reversed()) { // Reverse iteration to find max iterations equal to or lower than remaining blinks
                if (iterations == blinksLeft) {
                    // Found final result, return number of resulting stones
                    return stoneLookup[iterations]?.size?.toLong() ?: 0L // Logically, list size must be > 0, but compiler forces a nullcheck here
                } else if (iterations < blinksLeft) {
                    // TODO: If not finishing blinks here, add the new skipped iteration to buffer so that it is further accounted for in coming iterations
                    // Skip the number of iterations found and keep looking recursively from there
                    var resultingStones: Long = 0

                    // TODO: Probably start a new buffer for each recursive call here?

                    // Add current found stone and iterations to table so that it is kept updated by subsequent local iterations
                    buffer[strippedStone] = mutableMapOf(
                        "iterations" to iterations,
                        "result" to stoneLookup[iterations]!! // Resulting stones array
                    )

                    for (newStone in stoneLookup[iterations]!!) {
                        resultingStones+= blinkRecursively(newStone, blinks + iterations, maxBlinks, buffer)
                    }

                    return resultingStones
                }
            }
        } else {
            // No stone match is found. Add current iterations to local buffer and copy buffer to lookupTable
            // TODO: Use TreeMap when adding to lookup table to make sure it is always sorted

        }

        // If no appropriate stone iterations found in lookup table, keep recursive stone iteration normally

        // Rule logic and recursive invocation
        if (strippedStone.toLong() == 0L) { // Rule 1, if stone is 0, becomes 1
            return blinkRecursively("1", blinks + 1, maxBlinks, buffer)
        }
        if (strippedStone.length % 2 == 0) { // Rule 2, if an even number of digits, split into two stones
            val newLeftStone = strippedStone.substring(0, strippedStone.length/2)
            val newRightStone = strippedStone.substring(strippedStone.length/2, strippedStone.length)

            // TODO: Increasing buffer iterations for each call will NOT WORK since we branch out to different calls here

            // Return recursive blink on the two new stones
            return blinkRecursively(newLeftStone, blinks + 1, maxBlinks, buffer) + blinkRecursively(newRightStone, blinks + 1, maxBlinks, buffer)
        }
        // Final rule if previous rules don't apply, multiply stone number by 2024
        val multipliedStone = (strippedStone.toLong() * 2024).toString()
        return blinkRecursively(multipliedStone, blinks + 1, maxBlinks, buffer)
    }

    var totalNumberOfStonesFirstPuzzle: Long = 0
    var totalNumberOfStonesSecondPuzzle: Long = 0

    bufferedReader.useLines { lines ->
        lines.forEach { line ->
            val stones = line.split(" ")

            stones.forEach { stone ->
                // {stoneNum -> {"iterations" -> iterationNum, "result" -> [...resultingStoneNumbers...]}}
                val iterationsBufferFirstPuzzle: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()
                // TODO: Map here that you can iterate over in the recursive stone run
                totalNumberOfStonesFirstPuzzle += blinkRecursively(stone, 0, 25, iterationsBufferFirstPuzzle)
                val iterationsBufferSecondPuzzle: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()
                // totalNumberOfStonesSecondPuzzle += blinkRecursively(stone, 0, 75, iterationsBufferSecondPuzzle)
            }
        }
    }

    println("Total number of stones first puzzle: $totalNumberOfStonesFirstPuzzle")
    println("Total number of stones second puzzle: $totalNumberOfStonesSecondPuzzle")
}
