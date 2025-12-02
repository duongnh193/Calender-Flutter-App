import 'package:dio/dio.dart';

import '../constants/app_api.dart';

class ApiClient {
  ApiClient() : _dio = Dio(_defaultOptions) {
    _dio.interceptors.add(
      LogInterceptor(
        request: true,
        requestBody: true,
        responseBody: true,
        logPrint: (obj) => _logger(obj),
      ),
    );
  }

  final Dio _dio;

  static BaseOptions get _defaultOptions => BaseOptions(
        baseUrl: AppApi.baseUrl,
        connectTimeout: const Duration(seconds: 10),
        receiveTimeout: const Duration(seconds: 20),
        headers: {'Content-Type': 'application/json'},
      );

  Future<Response<dynamic>> get(
    String path, {
    Map<String, dynamic>? queryParameters,
  }) async {
    try {
      return await _dio.get(path, queryParameters: queryParameters);
    } on DioException catch (e) {
      _logger(e.message ?? 'Dio error');
      rethrow;
    }
  }

  static void _logger(Object obj) {
    // Centralized logger to avoid missing logs in release builds
    // ignore: avoid_print
    print(obj);
  }
}
