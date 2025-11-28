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
    final baseColor =
        isToday ? AppColors.backgroundDailyEnd : AppColors.transparent;

    final isSmall = sizeClass == ScreenSizeClass.small;
    final padding = isSmall ? AppSpacing.xs : AppSpacing.s;
    final gap = isSmall ? AppSpacing.xs / 2 : AppSpacing.xs;

    return GestureDetector(
      onTap: onTap,
      child: Opacity(
        opacity: opacity,
        child: Container(
          decoration: BoxDecoration(
            color: baseColor.withAlpha((255 * (isToday ? 0.4 : 0)).round()),
            border: border,
            borderRadius: BorderRadius.circular(AppSpacing.m),
          ),
          padding: EdgeInsets.all(padding),
          child: Stack(
            children: [
              if (isSpecial)
                Positioned(
                  top: AppSpacing.xs,
                  right: AppSpacing.xs,
                  child: Container(
                    width: 4,  // nhỏ hơn cho giống iOS
                    height: 4,
                    decoration: const BoxDecoration(
                      color: AppColors.calendarDotRed,
                      shape: BoxShape.circle,
                    ),
                  ),
                ),
              Center(
                child: FittedBox(
                  fit: BoxFit.scaleDown,
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        '${date.day}',
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: _dayTextStyle,
                      ),
                      SizedBox(height: gap),
                      Text(
                        lunarDay != null ? '$lunarDay' : '',
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: _lunarTextStyle,
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    ).opacity(opacity);
  }

  TextStyle get _dayTextStyle {
    // weekday thường vs Chủ Nhật
    final base = date.weekday == DateTime.sunday
        ? AppTypography.calendarDayWeekend(sizeClass)
        : AppTypography.calendarDay(sizeClass);
    return base;
  }

  TextStyle get _lunarTextStyle => AppTypography.calendarLunar(sizeClass);
}

extension on Widget {
  Widget opacity(double value) {
    return Opacity(opacity: value, child: this);
  }
}
