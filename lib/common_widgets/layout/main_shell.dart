import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../core/constants/size_breakpoints.dart';
import 'main_bottom_nav.dart';

class MainShell extends StatelessWidget {
  const MainShell({super.key, required this.navigationShell});

  final StatefulNavigationShell navigationShell;

  void _onTabSelected(int index) {
    navigationShell.goBranch(
      index,
      initialLocation: index == navigationShell.currentIndex,
    );
  }

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final sizeClass = getSizeClass(constraints.maxWidth);
        return Scaffold(
          backgroundColor: Colors.transparent,
          body: SafeArea(top: false, bottom: false, child: navigationShell),
          bottomNavigationBar: SafeArea(
            top: false,
            bottom: true,
            child: MainBottomNav(
              currentIndex: navigationShell.currentIndex,
              onTap: _onTabSelected,
              sizeClass: sizeClass,
            ),
          ),
        );
      },
    );
  }
}
