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
    final labelStyle = AppTypography.body2(
      sizeClass,
    ).copyWith(color: AppColors.textSecondary);
    final valueStyle = AppTypography.subtitle1(
      sizeClass,
    ).copyWith(fontWeight: FontWeight.w600);

    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: AppColors.cardBackground,
        borderRadius: BorderRadius.circular(AppRadius.medium),
        boxShadow: [
          BoxShadow(
            color: AppColors.shadow.withAlpha((255 * 0.03).round()),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      padding: const EdgeInsets.all(AppSpacing.l),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(weekdayLabel, style: AppTypography.headline2(sizeClass)),
                  const SizedBox(height: AppSpacing.xs),
                  Text(fullDateLabel, style: AppTypography.body1(sizeClass)),
                ],
              ),
              const Spacer(),
              Row(
                children: [
                  Container(
                    width: 10,
                    height: 10,
                    decoration: const BoxDecoration(
                      color: AppColors.dangerRedDot,
                      shape: BoxShape.circle,
                    ),
                  ),
                  const SizedBox(width: AppSpacing.s),
                  Text(goodDayLabel, style: AppTypography.body2(sizeClass)),
                ],
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.s),
          Text(lunarLabel, style: AppTypography.body2(sizeClass)),
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
                valueStyle: valueStyle.copyWith(color: AppColors.primaryRed),
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
