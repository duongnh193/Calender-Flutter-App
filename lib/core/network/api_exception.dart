import 'package:dio/dio.dart';

/// Custom exception for API errors with user-friendly messages
class ApiException implements Exception {
  final String message;
  final String? code;
  final int? statusCode;
  final dynamic originalError;

  ApiException({
    required this.message,
    this.code,
    this.statusCode,
    this.originalError,
  });

  factory ApiException.fromDioException(DioException e) {
    switch (e.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        return ApiException(
          message: 'Kết nối quá thời gian. Vui lòng thử lại.',
          code: 'TIMEOUT',
          originalError: e,
        );

      case DioExceptionType.connectionError:
        return ApiException(
          message: 'Không thể kết nối đến máy chủ. Kiểm tra kết nối mạng.',
          code: 'CONNECTION_ERROR',
          originalError: e,
        );

      case DioExceptionType.badResponse:
        return _handleBadResponse(e);

      case DioExceptionType.cancel:
        return ApiException(
          message: 'Yêu cầu đã bị hủy.',
          code: 'CANCELLED',
          originalError: e,
        );

      default:
        return ApiException(
          message: 'Đã xảy ra lỗi. Vui lòng thử lại.',
          code: 'UNKNOWN',
          originalError: e,
        );
    }
  }

  static ApiException _handleBadResponse(DioException e) {
    final statusCode = e.response?.statusCode;
    final data = e.response?.data;

    String message;
    String? code;

    // Try to extract error from response body
    if (data is Map<String, dynamic>) {
      message = data['message'] as String? ?? _defaultMessageForStatus(statusCode);
      code = data['code'] as String?;
    } else {
      message = _defaultMessageForStatus(statusCode);
    }

    return ApiException(
      message: message,
      code: code,
      statusCode: statusCode,
      originalError: e,
    );
  }

  static String _defaultMessageForStatus(int? statusCode) {
    switch (statusCode) {
      case 400:
        return 'Yêu cầu không hợp lệ.';
      case 401:
        return 'Chưa xác thực. Vui lòng đăng nhập lại.';
      case 403:
        return 'Không có quyền truy cập.';
      case 404:
        return 'Không tìm thấy dữ liệu.';
      case 500:
        return 'Lỗi máy chủ. Vui lòng thử lại sau.';
      case 503:
        return 'Dịch vụ tạm thời không khả dụng.';
      default:
        return 'Đã xảy ra lỗi (${statusCode ?? 'unknown'}).';
    }
  }

  @override
  String toString() => 'ApiException: $message (code: $code, status: $statusCode)';
}

