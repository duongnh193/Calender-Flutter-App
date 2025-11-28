import 'package:flutter/material.dart';

/// Returns days to render in a month grid starting on Monday, including
/// leading and trailing days to fill full weeks.
List<DateTime> generateMonthDays(DateTime month) {
  final firstDay = DateTime(month.year, month.month, 1);
  final daysInMonth = DateUtils.getDaysInMonth(month.year, month.month);
  final leading = firstDay.weekday - DateTime.monday; // Monday = 1
  final start = firstDay.subtract(Duration(days: leading));
  final total = leading + daysInMonth;
  final trailing = total % 7 == 0 ? 0 : 7 - (total % 7);
  final itemCount = total + trailing;

  return List.generate(
    itemCount,
    (index) => DateTime(start.year, start.month, start.day + index),
  );
}
