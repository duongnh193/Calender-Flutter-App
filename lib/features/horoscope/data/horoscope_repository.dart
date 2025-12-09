import '../../../core/constants/app_api.dart';
import '../../../core/network/api_client.dart';
import '../../../core/utils/date_time_utils.dart';
import '../domain/horoscope_models.dart';

/// Repository for horoscope-related API calls
class HoroscopeRepository {
  HoroscopeRepository(this._client);

  final ApiClient _client;

  /// Get daily horoscope
  /// Either zodiacId or zodiacCode must be provided
  Future<HoroscopeDaily> getDailyHoroscope({
    int? zodiacId,
    String? zodiacCode,
    DateTime? date,
  }) async {
    final queryParams = <String, dynamic>{};

    if (zodiacId != null) {
      queryParams['zodiacId'] = zodiacId;
    } else if (zodiacCode != null) {
      queryParams['zodiacCode'] = zodiacCode;
    }

    if (date != null) {
      queryParams['date'] = DateTimeUtils.formatDateForApi(date);
    }

    final response = await _client.get(
      AppApi.horoscopeDaily,
      queryParameters: queryParams,
    );

    return HoroscopeDaily.fromJson(response.data as Map<String, dynamic>);
  }

  /// Get monthly horoscope
  Future<HoroscopeMonthly> getMonthlyHoroscope({
    int? zodiacId,
    String? zodiacCode,
    required int year,
    required int month,
  }) async {
    final queryParams = <String, dynamic>{
      'year': year,
      'month': month,
    };

    if (zodiacId != null) {
      queryParams['zodiacId'] = zodiacId;
    } else if (zodiacCode != null) {
      queryParams['zodiacCode'] = zodiacCode;
    }

    final response = await _client.get(
      AppApi.horoscopeMonthly,
      queryParameters: queryParams,
    );

    return HoroscopeMonthly.fromJson(response.data as Map<String, dynamic>);
  }

  /// Get yearly horoscope
  Future<HoroscopeYearly> getYearlyHoroscope({
    int? zodiacId,
    String? zodiacCode,
    required int year,
  }) async {
    final queryParams = <String, dynamic>{
      'year': year,
    };

    if (zodiacId != null) {
      queryParams['zodiacId'] = zodiacId;
    } else if (zodiacCode != null) {
      queryParams['zodiacCode'] = zodiacCode;
    }

    final response = await _client.get(
      AppApi.horoscopeYearly,
      queryParameters: queryParams,
    );

    return HoroscopeYearly.fromJson(response.data as Map<String, dynamic>);
  }

  /// Get lifetime horoscope by Can-Chi and gender
  Future<HoroscopeLifetime> getLifetimeHoroscope({
    required String canChi,
    required String gender,
  }) async {
    final response = await _client.get(
      AppApi.horoscopeLifetime,
      queryParameters: {
        'canChi': canChi,
        'gender': gender,
      },
    );

    return HoroscopeLifetime.fromJson(response.data as Map<String, dynamic>);
  }

  /// Get lifetime horoscope by birth data
  Future<LifetimeByBirthResponse> getLifetimeByBirth({
    required String date,
    required int hour,
    int minute = 0,
    bool isLunar = false,
    bool isLeapMonth = false,
    required String gender,
  }) async {
    final response = await _client.post(
      AppApi.horoscopeLifetimeByBirth,
      data: {
        'date': date,
        'hour': hour,
        'minute': minute,
        'isLunar': isLunar,
        'isLeapMonth': isLeapMonth,
        'gender': gender,
      },
    );

    return LifetimeByBirthResponse.fromJson(
        response.data as Map<String, dynamic>);
  }

  /// Calculate Can-Chi from birth date
  Future<CanChiResult> calculateCanChi(DateTime birthDate) async {
    final response = await _client.get(
      AppApi.horoscopeCanChi,
      queryParameters: {
        'birthDate': DateTimeUtils.formatDateForApi(birthDate),
      },
    );

    return CanChiResult.fromJson(response.data as Map<String, dynamic>);
  }
}

