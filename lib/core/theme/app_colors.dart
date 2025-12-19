import 'package:flutter/material.dart';

/// Centralized semantic color tokens for the app.
class AppColors {
  AppColors._();

  static const Color primaryRed = Color(0xFFD32F2F);
  static const Color primaryGreen = Color(0xFF2E7D32);
  static const Color accentBlue = Color(0xFF1E88E5);
  static const Color accentOrange = Color(0xFFF57C00);
  static const Color accentYellow = Color(0xFFFFC107);
  static const Color accentLightBlue = Color(0xFF4FC3F7);
  static const Color accentPurple = Color(0xFF9C27B0);
  static const Color primary = Color(0xFF1976D2);
  static const Color primaryLight = Color(0xFFBBDEFB);
  static const Color accent = Color(0xFFE91E63);
  static const Color surfaceDark = Color(0xFF1E1E1E);
  static const Color iosRed = Color(0xFFFF3B30);
  static const Color iosLinkBlue = Color(0xFF007AFF);
  static const Color iosGrayPrimary = Color(0xFF1C1C1E);
  static const Color iosGraySecondary = Color(0xFF8E8E93);
  static const Color iosGrayDivider = Color(0xFFE5E5EA);
  static const Color iosBackground = Color(0xFFF2F2F7);
  static const Color backgroundDailyStart = Color(0xFFFFF3B0);
  static const Color backgroundDailyEnd = Color(0xFFD8F5A2);
  static const Color backgroundApp = Color(0xFFF5F5F5);
  static const Color textPrimary = Color(0xFF1F1F1F);
  static const Color textSecondary = Color(0xFF6E6E6E);
  static const Color textMuted = Color(0xFF9E9E9E);
  static const Color dangerRedDot = Color(0xFFE53935);
  static const Color cardBackground = Colors.white;
  static const Color dividerColor = Color(0xFFE5E5E5);
  static const Color shadow = Colors.black;
  static const Color transparent = Colors.transparent;

  // Calendar-specific
  static const Color calendarWeekdayText = iosGraySecondary;
  static const Color calendarWeekendText = iosRed;
  static const Color calendarLunarText = iosGraySecondary;
  static const Color calendarDotRed = iosRed;
  static const Color calendarDotBlack = textPrimary;
  static const Color calendarHeaderTitle = iosGrayPrimary;
  static const Color calendarHeaderSubtitle = iosGraySecondary;
  static const Color calendarNavArrow = iosLinkBlue;
  static const Color calendarCardBackground = Colors.white;
  static const Color calendarCardBorder = iosGrayDivider;
  static const Color calendarGoldenHourTagBg = Color(0xFFD7F5DD);
  static const Color calendarGoldenHourIconBgPositive = Color(0xFFDAF5D5);
  static const Color calendarGoldenHourIconBgNeutral = Color(0xFFFEECC8);
  static const Color calendarGoldenHourTextPrimary = iosGrayPrimary;
  static const Color calendarGoldenHourTextSecondary = iosGraySecondary;
}
