import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';

class HoroscopeResultSection extends StatelessWidget {
  const HoroscopeResultSection({
    super.key,
    required this.title,
    required this.content,
    required this.sizeClass,
  });

  final String title;
  final String content;
  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: AppSpacing.l),
      padding: const EdgeInsets.all(AppSpacing.l),
      decoration: BoxDecoration(
        color: AppColors.cardBackground,
        borderRadius: BorderRadius.circular(AppRadius.medium),
        border: Border.all(color: AppColors.dividerColor),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Section title
          Text(
            title,
            style: AppTypography.headline2(sizeClass).copyWith(
              fontWeight: FontWeight.bold,
              color: AppColors.primaryRed,
              fontSize: (AppTypography.headline2(sizeClass).fontSize ?? 20) * 0.9,
            ),
          ),
          const SizedBox(height: AppSpacing.m),
          // Section content
          Text(
            content,
            style: AppTypography.body1(sizeClass).copyWith(
              height: 1.6,
              color: AppColors.textPrimary,
            ),
          ),
        ],
      ),
    );
  }
}

