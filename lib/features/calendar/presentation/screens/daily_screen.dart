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

        return Scaffold(
          body: Stack(
            children: [
              Positioned.fill(
                child: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                      colors: const [
                        AppColors.backgroundDailyStart,
                        AppColors.backgroundDailyEnd,
                      ],
                    ),
                  ),
                ),
              ),
              SafeArea(
                child: Column(
                  children: [
                    Expanded(
                      child: SingleChildScrollView(
                        padding: EdgeInsets.symmetric(
                          horizontal: horizontalPaddingFor(sizeClass),
                          vertical: AppSpacing.m,
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            DailyHeaderMonthChip(
                              sizeClass: sizeClass,
                              label: monthLabel,
                            ),
                            const SizedBox(height: AppSpacing.l),
                            Text(
                              '${selectedDate.day}',
                              style: AppTypography.displayNumber(
                                sizeClass,
                              ).copyWith(color: AppColors.accentBlue),
                            ),
                            const SizedBox(height: AppSpacing.m),
                            Text(
                              weekdayLabel,
                              style: AppTypography.headline2(sizeClass),
                            ),
                            const SizedBox(height: AppSpacing.l),
                            DailyQuoteSection(
                              sizeClass: sizeClass,
                              quote:
                                  'Người quân tử ghi nhớ rõ nhiều những câu nói hay, việc làm tốt của người đời trước, để nuôi cái đức của mình.',
                              source: 'Kinh dịch',
                            ),
                            const SizedBox(height: AppSpacing.l),
                            DailyWeatherRow(
                              sizeClass: sizeClass,
                              location: 'Hanoi |',
                              temperature: '23°C / 14 - 24°C',
                            ),
                            const SizedBox(height: AppSpacing.s),
                            DateDetailGrid(
                              sizeClass: sizeClass,
                              time: '--',
                              day: lunarDayLabel,
                              month: lunarMonthLabel,
                              year: lunarYearLabel,
                              timeCanChi: '--',
                              dayCanChi: canChiDayLabel,
                              monthCanChi: canChiMonthLabel,
                              yearCanChi: canChiYearLabel,
                            ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: AppSpacing.l),
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
