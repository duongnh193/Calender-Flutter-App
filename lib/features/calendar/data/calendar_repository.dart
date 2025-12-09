import '../../../core/constants/app_api.dart';
import '../../../core/network/api_client.dart';
import '../../../core/utils/date_time_utils.dart';
import '../domain/day_info.dart';
import '../domain/month_calendar.dart';

/// Repository for calendar-related API calls
class CalendarRepository {
  CalendarRepository(this._client);

  final ApiClient _client;

  /// Get detailed information for a specific date
  Future<DayInfo> getDayInfo(DateTime date) async {
    final response = await _client.get(
      AppApi.calendarDay,
      queryParameters: {'date': DateTimeUtils.formatDateForApi(date)},
    );
    return DayInfo.fromJson(response.data as Map<String, dynamic>);
  }

  /// Get calendar data for a month
  Future<MonthCalendar> getMonthInfo(DateTime month) async {
    final response = await _client.get(
      AppApi.calendarMonth,
      queryParameters: {
        'year': month.year,
        'month': month.month,
      },
    );
    return MonthCalendar.fromJson(response.data as Map<String, dynamic>);
  }
}
