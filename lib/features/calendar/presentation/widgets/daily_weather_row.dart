import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';

class DailyWeatherRow extends StatelessWidget {
  const DailyWeatherRow({
    super.key,
    required this.sizeClass,
    required this.location,
    required this.temperature,
  });

  final ScreenSizeClass sizeClass;
  final String location;
  final String temperature;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: AppSpacing.m),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Flexible(
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Flexible(
                  child: Text(
                    location,
                    style: AppTypography.body1(
                      sizeClass,
                    ).copyWith(fontWeight: FontWeight.w600),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                const SizedBox(width: AppSpacing.s),
                const Icon(
                  Icons.cloud,
                  size: 18,
                  color: AppColors.textSecondary,
                ),
              ],
            ),
          ),
          const SizedBox(width: AppSpacing.s),
          Text(temperature, style: AppTypography.body1(sizeClass)),
        ],
      ),
    );
  }
}
