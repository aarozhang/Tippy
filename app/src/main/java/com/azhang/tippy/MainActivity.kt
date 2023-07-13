package com.azhang.tippy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azhang.tippy.ui.theme.TippyTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TippyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TippyApp()
                }
            }
        }
    }
}

@Composable
fun TippyApp() {
    var amountInput by remember {
        mutableStateOf("")
    }

    var tipPercentInput by remember {
        mutableStateOf("")
    }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipPercentInput.toDoubleOrNull() ?: 0.0
    val total = calculateTotal(amount, tipPercent)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier
                .padding(bottom = 64.dp)
        )

        EditNumberField(
            value = amountInput,
            onValueChanged = { amountInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            label = stringResource(id = R.string.bill_amount)
        )

        EditNumberField(
            value = tipPercentInput,
            onValueChanged = { tipPercentInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            label = stringResource(id = R.string.tip_percent)
        )

        Text(
            text = stringResource(R.string.total_amount, total),
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.align(Alignment.Start)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNumberField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier,
    label: String
) {
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = {
            Text(
                label
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

/**
 * Calculates the bill total based on bill amount and decided tip percent.
 * Accounts for local currency.
 */
private fun calculateTotal(billAmount: Double, tipPercent: Double = 15.0): String {
    val total = tipPercent / 100 * billAmount + billAmount
    return NumberFormat.getCurrencyInstance().format(total)
}

@Preview(showBackground = true)
@Composable
fun TippyAppPreview() {
    TippyTheme {
        TippyApp()
    }
}