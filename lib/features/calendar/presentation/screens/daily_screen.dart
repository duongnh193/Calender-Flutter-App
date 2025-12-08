import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/responsive_utils.dart';
import '../../../../core/di/providers.dart';
import '../../application/calendar_providers.dart';
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
        final dayInfo = asyncDay.asData?.value;
        final lunar = dayInfo?.lunar;
        final canChi = dayInfo?.canChi;

        final monthLabel =
            'Tháng ${selectedDate.month.toString().padLeft(2, '0')} - ${selectedDate.year}';
        final weekdayLabel = _weekdayLabel(selectedDate).toUpperCase();

        final lunarDayLabel =
            lunar != null ? '${lunar.day}' : selectedDate.day.toString();
        final lunarMonthLabel =
            lunar != null ? '${lunar.month}' : selectedDate.month.toString();
        final lunarYearLabel =
            lunar != null ? '${lunar.year}' : selectedDate.year.toString();

        final canChiDayLabel = canChi?.day ?? '--';
        final canChiMonthLabel = canChi?.month ?? '--';
        final canChiYearLabel = canChi?.year ?? '--';

        final currentTimeLabel = dayInfo?.currentTime?.timeLabel ?? '--';
        final canChiHourLabel = dayInfo?.currentTime?.canChiHour ?? '--';

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
                bottom: false, // để bottom nav sát đáy
                child: CustomScrollView(
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
                            // const SizedBox(height: AppSpacing.l),

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
                            const Padding(
                              padding: EdgeInsets.symmetric(
                                horizontal: AppSpacing.m,
                              ),
                              child: DailyQuoteSection(
                                // nếu DailyQuoteSection cần sizeClass dynamic
                                // bỏ const và truyền sizeClass như trước
                                sizeClass: ScreenSizeClass.medium,
                                quote:
                                    'Người quân tử ghi nhớ rõ nhiều những câu nói hay, việc làm tốt của người đời trước, để nuôi cái đức của mình.',
                                source: 'Kinh dịch',
                              ),
                            ),
                            const SizedBox(height: AppSpacing.xl),
                            // const Spacer(),

                            // WEATHER
                            DailyWeatherRow(
                              sizeClass: sizeClass,
                              location: 'Hanoi |',
                              temperature: '23°C / 14 - 24°C',
                            ),

                            // const SizedBox(height: AppSpacing.l),

                            const Spacer(),

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

                            // const SizedBox(height: AppSpacing.xxl),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  String _weekdayLabel(DateTime date) {
    switch (date.weekday) {
      case DateTime.monday:
        return 'Thứ hai';
      case DateTime.tuesday:
        return 'Thứ ba';
      case DateTime.wednesday:
        return 'Thứ tư';
      case DateTime.thursday:
        return 'Thứ năm';
      case DateTime.friday:
        return 'Thứ sáu';
      case DateTime.saturday:
        return 'Thứ bảy';
      default:
        return 'Chủ nhật';
    }
  }
}
