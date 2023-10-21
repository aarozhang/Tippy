package com.azhang.tippy

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.NumberFormat

class TippyTests {
    @Test
    fun calculateTip_isCorrect() {
        val billAmount = 100.00
        val tipPercent = 15
        val expected = NumberFormat.getCurrencyInstance().format(15)
        val returned =
            NumberFormat.getCurrencyInstance().format(calculateTip(billAmount, tipPercent))
        assertEquals(expected, returned)
    }

    @Test
    fun calculateTotal_isCorrect() {
        val billAmount = 100.00
        val tipPercent = 15
        val expected = NumberFormat.getCurrencyInstance().format(115)
        val returned = NumberFormat.getCurrencyInstance()
            .format(calculateTotal(billAmount, tipPercent))
        assertEquals(expected, returned)
    }

    @Test
    fun calculateBillSplit_isCorrect() {
        val billAmount = 100.00
        val tipPercent = 10
        val numberOfPeople = 2.0
        val expected = NumberFormat.getCurrencyInstance().format(55)
        val returned = NumberFormat.getCurrencyInstance()
            .format(calculateBillSplit(billAmount, tipPercent, numberOfPeople))
        assertEquals(expected, returned)
    }
}