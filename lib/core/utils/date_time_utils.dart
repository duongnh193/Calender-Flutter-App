import 'package:intl/intl.dart';

/// Utility class for date/time operations
/// All API dates are normalized to Asia/Bangkok (UTC+7) timezone
class DateTimeUtils {
  DateTimeUtils._();

  /// Vietnam timezone offset (UTC+7)
  static const Duration vietnamOffset = Duration(hours: 7);

  /// Format date for API requests (yyyy-MM-dd)
  static String formatDateForApi(DateTime date) {
    return DateFormat('yyyy-MM-dd').format(date);
  }

  /// Format date for display (dd/MM/yyyy)
  static String formatDateForDisplay(DateTime date) {
    return DateFormat('dd/MM/yyyy').format(date);
  }

  /// Format month for display (MM/yyyy)
  static String formatMonthForDisplay(DateTime date) {
    return DateFormat('MM/yyyy').format(date);
  }

  /// Get current date in Vietnam timezone (UTC+7)
  static DateTime nowInVietnam() {
    final utc = DateTime.now().toUtc();
    return utc.add(vietnamOffset);
  }

  /// Get today's date (date only, no time) in Vietnam timezone
  static DateTime todayInVietnam() {
    final now = nowInVietnam();
    return DateTime(now.year, now.month, now.day);
  }

  /// Convert local DateTime to Vietnam timezone for API
  static DateTime toVietnamTime(DateTime local) {
    // If already in UTC, add Vietnam offset
    if (local.isUtc) {
      return local.add(vietnamOffset);
    }
    // Otherwise, treat as local and get UTC first
    final utc = local.toUtc();
    return utc.add(vietnamOffset);
  }

  /// Parse API date string (yyyy-MM-dd) to DateTime
  static DateTime? parseApiDate(String? dateStr) {
    if (dateStr == null || dateStr.isEmpty) return null;
    try {
      return DateTime.parse(dateStr);
    } catch (_) {
      return null;
    }
  }

  /// Get weekday name in Vietnamese
  static String getVietnameseWeekday(DateTime date) {
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
      case DateTime.sunday:
        return 'Chủ Nhật';
      default:
        return '';
    }
  }

  /// Get short weekday name in Vietnamese
  static String getShortVietnameseWeekday(DateTime date) {
    switch (date.weekday) {
      case DateTime.monday:
        return 'T2';
      case DateTime.tuesday:
        return 'T3';
      case DateTime.wednesday:
        return 'T4';
      case DateTime.thursday:
        return 'T5';
      case DateTime.friday:
        return 'T6';
      case DateTime.saturday:
        return 'T7';
      case DateTime.sunday:
        return 'CN';
      default:
        return '';
    }
  }

  /// Check if two dates are the same day
  static bool isSameDay(DateTime a, DateTime b) {
    return a.year == b.year && a.month == b.month && a.day == b.day;
  }

  /// Check if date is today
  static bool isToday(DateTime date) {
    return isSameDay(date, todayInVietnam());
  }

  /// Get first day of month
  static DateTime firstDayOfMonth(DateTime date) {
    return DateTime(date.year, date.month, 1);
  }

  /// Get last day of month
  static DateTime lastDayOfMonth(DateTime date) {
    return DateTime(date.year, date.month + 1, 0);
  }

  /// Get number of days in month
  static int daysInMonth(DateTime date) {
    return lastDayOfMonth(date).day;
  }
}
