import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';

class MonthHeader extends StatelessWidget {
  const MonthHeader({
    super.key,
    required this.sizeClass,
    required this.monthLabel,
    required this.primaryText,
    required this.subText,
    required this.onPrevious,
    required this.onNext,
  });

  final ScreenSizeClass sizeClass;
  final String monthLabel;
  final String primaryText;
  final String subText;
  final VoidCallback onPrevious;
  final VoidCallback onNext;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            IconButton(
              onPressed: onPrevious,
              icon: const Icon(Icons.chevron_left),
            ),
            Expanded(
              child: Center(
                child: Text(
                  monthLabel,
                  style: AppTypography.subtitle1(
                    sizeClass,
                  ).copyWith(fontWeight: FontWeight.w600),
                ),
              ),
            ),
            IconButton(
              onPressed: onNext,
              icon: const Icon(Icons.chevron_right),
            ),
          ],
        ),
        const SizedBox(height: AppSpacing.s),
        Text(primaryText, style: AppTypography.headline1(sizeClass)),
        const SizedBox(height: AppSpacing.xs),
        Text(
          subText,
          style: AppTypography.body2(
            sizeClass,
          ).copyWith(color: AppColors.textSecondary),
        ),
      ],
    );
  }
}
