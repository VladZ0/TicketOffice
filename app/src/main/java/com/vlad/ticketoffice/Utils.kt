package com.vlad.ticketoffice

import java.text.SimpleDateFormat
import java.util.*

object Utils {
     const val CURRENT_RACE = "current_race"
     const val IS_EDITING = "is_editing"
     const val CURRENT_TICKET = "current_ticket"
     const val SEATS_COUNT = 78

     fun validateTime(time: String): Boolean{
         val timeRegex = "(\\d{2}):(\\d{2})".toRegex()
         if (!time.matches(timeRegex)){
             return false
         }

         val hours = time.split(":")[0]
         val minutes = time.split(":")[1]

         if(hours.matches("[0-1][0-9]".toRegex()) || hours.matches("2[0-3]".toRegex())){
             if(minutes.matches("[0-5][0-9]".toRegex())){
                 return true
             }
         }

         return false
     }

     fun validateDate(date: String): Boolean {
         val dateRegex = "\\d{2}\\.\\d{2}\\.\\d{4}"

         if(!date.matches(dateRegex.toRegex())){
             return false
         }

         val sdf = SimpleDateFormat("dd.MM.yyyy")

         if(Date().after(sdf.parse(date))){
             return false
         }

         val day: String = date.split("\\.".toRegex()).toTypedArray()[0]
         val month: String = date.split("\\.".toRegex()).toTypedArray()[1]
         val year: String = date.split("\\.".toRegex()).toTypedArray()[2]
         val yearNum: Int = Integer.valueOf(year)
         val dayNum: Int = if (day[0] == '0') Integer.valueOf(day.substring(1)) else Integer.valueOf(day)
         val monthNum: Int = if (month[0] == '0') Integer.valueOf(month.substring(1)) else Integer.valueOf(month)
         if (Integer.valueOf(monthNum) > 12) return false
         if (monthNum % 2 != 0 && monthNum < 7 || monthNum % 2 == 0 && monthNum > 7) {
             if (dayNum > 31) return false
         } else if ((monthNum % 2 == 0 && monthNum < 7 || monthNum % 2 != 0 && monthNum > 7) && monthNum != 2) {
             if (dayNum > 30) return false
         } else {
             if (yearNum % 4 == 0) {
                 if (dayNum > 29) return false
             } else {
                 if (dayNum > 28) return false
             }
         }
         return true
     }
}