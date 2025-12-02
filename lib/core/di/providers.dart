import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../features/calendar/data/calendar_repository.dart';
import '../../features/calendar/domain/day_info.dart';
import '../../features/calendar/domain/month_calendar.dart';
import '../network/api_client.dart';

final apiClientProvider = Provider<ApiClient>((ref) => ApiClient());

final calendarRepositoryProvider = Provider<CalendarRepository>((ref) {
  return CalendarRepository(ref.read(apiClientProvider));
});

final dayInfoProvider =
    FutureProvider.family.autoDispose<DayInfo, DateTime>((ref, date) {
  return ref.read(calendarRepositoryProvider).getDayInfo(date);
});

final monthCalendarProvider =
    FutureProvider.family.autoDispose<MonthCalendar, DateTime>((ref, date) {
  return ref.read(calendarRepositoryProvider).getMonthInfo(date);
});
