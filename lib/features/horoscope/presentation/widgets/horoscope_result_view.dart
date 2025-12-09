import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../common_widgets/async/async_value_widget.dart';
import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/responsive_utils.dart';
import '../../domain/horoscope_models.dart';
import '../screens/horoscope_screen.dart';
import '../providers/horoscope_providers.dart';
import 'horoscope_result_section.dart';

class HoroscopeResultView extends ConsumerWidget {
  const HoroscopeResultView({
    super.key,
    required this.type,
    required this.sizeClass,
    required this.onShowInput,
  });

  final HoroscopeType type;
  final ScreenSizeClass sizeClass;
  final VoidCallback onShowInput;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Container(
      color: Colors.white,
      child: _buildContent(context, ref),
    );
  }

  Widget _buildContent(BuildContext context, WidgetRef ref) {
    switch (type) {
      case HoroscopeType.lifetime:
        return _buildLifetimeView(context, ref);
      case HoroscopeType.yearly:
        return _buildYearlyView(context, ref);
      case HoroscopeType.monthly:
        return _buildMonthlyView(context, ref);
      case HoroscopeType.daily:
        return _buildDailyView(context, ref);
    }
  }

  Widget _buildLifetimeView(BuildContext context, WidgetRef ref) {
    final asyncResult = ref.watch(lifetimeByBirthResultProvider);
    final input = ref.watch(lifetimeByBirthInputProvider);

    if (input.date == null) {
      return _buildEmptyState(
        'Nhấn để xem tử vi trọn đời',
        'Chọn ngày sinh, giờ sinh và giới tính để xem tử vi',
      );
    }

    return AsyncValueWidget<LifetimeByBirthResponse?>(
      value: asyncResult,
      data: (result) {
        if (result == null) {
          return _buildEmptyState(
            'Chưa có dữ liệu',
            'Vui lòng nhập thông tin để xem tử vi',
          );
        }
        return _buildLifetimeContent(result);
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stack) => _buildErrorState(error),
    );
  }

  Widget _buildYearlyView(BuildContext context, WidgetRef ref) {
    final asyncResult = ref.watch(yearlyHoroscopeResultProvider);
    final input = ref.watch(yearlyHoroscopeInputProvider);

    if (input.year == null) {
      return _buildEmptyState(
        'Nhấn để xem tử vi năm',
        'Chọn năm và cung hoàng đạo',
      );
    }

    return AsyncValueWidget<HoroscopeYearly?>(
      value: asyncResult,
      data: (result) {
        if (result == null) {
          return _buildEmptyState(
            'Chưa có dữ liệu',
            'Vui lòng nhập thông tin để xem tử vi',
          );
        }
        return _buildYearlyContent(result);
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stack) => _buildErrorState(error),
    );
  }

  Widget _buildMonthlyView(BuildContext context, WidgetRef ref) {
    final asyncResult = ref.watch(monthlyHoroscopeResultProvider);
    final input = ref.watch(monthlyHoroscopeInputProvider);

    if (input.year == null || input.month == null) {
      return _buildEmptyState(
        'Nhấn để xem tử vi tháng',
        'Chọn tháng, năm và cung hoàng đạo',
      );
    }

    return AsyncValueWidget<HoroscopeMonthly?>(
      value: asyncResult,
      data: (result) {
        if (result == null) {
          return _buildEmptyState(
            'Chưa có dữ liệu',
            'Vui lòng nhập thông tin để xem tử vi',
          );
        }
        return _buildMonthlyContent(result);
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stack) => _buildErrorState(error),
    );
  }

  Widget _buildDailyView(BuildContext context, WidgetRef ref) {
    final asyncResult = ref.watch(dailyHoroscopeResultProvider);
    final input = ref.watch(dailyHoroscopeInputProvider);

    if (input.date == null) {
      return _buildEmptyState(
        'Nhấn để xem tử vi ngày',
        'Chọn ngày và cung hoàng đạo',
      );
    }

    return AsyncValueWidget<HoroscopeDaily?>(
      value: asyncResult,
      data: (result) {
        if (result == null) {
          return _buildEmptyState(
            'Chưa có dữ liệu',
            'Vui lòng nhập thông tin để xem tử vi',
          );
        }
        return _buildDailyContent(result);
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stack) => _buildErrorState(error),
    );
  }

  Widget _buildEmptyState(String title, String subtitle) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AppSpacing.xl),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              Icons.auto_awesome,
              size: 64,
              color: AppColors.textSecondary.withValues(alpha: 0.5),
            ),
            const SizedBox(height: AppSpacing.l),
            Text(
              title,
              style: AppTypography.headline2(sizeClass),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: AppSpacing.s),
            Text(
              subtitle,
              style: AppTypography.body2(sizeClass).copyWith(
                color: AppColors.textSecondary,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: AppSpacing.xl),
            ElevatedButton.icon(
              onPressed: onShowInput,
              icon: const Icon(Icons.edit),
              label: const Text('Nhập thông tin'),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primaryRed,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(
                  horizontal: AppSpacing.xl,
                  vertical: AppSpacing.m,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildErrorState(Object error) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AppSpacing.xl),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(
              Icons.error_outline,
              size: 64,
              color: AppColors.primaryRed,
            ),
            const SizedBox(height: AppSpacing.l),
            Text(
              'Đã xảy ra lỗi',
              style: AppTypography.headline2(sizeClass),
            ),
            const SizedBox(height: AppSpacing.s),
            Text(
              error.toString(),
              style: AppTypography.body2(sizeClass).copyWith(
                color: AppColors.textSecondary,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: AppSpacing.xl),
            ElevatedButton.icon(
              onPressed: onShowInput,
              icon: const Icon(Icons.refresh),
              label: const Text('Thử lại'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildLifetimeContent(LifetimeByBirthResponse result) {
    return SingleChildScrollView(
      padding: EdgeInsets.symmetric(
        horizontal: horizontalPaddingFor(sizeClass),
        vertical: AppSpacing.l,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header with zodiac info
          _buildZodiacHeader(
            zodiacName: result.zodiacName ?? 'N/A',
            zodiacCode: result.zodiacCode ?? 'N/A',
            canChi: result.canChi,
            gender: result.gender,
            hourBranch: result.hourBranchName,
          ),
          const SizedBox(height: AppSpacing.xl),
          // Overview section
          if (result.overview != null && result.overview!.isNotEmpty)
            HoroscopeResultSection(
              title: 'I. Thông tin tổng quan',
              content: result.overview!,
              sizeClass: sizeClass,
            ),
          // Career section
          if (result.career != null && result.career!.isNotEmpty)
            HoroscopeResultSection(
              title: 'II. Sự nghiệp',
              content: result.career!,
              sizeClass: sizeClass,
            ),
          // Love section
          if (result.love != null && result.love!.isNotEmpty)
            HoroscopeResultSection(
              title: 'III. Tình duyên',
              content: result.love!,
              sizeClass: sizeClass,
            ),
          // Health section
          if (result.health != null && result.health!.isNotEmpty)
            HoroscopeResultSection(
              title: 'IV. Sức khỏe',
              content: result.health!,
              sizeClass: sizeClass,
            ),
          // Family section
          if (result.family != null && result.family!.isNotEmpty)
            HoroscopeResultSection(
              title: 'V. Gia đình',
              content: result.family!,
              sizeClass: sizeClass,
            ),
          // Fortune section
          if (result.fortune != null && result.fortune!.isNotEmpty)
            HoroscopeResultSection(
              title: 'VI. Tài lộc',
              content: result.fortune!,
              sizeClass: sizeClass,
            ),
          // Unlucky section
          if (result.unlucky != null && result.unlucky!.isNotEmpty)
            HoroscopeResultSection(
              title: 'VII. Điều cần tránh',
              content: result.unlucky!,
              sizeClass: sizeClass,
            ),
          // Advice section
          if (result.advice != null && result.advice!.isNotEmpty)
            HoroscopeResultSection(
              title: 'VIII. Lời khuyên',
              content: result.advice!,
              sizeClass: sizeClass,
            ),
          // Fallback message
          if (result.isFallback && result.message != null)
            Container(
              padding: const EdgeInsets.all(AppSpacing.m),
              margin: const EdgeInsets.only(top: AppSpacing.l),
              decoration: BoxDecoration(
                color: AppColors.accentYellow.withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(AppRadius.medium),
                border: Border.all(
                  color: AppColors.accentYellow,
                  width: 1,
                ),
              ),
              child: Row(
                children: [
                  Icon(
                    Icons.info_outline,
                    color: AppColors.accentYellow,
                    size: 20,
                  ),
                  const SizedBox(width: AppSpacing.s),
                  Expanded(
                    child: Text(
                      result.message!,
                      style: AppTypography.body2(sizeClass).copyWith(
                        color: AppColors.textPrimary,
                      ),
                    ),
                  ),
                ],
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildYearlyContent(HoroscopeYearly result) {
    return SingleChildScrollView(
      padding: EdgeInsets.symmetric(
        horizontal: horizontalPaddingFor(sizeClass),
        vertical: AppSpacing.l,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildZodiacHeader(
            zodiacName: result.zodiac.nameVi,
            zodiacCode: result.zodiac.code,
            canChi: null,
            gender: null,
            year: result.year,
          ),
          const SizedBox(height: AppSpacing.xl),
          if (result.summary != null && result.summary!.isNotEmpty)
            HoroscopeResultSection(
              title: 'I. Tổng quan',
              content: result.summary!,
              sizeClass: sizeClass,
            ),
          if (result.career != null && result.career!.isNotEmpty)
            HoroscopeResultSection(
              title: 'II. Sự nghiệp',
              content: result.career!,
              sizeClass: sizeClass,
            ),
          if (result.love != null && result.love!.isNotEmpty)
            HoroscopeResultSection(
              title: 'III. Tình duyên',
              content: result.love!,
              sizeClass: sizeClass,
            ),
          if (result.health != null && result.health!.isNotEmpty)
            HoroscopeResultSection(
              title: 'IV. Sức khỏe',
              content: result.health!,
              sizeClass: sizeClass,
            ),
          if (result.fortune != null && result.fortune!.isNotEmpty)
            HoroscopeResultSection(
              title: 'V. Tài lộc',
              content: result.fortune!,
              sizeClass: sizeClass,
            ),
        ],
      ),
    );
  }

  Widget _buildMonthlyContent(HoroscopeMonthly result) {
    return SingleChildScrollView(
      padding: EdgeInsets.symmetric(
        horizontal: horizontalPaddingFor(sizeClass),
        vertical: AppSpacing.l,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildZodiacHeader(
            zodiacName: result.zodiac.nameVi,
            zodiacCode: result.zodiac.code,
            canChi: null,
            gender: null,
            year: result.year,
            month: result.month,
          ),
          const SizedBox(height: AppSpacing.xl),
          if (result.summary != null && result.summary!.isNotEmpty)
            HoroscopeResultSection(
              title: 'I. Tổng quan',
              content: result.summary!,
              sizeClass: sizeClass,
            ),
          if (result.career != null && result.career!.isNotEmpty)
            HoroscopeResultSection(
              title: 'II. Sự nghiệp',
              content: result.career!,
              sizeClass: sizeClass,
            ),
          if (result.love != null && result.love!.isNotEmpty)
            HoroscopeResultSection(
              title: 'III. Tình duyên',
              content: result.love!,
              sizeClass: sizeClass,
            ),
          if (result.health != null && result.health!.isNotEmpty)
            HoroscopeResultSection(
              title: 'IV. Sức khỏe',
              content: result.health!,
              sizeClass: sizeClass,
            ),
          if (result.fortune != null && result.fortune!.isNotEmpty)
            HoroscopeResultSection(
              title: 'V. Tài lộc',
              content: result.fortune!,
              sizeClass: sizeClass,
            ),
        ],
      ),
    );
  }

  Widget _buildDailyContent(HoroscopeDaily result) {
    return SingleChildScrollView(
      padding: EdgeInsets.symmetric(
        horizontal: horizontalPaddingFor(sizeClass),
        vertical: AppSpacing.l,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildZodiacHeader(
            zodiacName: result.zodiac.nameVi,
            zodiacCode: result.zodiac.code,
            canChi: null,
            gender: null,
            date: result.solarDate,
          ),
          const SizedBox(height: AppSpacing.xl),
          if (result.summary != null && result.summary!.isNotEmpty)
            HoroscopeResultSection(
              title: 'I. Tổng quan',
              content: result.summary!,
              sizeClass: sizeClass,
            ),
          if (result.career != null && result.career!.isNotEmpty)
            HoroscopeResultSection(
              title: 'II. Sự nghiệp',
              content: result.career!,
              sizeClass: sizeClass,
            ),
          if (result.love != null && result.love!.isNotEmpty)
            HoroscopeResultSection(
              title: 'III. Tình duyên',
              content: result.love!,
              sizeClass: sizeClass,
            ),
          if (result.health != null && result.health!.isNotEmpty)
            HoroscopeResultSection(
              title: 'IV. Sức khỏe',
              content: result.health!,
              sizeClass: sizeClass,
            ),
          if (result.fortune != null && result.fortune!.isNotEmpty)
            HoroscopeResultSection(
              title: 'V. Tài lộc',
              content: result.fortune!,
              sizeClass: sizeClass,
            ),
        ],
      ),
    );
  }

  Widget _buildZodiacHeader({
    required String zodiacName,
    required String zodiacCode,
    String? canChi,
    String? gender,
    String? hourBranch,
    int? year,
    int? month,
    DateTime? date,
  }) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.l),
      decoration: BoxDecoration(
        color: AppColors.cardBackground,
        borderRadius: BorderRadius.circular(AppRadius.medium),
        border: Border.all(color: AppColors.dividerColor),
      ),
      child: Column(
        children: [
          // Zodiac icon placeholder
          Container(
            width: 80,
            height: 80,
            decoration: BoxDecoration(
              color: AppColors.primaryRed.withValues(alpha: 0.1),
              shape: BoxShape.circle,
            ),
            child: Icon(
              _getZodiacIcon(zodiacCode),
              size: 40,
              color: AppColors.primaryRed,
            ),
          ),
          const SizedBox(height: AppSpacing.m),
          // Title
          Text(
            _buildTitle(canChi, gender, year, month, date),
            style: AppTypography.headline2(sizeClass).copyWith(
              fontWeight: FontWeight.bold,
            ),
            textAlign: TextAlign.center,
          ),
          if (canChi != null && gender != null) ...[
            const SizedBox(height: AppSpacing.s),
            Text(
              '${canChi.toUpperCase()} - ${gender.toUpperCase()}',
              style: AppTypography.body1(sizeClass).copyWith(
                color: AppColors.textSecondary,
              ),
            ),
          ],
          if (hourBranch != null) ...[
            const SizedBox(height: AppSpacing.xs),
            Text(
              'Canh giờ: $hourBranch',
              style: AppTypography.body2(sizeClass).copyWith(
                color: AppColors.textSecondary,
              ),
            ),
          ],
        ],
      ),
    );
  }

  String _buildTitle(
    String? canChi,
    String? gender,
    int? year,
    int? month,
    DateTime? date,
  ) {
    if (canChi != null && gender != null) {
      return 'Tử vi tuổi $canChi ${gender == "male" ? "nam" : "nữ"} mạng';
    }
    if (year != null && month != null) {
      return 'Tử vi năm $year tháng $month';
    }
    if (year != null) {
      return 'Tử vi năm $year';
    }
    if (date != null) {
      return 'Tử vi ngày ${date.day}/${date.month}/${date.year}';
    }
    return 'Tử vi';
  }

  IconData _getZodiacIcon(String code) {
    // Map zodiac codes to icons
    switch (code.toLowerCase()) {
      case 'ti':
        return Icons.mouse;
      case 'suu':
        return Icons.agriculture;
      case 'dan':
        return Icons.pets;
      case 'mao':
        return Icons.cruelty_free;
      case 'thin':
        return Icons.water_drop;
      case 'ty':
        return Icons.grass;
      case 'ngo':
        return Icons.bolt;
      case 'mui':
        return Icons.emoji_nature;
      case 'than':
        return Icons.forest;
      case 'dau':
        return Icons.pets;
      case 'tuat':
        return Icons.pets;
      case 'hoi':
        return Icons.water_drop;
      default:
        return Icons.star;
    }
  }
}

