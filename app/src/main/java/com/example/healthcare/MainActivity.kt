package com.example.healthcare

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_xml)

        val ageInput: EditText = findViewById(R.id.age)
        val weightInput: EditText = findViewById(R.id.weight)
        val heightInput: EditText = findViewById(R.id.height)
        val radio: RadioGroup = findViewById(R.id.radioGroup)
        val view: TextView = findViewById(R.id.view)
        val calculate: Button = findViewById(R.id.calculate)
        var gender: String = ""

        radio.setOnCheckedChangeListener { _, i ->
            gender = findViewById<RadioButton>(i).text.toString()
        }

        calculate.setOnClickListener {
            val age = ageInput.text.toString()
            val weight = weightInput.text.toString()
            val height = heightInput.text.toString()

            if (age.isNotEmpty() && weight.isNotEmpty() && height.isNotEmpty() && gender.isNotEmpty()) {
                val bmiCal = CalculateBMI(gender, age.toInt(), height.toDouble(), weight.toDouble())
                bmiCal.getStatus()

                view.text = "${bmiCal.status} (BMI: ${bmiCal.bmi.roundToInt()})"
                view.setTextColor(bmiCal.color)
            } else {
                view.text = Status.MISSING_DATA.text
                view.setTextColor(Status.MISSING_DATA.color)
            }
        }
    }
}

class CalculateBMI(private val gender: String, private val age: Int, height: Double, weight: Double) {
    var color: Int = Color.BLACK
    val bmi: Double = calculateBMI(height, weight)
    var status: String = Status.MISSING_DATA.text

    private fun calculateBMI(height: Double, weight: Double): Double {
        val normalizedHeight = if (height > 3) height / 100 else height
        return weight / (normalizedHeight * normalizedHeight)
    }

    fun getStatus() {
        val message = when {
            (age > 17 && this.bmi < 18.5) -> Status.UNDERWEIGHT
            age > 17 && bmi in 18.5..24.9 -> Status.NORMAL
            age > 17 && bmi >= 25.0 -> Status.OVERWEIGHT
            // Child classification (age ≤ 17)
            age <= 17 -> when (gender) {
                "Male" -> when {
                    bmi < 17.0 -> Status.UNDERWEIGHT
                    bmi in 17.0..22.9 -> Status.NORMAL
                    bmi >= 23.0 -> Status.OVERWEIGHT
                    else -> Status.NORMAL
                }

                "Female" -> when {
                    bmi < 16.5 -> Status.UNDERWEIGHT
                    bmi in 16.5..21.9 -> Status.NORMAL
                    bmi >= 22.0 -> Status.OVERWEIGHT
                    else -> Status.NORMAL
                }
                else -> Status.NORMAL
            }
            else -> Status.MISSING_DATA
        }
        this.status = message.text
        this.color = message.color
    }
}

enum class Status(val color: Int, val text: String) {
    NORMAL(Color.GREEN, "You are Normal"),
    OVERWEIGHT(Color.RED, "You are Overweight"),
    UNDERWEIGHT(Color.RED, "You are Underweight"),
    MISSING_DATA(Color.BLACK, "Invalid or missing data");
}
