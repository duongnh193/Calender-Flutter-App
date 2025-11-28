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
      fontSize: 22 + delta,
      fontWeight: FontWeight.w600,
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
}
