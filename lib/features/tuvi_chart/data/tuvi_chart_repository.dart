import '../../../core/constants/app_api.dart';
import '../../../core/network/api_client.dart';
import '../domain/tuvi_chart_models.dart';

/// Repository for Tu Vi Chart API calls
class TuViChartRepository {
  TuViChartRepository(this._client);

  final ApiClient _client;

  /// Generate Tu Vi chart from birth data
  Future<TuViChartResponse> generateChart(TuViChartRequest request) async {
    final response = await _client.post(
      AppApi.tuViChart,
      data: request.toJson(),
    );

    return TuViChartResponse.fromJson(response.data as Map<String, dynamic>);
  }

  /// Generate Tu Vi chart using GET (alternative)
  Future<TuViChartResponse> generateChartGet({
    required String date,
    required int hour,
    int minute = 0,
    required String gender,
    bool isLunar = false,
    bool isLeapMonth = false,
    String? name,
    String? birthPlace,
  }) async {
    final queryParams = <String, dynamic>{
      'date': date,
      'hour': hour,
      'minute': minute,
      'gender': gender,
      'isLunar': isLunar,
      'isLeapMonth': isLeapMonth,
    };

    if (name != null) queryParams['name'] = name;
    if (birthPlace != null) queryParams['birthPlace'] = birthPlace;

    final response = await _client.get(
      AppApi.tuViChart,
      queryParameters: queryParams,
    );

    return TuViChartResponse.fromJson(response.data as Map<String, dynamic>);
  }
}
