package ru.netology.neworkapp.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object AndroidUtils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateAndTime(dateAndTime: String): String {
        return if (dateAndTime == "") {
            ""
        } else {
            val parsedDate = LocalDateTime.parse(dateAndTime, DateTimeFormatter.ISO_DATE_TIME)
            return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDate(date: String): String {
        return if (date == "") {
            ""
        } else {
            val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
            return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }
    }

    @SuppressLint("NewApi")
    fun selectDateDialog(editText: EditText?, context: Context) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val pickedDateTime = Calendar.getInstance()
            pickedDateTime.set(year, month, dayOfMonth)
            val result = GregorianCalendar(year, month, dayOfMonth).time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.uuu'Z'", Locale.getDefault())
            editText?.setText(dateFormat.format(result))
        }, startYear, startMonth, startDay).show()
    }

    fun selectDateTimeDialog(editText: EditText?, context: Context) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, dayOfMonth, hourOfDay, minute)
                val result = GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute).time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                editText?.setText(dateFormat.format(result))
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }
}