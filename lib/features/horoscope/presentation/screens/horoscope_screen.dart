import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/responsive_utils.dart';
import '../widgets/horoscope_tab_bar.dart';
import '../widgets/horoscope_input_modal.dart';
import '../widgets/horoscope_result_view.dart';
import '../providers/horoscope_providers.dart';

class HoroscopeScreen extends ConsumerStatefulWidget {
  const HoroscopeScreen({super.key});

  @override
  ConsumerState<HoroscopeScreen> createState() => _HoroscopeScreenState();
}

class _HoroscopeScreenState extends ConsumerState<HoroscopeScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  HoroscopeType _selectedType = HoroscopeType.lifetime;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
    _tabController.addListener(() {
      if (_tabController.indexIsChanging) {
        setState(() {
          _selectedType = HoroscopeType.values[_tabController.index];
        });
      }
    });
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  void _showInputModal() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => HoroscopeInputModal(
        type: _selectedType,
        onSubmitted: () {
          Navigator.of(context).pop();
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final sizeClass = getSizeClass(constraints.maxWidth);

        return Scaffold(
          backgroundColor: AppColors.backgroundDailyStart,
          body: SafeArea(
            child: Column(
              children: [
                // Header
                _buildHeader(sizeClass, ref),
                // Tab Bar
                HoroscopeTabBar(
                  controller: _tabController,
                  sizeClass: sizeClass,
                ),
                // Content
                Expanded(
                  child: TabBarView(
                    controller: _tabController,
                    children: HoroscopeType.values.map((type) {
                      return HoroscopeResultView(
                        type: type,
                        sizeClass: sizeClass,
                        onShowInput: _showInputModal,
                      );
                    }).toList(),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildHeader(ScreenSizeClass sizeClass, WidgetRef ref) {
    return Container(
      padding: EdgeInsets.symmetric(
        horizontal: horizontalPaddingFor(sizeClass),
        vertical: AppSpacing.m,
      ),
      child: Row(
        children: [
          IconButton(
            onPressed: () => Navigator.of(context).pop(),
            icon: const Icon(Icons.arrow_back),
            color: AppColors.textPrimary,
          ),
          Expanded(
            child: Center(
              child: Text(
                'Tử vi bói toán',
                style: AppTypography.headline1(sizeClass),
              ),
            ),
          ),
          IconButton(
            onPressed: () {
              // Reset all horoscope inputs
              ref.read(lifetimeByBirthInputProvider.notifier).clear();
              ref.read(yearlyHoroscopeInputProvider.notifier).clear();
              ref.read(monthlyHoroscopeInputProvider.notifier).clear();
              ref.read(dailyHoroscopeInputProvider.notifier).clear();
              
              // Show confirmation message
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text('Đã xóa dữ liệu tử vi'),
                  duration: Duration(seconds: 2),
                ),
              );
            },
            icon: const Icon(Icons.refresh),
            color: AppColors.textPrimary,
            tooltip: 'Xóa dữ liệu tử vi',
          ),
        ],
      ),
    );
  }
}

enum HoroscopeType {
  lifetime,
  yearly,
  monthly,
  daily;

  String get label {
    switch (this) {
      case HoroscopeType.lifetime:
        return 'Tử vi trọn đời';
      case HoroscopeType.yearly:
        return 'Tử vi năm';
      case HoroscopeType.monthly:
        return 'Tử vi tháng';
      case HoroscopeType.daily:
        return 'Tử vi ngày';
    }
  }
}

