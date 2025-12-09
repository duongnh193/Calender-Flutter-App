import 'package:flutter/foundation.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

/// Environment configuration with validation
class EnvConfig {
  EnvConfig._();

  static bool _initialized = false;

  /// Initialize environment configuration
  /// Throws [EnvConfigException] if required variables are missing
  static Future<void> initialize({String fileName = '.env.dev'}) async {
    if (_initialized) return;

    try {
      await dotenv.load(fileName: fileName);
    } catch (e) {
      throw EnvConfigException(
        'Failed to load environment file: $fileName. '
        'Make sure the file exists and is added to pubspec.yaml assets.',
      );
    }

    // Validate required variables
    _validateRequired('BASE_URL');

    _initialized = true;

    if (kDebugMode) {
      print('✅ Environment loaded: $fileName');
      print('   BASE_URL: $baseUrl');
      print('   ENV_NAME: $envName');
    }
  }

  static void _validateRequired(String key) {
    final value = dotenv.env[key];
    if (value == null || value.isEmpty) {
      throw EnvConfigException(
        'Required environment variable "$key" is missing or empty. '
        'Please check your .env file.',
      );
    }
  }

  /// Base URL for API requests
  static String get baseUrl {
    final url = dotenv.env['BASE_URL'];
    if (url == null || url.isEmpty) {
      throw EnvConfigException('BASE_URL is not configured');
    }
    return url;
  }

  /// Environment name (development, staging, production)
  static String get envName => dotenv.env['ENV_NAME'] ?? 'unknown';

  /// Check if running in development environment
  static bool get isDevelopment => envName == 'development';

  /// Check if running in production environment
  static bool get isProduction => envName == 'production';
}

/// Exception thrown when environment configuration fails
class EnvConfigException implements Exception {
  final String message;
  EnvConfigException(this.message);

  @override
  String toString() => 'EnvConfigException: $message';
}

