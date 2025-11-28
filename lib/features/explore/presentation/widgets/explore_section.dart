import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import 'explore_item.dart';

class ExploreSection extends StatelessWidget {
  const ExploreSection({
    super.key,
    required this.title,
    required this.items,
    required this.sizeClass,
  });

  final String title;
  final List<ExploreItemModel> items;
  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    final crossAxisCount = sizeClass == ScreenSizeClass.small ? 3 : 4;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(title, style: AppTypography.headline2(sizeClass)),
        const SizedBox(height: AppSpacing.m),
        GridView.count(
          crossAxisCount: crossAxisCount,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          mainAxisSpacing: AppSpacing.m,
          crossAxisSpacing: AppSpacing.m,
          childAspectRatio: 0.75,
          children: items
              .map((item) => ExploreItem(item: item, sizeClass: sizeClass))
              .toList(),
        ),
      ],
    );
  }
}
