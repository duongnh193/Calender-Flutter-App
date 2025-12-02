import '../../../core/constants/app_api.dart';
import '../../../core/network/api_client.dart';
import '../domain/day_info.dart';
import '../domain/month_calendar.dart';

class CalendarRepository {
  CalendarRepository(this._client);

  final ApiClient _client;

  Future<DayInfo> getDayInfo(DateTime date) async {
    final response = await _client.get(
      AppApi.calendarDay,
      queryParameters: {'date': _formatDate(date)},
    );
    return DayInfo.fromJson(response.data as Map<String, dynamic>);
  }

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

  String _formatDate(DateTime date) {
    final m = date.month.toString().padLeft(2, '0');
    final d = date.day.toString().padLeft(2, '0');
    return '${date.year}-$m-$d';
  }
}
