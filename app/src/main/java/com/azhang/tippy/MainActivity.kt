package com.azhang.tippy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.azhang.tippy.models.SplashViewModel
import com.azhang.tippy.ui.theme.TippyTheme
import java.text.NumberFormat
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }

        setContent {
            TippyTheme() {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TippyApp() {
    // stores string user input values
    var amountInput by remember {
        mutableStateOf("")
    }
    var tipPercentInput by remember {
        mutableStateOf(15F)
    }
    var billSplitInput by remember {
        mutableStateOf(1)
    }

    // Char limits for text inputs
    val maxCharsForBillAmount = 7

    // stores casted numeric user input values
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipPercentInput.roundToInt()
    val numberOfPeople = billSplitInput.toDouble()

    // stores calculated values
    val total = calculateTotal(amount, tipPercent)
    val tipValue = calculateTip(amount, tipPercent)
    val costPerPerson = calculateBillSplit(amount, tipPercent, numberOfPeople)

    // start page UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.tippyBlue)),
//            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.weight(3f)) {
            Image(
                painter = painterResource(R.drawable.tippywhite),
                contentDescription = null,
            )
        }

        Row(
            modifier = Modifier
                .weight(5f)
                .background(
                    colorResource(id = R.color.offWhite),
                    shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)
                )
                .clip(shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 36.dp, end = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {



                EditNumberField(
                    value = amountInput,
                    onValueChanged = {
                        if (it.length <= maxCharsForBillAmount) {
                            amountInput = it
                        }
                    },
                    modifier = Modifier
                        .padding(bottom = 28.dp, start = 28.dp, end = 28.dp),
                    label = stringResource(id = R.string.bill_amount)
                )

                // Start tip input UI
                Text(
                    text = "Tip: $tipPercent% | $tipValue",
                    modifier = Modifier
                )

                Slider(value = tipPercentInput,
                    onValueChange = { tipPercentInput = it },
                    valueRange = 15f..30f,
                    modifier = Modifier.padding(bottom = 28.dp, start = 28.dp, end = 28.dp),
                    steps = 4,
                    thumb = {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = colorResource(id = R.color.tippyBlue)
                        )
                    }
                )

                // Start bill split UI
                Text(
                    text = stringResource(id = R.string.split_bill),
                    modifier = Modifier
                )
                Row(modifier = Modifier.padding(start = 28.dp, end = 28.dp, bottom = 28.dp)) {
                    ElevatedButton(
                        onClick = {
                            if (billSplitInput > 1) billSplitInput -= 1
                        },
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            top = 12.dp,
                            end = 12.dp,
                            bottom = 12.dp
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.baseline_remove_24),
                            contentDescription = "Remove",
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            tint = colorResource(id = R.color.tippyBlue)
                        )
                    }

                    Text(
                        "$billSplitInput",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 28.sp
                    )

                    ElevatedButton(
                        onClick = { billSplitInput += 1 },
                        // Uses ButtonDefaults.ContentPadding by default
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            top = 12.dp,
                            end = 12.dp,
                            bottom = 12.dp
                        ),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            tint = colorResource(id = R.color.tippyBlue)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(16.dp))


                CalculatedValueText(
                    R.string.total_amount,
                    total
                )

                if (billSplitInput > 1) {
                    CalculatedValueText(
                        R.string.cost_per_person,
                        costPerPerson
                    )
                } else {
                    // The specific sizing of 21.75 dp causing the least amount of UI movement when
                    // swapping to the Text composable
                    Spacer(modifier = Modifier.padding(21.75.dp))
                }
            }
        }

//        Row(
//            modifier = Modifier
//                .weight(1f)
//                .background(colorResource(id = R.color.offWhite))
//        ) {
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                CalculatedValueText(
//                    R.string.total_amount,
//                    total
//                )
//
//                if (billSplitInput > 1) {
//                    CalculatedValueText(
//                        R.string.cost_per_person,
//                        costPerPerson
//                    )
//                }
//            }
//        }
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
                textAlign = TextAlign.Center,
//                fontFamily = FontFamily(Font(R.font.roboto_black)),
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        //shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun CalculatedValueText(
    stringId: Int,
    value: String
) {
    Text(
        text = stringResource(stringId, value),
        fontSize = 32.sp,
        fontFamily = FontFamily(Font(R.font.roboto_black)),
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
    )
}

/**
 * Calculates the tip value.
 * Accounts for local currency.
 */
private fun calculateTip(billAmount: Double, tipPercent: Int = 15): String {
    val tip = tipPercent.toDouble() / 100 * billAmount
    return NumberFormat.getCurrencyInstance().format(tip)
}

/**
 * Calculates the bill total.
 * Accounts for local currency.
 */
private fun calculateTotal(billAmount: Double, tipPercent: Int = 15): String {
    val total = tipPercent.toDouble() / 100 * billAmount + billAmount
    return NumberFormat.getCurrencyInstance().format(total)
}

/**
 * Splits the bill.
 * Accounts for local currency.
 */
private fun calculateBillSplit(
    billAmount: Double,
    tipPercent: Int = 15,
    numberOfPeople: Double = 1.0
): String {
    val costPerPerson = (tipPercent.toDouble() / 100 * billAmount + billAmount) / numberOfPeople
    return NumberFormat.getCurrencyInstance().format(costPerPerson)
}

@Preview(showBackground = true)
@Composable
fun TippyAppPreview() {
    TippyTheme {
        TippyApp()
    }
}