import '../config/env_config.dart';

/// API endpoint constants
/// All paths are relative to the base URL configured in environment
class AppApi {
  AppApi._();

  /// Base URL from environment configuration
  static String get baseUrl => EnvConfig.baseUrl;

  // ==================== Calendar Endpoints ====================
  static const String calendarDay = '/calendar/day';
  static const String calendarMonth = '/calendar/month';

  // ==================== Zodiac Endpoints ====================
  static const String zodiacs = '/zodiacs';
  static const String zodiacById = '/zodiacs'; // Append /{id}

  // ==================== Horoscope Endpoints ====================
  /// GET /horoscope/lifetime?canChi={canChi}&gender={gender}
  static const String horoscopeLifetime = '/horoscope/lifetime';

  /// POST /horoscope/lifetime/by-birth
  static const String horoscopeLifetimeByBirth = '/horoscope/lifetime/by-birth';

  /// GET /horoscope/yearly?zodiacId={id}&year={year}
  static const String horoscopeYearly = '/horoscope/yearly';

  /// GET /horoscope/monthly?zodiacId={id}&year={year}&month={month}
  static const String horoscopeMonthly = '/horoscope/monthly';

  /// GET /horoscope/daily?zodiacId={id}&date={date}
  static const String horoscopeDaily = '/horoscope/daily';

  /// GET /horoscope/can-chi?birthDate={date}
  static const String horoscopeCanChi = '/horoscope/can-chi';
}
