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
  });

  final ScreenSizeClass sizeClass;
  final DateTime month;
  final DateTime selectedDate;
  final Set<int> specialDates;
  final ValueChanged<DateTime> onSelect;

  @override
  Widget build(BuildContext context) {
    final days = generateMonthDays(month);
    final weekdayLabels = ['HAI', 'BA', 'TƯ', 'NĂM', 'SÁU', 'BẢY', 'C.N'];

    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: weekdayLabels
              .map(
                (label) => Expanded(
                  child: Center(
                    child: Text(
                      label,
                      style: AppTypography.body2(sizeClass).copyWith(
                        color: AppColors.textSecondary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
              )
              .toList(),
        ),
        const SizedBox(height: AppSpacing.s),
        GridView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 7,
            crossAxisSpacing: AppSpacing.xs,
            mainAxisSpacing: AppSpacing.xs,
          ),
          itemCount: days.length,
          itemBuilder: (context, index) {
            final date = days[index];
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
            final isSpecial = isCurrentMonth && specialDates.contains(date.day);

            return DayCell(
              date: date,
              isCurrentMonth: isCurrentMonth,
              isToday: isToday,
              isSelected: isSelected,
              isSpecial: isSpecial,
              lunarDay: date.day,
              sizeClass: sizeClass,
              onTap: () => onSelect(date),
            );
          },
        ),
      ],
    );
  }
}
