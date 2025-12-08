import 'package:flutter/material.dart';

import '../constants/size_breakpoints.dart';
import 'app_colors.dart';

class AppTypography {
  AppTypography._();

  static TextStyle displayNumber(ScreenSizeClass sizeClass) {
    final size = switch (sizeClass) {
      ScreenSizeClass.small => 90.0,
      ScreenSizeClass.medium => 110.0,
      ScreenSizeClass.large => 120.0,
    };
    return TextStyle(
      fontSize: size,
      fontWeight: FontWeight.w700,
      color: AppColors.textPrimary,
      height: 1.0,
    );
  }

  static TextStyle headline1(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -1.0 : 0.0;
    return TextStyle(
      fontSize: 14 + delta,
      fontWeight: FontWeight.w700,
      color: AppColors.textPrimary,
    );
  }

  static TextStyle headline2(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -1.0 : 0.0;
    return TextStyle(
      fontSize: 18 + delta,
      fontWeight: FontWeight.w600,
      color: AppColors.textPrimary,
    );
  }

  static TextStyle subtitle1(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -1.0 : 0.0;
    return TextStyle(
      fontSize: 16 + delta,
      fontWeight: FontWeight.w500,
      color: AppColors.textPrimary,
    );
  }

  static TextStyle body1(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -0.5 : 0.0;
    return TextStyle(
      fontSize: 14 + delta,
      fontWeight: FontWeight.w400,
      color: AppColors.textPrimary,
      height: 1.4,
    );
  }

  static TextStyle body2(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -0.5 : 0.0;
    return TextStyle(
      fontSize: 12 + delta,
      fontWeight: FontWeight.w400,
      color: AppColors.textSecondary,
    );
  }

  static TextStyle caption(ScreenSizeClass sizeClass) {
    return TextStyle(
      fontSize: 11,
      fontWeight: FontWeight.w400,
      color: AppColors.textMuted,
    );
  }

  // Calendar styles
  static TextStyle calendarHeaderTitle(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -1.0 : 0.0;
    return TextStyle(
      fontSize: 16 + delta,
      fontWeight: FontWeight.w800,
      color: AppColors.calendarHeaderTitle,
    );
  }

  static TextStyle calendarHeaderSubtitle(ScreenSizeClass sizeClass) {
    return TextStyle(
      fontSize: 12 + (sizeClass == ScreenSizeClass.small ? -1.0 : 0.0),
      fontWeight: FontWeight.w700,
      color: AppColors.calendarHeaderSubtitle,
    );
  }

  static TextStyle calendarDay(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -1.0 : 0.0;
    return TextStyle(
      fontSize: 25 + delta,
      fontWeight: FontWeight.w900,
      color: AppColors.textPrimary,
    );
  }

  static TextStyle calendarDayWeekend(ScreenSizeClass sizeClass) =>
      calendarDay(sizeClass).copyWith(color: AppColors.calendarWeekendText);

  static TextStyle calendarLunar(ScreenSizeClass sizeClass) {
    return TextStyle(
      fontSize: 18 + (sizeClass == ScreenSizeClass.small ? -1.0 : 0.0),
      fontWeight: FontWeight.w700,
      color: AppColors.calendarLunarText,
    );
  }

  static TextStyle calendarPanelLabel(ScreenSizeClass sizeClass) {
    return TextStyle(
      fontSize: 16 + (sizeClass == ScreenSizeClass.small ? -1.0 : 0.0),
      fontWeight: FontWeight.w500,
      letterSpacing: 0.3,
      color: AppColors.textSecondary,
    );
  }

  static TextStyle calendarPanelValue(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -0.5 : 0.0;
    return TextStyle(
      fontSize: 16 + delta,
      fontWeight: FontWeight.w600,
      color: AppColors.textPrimary,
    );
  }

  static TextStyle calendarGoldenHourLabel(ScreenSizeClass sizeClass) {
    return TextStyle(
      fontSize: 13,
      fontWeight: FontWeight.w600,
      color: AppColors.calendarGoldenHourTextPrimary,
    );
  }

  static TextStyle calendarGoldenHourTime(ScreenSizeClass sizeClass) {
    return TextStyle(
      fontSize: 12,
      fontWeight: FontWeight.w400,
      color: AppColors.calendarGoldenHourTextSecondary,
    );
  }

  // Date detail (daily) styles
  static TextStyle dateDetailLabel(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -0.5 : 0.0;
    return TextStyle(
      fontSize: 16 + delta,
      fontWeight: FontWeight.w700,
      letterSpacing: 0.3,
      // ignore: deprecated_member_use
      color: AppColors.textPrimary,
    );
  }

  static TextStyle dateDetailValue(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -2.0 : 0.0;
    return TextStyle(
      fontSize: 16 + delta,
      fontWeight: FontWeight.w700,
      color: AppColors.textPrimary,
      height: 4.5 + delta,
    );
  }

  static TextStyle dateDetailCanChi(ScreenSizeClass sizeClass) {
    final delta = sizeClass == ScreenSizeClass.small ? -1.0 : 0.0;
    return TextStyle(
      fontSize: 15 + delta,
      fontWeight: FontWeight.w600,
      color: AppColors.textPrimary,
    );
  }
}
