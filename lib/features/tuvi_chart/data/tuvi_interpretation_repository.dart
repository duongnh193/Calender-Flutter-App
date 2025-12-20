import '../../../core/constants/app_api.dart';
import '../../../core/network/api_client.dart';
import '../domain/tuvi_chart_models.dart';
import '../domain/tuvi_interpretation_models.dart';

/// Repository for Tu Vi Chart Interpretation API calls
class TuViInterpretationRepository {
  TuViInterpretationRepository(this._client);

  final ApiClient _client;

  /// Generate Tu Vi chart interpretation from birth data.
  /// Note: This API call may take 30-60 seconds due to AI generation.
  Future<TuViInterpretationResponse> generateInterpretation(
      TuViChartRequest request) async {
    final response = await _client.post(
      AppApi.tuViInterpretation,
      data: request.toJson(),
    );

    return TuViInterpretationResponse.fromJson(
        response.data as Map<String, dynamic>);
  }

  /// Generate Tu Vi chart interpretation using GET (alternative)
  Future<TuViInterpretationResponse> generateInterpretationGet({
    required String date,
    required int hour,
    int minute = 0,
    required String gender,
    required String name,
    bool isLunar = false,
    bool isLeapMonth = false,
    String? birthPlace,
  }) async {
    final queryParams = <String, dynamic>{
      'date': date,
      'hour': hour,
      'minute': minute,
      'gender': gender,
      'name': name,
      'isLunar': isLunar,
      'isLeapMonth': isLeapMonth,
    };

    if (birthPlace != null) queryParams['birthPlace'] = birthPlace;

    final response = await _client.get(
      AppApi.tuViInterpretation,
      queryParameters: queryParams,
    );

    return TuViInterpretationResponse.fromJson(
        response.data as Map<String, dynamic>);
  }
}
