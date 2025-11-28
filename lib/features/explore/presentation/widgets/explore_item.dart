import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';

class ExploreItemModel {
  ExploreItemModel({
    required this.icon,
    required this.label,
    required this.color,
  });

  final IconData icon;
  final String label;
  final Color color;
}

class ExploreItem extends StatelessWidget {
  const ExploreItem({super.key, required this.item, required this.sizeClass});

  final ExploreItemModel item;
  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    final bubbleSize = sizeClass == ScreenSizeClass.small ? 56.0 : 64.0;

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: bubbleSize,
          height: bubbleSize,
          decoration: BoxDecoration(
            color: item.color.withAlpha((255 * 0.15).round()),
            shape: BoxShape.circle,
          ),
          child: Icon(item.icon, color: item.color, size: bubbleSize * 0.5),
        ),
        const SizedBox(height: AppSpacing.s),
        SizedBox(
          width: bubbleSize + AppSpacing.l,
          child: Text(
            item.label,
            style: AppTypography.body2(
              sizeClass,
            ).copyWith(color: AppColors.textPrimary),
            textAlign: TextAlign.center,
            maxLines: 2,
          ),
        ),
      ],
    );
  }
}
