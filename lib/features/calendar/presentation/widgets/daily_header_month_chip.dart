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
    required this.selectedDate,
    required this.onDateSelected,
  });

  final ScreenSizeClass sizeClass;
  final String label;
  final DateTime selectedDate;
  final ValueChanged<DateTime> onDateSelected;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: double.infinity,
      child: Stack(
        alignment: Alignment.topCenter,
        children: [
          InkWell(
            onTap: () async {
              // Ensure we use the root navigator context to get MaterialLocalizations
              final navigatorContext = Navigator.of(context, rootNavigator: true).context;
              final date = await showDatePicker(
                context: navigatorContext,
                initialDate: selectedDate,
                firstDate: DateTime(1900),
                lastDate: DateTime(2100),
                helpText: 'Chọn ngày',
              );
              if (date != null) {
                onDateSelected(date);
              }
            },
            borderRadius: BorderRadius.circular(AppRadius.pill),
            child: Container(
              padding: const EdgeInsets.symmetric(
                horizontal: AppSpacing.l,
                vertical: AppSpacing.s,
              ),
              decoration: BoxDecoration(
                color: AppColors.cardBackground.withOpacity(0.4),
                borderRadius: BorderRadius.circular(AppRadius.pill),
                boxShadow: [
                  BoxShadow(
                    color: AppColors.shadow.withAlpha((255 * 0.1).round()),
                    blurRadius: 6,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    label,
                    style: AppTypography.body1(
                      sizeClass,
                    ).copyWith(fontWeight: FontWeight.w600),
                  ),
                  const SizedBox(width: AppSpacing.xs),
                  const Icon(
                    Icons.calendar_today,
                    size: 16,
                    color: AppColors.textSecondary,
                  ),
                ],
              ),
            ),
          ),
          Align(
            alignment: Alignment.centerRight,
            child: Padding(
              padding: const EdgeInsets.only(right:AppSpacing.s),
              child: Column(
                children: const [
                  _RoundIcon(icon: Icons.photo_library_outlined),
                  SizedBox(height: AppSpacing.s),
                  _RoundIcon(icon: Icons.share),
                ],
              ),
            ),
          ),
        ],
      ),
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
