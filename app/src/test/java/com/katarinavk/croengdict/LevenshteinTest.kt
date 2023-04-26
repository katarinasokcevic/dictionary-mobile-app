package net.katarinavk.croengdict

import org.junit.Assert.assertEquals
import org.junit.Test

class LevenshteinTest {

    @Test
    fun compute_example() {
        val ld = Levenshtein()
        assertEquals(2, ld.compute("hallowin", "halloween"))
    }
}