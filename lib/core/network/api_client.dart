import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

import '../constants/app_api.dart';
import 'api_exception.dart';

/// HTTP client for API requests
class ApiClient {
  ApiClient() : _dio = Dio(_defaultOptions) {
    // Only add logging in debug mode to avoid leaking data in release
    if (kDebugMode) {
      _dio.interceptors.add(
        LogInterceptor(
          request: true,
          requestBody: true,
          responseBody: true,
          logPrint: (obj) => debugPrint(obj.toString()),
        ),
      );
    }

    // Add error handling interceptor
    _dio.interceptors.add(_ErrorInterceptor());
  }

  final Dio _dio;

  static BaseOptions get _defaultOptions => BaseOptions(
        baseUrl: AppApi.baseUrl,
        connectTimeout: const Duration(seconds: 15),
        receiveTimeout: const Duration(seconds: 30),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      );

  /// GET request
  Future<Response<dynamic>> get(
    String path, {
    Map<String, dynamic>? queryParameters,
  }) async {
    return _dio.get(path, queryParameters: queryParameters);
  }

  /// POST request
  Future<Response<dynamic>> post(
    String path, {
    dynamic data,
    Map<String, dynamic>? queryParameters,
  }) async {
    return _dio.post(path, data: data, queryParameters: queryParameters);
  }

  /// PUT request
  Future<Response<dynamic>> put(
    String path, {
    dynamic data,
    Map<String, dynamic>? queryParameters,
  }) async {
    return _dio.put(path, data: data, queryParameters: queryParameters);
  }

  /// DELETE request
  Future<Response<dynamic>> delete(
    String path, {
    Map<String, dynamic>? queryParameters,
  }) async {
    return _dio.delete(path, queryParameters: queryParameters);
  }
}

/// Interceptor for handling errors consistently
class _ErrorInterceptor extends Interceptor {
  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    final apiException = ApiException.fromDioException(err);

    if (kDebugMode) {
      debugPrint('API Error: ${apiException.message}');
      debugPrint('  Status: ${apiException.statusCode}');
      debugPrint('  Code: ${apiException.code}');
    }

    handler.next(DioException(
      requestOptions: err.requestOptions,
      error: apiException,
      type: err.type,
      response: err.response,
    ));
  }
}
