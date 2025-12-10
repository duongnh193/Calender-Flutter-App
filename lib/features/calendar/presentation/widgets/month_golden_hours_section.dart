import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';

class MonthGoldenHoursSection extends StatelessWidget {
  const MonthGoldenHoursSection({
    super.key,
    required this.sizeClass,
    required this.items,
  });

  final ScreenSizeClass sizeClass;
  final List<GoldenHourItem> items;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(AppSpacing.l),
      decoration: BoxDecoration(
        color: AppColors.calendarCardBackground,
        borderRadius: BorderRadius.circular(AppRadius.medium),
        border: Border.all(color: AppColors.calendarCardBorder),
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
              color: AppColors.calendarGoldenHourTagBg,
              borderRadius: BorderRadius.circular(AppRadius.pill),
            ),
            child: Text(
              'GIỜ HOÀNG ĐẠO',
              style: AppTypography.body1(
                sizeClass,
              ).copyWith(fontWeight: FontWeight.w700),
            ),
          ),
          const SizedBox(height: AppSpacing.m),
          // Display in 2 rows, 3 columns each (6 items max)
          Column(
            children: [
              // First row (first 3 items)
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: items
                    .take(3)
                    .map(
                      (item) => Expanded(
                        child: Padding(
                          padding: const EdgeInsets.symmetric(
                            horizontal: AppSpacing.xs,
                          ),
                          child: _GoldenHourItem(
                            item: item,
                            sizeClass: sizeClass,
                          ),
                        ),
                      ),
                    )
                    .toList(),
              ),
              if (items.length > 3) ...[
                const SizedBox(height: AppSpacing.l),
                // Second row (items 4-6)
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: items
                      .skip(3)
                      .take(3)
                      .map(
                        (item) => Expanded(
                          child: Padding(
                            padding: const EdgeInsets.symmetric(
                              horizontal: AppSpacing.xs,
                            ),
                            child: _GoldenHourItem(
                              item: item,
                              sizeClass: sizeClass,
                            ),
                          ),
                        ),
                      )
                      .toList(),
                ),
              ],
            ],
          ),
        ],
      ),
    );
  }
}

class GoldenHourItem {
  GoldenHourItem({
    required this.name,
    required this.timeRange,
    required this.color,
    required this.zodiacImagePath,
  });

  final String name;
  final String timeRange;
  final Color color;
  final String? zodiacImagePath; // Path to zodiac PNG image
}

class _GoldenHourItem extends StatelessWidget {
  const _GoldenHourItem({required this.item, required this.sizeClass});

  final GoldenHourItem item;
  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: 44,
          height: 44,
          decoration: BoxDecoration(
            color: item.color.withAlpha((255 * 0.18).round()),
            shape: BoxShape.circle,
          ),
          child: item.zodiacImagePath != null
              ? ClipOval(
                  child: Image.asset(
                    item.zodiacImagePath!,
                    width: 44,
                    height: 44,
                    fit: BoxFit.cover,
                    errorBuilder: (context, error, stackTrace) {
                      // Fallback to colored circle if image fails to load
                      return Container(
                        color: item.color.withAlpha((255 * 0.3).round()),
                        child: Icon(Icons.star, color: item.color, size: 26),
                      );
                    },
                  ),
                )
              : Icon(Icons.star, color: item.color, size: 26),
        ),
        const SizedBox(height: AppSpacing.s),
        Text(
          item.name,
          style: AppTypography.calendarGoldenHourLabel(sizeClass),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: AppSpacing.xs),
        Text(
          item.timeRange,
          style: AppTypography.calendarGoldenHourTime(sizeClass),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }
}
