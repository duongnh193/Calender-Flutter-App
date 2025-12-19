import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../common_widgets/async/async_value_widget.dart';
import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/responsive_utils.dart';
import '../../../../core/utils/zodiac_utils.dart';
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
        // Extract year from birth date input
        int? birthYear;
        if (input.date != null) {
          try {
            final dateParts = input.date!.split('-');
            if (dateParts.isNotEmpty) {
              birthYear = int.tryParse(dateParts[0]);
            }
          } catch (_) {
            // Ignore parsing errors
          }
        }
        return _buildLifetimeContent(result, birthYear);
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

  Widget _buildLifetimeContent(LifetimeByBirthResponse result, int? birthYear) {
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
            year: birthYear,
          ),
          const SizedBox(height: AppSpacing.xl),
          
          // I. Tổng quát
          if (result.overview != null && result.overview!.isNotEmpty)
            HoroscopeResultSection(
              title: 'I. Tổng quát',
              content: result.overview!,
              sizeClass: sizeClass,
            ),
          
          // II. Tình duyên (with month groups)
          if (result.loveByMonthGroup1 != null || result.loveByMonthGroup2 != null || result.loveByMonthGroup3 != null)
            _buildLoveByMonthSection(result, sizeClass)
          else if (result.love != null && result.love!.isNotEmpty)
            HoroscopeResultSection(
              title: 'II. Tình duyên',
              content: result.love!,
              sizeClass: sizeClass,
            ),
          
          // III. Gia đình sự nghiệp
          if (result.family != null && result.family!.isNotEmpty || result.career != null && result.career!.isNotEmpty)
            _buildFamilyCareerSection(result, sizeClass),
          
          // IV. Tài vận
          if (result.fortune != null && result.fortune!.isNotEmpty)
            HoroscopeResultSection(
              title: 'IV. Tài vận',
              content: result.fortune!,
              sizeClass: sizeClass,
            ),
          
          // V. Tuổi hợp làm ăn
          if (result.compatibleAges != null && result.compatibleAges!.isNotEmpty)
            _buildCompatibleAgesSection(result.compatibleAges!, sizeClass),
          
          // VI. Năm khó khăn nhất
          if (result.difficultYears != null && result.difficultYears!.isNotEmpty)
            _buildDifficultYearsSection(result.difficultYears!, sizeClass),
          
          // VII. Diễn biến từng năm
          if (result.yearlyProgression != null && result.yearlyProgression!.isNotEmpty)
            _buildYearlyProgressionSection(result.yearlyProgression!, sizeClass),
          
          // VIII. Tuổi đại kỵ
          if (result.incompatibleAges != null && result.incompatibleAges!.isNotEmpty)
            _buildIncompatibleAgesSection(result.incompatibleAges!, sizeClass),
          
          // IX. Nghi lễ cúng sao
          if (result.ritualGuidance != null && result.ritualGuidance!.isNotEmpty)
            HoroscopeResultSection(
              title: 'IX. Nghi lễ cúng sao',
              content: result.ritualGuidance!,
              sizeClass: sizeClass,
            ),
          
          // Health section (if exists)
          if (result.health != null && result.health!.isNotEmpty)
            HoroscopeResultSection(
              title: 'Sức khỏe',
              content: result.health!,
              sizeClass: sizeClass,
            ),
          
          // Unlucky section (if exists)
          if (result.unlucky != null && result.unlucky!.isNotEmpty)
            HoroscopeResultSection(
              title: 'Điều cần tránh',
              content: result.unlucky!,
              sizeClass: sizeClass,
            ),
          
          // Advice section (if exists)
          if (result.advice != null && result.advice!.isNotEmpty)
            HoroscopeResultSection(
              title: 'Lời khuyên',
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
          
          // Summary section (fallback if new fields don't exist)
          if (result.summary != null && result.summary!.isNotEmpty)
            HoroscopeResultSection(
              title: 'I. Tổng quan',
              content: result.summary!,
              sizeClass: sizeClass,
            ),
          
          // I. Cung Mệnh
          if (result.cungMenh != null && result.cungMenh!.isNotEmpty)
            HoroscopeResultSection(
              title: 'II. Cung Mệnh (Cung Tiểu Vận)',
              content: result.cungMenh!,
              sizeClass: sizeClass,
            ),
          
          // II. Cung Xung Chiếu
          if (result.cungXungChieu != null && result.cungXungChieu!.isNotEmpty)
            HoroscopeResultSection(
              title: 'III. Cung Xung Chiếu',
              content: result.cungXungChieu!,
              sizeClass: sizeClass,
            ),
          
          // III. Cung Tam Hợp
          if (result.cungTamHop != null && result.cungTamHop!.isNotEmpty)
            HoroscopeResultSection(
              title: 'IV. Cung Tam Hợp',
              content: result.cungTamHop!,
              sizeClass: sizeClass,
            ),
          
          // IV. Cung Nhị Hợp
          if (result.cungNhiHop != null && result.cungNhiHop!.isNotEmpty)
            HoroscopeResultSection(
              title: 'V. Cung Nhị Hợp',
              content: result.cungNhiHop!,
              sizeClass: sizeClass,
            ),
          
          // V. Vận hạn
          if (result.vanHan != null && result.vanHan!.isNotEmpty)
            _buildVanHanSection(result.vanHan!, sizeClass),
          
          // VI. Tứ trụ
          if (result.tuTru != null && result.tuTru!.isNotEmpty)
            _buildTuTruSection(result.tuTru!, sizeClass),
          
          // VII. Luận giải theo phương diện
          _buildYearlyAspectsSection(result, sizeClass),
          
          // VIII. Dự đoán theo tháng
          if (result.monthlyBreakdown != null && result.monthlyBreakdown!.isNotEmpty)
            _buildMonthlyBreakdownSection(result.monthlyBreakdown!, result.year, sizeClass),
          
          // IX. Phong thủy may mắn
          if (result.phongThuy != null && result.phongThuy!.isNotEmpty)
            _buildPhongThuySection(result.phongThuy!, sizeClass),
          
          // X. Q&A Section
          if (result.qaSection != null && result.qaSection!.isNotEmpty)
            _buildQASection(result.qaSection!, sizeClass),
          
          // XI. Lời kết
          if (result.conclusion != null && result.conclusion!.isNotEmpty)
            HoroscopeResultSection(
              title: 'Lời kết',
              content: result.conclusion!,
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
      alignment: Alignment.center,
      padding: const EdgeInsets.all(AppSpacing.l),
      decoration: BoxDecoration(
        color: AppColors.cardBackground,
        borderRadius: BorderRadius.circular(AppRadius.medium),
        border: Border.all(color: AppColors.dividerColor),
      ),
      child: Column(
        children: [
          // Zodiac image
          Builder(
            builder: (context) {
              final imagePath = ZodiacUtils.getZodiacImagePath(zodiacCode);
              return Container(
            width: 80,
            height: 80,
            decoration: BoxDecoration(
              color: AppColors.primaryRed.withValues(alpha: 0.1),
              shape: BoxShape.circle,
            ),
                child: imagePath != null
                    ? ClipOval(
                        child: Image.asset(
                          imagePath,
                          width: 80,
                          height: 80,
                          fit: BoxFit.cover,
                          errorBuilder: (context, error, stackTrace) {
                            // Fallback to icon if image fails to load
                            return Icon(
                              Icons.star,
                              size: 40,
                              color: AppColors.primaryRed,
                            );
                          },
                        ),
                      )
                    : Icon(
                        Icons.star,
              size: 40,
              color: AppColors.primaryRed,
            ),
              );
            },
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
              '${canChi.toUpperCase()}',
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
      final yearText = year != null ? ' năm $year' : '';
      return 'Tử vi tuổi $canChi$yearText ${gender == "male" ? "nam" : "nữ"} mạng';
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

  // Helper methods for new sections
  Widget _buildLoveByMonthSection(LifetimeByBirthResponse result, ScreenSizeClass sizeClass) {
    final content = StringBuffer();
    content.writeln('Vấn đề nhân duyên được chia thành ba trường hợp sau:\n');
    
    if (result.loveByMonthGroup1 != null && result.loveByMonthGroup1!.isNotEmpty) {
      content.writeln(result.loveByMonthGroup1!);
      content.writeln('\n');
    }
    if (result.loveByMonthGroup2 != null && result.loveByMonthGroup2!.isNotEmpty) {
      content.writeln(result.loveByMonthGroup2!);
      content.writeln('\n');
    }
    if (result.loveByMonthGroup3 != null && result.loveByMonthGroup3!.isNotEmpty) {
      content.writeln(result.loveByMonthGroup3!);
    }
    
    return HoroscopeResultSection(
      title: 'II. Tình duyên',
      content: content.toString(),
      sizeClass: sizeClass,
    );
  }

  Widget _buildFamilyCareerSection(LifetimeByBirthResponse result, ScreenSizeClass sizeClass) {
    final content = StringBuffer();
    if (result.family != null && result.family!.isNotEmpty) {
      content.writeln(result.family!);
      content.writeln('\n');
    }
    if (result.career != null && result.career!.isNotEmpty) {
      content.writeln(result.career!);
    }
    
    if (content.isEmpty) return const SizedBox.shrink();
    
    return HoroscopeResultSection(
      title: 'III. Gia đình sự nghiệp',
      content: content.toString(),
      sizeClass: sizeClass,
    );
  }

  Widget _buildCompatibleAgesSection(List<String> compatibleAges, ScreenSizeClass sizeClass) {
    final content = 'Tuổi hợp làm ăn: ${compatibleAges.join(', ')}. '
        'Nên chọn các tuổi này để làm ăn chung, sẽ đạt được sự thịnh vượng, thuận tiện, may mắn và thành công ngày càng tăng.';
    
    return HoroscopeResultSection(
      title: 'V. Tuổi hợp làm ăn',
      content: content,
      sizeClass: sizeClass,
    );
  }

  Widget _buildDifficultYearsSection(List<int> difficultYears, ScreenSizeClass sizeClass) {
    final yearsText = difficultYears.map((y) => '$y tuổi').join(', ');
    final content = 'Năm khó khăn nhất: Cần thận trọng vào các năm $yearsText. '
        'Trong những năm này, có thể gặp nhiều khó khăn trong tình cảm và gia đình, '
        'cần cẩn thận trong công việc, nên trì hoãn các ý tưởng mới hoặc kế hoạch lớn sang các năm khác để có kết quả tốt hơn.';
    
    return HoroscopeResultSection(
      title: 'VI. Năm khó khăn nhất',
      content: content,
      sizeClass: sizeClass,
    );
  }

  Widget _buildYearlyProgressionSection(Map<String, String> yearlyProgression, ScreenSizeClass sizeClass) {
    final content = StringBuffer();
    content.writeln('Diễn biến từng năm:\n');
    
    final sortedKeys = yearlyProgression.keys.toList()..sort();
    for (final key in sortedKeys) {
      final value = yearlyProgression[key];
      if (value != null && value.isNotEmpty) {
        content.writeln('Từ năm $key:');
        content.writeln(value);
        content.writeln('\n');
      }
    }
    
    return HoroscopeResultSection(
      title: 'VII. Diễn biến từng năm',
      content: content.toString(),
      sizeClass: sizeClass,
    );
  }

  Widget _buildIncompatibleAgesSection(List<String> incompatibleAges, ScreenSizeClass sizeClass) {
    final agesText = incompatibleAges.join(', ');
    final content = 'Tuổi đại kỵ: Không nên kết hôn hay cộng tác trong công việc với những tuổi xung khắc: $agesText. '
        'Nếu vẫn muốn kết hôn, nên tránh tổ chức cưới xin rầm rộ mà chỉ được làm mâm cơm cúng gia tộc tổ tiên. '
        'Quan trọng nhất vẫn là ăn ở hiền lành, làm nhiều việc thiện, tích nhiều phúc đức thì mọi chuyện xấu sẽ được hóa giải.';
    
    return HoroscopeResultSection(
      title: 'VIII. Tuổi đại kỵ',
      content: content,
      sizeClass: sizeClass,
    );
  }

  // Yearly helper methods
  Widget _buildVanHanSection(Map<String, dynamic> vanHan, ScreenSizeClass sizeClass) {
    final content = StringBuffer();
    if (vanHan['han_nam'] != null) {
      content.writeln(vanHan['han_nam'].toString());
      content.writeln('\n');
    }
    if (vanHan['han_tuoi'] != null) {
      content.writeln('Hạn tuổi: ${vanHan['han_tuoi']}');
      content.writeln('\n');
    }
    if (vanHan['hoa_giai'] != null) {
      content.writeln('Cách hóa giải: ${vanHan['hoa_giai']}');
    }
    
    return HoroscopeResultSection(
      title: 'V. Vận hạn',
      content: content.toString(),
      sizeClass: sizeClass,
    );
  }

  Widget _buildTuTruSection(Map<String, dynamic> tuTru, ScreenSizeClass sizeClass) {
    final content = StringBuffer();
    if (tuTru['tong_quan'] != null) {
      content.writeln(tuTru['tong_quan'].toString());
      content.writeln('\n');
    }
    if (tuTru['cong_viec'] != null) {
      content.writeln('Công việc: ${tuTru['cong_viec']}');
      content.writeln('\n');
    }
    if (tuTru['tai_chinh'] != null) {
      content.writeln('Tài chính: ${tuTru['tai_chinh']}');
      content.writeln('\n');
    }
    if (tuTru['suc_khoe'] != null) {
      content.writeln('Sức khỏe: ${tuTru['suc_khoe']}');
    }
    
    return HoroscopeResultSection(
      title: 'VI. Tứ trụ',
      content: content.toString(),
      sizeClass: sizeClass,
    );
  }

  Widget _buildYearlyAspectsSection(HoroscopeYearly result, ScreenSizeClass sizeClass) {
    final sections = <Widget>[];
    
    if (result.career != null && result.career!.isNotEmpty) {
      sections.add(HoroscopeResultSection(
        title: '1. Công danh sự nghiệp',
        content: result.career!,
        sizeClass: sizeClass,
      ));
    }
    
    if (result.fortune != null && result.fortune!.isNotEmpty) {
      sections.add(HoroscopeResultSection(
        title: '2. Tài chính',
        content: result.fortune!,
        sizeClass: sizeClass,
      ));
    }
    
    if (result.love != null && result.love!.isNotEmpty) {
      sections.add(HoroscopeResultSection(
        title: '3. Tình duyên, gia đạo',
        content: result.love!,
        sizeClass: sizeClass,
      ));
    }
    
    if (result.health != null && result.health!.isNotEmpty) {
      sections.add(HoroscopeResultSection(
        title: '4. Sức khỏe',
        content: result.health!,
        sizeClass: sizeClass,
      ));
    }
    
    if (sections.isEmpty) return const SizedBox.shrink();
    
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          margin: const EdgeInsets.only(bottom: AppSpacing.m),
          child: Text(
            'VII. Luận giải tử vi trên các phương diện cuộc sống',
            style: AppTypography.headline2(sizeClass).copyWith(
              fontWeight: FontWeight.bold,
              color: AppColors.primaryRed,
            ),
          ),
        ),
        ...sections,
      ],
    );
  }

  Widget _buildMonthlyBreakdownSection(Map<String, String> monthlyBreakdown, int year, ScreenSizeClass sizeClass) {
    final content = StringBuffer();
    content.writeln('IV. Dự đoán tử vi theo tháng năm $year\n');
    
    final sortedKeys = monthlyBreakdown.keys.toList()
      ..sort((a, b) => int.tryParse(a)?.compareTo(int.tryParse(b) ?? 0) ?? 0);
    
    for (final monthKey in sortedKeys) {
      final month = int.tryParse(monthKey);
      if (month != null && month >= 1 && month <= 12) {
        final value = monthlyBreakdown[monthKey];
        if (value != null && value.isNotEmpty) {
          content.writeln('- Tử vi tháng $month/$year:');
          content.writeln(value);
          content.writeln('\n');
        }
      }
    }
    
    return HoroscopeResultSection(
      title: 'VIII. Dự đoán theo tháng',
      content: content.toString(),
      sizeClass: sizeClass,
    );
  }

  Widget _buildPhongThuySection(Map<String, dynamic> phongThuy, ScreenSizeClass sizeClass) {
    final content = StringBuffer();
    content.writeln('V. Phong thủy may mắn đầu năm\n');
    
    if (phongThuy['nguoi_xong_nha'] != null) {
      content.writeln('1. Chọn người xông nhà hợp tuổi: Nên mời người sinh năm ${phongThuy['nguoi_xong_nha']} đến xông đất để mang lại nhiều may mắn, giúp mọi việc thuận lợi, tài lộc dồi dào và gia đình hạnh phúc, hòa thuận.');
      content.writeln('\n');
    }
    
    if (phongThuy['ngay_xuat_hanh'] != null || phongThuy['huong'] != null) {
      content.writeln('2. Chọn ngày đẹp, hướng tốt xuất hành khai xuân:');
      if (phongThuy['ngay_xuat_hanh'] != null) {
        content.writeln('   Ngày lý tưởng để xuất hành: ${phongThuy['ngay_xuat_hanh']}');
      }
      if (phongThuy['huong'] != null) {
        content.writeln('   Hướng xuất hành thuận lợi: ${phongThuy['huong']}');
      }
      content.writeln('\n');
    }
    
    if (phongThuy['ngay_khai_truong'] != null) {
      content.writeln('3. Chọn ngày tốt khai trương: ${phongThuy['ngay_khai_truong']}');
    }
    
    return HoroscopeResultSection(
      title: 'IX. Phong thủy may mắn',
      content: content.toString(),
      sizeClass: sizeClass,
    );
  }

  Widget _buildQASection(List<Map<String, String>> qaSection, ScreenSizeClass sizeClass) {
    final qaItems = <Widget>[];
    
    for (int i = 0; i < qaSection.length; i++) {
      final qa = qaSection[i];
      final question = qa['question'] ?? '';
      final answer = qa['answer'] ?? '';
      
      if (question.isNotEmpty && answer.isNotEmpty) {
        qaItems.add(
          Padding(
            padding: EdgeInsets.only(
              bottom: i < qaSection.length - 1 ? AppSpacing.l : 0,
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  question,
                  style: AppTypography.headline2(sizeClass).copyWith(
                    fontWeight: FontWeight.bold,
                    color: AppColors.primaryRed,
                    fontSize: (AppTypography.headline2(sizeClass).fontSize ?? 20) * 0.85,
                  ),
                ),
                const SizedBox(height: AppSpacing.m),
                Text(
                  answer,
                  style: AppTypography.body1(sizeClass).copyWith(
                    height: 1.6,
                    color: AppColors.textPrimary,
                  ),
                ),
              ],
            ),
          ),
        );
      }
    }
    
    if (qaItems.isEmpty) return const SizedBox.shrink();
    
    // Wrap in same container style as HoroscopeResultSection
    return Container(
      margin: const EdgeInsets.only(bottom: AppSpacing.l),
      padding: const EdgeInsets.all(AppSpacing.l),
      decoration: BoxDecoration(
        color: AppColors.cardBackground,
        borderRadius: BorderRadius.circular(AppRadius.medium),
        border: Border.all(color: AppColors.dividerColor),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Section title
          Text(
            'X. Câu hỏi thường gặp',
            style: AppTypography.headline2(sizeClass).copyWith(
              fontWeight: FontWeight.bold,
              color: AppColors.primaryRed,
              fontSize: (AppTypography.headline2(sizeClass).fontSize ?? 20) * 0.9,
            ),
          ),
          const SizedBox(height: AppSpacing.m),
          // Q&A items
          ...qaItems,
        ],
      ),
    );
  }
}

