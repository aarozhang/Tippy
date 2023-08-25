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
import androidx.compose.ui.graphics.painter.Painter
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

    // Spacing constants
    val componentSpacing = 20.dp

    // start page UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.tippyBlue)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Start logo *****************************************************************************
        Row(modifier = Modifier.weight(3f)) {
            Image(
                painter = painterResource(R.drawable.tippywhite),
                contentDescription = "Tippy Logo",
            )
        }

        // Start body content *********************************************************************
        Row(
            modifier = Modifier
                .weight(5f)
                .background(
                    colorResource(id = R.color.white),
                    shape = RoundedCornerShape(topStart = 52.dp, topEnd = 52.dp)
                )
                .clip(shape = RoundedCornerShape(topStart = 52.dp, topEnd = 52.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 36.dp, end = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Start "Bill Amount Input" UI ***************************************************
                Row(modifier = Modifier.padding(start = 28.dp, end = 28.dp)) {
                    EditNumberField(
                        value = amountInput,
                        onValueChanged = {
                            if (it.length <= maxCharsForBillAmount) {
                                amountInput = it
                            }
                        },
                        modifier = Modifier
                            .weight(4f)
                            .padding(end = 4.dp),
                        label = stringResource(id = R.string.bill_amount)
                    )

                    // Start reset button
                    ElevatedButton(
                        onClick = {
                            amountInput = ""
                            tipPercentInput = 15f
                            billSplitInput = 1
                        },
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            top = 12.dp,
                            end = 12.dp,
                            bottom = 12.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, top = 12.dp, bottom = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.buttonColor)
                        )

                    ) {
                        Icon(
                            painterResource(id = R.drawable.baseline_delete_24),
                            contentDescription = "Reset Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            tint = colorResource(id = R.color.black)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(componentSpacing))

                // Start tip input UI *************************************************************
                Text(
                    text = "Tip: $tipPercent% | $tipValue",
                    fontSize = 20.sp
                )

                Slider(value = tipPercentInput,
                    onValueChange = { tipPercentInput = it },
                    valueRange = 15f..30f,
                    modifier = Modifier.padding(start = 28.dp, end = 28.dp),
                    steps = 15,
                    thumb = {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Heart Icon",
                            tint = colorResource(id = R.color.tippyBlue)
                        )
                    }
                )

                Spacer(modifier = Modifier.padding(componentSpacing))

                // Start bill split UI ************************************************************
                Text(
                    text = stringResource(id = R.string.split_bill)
                )

                Row(modifier = Modifier.padding(start = 28.dp, end = 28.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    IncrementButton(
                        onClick = {
                            if (billSplitInput > 1) billSplitInput -= 1
                        },
                        modifier = Modifier.weight(1f),
                        iconPainter = painterResource(id = R.drawable.baseline_remove_24),
                        contentDescription = "Minus Button"
                    )

                    Text(
                        "$billSplitInput",
                        modifier = Modifier.weight(2f),
                        textAlign = TextAlign.Center,
                        fontSize = 28.sp
                    )

                    IncrementButton(
                        onClick = { billSplitInput += 1 },
                        modifier = Modifier.weight(1f),
                        iconPainter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = "Add Button"
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.padding(componentSpacing))

                // Start "bill total" UI **********************************************************
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
                    // The specific sizing of 21.75 dp matches the Text composable the best.
                    // Helps avoid UI shifting.
                    Spacer(modifier = Modifier.padding(21.75.dp))
                }
            }
        }
    }
}

@Composable
fun IncrementButton(
    onClick: () -> Unit,
    modifier: Modifier,
    iconPainter: Painter,
    contentDescription: String
) {
    ElevatedButton(
        onClick = onClick,
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 12.dp,
            end = 12.dp,
            bottom = 12.dp
        ),
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.buttonColor)
        )
    ) {
        Icon(
            iconPainter,
            contentDescription = contentDescription,
            modifier = Modifier.size(ButtonDefaults.IconSize),
            tint = colorResource(id = R.color.black)
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
    OutlinedTextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = {
            Text(
                label,
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(16.dp)
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