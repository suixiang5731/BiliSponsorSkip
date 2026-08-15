package com.retrsoft.bilisponsorskip

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TitleSetTextArgumentsTest {
    @Test
    fun acceptsSingleTextArgument() {
        assertEquals("title", firstTextArgument(arrayOf("title")))
    }

    @Test
    fun ignoresMissingAndNonTextArguments() {
        assertNull(firstTextArgument(emptyArray()))
        assertNull(firstTextArgument(arrayOf(42)))
    }
}
