import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../domain/tuvi_interpretation_models.dart';
import '../providers/tuvi_chart_providers.dart';
import '../widgets/overview_interpretation_card.dart';
import '../widgets/palace_interpretation_card.dart';

/// Screen for displaying Tu Vi chart interpretation.
class TuViInterpretationScreen extends ConsumerStatefulWidget {
  const TuViInterpretationScreen({
    super.key,
    required this.interpretation,
  });

  final TuViInterpretationResponse interpretation;

  @override
  ConsumerState<TuViInterpretationScreen> createState() =>
      _TuViInterpretationScreenState();
}

class _TuViInterpretationScreenState
    extends ConsumerState<TuViInterpretationScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;

  // Tab definitions with palace codes
  static const _tabs = [
    {'label': 'Tổng quan', 'code': 'OVERVIEW', 'icon': Icons.dashboard},
    {'label': 'Mệnh', 'code': 'MENH', 'icon': Icons.person},
    {'label': 'Quan Lộc', 'code': 'QUAN_LOC', 'icon': Icons.work},
    {'label': 'Tài Bạch', 'code': 'TAI_BACH', 'icon': Icons.attach_money},
    {'label': 'Phu Thê', 'code': 'PHU_THE', 'icon': Icons.favorite},
    {'label': 'Tật Ách', 'code': 'TAT_ACH', 'icon': Icons.healing},
    {'label': 'Các cung khác', 'code': 'OTHER', 'icon': Icons.more_horiz},
  ];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: _tabs.length, vsync: this);
    _tabController.addListener(() {
      if (!_tabController.indexIsChanging) {
        ref.read(interpretationTabIndexProvider.notifier).state =
            _tabController.index;
      }
    });
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final interpretation = widget.interpretation;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Luận giải Tử Vi'),
        centerTitle: true,
        elevation: 0,
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          tabs: _tabs.map((tab) {
            return Tab(
              icon: Icon(tab['icon'] as IconData, size: 20),
              text: tab['label'] as String,
            );
          }).toList(),
          labelColor: colorScheme.primary,
          unselectedLabelColor: colorScheme.onSurfaceVariant,
          indicatorSize: TabBarIndicatorSize.tab,
          tabAlignment: TabAlignment.start,
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          // Tab 0: Overview
          _buildOverviewTab(interpretation),

          // Tab 1: Mệnh
          _buildSinglePalaceTab(interpretation.menhInterpretation),

          // Tab 2: Quan Lộc
          _buildSinglePalaceTab(interpretation.quanLocInterpretation),

          // Tab 3: Tài Bạch
          _buildSinglePalaceTab(interpretation.taiBachInterpretation),

          // Tab 4: Phu Thê
          _buildSinglePalaceTab(interpretation.phuTheInterpretation),

          // Tab 5: Tật Ách
          _buildSinglePalaceTab(interpretation.tatAchInterpretation),

          // Tab 6: Other palaces
          _buildOtherPalacesTab(interpretation),
        ],
      ),
      bottomNavigationBar: _buildBottomInfo(context, interpretation),
    );
  }

  Widget _buildOverviewTab(TuViInterpretationResponse interpretation) {
    if (interpretation.overview == null) {
      return const Center(
        child: Text('Không có dữ liệu tổng quan'),
      );
    }

    return OverviewInterpretationCard(
      overview: interpretation.overview!,
      name: interpretation.name,
      gender: interpretation.gender,
      lunarYearCanChi: interpretation.lunarYearCanChi,
    );
  }

  Widget _buildSinglePalaceTab(PalaceInterpretation? palace) {
    if (palace == null) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.hourglass_empty, size: 48, color: Colors.grey),
            SizedBox(height: 16),
            Text('Đang cập nhật nội dung luận giải...'),
          ],
        ),
      );
    }

    return SingleChildScrollView(
      child: PalaceInterpretationCard(
        interpretation: palace,
        isExpanded: true,
      ),
    );
  }

  Widget _buildOtherPalacesTab(TuViInterpretationResponse interpretation) {
    final otherPalaces = [
      interpretation.tuTucInterpretation,
      interpretation.dienTrachInterpretation,
      interpretation.phuMauInterpretation,
      interpretation.huynhDeInterpretation,
      interpretation.phucDucInterpretation,
      interpretation.noBocInterpretation,
      interpretation.thienDiInterpretation,
    ].where((p) => p != null).cast<PalaceInterpretation>().toList();

    if (otherPalaces.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.hourglass_empty, size: 48, color: Colors.grey),
            SizedBox(height: 16),
            Text('Đang cập nhật nội dung luận giải...'),
          ],
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.symmetric(vertical: 8),
      itemCount: otherPalaces.length,
      itemBuilder: (context, index) {
        return PalaceInterpretationCard(
          interpretation: otherPalaces[index],
          isExpanded: index == 0,
        );
      },
    );
  }

  Widget _buildBottomInfo(
      BuildContext context, TuViInterpretationResponse interpretation) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: colorScheme.surface,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.05),
            offset: const Offset(0, -1),
            blurRadius: 4,
          ),
        ],
      ),
      child: SafeArea(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              children: [
                Icon(
                  Icons.smart_toy_outlined,
                  size: 16,
                  color: colorScheme.onSurfaceVariant,
                ),
                const SizedBox(width: 4),
                Text(
                  'AI: ${interpretation.aiModel ?? 'Unknown'}',
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: colorScheme.onSurfaceVariant,
                  ),
                ),
              ],
            ),
            Text(
              'Được tạo: ${_formatDate(interpretation.generatedAt)}',
              style: theme.textTheme.bodySmall?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _formatDate(String? dateStr) {
    if (dateStr == null) return 'N/A';
    try {
      final date = DateTime.parse(dateStr);
      return '${date.day}/${date.month}/${date.year}';
    } catch (e) {
      return dateStr;
    }
  }
}
