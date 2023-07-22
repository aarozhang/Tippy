package com.azhang.tippy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.azhang.tippy.models.SplashViewModel
import com.azhang.tippy.ui.theme.TippyTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }

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

    val maxCharsForBillAmount = 7
    val maxCharsForTipPercent = 5
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipPercentInput.toDoubleOrNull() ?: 0.0
    val total = calculateTotal(amount, tipPercent)
    val tipValue = calculateTip(amount, tipPercent)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.tippyblack),
            contentDescription = null,
            modifier = Modifier.padding(top = 32.dp)
        )

        EditNumberField(
            value = amountInput,
            onValueChanged = {
                if (it.length <= maxCharsForBillAmount) {
                    amountInput = it
                }
            },
            modifier = Modifier
                .padding(bottom = 32.dp, start = 28.dp, end = 28.dp)
                .fillMaxWidth(),
            label = stringResource(id = R.string.bill_amount)
        )

        EditNumberField(
            value = tipPercentInput,
            onValueChanged = {
                if (it.length <= maxCharsForTipPercent) {
                    tipPercentInput = it
                }
            },
            modifier = Modifier
                .padding(bottom = 32.dp, start = 28.dp, end = 28.dp)
                .fillMaxWidth(),
            label = stringResource(id = R.string.tip_percent)
        )

        calculatedValueText(
            R.string.tip_value,
            tipValue,
            Modifier
                .align(Alignment.Start)
                .padding(start = 28.dp)
        )

        calculatedValueText(
            R.string.total_amount,
            total,
            Modifier
                .align(Alignment.Start)
                .padding(start = 28.dp)
        )

        Spacer(modifier = Modifier.padding(36.dp))

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
    OutlinedTextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = {
            Text(
                label,
                fontFamily = FontFamily(Font(R.font.nunito))
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun calculatedValueText(
    stringId: Int,
    value: String,
    modifier: Modifier
) {
    Text(
        text = stringResource(stringId, value),
        fontSize = 28.sp,
        modifier = modifier,
        fontFamily = FontFamily(Font(R.font.nunito)),
        softWrap = false,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * Calculates the bill total.
 * Accounts for local currency.
 */
private fun calculateTotal(billAmount: Double, tipPercent: Double = 15.0): String {
    val total = tipPercent / 100 * billAmount + billAmount
    return NumberFormat.getCurrencyInstance().format(total)
}

/**
 * Calculates the tip based on bill amount and tip percent.
 * Accounts for local currency.
 */
private fun calculateTip(billAmount: Double, tipPercent: Double = 15.0): String {
    val tip = tipPercent / 100 * billAmount
    return NumberFormat.getCurrencyInstance().format(tip)
}

@Preview(showBackground = true)
@Composable
fun TippyAppPreview() {
    TippyTheme {
        TippyApp()
    }
}