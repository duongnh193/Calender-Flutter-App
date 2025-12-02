import 'package:flutter_dotenv/flutter_dotenv.dart';

class AppApi {
  AppApi._();

  static final String baseUrl = dotenv.env['BASE_URL']!;

  static const String calendarDay = '/calendar/day';
  static const String calendarMonth = '/calendar/month';
  static const String zodiacs = '/zodiacs';
  static const String horoscopeYearly = '/horoscopes/yearly';
  static const String horoscopeDaily = '/horoscopes/daily';
}
