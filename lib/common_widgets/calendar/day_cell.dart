import 'package:flutter/material.dart';

import '../../core/constants/size_breakpoints.dart';
import '../../core/theme/app_colors.dart';
import '../../core/theme/app_spacing.dart';
import '../../core/theme/app_typography.dart';

class DayCell extends StatelessWidget {
  const DayCell({
    super.key,
    required this.date,
    required this.isCurrentMonth,
    required this.isToday,
    required this.isSelected,
    required this.isSpecial,
    required this.lunarDay,
    required this.sizeClass,
    this.onTap,
  });

  final DateTime date;
  final bool isCurrentMonth;
  final bool isToday;
  final bool isSelected;
  final bool isSpecial;
  final int? lunarDay;
  final ScreenSizeClass sizeClass;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    final opacity = isCurrentMonth ? 1.0 : 0.45;
    final border = isSelected
        ? Border.all(color: AppColors.accentBlue, width: 2)
        : null;
    final baseColor = isToday
        ? AppColors.backgroundDailyEnd
        : AppColors.transparent;

    return GestureDetector(
      onTap: onTap,
      child: AspectRatio(
        aspectRatio: 1,
        child: Container(
          decoration: BoxDecoration(
            color: baseColor.withAlpha((255 * (isToday ? 0.4 : 0)).round()),
            border: border,
            borderRadius: BorderRadius.circular(AppSpacing.m),
          ),
          padding: const EdgeInsets.all(AppSpacing.s),
          child: Stack(
            children: [
              if (isSpecial)
                Positioned(
                  top: AppSpacing.xs,
                  right: AppSpacing.xs,
                  child: Container(
                    width: 6,
                    height: 6,
                    decoration: const BoxDecoration(
                      color: AppColors.dangerRedDot,
                      shape: BoxShape.circle,
                    ),
                  ),
                ),
              Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Text(
                    '${date.day}',
                    style: AppTypography.body1(sizeClass).copyWith(
                      fontWeight: FontWeight.w600,
                      color: AppColors.textPrimary,
                    ),
                  ),
                  const SizedBox(height: AppSpacing.xs),
                  Text(
                    lunarDay != null ? '$lunarDay' : '',
                    style: AppTypography.body2(
                      sizeClass,
                    ).copyWith(color: AppColors.textSecondary),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    ).opacity(opacity);
  }
}

extension on Widget {
  Widget opacity(double value) {
    return Opacity(opacity: value, child: this);
  }
}
