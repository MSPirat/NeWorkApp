package ru.netology.neworkapp.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.widget.EditText
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

private val calendar = Calendar.getInstance()

fun pickDate(editText: EditText?, context: Context?) {
    val datePicker = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = monthOfYear
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth

        editText?.setText(
            SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
                .format(calendar.time)
        )
    }

    DatePickerDialog(
        context!!,
        datePicker,
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    )
        .show()
}

fun pickTime(editText: EditText, context: Context) {
    val timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        calendar[Calendar.MINUTE] = minute

        editText.setText(
            SimpleDateFormat("HH:mm", Locale.ROOT)
                .format(calendar.time)
        )
    }

    TimePickerDialog(
        context,
        timePicker,
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )
        .show()
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatToDate(value: String): String {
    val transformation = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.ROOT)
        .withZone(ZoneId.systemDefault())

    return transformation.format(Instant.parse(value))
}


@RequiresApi(Build.VERSION_CODES.O)
fun formatToInstant(value: String): String {
    val datetime = SimpleDateFormat(
        "yyyy-MM-dd HH:mm",
        Locale.getDefault()
    )
        .parse(value)
    val transformation = DateTimeFormatter.ISO_INSTANT

    return transformation.format(datetime?.toInstant())
}

@RequiresApi(Build.VERSION_CODES.O)
fun dateToEpochSec(string: String?): Long? {
    return if (string.isNullOrBlank()) null else LocalDate.parse(string)
        .atStartOfDay(ZoneId.of("Europe/Moscow")).toEpochSecond()
}

@SuppressLint("SimpleDateFormat")
fun epochSecToDate(second: Long): String {
    val date = Date(second * 1_000)
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    return simpleDateFormat.format(date)
}