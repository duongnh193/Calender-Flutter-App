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
        color: AppColors.primaryGreen.withAlpha((255 * 0.1).round()),
        borderRadius: BorderRadius.circular(AppRadius.medium),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('GIỜ HOÀNG ĐẠO', style: AppTypography.headline2(sizeClass)),
          const SizedBox(height: AppSpacing.m),
          Wrap(
            spacing: AppSpacing.l,
            runSpacing: AppSpacing.m,
            children: items
                .map(
                  (item) => _GoldenHourItem(item: item, sizeClass: sizeClass),
                )
                .toList(),
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
    required this.icon,
  });

  final String name;
  final String timeRange;
  final Color color;
  final IconData icon;
}

class _GoldenHourItem extends StatelessWidget {
  const _GoldenHourItem({required this.item, required this.sizeClass});

  final GoldenHourItem item;
  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: 40,
          height: 40,
          decoration: BoxDecoration(
            color: item.color.withAlpha((255 * 0.18).round()),
            shape: BoxShape.circle,
          ),
          child: Icon(item.icon, color: item.color),
        ),
        const SizedBox(width: AppSpacing.s),
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(item.name, style: AppTypography.subtitle1(sizeClass)),
            Text(item.timeRange, style: AppTypography.body2(sizeClass)),
          ],
        ),
      ],
    );
  }
}
