package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * A calendar-based date picker dialog component.
 *
 * @param initialDate The currently selected date
 * @param onDateSelected Callback when user selects a date
 * @param minDate Minimum selectable date (e.g., for age validation)
 * @param maxDate Maximum selectable date (usually today)
 */
@OptIn(ExperimentalTime::class)
@Composable
fun CalendarDatePicker(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDismiss: (() -> Unit)? = null
) {
    var currentMonth by remember { mutableStateOf(initialDate) }
    val now = Clock.System.now()
    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

    // Clamp maxDate to today if not specified
    val effectiveMaxDate = maxDate ?: today
    // Default minDate to 100 years ago if not specified
    val effectiveMinDate = minDate ?: LocalDate(today.year - 100, today.month, today.day)

    // Navigate to previous month
    fun previousMonth() {
        val newMonth = currentMonth.minus(DatePeriod(months = 1))
        if (newMonth >= effectiveMinDate) {
            currentMonth = newMonth
        }
    }

    // Navigate to next month
    fun nextMonth() {
        val newMonth = currentMonth.plus(DatePeriod(months = 1))
        if (newMonth <= effectiveMaxDate) {
            currentMonth = newMonth
        }
    }

    // Check if a date is selectable
    fun isSelectable(date: LocalDate): Boolean {
        return date in effectiveMinDate..effectiveMaxDate
    }

    // Generate days in current month
    val firstDayOfMonth = LocalDate(currentMonth.year, currentMonth.month, 1)
    val daysInMonth = when (currentMonth.month) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY, Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
        Month.FEBRUARY -> if ((currentMonth.year % 4 == 0 && currentMonth.year % 100 != 0) || currentMonth.year % 400 == 0) 29 else 28
        else -> 30
    }

    // Determine day of week for first day (1 = Monday, 7 = Sunday in ISO)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek // 1-7 (Monday=1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Header with month/year and navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous month button
            Text(
                text = "<",
                modifier = Modifier
                    .clickable(enabled = currentMonth > effectiveMinDate) { previousMonth() }
                    .padding(8.dp),
                color = if (currentMonth > effectiveMinDate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )

            // Month and year
            Text(
                text = "${currentMonth.month.name.lowercase().capitalize()} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Next month button
            Text(
                text = ">",
                modifier = Modifier
                    .clickable(enabled = currentMonth < effectiveMaxDate) { nextMonth() }
                    .padding(8.dp),
                color = if (currentMonth < effectiveMaxDate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val days = mutableListOf<LocalDate?>()
        // Add empty slots for days before the first day of month
        // firstDayOfWeek.ordinal: Monday=0, Sunday=6
        repeat(firstDayOfWeek.ordinal) { days.add(null) }
        // Add days of the month
        for (day in 1..daysInMonth) {
            days.add(LocalDate(currentMonth.year, currentMonth.month, day))
        }

        // Display in rows of 7
        val rows = days.chunked(7)
        rows.forEach { rowDays ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowDays.forEach { date ->
                    if (date == null) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val isSelected = date == initialDate
                        val isCurrentMonth = date.month == currentMonth.month
                        val isSelectable = isSelectable(date)
                        val isToday = date == today

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .then(
                                    if (isSelectable) {
                                        Modifier.clickable { onDateSelected(date) }
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isSelected -> {
                                    // Selected date circle
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                isToday -> {
                                    // Today indicator
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                else -> {
                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        modifier = Modifier.fillMaxSize(),
                                        textAlign = TextAlign.Center,
                                        color = if (isSelectable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
