import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';

class MonthSelectedDayPanel extends StatelessWidget {
  const MonthSelectedDayPanel({
    super.key,
    required this.sizeClass,
    required this.weekdayLabel,
    required this.fullDateLabel,
    required this.lunarLabel,
    required this.timeLabel,
    required this.dayLabel,
    required this.monthLabel,
    required this.yearLabel,
    this.goodDayLabel = 'Ngày hoàng đạo',
  });

  final ScreenSizeClass sizeClass;
  final String weekdayLabel;
  final String fullDateLabel;
  final String lunarLabel;
  final String timeLabel;
  final String dayLabel;
  final String monthLabel;
  final String yearLabel;
  final String goodDayLabel;

  @override
  Widget build(BuildContext context) {
    final labelStyle = AppTypography.calendarPanelLabel(sizeClass);
    final valueStyle = AppTypography.calendarPanelValue(sizeClass);

    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: AppColors.calendarCardBackground,
        borderRadius: BorderRadius.circular(AppRadius.large),
        border: Border.all(color: AppColors.calendarCardBorder),
      ),
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.l,
        vertical: AppSpacing.m,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.symmetric(
              horizontal: AppSpacing.m,
              vertical: AppSpacing.xs,
            ),
            decoration: BoxDecoration(
              color: AppColors.iosRed.withAlpha((255 * 0.1).round()),
              borderRadius: BorderRadius.circular(AppRadius.pill),
            ),
            child: Text(
              weekdayLabel,
              style: AppTypography.body2(
                sizeClass,
              ).copyWith(color: AppColors.iosRed, fontWeight: FontWeight.w700),
            ),
          ),
          const SizedBox(height: AppSpacing.s),
          Row(
            children: [
              Expanded(
                child: Text(
                  fullDateLabel,
                  style: AppTypography.headline1(sizeClass),
                ),
              ),
              const SizedBox(width: AppSpacing.m),
              Text(lunarLabel, style: AppTypography.body1(sizeClass)),
            ],
          ),
          const SizedBox(height: AppSpacing.m),
          Row(
            children: [
              _ValueColumn(
                label: 'GIỜ',
                value: timeLabel,
                sizeClass: sizeClass,
                labelStyle: labelStyle,
                valueStyle: valueStyle,
              ),
              _ValueColumn(
                label: 'NGÀY',
                value: dayLabel,
                sizeClass: sizeClass,
                labelStyle: labelStyle,
                valueStyle: valueStyle.copyWith(color: AppColors.iosRed),
              ),
              _ValueColumn(
                label: 'THÁNG',
                value: monthLabel,
                sizeClass: sizeClass,
                labelStyle: labelStyle,
                valueStyle: valueStyle,
              ),
              _ValueColumn(
                label: 'NĂM',
                value: yearLabel,
                sizeClass: sizeClass,
                labelStyle: labelStyle,
                valueStyle: valueStyle,
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _ValueColumn extends StatelessWidget {
  const _ValueColumn({
    required this.label,
    required this.value,
    required this.sizeClass,
    required this.labelStyle,
    required this.valueStyle,
  });

  final String label;
  final String value;
  final ScreenSizeClass sizeClass;
  final TextStyle labelStyle;
  final TextStyle valueStyle;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: labelStyle),
          const SizedBox(height: AppSpacing.xs),
          Text(value, style: valueStyle),
        ],
      ),
    );
  }
}
