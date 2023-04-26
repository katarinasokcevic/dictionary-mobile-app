package net.katarinavk.croengdict

const val INSERT_COST = 1
const val DELETE_COST = 1
const val REPLACE_COST = 1
const val SWAP_COST = 1
const val MAX_COST = 1000

class Levenshtein {
    fun compute(source: String, target: String): Int {
        if (source.isEmpty()) {
            return target.length * INSERT_COST
        }
        if (target.isEmpty()) {
            return source.length * DELETE_COST
        }
        if (source == target) {
            return 0
        }

        if (source.length < 2 || target.length < 2 || source[0] != target[0] || source[1] != target[1]) {
            // optimization
            return MAX_COST
        }

        val table = Array(source.length) {IntArray(target.length)}
        val index = HashMap<Char, Int>()
        index[source[0]] = 0
        for (i in 1 until source.length) {
            table[i][0] = i
        }

        for (i in 1 until target.length) {
            table[0][i] = i
        }
        for (i in 1 until source.length) {
            var matchIndex = if (source[i] == target[0]) 0 else -1
            for (j in 1 until target.length) {
                val candidateSwapIndex = index[target[j]]
                val swap = matchIndex
                val deleteDistance = table[i-1][j] + DELETE_COST
                val insertDistance = table[i][j-1] + INSERT_COST
                var matchInDistance = table[i-1][j-1]
                if (source[i] != target[j]) {
                    matchInDistance += REPLACE_COST
                } else {
                    matchIndex = j
                }
                var swapDistance = MAX_COST
                if (candidateSwapIndex != null && swap != -1) {
                    swapDistance = 0
                    if (candidateSwapIndex > 0 || swap > 0) {
                        swapDistance = table[(candidateSwapIndex - 1).coerceAtLeast(0)][(swap - 1).coerceAtLeast(0)]
                    }
                    swapDistance += (i - candidateSwapIndex - 1) * DELETE_COST
                    swapDistance += (j - swap - 1) * INSERT_COST + SWAP_COST
                }
                table[i][j] = intArrayOf(deleteDistance, insertDistance, matchInDistance, swapDistance).minOrNull()!!
            }
            index[source[i]] = i
        }
        return table.last().last()
    }
}