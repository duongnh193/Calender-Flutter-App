import 'package:flutter/material.dart';

import '../../core/constants/size_breakpoints.dart';
import '../../core/theme/app_colors.dart';
import '../../core/theme/app_typography.dart';

class MainBottomNav extends StatelessWidget {
  const MainBottomNav({
    super.key,
    required this.currentIndex,
    required this.onTap,
    required this.sizeClass,
  });

  final int currentIndex;
  final ValueChanged<int> onTap;
  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    final selectedStyle = AppTypography.body2(
      sizeClass,
    ).copyWith(color: AppColors.primaryRed);
    final unselectedStyle = AppTypography.body2(
      sizeClass,
    ).copyWith(color: AppColors.textSecondary);

    return BottomNavigationBar(
      type: BottomNavigationBarType.fixed,
      currentIndex: currentIndex,
      onTap: onTap,
      selectedItemColor: AppColors.primaryRed,
      unselectedItemColor: AppColors.textSecondary,
      selectedLabelStyle: selectedStyle,
      unselectedLabelStyle: unselectedStyle,
      items: const [
        BottomNavigationBarItem(icon: Icon(Icons.event), label: 'Lịch ngày'),
        BottomNavigationBarItem(
          icon: Icon(Icons.calendar_month),
          label: 'Lịch tháng',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.notes_outlined),
          label: 'Ghi chú',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.public),
          label: 'Văn hóa Việt',
        ),
        BottomNavigationBarItem(icon: Icon(Icons.apps), label: 'Khám phá'),
      ],
    );
  }
}
