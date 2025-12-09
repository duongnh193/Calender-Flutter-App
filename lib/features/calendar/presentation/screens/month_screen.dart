import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/responsive_utils.dart';
import '../../../../core/di/providers.dart';
import '../../application/calendar_providers.dart';
import '../../domain/day_info.dart';
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
        final asyncMonth = ref.watch(monthCalendarProvider(focusedMonth));
        final monthData = asyncMonth.asData?.value;
        final monthLabel = 'Tháng ${focusedMonth.month} ${focusedMonth.year}';
        final lunarLookup = {
          for (final day in monthData?.days ?? [])
            _formatDate(day.solarDate): day.lunar.day,
        };
        final goodDayLookup = {
          for (final day in monthData?.days ?? [])
            _formatDate(day.solarDate): day.goodDayType,
        };
        final specialDates = monthData != null
            ? monthData.days
                .where((d) => d.special || d.goodDayType == GoodDayType.hacDao)
                .map((d) => d.solarDate.day)
                .toSet()
            : ref.watch(specialDatesProvider);

        final asyncDay = ref.watch(dayInfoProvider(selectedDate));
        final dayInfo = asyncDay.asData?.value;
        final lunar = dayInfo?.lunar;
        final canChi = dayInfo?.canChi;
        final goldenHours = dayInfo?.goldenHours ?? [];
        final selectedGoodDay =
            dayInfo?.goodDayType ?? goodDayLookup[_formatDate(selectedDate)];
        final primaryText =
            '${_weekdayLabel(selectedDate)}, ${selectedDate.day} Tháng ${selectedDate.month}, ${selectedDate.year}';
        final subText = lunar != null && canChi != null
            ? '${lunar.day} Tháng ${lunar.month} Âm Lịch, Năm ${canChi.year}'
            : 'Đang tải...';

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
                          selectedDate: selectedDate,
                          onDateSelected: (date) {
                            ref.read(selectedDateProvider.notifier).state = date;
                            ref.read(focusedMonthProvider.notifier).state =
                                DateTime(date.year, date.month);
                          },
                        ),
                        const SizedBox(height: AppSpacing.l),
                        MonthCalendarGrid(
                          sizeClass: sizeClass,
                          month: focusedMonth,
                          selectedDate: selectedDate,
                          specialDates: specialDates,
                          lunarDayResolver: (date) =>
                              lunarLookup[_formatDate(date)],
                          dotColorResolver: (date) {
                            final type = goodDayLookup[_formatDate(date)];
                            if (type == GoodDayType.hacDao) {
                              return AppColors.calendarDotBlack;
                            }
                            return AppColors.calendarDotRed;
                          },
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
                          ).toUpperCase() + _goodDayTag(selectedGoodDay),
                          fullDateLabel:
                              '${selectedDate.day} Tháng ${selectedDate.month}, ${selectedDate.year}',
                          lunarLabel: lunar != null && canChi != null
                              ? '${lunar.day} Tháng ${lunar.month}, ${canChi.year}'
                              : 'Đang tải...',
                          timeLabel: _firstGoldenHourBranch(
                                goldenHours,
                              ) ??
                              canChi?.day ??
                              '--',
                          dayLabel: canChi?.day ?? '--',
                          monthLabel: canChi?.month ?? '--',
                          yearLabel: canChi?.year ?? '--',
                        ),
                        const SizedBox(height: AppSpacing.l),
                        MonthGoldenHoursSection(
                          sizeClass: sizeClass,
                          items: _mapGoldenHours(goldenHours),
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

String _formatDate(DateTime date) {
  final m = date.month.toString().padLeft(2, '0');
  final d = date.day.toString().padLeft(2, '0');
  return '${date.year}-$m-$d';
}

String _goodDayTag(GoodDayType? type) {
  if (type == null) return '';
  switch (type) {
    case GoodDayType.hoangDao:
      return ' • HOÀNG ĐẠO';
    case GoodDayType.hacDao:
      return ' • HẮC ĐẠO';
    default:
      return '';
  }
}

String? _firstGoldenHourBranch(List<GoldenHour> items) {
  if (items.isEmpty) return null;
  return _branchLabel(items.first.branch);
}

List<GoldenHourItem> _mapGoldenHours(List<GoldenHour> items) {
  return items
      .map(
        (gh) => GoldenHourItem(
          name: _branchLabel(gh.branch) ?? gh.branch,
          timeRange: gh.label,
          color: _colorForBranch(gh.branch),
          icon: _iconForBranch(gh.branch),
        ),
      )
      .toList();
}

String? _branchLabel(String code) {
  const map = {
    'ti': 'Tý',
    'suu': 'Sửu',
    'dan': 'Dần',
    'mao': 'Mão',
    'thin': 'Thìn',
    'ty': 'Tỵ',
    'ngo': 'Ngọ',
    'mui': 'Mùi',
    'than': 'Thân',
    'dau': 'Dậu',
    'tuat': 'Tuất',
    'hoi': 'Hợi',
  };
  return map[code.toLowerCase()];
}

Color _colorForBranch(String code) {
  switch (code.toLowerCase()) {
    case 'ti':
    case 'ngo':
    case 'than':
      return AppColors.accentBlue;
    case 'suu':
    case 'mui':
    case 'tuat':
      return AppColors.accentOrange;
    case 'thin':
    case 'ty':
    case 'dau':
      return AppColors.primaryRed;
    default:
      return AppColors.primaryGreen;
  }
}

IconData _iconForBranch(String code) {
  switch (code.toLowerCase()) {
    case 'ti':
      return Icons.cruelty_free;
    case 'suu':
      return Icons.pets;
    case 'ty':
      return Icons.grass;
    case 'ngo':
      return Icons.bolt;
    case 'than':
      return Icons.emoji_nature;
    default:
      return Icons.star_rate_rounded;
  }
}

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
