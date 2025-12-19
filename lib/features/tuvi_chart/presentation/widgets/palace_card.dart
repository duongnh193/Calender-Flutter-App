import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_typography.dart';
import '../../domain/tuvi_chart_models.dart';

/// Palace card displaying a single palace with its stars.
class PalaceCard extends StatelessWidget {
  const PalaceCard({
    super.key,
    required this.palace,
    this.showDebug = false,
  });

  final PalaceInfo palace;
  final bool showDebug;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final sizeClass = ScreenSizeClass.small; // Default for palace cards

    return Container(
      decoration: BoxDecoration(
        color: _getBackgroundColor(isDark),
        borderRadius: BorderRadius.circular(AppRadius.small),
        border: Border.all(
          color: _getBorderColor(isDark),
          width: palace.isThanCu ? 2 : 1,
        ),
      ),
      padding: const EdgeInsets.all(4),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Header: Palace name + DiaChi
          _buildHeader(sizeClass),

          const Divider(height: 4, thickness: 0.5),

          // Stars list
          Expanded(
            child: _buildStarsList(sizeClass),
          ),

          // Footer: Đại vận + markers
          _buildFooter(sizeClass),
        ],
      ),
    );
  }

  Widget _buildHeader(ScreenSizeClass sizeClass) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 2, vertical: 1),
      child: Column(
        children: [
          // Palace name
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              if (palace.isThanCu)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 2),
                  margin: const EdgeInsets.only(right: 2),
                  decoration: BoxDecoration(
                    color: AppColors.accent,
                    borderRadius: BorderRadius.circular(2),
                  ),
                  child: Text(
                    'Thân',
                    style: AppTypography.caption(sizeClass).copyWith(
                      fontSize: 8,
                      color: Colors.white,
                    ),
                  ),
                ),
              Flexible(
                child: Text(
                  palace.name,
                  style: AppTypography.body2(sizeClass).copyWith(
                    fontWeight: FontWeight.bold,
                    fontSize: 11,
                  ),
                  textAlign: TextAlign.center,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
          // DiaChi
          Text(
            palace.canChiPrefix ?? palace.diaChi,
            style: AppTypography.caption(sizeClass).copyWith(
              fontSize: 9,
              color: Colors.grey[600],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStarsList(ScreenSizeClass sizeClass) {
    if (palace.stars.isEmpty) {
      return const SizedBox();
    }

    // Group stars by type
    final mainStars = palace.stars.where((s) => s.isMainStar).toList();
    final auxStars = palace.stars.where((s) => !s.isMainStar).toList();

    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Main stars (larger, highlighted)
          ...mainStars.map((star) => _buildStarChip(star, isMain: true, sizeClass: sizeClass)),

          // Auxiliary stars (smaller)
          if (auxStars.isNotEmpty) ...[
            const SizedBox(height: 2),
            Wrap(
              spacing: 2,
              runSpacing: 1,
              children: auxStars
                  .take(6) // Limit to avoid overflow
                  .map((star) => _buildStarChip(star, isMain: false, sizeClass: sizeClass))
                  .toList(),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildStarChip(StarInfo star,
      {required bool isMain, required ScreenSizeClass sizeClass}) {
    final starColor = _getStarColor(star);

    if (isMain) {
      return Container(
        width: double.infinity,
        margin: const EdgeInsets.symmetric(vertical: 1),
        padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 2),
        decoration: BoxDecoration(
          color: starColor.withOpacity(0.15),
          borderRadius: BorderRadius.circular(4),
          border: Border.all(color: starColor.withOpacity(0.3)),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Flexible(
              child: Text(
                star.name,
                style: AppTypography.body2(sizeClass).copyWith(
                  fontSize: 10,
                  fontWeight: FontWeight.bold,
                  color: starColor,
                ),
                overflow: TextOverflow.ellipsis,
              ),
            ),
            if (star.brightnessCode != null) ...[
              const SizedBox(width: 2),
              Text(
                '(${star.brightnessCode})',
                style: AppTypography.caption(sizeClass).copyWith(
                  fontSize: 8,
                  color: starColor,
                ),
              ),
            ],
          ],
        ),
      );
    }

    return Text(
      star.name,
      style: AppTypography.caption(sizeClass).copyWith(
        fontSize: 8,
        color: starColor,
      ),
    );
  }

  Widget _buildFooter(ScreenSizeClass sizeClass) {
    final List<Widget> footerItems = [];

    // Trường sinh stage
    if (palace.truongSinhStage != null) {
      footerItems.add(
        Text(
          palace.truongSinhStage!,
          style: AppTypography.caption(sizeClass).copyWith(
            fontSize: 8,
            fontStyle: FontStyle.italic,
          ),
        ),
      );
    }

    // Đại vận
    if (palace.daiVanLabel != null) {
      footerItems.add(
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 3, vertical: 1),
          decoration: BoxDecoration(
            color: AppColors.primary.withOpacity(0.1),
            borderRadius: BorderRadius.circular(2),
          ),
          child: Text(
            palace.daiVanLabel!,
            style: AppTypography.caption(sizeClass).copyWith(
              fontSize: 9,
              fontWeight: FontWeight.bold,
              color: AppColors.primary,
            ),
          ),
        ),
      );
    }

    // Tuần/Triệt markers
    if (palace.hasTuan) {
      footerItems.add(
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 2),
          decoration: BoxDecoration(
            color: Colors.purple.withOpacity(0.2),
            borderRadius: BorderRadius.circular(2),
          ),
          child: Text(
            'Tuần',
            style: AppTypography.caption(sizeClass).copyWith(
              fontSize: 7,
              color: Colors.purple,
            ),
          ),
        ),
      );
    }

    if (palace.hasTriet) {
      footerItems.add(
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 2),
          decoration: BoxDecoration(
            color: Colors.red.withOpacity(0.2),
            borderRadius: BorderRadius.circular(2),
          ),
          child: Text(
            'Triệt',
            style: AppTypography.caption(sizeClass).copyWith(
              fontSize: 7,
              color: Colors.red,
            ),
          ),
        ),
      );
    }

    if (footerItems.isEmpty) return const SizedBox(height: 12);

    return Container(
      padding: const EdgeInsets.only(top: 2),
      child: Wrap(
        spacing: 2,
        runSpacing: 2,
        alignment: WrapAlignment.center,
        children: footerItems,
      ),
    );
  }

  Color _getBackgroundColor(bool isDark) {
    if (palace.hasTuan || palace.hasTriet) {
      return isDark ? Colors.grey[850]! : Colors.grey[200]!;
    }
    return isDark ? AppColors.surfaceDark : Colors.white;
  }

  Color _getBorderColor(bool isDark) {
    if (palace.isThanCu) {
      return AppColors.accent;
    }
    return isDark ? Colors.grey[700]! : Colors.grey[300]!;
  }

  Color _getStarColor(StarInfo star) {
    // Color by NgũHành
    switch (star.nguHanh) {
      case 'KIM':
        return const Color(0xFFB8860B);
      case 'MOC':
        return const Color(0xFF228B22);
      case 'THUY':
        return const Color(0xFF1E90FF);
      case 'HOA':
        return const Color(0xFFFF4500);
      case 'THO':
        return const Color(0xFFDAA520);
      default:
        return Colors.grey[700]!;
    }
  }
}
