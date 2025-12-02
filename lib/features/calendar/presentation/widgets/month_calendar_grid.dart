import 'package:flutter/material.dart';

import '../../../../common_widgets/calendar/day_cell.dart';
import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/date_time_utils.dart';

class MonthCalendarGrid extends StatelessWidget {
  const MonthCalendarGrid({
    super.key,
    required this.sizeClass,
    required this.month,
    required this.selectedDate,
    required this.specialDates,
    required this.onSelect,
    this.lunarDayResolver,
  });

  final ScreenSizeClass sizeClass;
  final DateTime month;
  final DateTime selectedDate;
  final Set<int> specialDates;
  final ValueChanged<DateTime> onSelect;
  final int? Function(DateTime date)? lunarDayResolver;

  @override
  Widget build(BuildContext context) {
    final days = generateMonthDays(month);
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
              runSpacing: AppSpacing.s,
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
