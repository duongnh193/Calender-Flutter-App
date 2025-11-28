import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/responsive_utils.dart';
import '../../application/calendar_providers.dart';
import '../widgets/month_calendar_grid.dart';
import '../widgets/month_golden_hours_section.dart';
import '../widgets/month_header.dart';
import '../widgets/month_selected_day_panel.dart';

class MonthScreen extends ConsumerWidget {
  const MonthScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final sizeClass = getSizeClass(constraints.maxWidth);
        final focusedMonth = ref.watch(focusedMonthProvider);
        final selectedDate = ref.watch(selectedDateProvider);
        final specialDates = ref.watch(specialDatesProvider);
        final monthLabel = 'Tháng ${focusedMonth.month} ${focusedMonth.year}';
        final primaryText =
            '${_weekdayLabel(selectedDate)}, ${selectedDate.day} Tháng ${selectedDate.month}, ${selectedDate.year}';
        final subText = '9 Tháng 10 Âm lịch, Năm Ất Tỵ';

        return Scaffold(
          body: SafeArea(
            child: LayoutBuilder(
              builder: (context, constraints) {
                final minHeight = constraints.maxHeight;
                return SingleChildScrollView(
                  padding: EdgeInsets.symmetric(
                    horizontal: horizontalPaddingFor(sizeClass),
                    vertical: AppSpacing.l,
                  ),
                  child: ConstrainedBox(
                    constraints: BoxConstraints(minHeight: minHeight),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        MonthHeader(
                          sizeClass: sizeClass,
                          monthLabel: monthLabel,
                          primaryText: primaryText,
                          subText: subText,
                          onPrevious: () => _shiftMonth(ref, -1),
                          onNext: () => _shiftMonth(ref, 1),
                        ),
                        const SizedBox(height: AppSpacing.l),
                        MonthCalendarGrid(
                          sizeClass: sizeClass,
                          month: focusedMonth,
                          selectedDate: selectedDate,
                          specialDates: specialDates,
                          onSelect: (date) {
                            ref.read(selectedDateProvider.notifier).state =
                                date;
                            ref.read(focusedMonthProvider.notifier).state =
                                DateTime(date.year, date.month);
                          },
                        ),
                        const SizedBox(height: AppSpacing.l),
                        MonthSelectedDayPanel(
                          sizeClass: sizeClass,
                          weekdayLabel: _weekdayLabel(
                            selectedDate,
                          ).toUpperCase(),
                          fullDateLabel:
                              '${selectedDate.day} Tháng ${selectedDate.month}, ${selectedDate.year}',
                          lunarLabel: '9 Tháng 10, Ất Tỵ',
                          timeLabel: 'Giáp Ngọ',
                          dayLabel: 'Tân Sửu',
                          monthLabel: 'Đinh Hợi',
                          yearLabel: 'Ất Tỵ',
                        ),
                        const SizedBox(height: AppSpacing.l),
                        MonthGoldenHoursSection(
                          sizeClass: sizeClass,
                          items: _mockGoldenHours,
                        ),
                        const SizedBox(height: AppSpacing.l),
                        _AdPlaceholder(sizeClass: sizeClass),
                      ],
                    ),
                  ),
                );
              },
            ),
          ),
        );
      },
    );
  }

  void _shiftMonth(WidgetRef ref, int delta) {
    final current = ref.read(focusedMonthProvider);
    final newMonth = DateTime(current.year, current.month + delta);
    ref.read(focusedMonthProvider.notifier).state = newMonth;
  }

  String _weekdayLabel(DateTime date) {
    switch (date.weekday) {
      case DateTime.monday:
        return 'Thứ Hai';
      case DateTime.tuesday:
        return 'Thứ Ba';
      case DateTime.wednesday:
        return 'Thứ Tư';
      case DateTime.thursday:
        return 'Thứ Năm';
      case DateTime.friday:
        return 'Thứ Sáu';
      case DateTime.saturday:
        return 'Thứ Bảy';
      default:
        return 'Chủ Nhật';
    }
  }
}

final _mockGoldenHours = [
  GoldenHourItem(
    name: 'Tý',
    timeRange: '23-1h',
    color: AppColors.accentBlue,
    icon: Icons.pets,
  ),
  GoldenHourItem(
    name: 'Sửu',
    timeRange: '1-3h',
    color: AppColors.accentOrange,
    icon: Icons.pets,
  ),
  GoldenHourItem(
    name: 'Tị',
    timeRange: '9-11h',
    color: AppColors.primaryRed,
    icon: Icons.pets,
  ),
  GoldenHourItem(
    name: 'Thìn',
    timeRange: '7-9h',
    color: AppColors.primaryGreen,
    icon: Icons.pets,
  ),
  GoldenHourItem(
    name: 'Thân',
    timeRange: '15-17h',
    color: AppColors.primaryRed,
    icon: Icons.pets,
  ),
  GoldenHourItem(
    name: 'Hợi',
    timeRange: '21-23h',
    color: AppColors.accentBlue,
    icon: Icons.pets,
  ),
];

class _AdPlaceholder extends StatelessWidget {
  const _AdPlaceholder({required this.sizeClass});

  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 64,
      decoration: BoxDecoration(
        color: AppColors.cardBackground,
        borderRadius: BorderRadius.circular(AppRadius.medium),
        border: Border.all(color: AppColors.dividerColor),
      ),
      alignment: Alignment.center,
      child: Text(
        'Ad banner placeholder',
        style: AppTypography.body2(sizeClass),
      ),
    );
  }
}
