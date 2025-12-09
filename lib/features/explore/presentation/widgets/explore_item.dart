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
  const ExploreItem({
    super.key,
    required this.item,
    required this.sizeClass,
    this.onTap,
  });

  final ExploreItemModel item;
  final ScreenSizeClass sizeClass;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    final isSmall = sizeClass == ScreenSizeClass.small;
    final bubbleSize = isSmall ? 42.0 : 50.0;
    final gap = isSmall ? AppSpacing.xs : AppSpacing.s;
    final labelWidth = bubbleSize + AppSpacing.l;
    // final double estimatedMinHeight = bubbleSize + gap + (isSmall ? 28 : 32);

    final textStyle = AppTypography.body2(sizeClass)
        .copyWith(color: AppColors.textPrimary);
    
    final lineHeight = (textStyle.fontSize ?? 14) * (textStyle.height ?? 1.2);
    final minHeight = bubbleSize + gap + lineHeight * 2;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(8),
      child: ConstrainedBox(
        constraints: BoxConstraints(
          minWidth: bubbleSize,
          minHeight: minHeight,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: bubbleSize,
              height: bubbleSize,
              decoration: BoxDecoration(
                color: item.color.withValues(alpha: 0.15),
                shape: BoxShape.circle,
              ),
              child: Icon(
                item.icon,
                color: item.color,
                size: bubbleSize * 0.55,
              ),
            ),
            SizedBox(height: gap),
            SizedBox(
              width: labelWidth,
              child: Text(
                item.label,
                style: textStyle,
                textAlign: TextAlign.center,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
