package com.sudoajay.quantumit_app.util

import java.text.SimpleDateFormat
import java.time.temporal.TemporalAccessor
import java.util.*


class Utils {


}
fun toLocalDate(date: String):String{
    val format = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
    val formatNew = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSS'Z'", Locale.getDefault())
    formatNew.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(formatNew.parse(date)!!).toString()
}