import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/date_time_utils.dart';
import '../../../../core/utils/responsive_utils.dart';
import '../../../../core/di/providers.dart';
import '../../application/calendar_providers.dart';
import '../../domain/day_info.dart';
import '../widgets/daily_header_month_chip.dart';
import '../widgets/daily_quote_section.dart';
import '../widgets/daily_weather_row.dart';
import '../widgets/date_detail_grid.dart';

class DailyScreen extends ConsumerWidget {
  const DailyScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final sizeClass = getSizeClass(constraints.maxWidth);
        final selectedDate = ref.watch(selectedDateProvider);
        final asyncDay = ref.watch(dayInfoProvider(selectedDate));

        final monthLabel =
            'Tháng ${selectedDate.month.toString().padLeft(2, '0')} - ${selectedDate.year}';

        final horizontalPadding = horizontalPaddingFor(sizeClass);

        return Scaffold(
          backgroundColor: Colors.transparent,
          body: Stack(
            children: [
              const Positioned.fill(
                child: DecoratedBox(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                      colors: [
                        AppColors.backgroundDailyStart,
                        AppColors.backgroundDailyEnd,
                      ],
                    ),
                  ),
                ),
              ),
              SafeArea(
                top: true,
                bottom: false,
                child: asyncDay.when(
                  loading: () => _buildContent(
                    context,
                    sizeClass: sizeClass,
                    selectedDate: selectedDate,
                    monthLabel: monthLabel,
                    horizontalPadding: horizontalPadding,
                    dayInfo: null,
                    isLoading: true,
                  ),
                  error: (error, stack) => _buildErrorState(
                    context,
                    sizeClass: sizeClass,
                    error: error,
                    onRetry: () => ref.invalidate(dayInfoProvider(selectedDate)),
                  ),
                  data: (dayInfo) => _buildContent(
                    context,
                    sizeClass: sizeClass,
                    selectedDate: selectedDate,
                    monthLabel: monthLabel,
                    horizontalPadding: horizontalPadding,
                    dayInfo: dayInfo,
                    isLoading: false,
                  ),
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _buildContent(
    BuildContext context, {
    required ScreenSizeClass sizeClass,
    required DateTime selectedDate,
    required String monthLabel,
    required double horizontalPadding,
    required DayInfo? dayInfo,
    required bool isLoading,
  }) {
    final lunar = dayInfo?.lunar;
    final canChi = dayInfo?.canChi;

    final weekdayLabel = DateTimeUtils.getVietnameseWeekday(selectedDate).toUpperCase();

    final lunarDayLabel = lunar != null ? '${lunar.day}' : '--';
    final lunarMonthLabel = lunar != null ? '${lunar.month}' : '--';
    final lunarYearLabel = lunar != null ? '${lunar.year}' : '--';

    final canChiDayLabel = canChi?.day ?? '--';
    final canChiMonthLabel = canChi?.month ?? '--';
    final canChiYearLabel = canChi?.year ?? '--';

    final currentTimeLabel = dayInfo?.currentTime?.timeLabel ?? '--';
    final canChiHourLabel = dayInfo?.currentTime?.canChiHour ?? '--';

    return CustomScrollView(
      slivers: [
        SliverFillRemaining(
          hasScrollBody: false,
          child: Padding(
            padding: EdgeInsets.only(
              left: horizontalPadding,
              right: horizontalPadding,
              top: AppSpacing.m,
              bottom: AppSpacing.m,
            ),
            child: Column(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                // CHIP THÁNG
                DailyHeaderMonthChip(
                  sizeClass: sizeClass,
                  label: monthLabel,
                ),

                // SỐ NGÀY
                Text(
                  '${selectedDate.day}',
                  style: AppTypography.displayNumber(sizeClass)
                      .copyWith(color: AppColors.accentBlue),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: AppSpacing.l),

                // THỨ
                Text(
                  weekdayLabel,
                  style: AppTypography.headline2(sizeClass),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: AppSpacing.xxl),

                // QUOTE
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: AppSpacing.m),
                  child: DailyQuoteSection(
                    sizeClass: sizeClass,
                    quote: _getQuoteForDate(selectedDate),
                    source: 'Kinh dịch',
                  ),
                ),
                const SizedBox(height: AppSpacing.xl),

                // WEATHER - TODO: Connect to weather API
                DailyWeatherRow(
                  sizeClass: sizeClass,
                  location: 'Hà Nội |',
                  temperature: '-- °C',
                ),

                const Spacer(),

                // Loading indicator overlay
                if (isLoading)
                  const Padding(
                    padding: EdgeInsets.symmetric(vertical: AppSpacing.m),
                    child: SizedBox(
                      height: 24,
                      width: 24,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    ),
                  ),

                // GRID GIỜ / NGÀY / THÁNG / NĂM
                DateDetailGrid(
                  sizeClass: sizeClass,
                  time: currentTimeLabel,
                  day: lunarDayLabel,
                  month: lunarMonthLabel,
                  year: lunarYearLabel,
                  timeCanChi: canChiHourLabel,
                  dayCanChi: canChiDayLabel,
                  monthCanChi: canChiMonthLabel,
                  yearCanChi: canChiYearLabel,
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildErrorState(
    BuildContext context, {
    required ScreenSizeClass sizeClass,
    required Object error,
    required VoidCallback onRetry,
  }) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AppSpacing.xl),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(
              Icons.cloud_off,
              size: 64,
              color: AppColors.textSecondary,
            ),
            const SizedBox(height: AppSpacing.l),
            Text(
              'Không thể tải dữ liệu',
              style: AppTypography.headline2(sizeClass),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: AppSpacing.s),
            Text(
              'Kiểm tra kết nối mạng và thử lại',
              style: AppTypography.body2(sizeClass).copyWith(
                color: AppColors.textSecondary,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: AppSpacing.xl),
            ElevatedButton.icon(
              onPressed: onRetry,
              icon: const Icon(Icons.refresh),
              label: const Text('Thử lại'),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primaryRed,
                foregroundColor: Colors.white,
              ),
            ),
          ],
        ),
      ),
    );
  }

  /// Get a quote based on the date (simple rotation)
  String _getQuoteForDate(DateTime date) {
    final quotes = [
      'Người quân tử ghi nhớ rõ nhiều những câu nói hay, việc làm tốt của người đời trước, để nuôi cái đức của mình.',
      'Thiên hành kiện, quân tử dĩ tự cường bất tức.',
      'Địa thế khôn, quân tử dĩ hậu đức tải vật.',
      'Đức bạc nhi vị tôn, trí tiểu nhi mưu đại, lực tiểu nhi nhiệm trọng, tiền bất cập hĩ.',
      'Quân tử cư kì thất, xuất kì ngôn thiện, tắc thiên lý chi ngoại ứng chi.',
      'Tích thiện chi gia, tất hữu dư khương. Tích bất thiện chi gia, tất hữu dư ương.',
      'Kiến thiện tắc thiên, hữu quá tắc cải.',
    ];

    final index = (date.day + date.month) % quotes.length;
    return quotes[index];
  }
}
