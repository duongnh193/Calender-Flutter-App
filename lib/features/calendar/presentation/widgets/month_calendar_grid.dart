import 'package:flutter/material.dart';

import '../../../../common_widgets/calendar/day_cell.dart';
import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';

class MonthCalendarGrid extends StatelessWidget {
  const MonthCalendarGrid({
    super.key,
    required this.sizeClass,
    required this.month,
    required this.selectedDate,
    required this.specialDates,
    required this.onSelect,
    this.lunarDayResolver,
    this.dotColorResolver,
  });

  final ScreenSizeClass sizeClass;
  final DateTime month;
  final DateTime selectedDate;
  final Set<int> specialDates;
  final ValueChanged<DateTime> onSelect;
  final int? Function(DateTime date)? lunarDayResolver;
  final Color Function(DateTime date)? dotColorResolver;

  /// Generate list of dates for the month grid (including padding days)
  List<DateTime> _generateMonthDays(DateTime month) {
    final firstDayOfMonth = DateTime(month.year, month.month, 1);
    final lastDayOfMonth = DateTime(month.year, month.month + 1, 0);

    // Get the weekday of the first day (Monday = 1, Sunday = 7)
    // We want Monday to be the first day of the week
    int startPadding = firstDayOfMonth.weekday - 1;

    final days = <DateTime>[];

    // Add padding days from previous month
    for (int i = startPadding; i > 0; i--) {
      days.add(firstDayOfMonth.subtract(Duration(days: i)));
    }

    // Add days of the current month
    for (int i = 1; i <= lastDayOfMonth.day; i++) {
      days.add(DateTime(month.year, month.month, i));
    }

    // Add padding days for next month to complete the grid
    final remainingDays = 7 - (days.length % 7);
    if (remainingDays < 7) {
      for (int i = 1; i <= remainingDays; i++) {
        days.add(DateTime(month.year, month.month + 1, i));
      }
    }

    return days;
  }

  @override
  Widget build(BuildContext context) {
    final days = _generateMonthDays(month);
    final weekdayLabels = ['HAI', 'BA', 'TƯ', 'NĂM', 'SÁU', 'BẢY', 'C.N'];

    return LayoutBuilder(
      builder: (context, constraints) {
        final cellWidth = constraints.maxWidth / 7;
        final cellHeight = cellWidth * 0.8;

        return Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: weekdayLabels
                  .map(
                    (label) => SizedBox(
                      width: cellWidth,
                      child: Center(
                        child: Text(
                          label,
                          style: AppTypography.body2(sizeClass).copyWith(
                            color: AppColors.calendarWeekdayText,
                            fontWeight: FontWeight.w900,
                            letterSpacing: 0.2,
                          ),
                        ),
                      ),
                    ),
                  )
                  .toList(),
            ),
            const SizedBox(height: AppSpacing.s),
            Wrap(
              spacing: AppSpacing.s,
              runSpacing: AppSpacing.m,
              children: days.map((date) {
                final isCurrentMonth = date.month == month.month;
                final isSelected =
                    date.year == selectedDate.year &&
                    date.month == selectedDate.month &&
                    date.day == selectedDate.day;
                final now = DateTime.now();
                final isToday =
                    date.year == now.year &&
                    date.month == now.month &&
                    date.day == now.day;
                final isSpecial =
                    isCurrentMonth && specialDates.contains(date.day);
                final resolvedLunar =
                    lunarDayResolver != null ? lunarDayResolver!(date) : null;
                final color =
                    dotColorResolver != null ? dotColorResolver!(date) : AppColors.calendarDotRed;

                return SizedBox(
                  width: cellWidth - (AppSpacing.l / 2),
                  height: cellHeight,
                  child: DayCell(
                    date: date,
                    isCurrentMonth: isCurrentMonth,
                    isToday: isToday,
                    isSelected: isSelected,
                    isSpecial: isSpecial,
                    lunarDay: resolvedLunar ?? date.day,
                    dotColor: color,
                    sizeClass: sizeClass,
                    onTap: () => onSelect(date),
                  ),
                );
              }).toList(),
            ),
          ],
        );
      },
    );
  }
}
