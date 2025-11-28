import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';

class DailyHeaderMonthChip extends StatelessWidget {
  const DailyHeaderMonthChip({
    super.key,
    required this.sizeClass,
    required this.label,
  });

  final ScreenSizeClass sizeClass;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Spacer(),
        Expanded(
          flex: 3,
          child: Center(
            child: Container(
              padding: const EdgeInsets.symmetric(
                horizontal: AppSpacing.l,
                vertical: AppSpacing.s,
              ),
              decoration: BoxDecoration(
                color: AppColors.cardBackground.withAlpha((255 * 0.9).round()),
                borderRadius: BorderRadius.circular(AppRadius.pill),
                boxShadow: [
                  BoxShadow(
                    color: AppColors.shadow.withAlpha((255 * 0.05).round()),
                    blurRadius: 6,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: Text(
                label,
                style: AppTypography.body1(
                  sizeClass,
                ).copyWith(fontWeight: FontWeight.w600),
              ),
            ),
          ),
        ),
        const Spacer(),
        Column(
          children: const [
            _RoundIcon(icon: Icons.photo_library_outlined),
            SizedBox(height: AppSpacing.s),
            _RoundIcon(icon: Icons.share),
          ],
        ),
      ],
    );
  }
}

class _RoundIcon extends StatelessWidget {
  const _RoundIcon({required this.icon});
  final IconData icon;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 40,
      height: 40,
      decoration: BoxDecoration(
        color: AppColors.cardBackground,
        shape: BoxShape.circle,
        boxShadow: [
          BoxShadow(
            color: AppColors.shadow.withAlpha((255 * 0.08).round()),
            blurRadius: 6,
            offset: const Offset(0, 3),
          ),
        ],
      ),
      child: Icon(icon, color: AppColors.textPrimary, size: 22),
    );
  }
}
