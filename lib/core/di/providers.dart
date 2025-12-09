import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../features/calendar/data/calendar_repository.dart';
import '../../features/calendar/domain/day_info.dart';
import '../../features/calendar/domain/month_calendar.dart';
import '../../features/horoscope/data/horoscope_repository.dart';
import '../../features/horoscope/domain/horoscope_models.dart';
import '../network/api_client.dart';

// ==================== Core Providers ====================

/// API client provider
final apiClientProvider = Provider<ApiClient>((ref) => ApiClient());

// ==================== Calendar Providers ====================

/// Calendar repository provider
final calendarRepositoryProvider = Provider<CalendarRepository>((ref) {
  return CalendarRepository(ref.read(apiClientProvider));
});

/// Day info provider - fetches detailed info for a specific date
final dayInfoProvider =
    FutureProvider.family.autoDispose<DayInfo, DateTime>((ref, date) {
  return ref.read(calendarRepositoryProvider).getDayInfo(date);
});

/// Month calendar provider - fetches calendar data for a month
final monthCalendarProvider =
    FutureProvider.family.autoDispose<MonthCalendar, DateTime>((ref, date) {
  return ref.read(calendarRepositoryProvider).getMonthInfo(date);
});

// ==================== Horoscope Providers ====================

/// Horoscope repository provider
final horoscopeRepositoryProvider = Provider<HoroscopeRepository>((ref) {
  return HoroscopeRepository(ref.read(apiClientProvider));
});

/// Daily horoscope provider
final dailyHoroscopeProvider = FutureProvider.family
    .autoDispose<HoroscopeDaily, DailyHoroscopeParams>((ref, params) {
  return ref.read(horoscopeRepositoryProvider).getDailyHoroscope(
        zodiacId: params.zodiacId,
        zodiacCode: params.zodiacCode,
        date: params.date,
      );
});

/// Monthly horoscope provider
final monthlyHoroscopeProvider = FutureProvider.family
    .autoDispose<HoroscopeMonthly, MonthlyHoroscopeParams>((ref, params) {
  return ref.read(horoscopeRepositoryProvider).getMonthlyHoroscope(
        zodiacId: params.zodiacId,
        zodiacCode: params.zodiacCode,
        year: params.year,
        month: params.month,
      );
});

/// Yearly horoscope provider
final yearlyHoroscopeProvider = FutureProvider.family
    .autoDispose<HoroscopeYearly, YearlyHoroscopeParams>((ref, params) {
  return ref.read(horoscopeRepositoryProvider).getYearlyHoroscope(
        zodiacId: params.zodiacId,
        zodiacCode: params.zodiacCode,
        year: params.year,
      );
});

/// Lifetime horoscope provider
final lifetimeHoroscopeProvider = FutureProvider.family
    .autoDispose<HoroscopeLifetime, LifetimeHoroscopeParams>((ref, params) {
  return ref.read(horoscopeRepositoryProvider).getLifetimeHoroscope(
        canChi: params.canChi,
        gender: params.gender,
      );
});

/// Lifetime horoscope by birth provider
final lifetimeByBirthProvider = FutureProvider.family
    .autoDispose<LifetimeByBirthResponse, LifetimeByBirthParams>((ref, params) {
  return ref.read(horoscopeRepositoryProvider).getLifetimeByBirth(
        date: params.date,
        hour: params.hour,
        minute: params.minute,
        isLunar: params.isLunar,
        isLeapMonth: params.isLeapMonth,
        gender: params.gender,
      );
});

/// Can-Chi calculation provider
final canChiProvider =
    FutureProvider.family.autoDispose<CanChiResult, DateTime>((ref, birthDate) {
  return ref.read(horoscopeRepositoryProvider).calculateCanChi(birthDate);
});

// ==================== Parameter Classes ====================

/// Parameters for daily horoscope query
class DailyHoroscopeParams {

  const DailyHoroscopeParams({
    this.zodiacId,
    this.zodiacCode,
    this.date,
  });
  final int? zodiacId;
  final String? zodiacCode;
  final DateTime? date;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is DailyHoroscopeParams &&
          zodiacId == other.zodiacId &&
          zodiacCode == other.zodiacCode &&
          date?.day == other.date?.day &&
          date?.month == other.date?.month &&
          date?.year == other.date?.year;

  @override
  int get hashCode => Object.hash(zodiacId, zodiacCode, date?.day, date?.month, date?.year);
}

/// Parameters for monthly horoscope query
class MonthlyHoroscopeParams {

  const MonthlyHoroscopeParams({
    this.zodiacId,
    this.zodiacCode,
    required this.year,
    required this.month,
  });
  final int? zodiacId;
  final String? zodiacCode;
  final int year;
  final int month;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is MonthlyHoroscopeParams &&
          zodiacId == other.zodiacId &&
          zodiacCode == other.zodiacCode &&
          year == other.year &&
          month == other.month;

  @override
  int get hashCode => Object.hash(zodiacId, zodiacCode, year, month);
}

/// Parameters for yearly horoscope query
class YearlyHoroscopeParams {

  const YearlyHoroscopeParams({
    this.zodiacId,
    this.zodiacCode,
    required this.year,
  });
  final int? zodiacId;
  final String? zodiacCode;
  final int year;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is YearlyHoroscopeParams &&
          zodiacId == other.zodiacId &&
          zodiacCode == other.zodiacCode &&
          year == other.year;

  @override
  int get hashCode => Object.hash(zodiacId, zodiacCode, year);
}

/// Parameters for lifetime horoscope query
class LifetimeHoroscopeParams {

  const LifetimeHoroscopeParams({
    required this.canChi,
    required this.gender,
  });
  final String canChi;
  final String gender;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is LifetimeHoroscopeParams &&
          canChi == other.canChi &&
          gender == other.gender;

  @override
  int get hashCode => Object.hash(canChi, gender);
}

/// Parameters for lifetime by birth query
class LifetimeByBirthParams {

  const LifetimeByBirthParams({
    required this.date,
    required this.hour,
    this.minute = 0,
    this.isLunar = false,
    this.isLeapMonth = false,
    required this.gender,
  });
  final String date;
  final int hour;
  final int minute;
  final bool isLunar;
  final bool isLeapMonth;
  final String gender;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is LifetimeByBirthParams &&
          date == other.date &&
          hour == other.hour &&
          minute == other.minute &&
          isLunar == other.isLunar &&
          isLeapMonth == other.isLeapMonth &&
          gender == other.gender;

  @override
  int get hashCode =>
      Object.hash(date, hour, minute, isLunar, isLeapMonth, gender);
}
