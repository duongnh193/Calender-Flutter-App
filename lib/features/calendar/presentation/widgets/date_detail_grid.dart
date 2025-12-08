import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/theme/app_colors.dart';

class DateDetailGrid extends StatelessWidget {
  const DateDetailGrid({
    super.key,
    required this.sizeClass,
    required this.time,
    required this.day,
    required this.month,
    required this.year,
    required this.timeCanChi,
    required this.dayCanChi,
    required this.monthCanChi,
    required this.yearCanChi,
  });

  final ScreenSizeClass sizeClass;
  final String time;
  final String day;
  final String month;
  final String year;
  final String timeCanChi;
  final String dayCanChi;
  final String monthCanChi;
  final String yearCanChi;

  @override
  Widget build(BuildContext context) {
    final labelStyle = AppTypography.dateDetailLabel(sizeClass);
    final valueStyle = AppTypography.dateDetailValue(sizeClass);
    final subStyle = AppTypography.dateDetailCanChi(sizeClass);

    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.s,
        vertical: AppSpacing.m,
      ),
      child: Row(
        children: [
          _DateColumn(
            label: 'GIỜ',
            value: time,
            subValue: timeCanChi,
            labelStyle: labelStyle,
            valueStyle: valueStyle,
            subStyle: subStyle,
          ),
          _DateColumn(
            label: 'NGÀY',
            value: day,
            subValue: dayCanChi,
            labelStyle: labelStyle,
            valueStyle: valueStyle.copyWith(color: AppColors.calendarNavArrow),
            subStyle: subStyle,
          ),
          _DateColumn(
            label: 'THÁNG',
            value: month,
            subValue: monthCanChi,
            labelStyle: labelStyle,
            valueStyle: valueStyle,
            subStyle: subStyle,
          ),
          _DateColumn(
            label: 'NĂM',
            value: year,
            subValue: yearCanChi,
            labelStyle: labelStyle,
            valueStyle: valueStyle,
            subStyle: subStyle,
          ),
        ],
      ),
    );
  }
}

class _DateColumn extends StatelessWidget {
  const _DateColumn({
    required this.label,
    required this.value,
    required this.subValue,
    required this.labelStyle,
    required this.valueStyle,
    required this.subStyle,
  });

  final String label;
  final String value;
  final String subValue;
  final TextStyle labelStyle;
  final TextStyle valueStyle;
  final TextStyle subStyle;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Text(label, style: labelStyle),
          const SizedBox(height: AppSpacing.s),
          Text(value, style: valueStyle, textAlign: TextAlign.center),
          const SizedBox(height: AppSpacing.xs),
          Text(subValue, style: subStyle, textAlign: TextAlign.center),
        ],
      ),
    );
  }
}
