import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';

class DailyQuoteSection extends StatelessWidget {
  const DailyQuoteSection({
    super.key,
    required this.sizeClass,
    required this.quote,
    required this.source,
  });

  final ScreenSizeClass sizeClass;
  final String quote;
  final String source;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        Expanded(
          flex: 3,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(
                quote,
                style: AppTypography.body1(sizeClass),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: AppSpacing.s),
              Align(
                alignment: Alignment.centerRight,
                child: Text(
                  source,
                  style: AppTypography.body1(sizeClass).copyWith(
                    fontStyle: FontStyle.italic,
                    color: AppColors.textSecondary,
                  ),
                ),
              ),
            ],
          ),
        ),
        const SizedBox(width: AppSpacing.l),
        Container(
          width: 120,
          height: 140,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(AppRadius.medium),
            gradient: LinearGradient(
              colors: [
                AppColors.cardBackground.withAlpha((255 * 0.9).round()),
                AppColors.cardBackground.withAlpha(0),
              ],
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
            ),
          ),
          child: const Align(
            alignment: Alignment.bottomRight,
            child: Icon(
              Icons.family_restroom,
              size: 64,
              color: AppColors.textSecondary,
            ),
          ),
        ),
      ],
    );
  }
}
