import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../domain/tuvi_chart_models.dart';

/// Center card displaying birth info and destiny calculations.
class CenterCard extends StatelessWidget {
  const CenterCard({
    super.key,
    required this.center,
  });

  final CenterInfo center;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final sizeClass = ScreenSizeClass.medium; // Default for center card

    return Container(
      decoration: BoxDecoration(
        color: isDark
            ? AppColors.surfaceDark
            : AppColors.primaryLight.withOpacity(0.1),
        borderRadius: BorderRadius.circular(AppRadius.medium),
        border: Border.all(
          color: AppColors.primary.withOpacity(0.3),
          width: 2,
        ),
      ),
      padding: const EdgeInsets.all(AppSpacing.s),
      child: SingleChildScrollView(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            // Name (if provided)
            if (center.name != null && center.name!.isNotEmpty) ...[
              Text(
                center.name!,
                style: AppTypography.headline2(sizeClass).copyWith(
                  color: AppColors.primary,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: AppSpacing.xs),
            ],

            // Year Can-Chi
            _buildInfoRow('Năm sinh:', center.lunarYearCanChi, sizeClass),

            // Solar date
            _buildInfoRow('Dương lịch:', center.solarDate, sizeClass),

            // Lunar month
            _buildInfoRow(
              'Tháng:',
              '${center.lunarMonth} (${center.lunarMonthCanChi})'
                  '${center.isLeapMonth ? ' - Nhuận' : ''}',
              sizeClass,
            ),

            // Lunar day
            _buildInfoRow(
              'Ngày:',
              '${center.lunarDay} (${center.lunarDayCanChi})',
              sizeClass,
            ),

            // Hour
            _buildInfoRow(
              'Giờ:',
              '${center.birthHour}:${center.birthMinute.toString().padLeft(2, '0')} (${center.birthHourCanChi})',
              sizeClass,
            ),

            const Divider(height: AppSpacing.m),

            // Gender & Direction
            _buildInfoRow(
              'Giới tính:',
              '${center.gender == 'male' ? 'Nam' : 'Nữ'} - ${center.amDuong}',
              sizeClass,
            ),
            _buildInfoRow('Lý:', center.thuanNghich, sizeClass),

            const Divider(height: AppSpacing.m),

            // Bản mệnh
            Container(
              padding: const EdgeInsets.symmetric(
                horizontal: AppSpacing.s,
                vertical: AppSpacing.xs,
              ),
              decoration: BoxDecoration(
                color: _getNguHanhColor(center.banMenhNguHanh).withOpacity(0.2),
                borderRadius: BorderRadius.circular(AppRadius.small),
              ),
              child: Text(
                center.banMenh,
                style: AppTypography.body1(sizeClass).copyWith(
                  fontWeight: FontWeight.bold,
                  color: _getNguHanhColor(center.banMenhNguHanh),
                ),
                textAlign: TextAlign.center,
              ),
            ),
            if (center.banMenhDescription != null)
              Text(
                center.banMenhDescription!,
                style: AppTypography.caption(sizeClass),
                textAlign: TextAlign.center,
              ),

            const SizedBox(height: AppSpacing.xs),

            // Cục
            _buildInfoRow('Cục:', center.cuc, sizeClass),
            if (center.menhCucRelation != null)
              Text(
                center.menhCucRelation!,
                style: AppTypography.caption(sizeClass).copyWith(
                  fontStyle: FontStyle.italic,
                ),
              ),

            const Divider(height: AppSpacing.m),

            // Chủ mệnh / Chủ thân
            if (center.chuMenh != null)
              _buildInfoRow('Chủ mệnh:', center.chuMenh!, sizeClass),
            if (center.chuThan != null)
              _buildInfoRow('Chủ thân:', center.chuThan!, sizeClass),
            if (center.thanCu != null)
              _buildInfoRow('Thân cư:', center.thanCu!, sizeClass),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value, ScreenSizeClass sizeClass) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            label,
            style: AppTypography.caption(sizeClass).copyWith(
              color: Colors.grey,
            ),
          ),
          const SizedBox(width: AppSpacing.xs),
          Flexible(
            child: Text(
              value,
              style: AppTypography.body2(sizeClass).copyWith(
                fontWeight: FontWeight.w600,
              ),
              overflow: TextOverflow.ellipsis,
            ),
          ),
        ],
      ),
    );
  }

  Color _getNguHanhColor(String nguHanh) {
    switch (nguHanh) {
      case 'KIM':
        return const Color(0xFFFFD700);
      case 'MOC':
        return const Color(0xFF228B22);
      case 'THUY':
        return const Color(0xFF1E90FF);
      case 'HOA':
        return const Color(0xFFFF4500);
      case 'THO':
        return const Color(0xFFDAA520);
      default:
        return Colors.grey;
    }
  }
}
