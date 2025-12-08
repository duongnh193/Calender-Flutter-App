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
    final titleStyle = AppTypography.calendarHeaderTitle(sizeClass).copyWith(
      fontSize: AppTypography.calendarHeaderTitle(sizeClass).fontSize! - 2,
    );

    return Row(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        IconButton(
          onPressed: onPrevious,
          icon: const Icon(
            Icons.chevron_left,
            color: AppColors.calendarNavArrow,
          ),
        ),
        Expanded(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Flexible(
                    child: Text(
                      primaryText,
                      style: titleStyle,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  const SizedBox(width: AppSpacing.xs),
                  const Icon(
                    Icons.arrow_drop_down,
                    color: AppColors.calendarNavArrow,
                  ),
                ],
              ),
              const SizedBox(height: AppSpacing.xs / 2),
              Text(
                subText,
                style: AppTypography.calendarHeaderSubtitle(sizeClass),
                textAlign: TextAlign.center,
              ),
            ],
          ),
        ),
        IconButton(
          onPressed: onNext,
          icon: const Icon(
            Icons.chevron_right,
            color: AppColors.calendarNavArrow,
          ),
        ),
      ],
    );
  }
}
