package org.grida.domain.diary

import org.grida.domain.base.Timestamp
import java.time.LocalDate

data class Diary(
    val id: Long,
    val timestamp: Timestamp,
    val targetDate: LocalDate,
    val content: String,
    val scope: DiaryScope
)