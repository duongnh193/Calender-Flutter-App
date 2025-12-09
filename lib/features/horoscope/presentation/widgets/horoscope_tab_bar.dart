import 'package:flutter/material.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_typography.dart';
import '../screens/horoscope_screen.dart';

class HoroscopeTabBar extends StatelessWidget {
  const HoroscopeTabBar({
    super.key,
    required this.controller,
    required this.sizeClass,
  });

  final TabController controller;
  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.white,
      child: TabBar(
        controller: controller,
        isScrollable: true,
        indicatorColor: AppColors.primaryRed,
        indicatorWeight: 3,
        labelColor: AppColors.primaryRed,
        unselectedLabelColor: AppColors.textSecondary,
        labelStyle: AppTypography.body1(sizeClass).copyWith(
          fontWeight: FontWeight.w600,
        ),
        unselectedLabelStyle: AppTypography.body1(sizeClass),
        tabs: HoroscopeType.values
            .map((type) => Tab(text: type.label))
            .toList(),
      ),
    );
  }
}

