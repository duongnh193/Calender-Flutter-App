import 'package:flutter/material.dart';

import '../../domain/tuvi_interpretation_models.dart';

/// Card widget for displaying the overview interpretation section.
class OverviewInterpretationCard extends StatelessWidget {
  const OverviewInterpretationCard({
    super.key,
    required this.overview,
    required this.name,
    required this.gender,
    required this.lunarYearCanChi,
  });

  final OverviewSection overview;
  final String? name;
  final String? gender;
  final String? lunarYearCanChi;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header card with basic info
          Card(
            elevation: 3,
            shape:
                RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            child: Container(
              width: double.infinity,
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(16),
                gradient: LinearGradient(
                  colors: [
                    colorScheme.primaryContainer,
                    colorScheme.primaryContainer.withValues(alpha: 0.7),
                  ],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
              ),
              child: Column(
                children: [
                  Icon(
                    Icons.auto_awesome,
                    size: 48,
                    color: colorScheme.primary,
                  ),
                  const SizedBox(height: 12),
                  Text(
                    name ?? 'Thân chủ',
                    style: theme.textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: colorScheme.onPrimaryContainer,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '${gender == 'male' ? 'Nam' : 'Nữ'} - Năm $lunarYearCanChi',
                    style: theme.textTheme.bodyLarge?.copyWith(
                      color: colorScheme.onPrimaryContainer.withValues(alpha: 0.8),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Wrap(
                    spacing: 12,
                    runSpacing: 8,
                    alignment: WrapAlignment.center,
                    children: [
                      _buildInfoChip(
                        context,
                        '${overview.banMenhName}',
                        colorScheme.secondary,
                      ),
                      _buildInfoChip(
                        context,
                        '${overview.cucName}',
                        colorScheme.tertiary,
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 20),

          // Introduction
          if (overview.introduction != null &&
              overview.introduction!.isNotEmpty)
            _buildInterpretationSection(
              context,
              title: 'Giới thiệu',
              content: overview.introduction!,
              icon: Icons.info_outline,
              color: colorScheme.primary,
            ),

          // Bản mệnh
          _buildInterpretationSection(
            context,
            title: 'Bản mệnh của bạn',
            subtitle: '${overview.banMenhName} (${overview.banMenhNguHanh})',
            content: overview.banMenhInterpretation ?? 'Đang cập nhật...',
            icon: Icons.local_fire_department,
            color: Colors.red.shade700,
          ),

          // Cục mệnh
          _buildInterpretationSection(
            context,
            title: 'Cục mệnh của bạn',
            subtitle: overview.cucName,
            content: overview.cucInterpretation ?? 'Đang cập nhật...',
            icon: Icons.settings_suggest,
            color: Colors.purple.shade700,
          ),

          // Chủ mệnh
          _buildInterpretationSection(
            context,
            title: 'Chủ mệnh của bạn',
            subtitle: 'Sao ${overview.chuMenh} thủ mệnh',
            content: overview.chuMenhInterpretation ?? 'Đang cập nhật...',
            icon: Icons.stars,
            color: Colors.amber.shade700,
          ),

          // Chủ thân
          _buildInterpretationSection(
            context,
            title: 'Chủ thân của bạn',
            subtitle: 'Sao ${overview.chuThan} thủ thân',
            content: overview.chuThanInterpretation ?? 'Đang cập nhật...',
            icon: Icons.accessibility_new,
            color: Colors.green.shade700,
          ),

          // Lai nhân
          _buildInterpretationSection(
            context,
            title: 'Lai nhân (Thân cư)',
            subtitle: 'Thân cư ${overview.thanCu}',
            content: overview.laiNhanInterpretation ?? 'Đang cập nhật...',
            icon: Icons.home,
            color: Colors.blue.shade700,
          ),

          // Thuận Nghịch
          _buildInterpretationSection(
            context,
            title: 'Âm dương Thuận Nghịch',
            subtitle: overview.thuanNghich,
            content: overview.thuanNghichInterpretation ?? 'Đang cập nhật...',
            icon: Icons.sync_alt,
            color: Colors.teal.shade700,
          ),

          // Overall Summary
          if (overview.overallSummary != null &&
              overview.overallSummary!.isNotEmpty)
            _buildInterpretationSection(
              context,
              title: 'Tổng kết',
              content: overview.overallSummary!,
              icon: Icons.summarize,
              color: colorScheme.secondary,
              isHighlighted: true,
            ),

          const SizedBox(height: 20),
        ],
      ),
    );
  }

  Widget _buildInfoChip(BuildContext context, String label, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.2),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: color.withValues(alpha: 0.5)),
      ),
      child: Text(
        label,
        style: TextStyle(
          color: color,
          fontWeight: FontWeight.bold,
          fontSize: 13,
        ),
      ),
    );
  }

  Widget _buildInterpretationSection(
    BuildContext context, {
    required String title,
    String? subtitle,
    required String content,
    required IconData icon,
    required Color color,
    bool isHighlighted = false,
  }) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      child: Card(
        elevation: isHighlighted ? 3 : 1,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
          side: isHighlighted
              ? BorderSide(color: color.withValues(alpha: 0.5), width: 2)
              : BorderSide.none,
        ),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      color: color.withValues(alpha: 0.1),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Icon(icon, color: color, size: 22),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          title,
                          style: theme.textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.bold,
                            color: color,
                          ),
                        ),
                        if (subtitle != null && subtitle.isNotEmpty)
                          Padding(
                            padding: const EdgeInsets.only(top: 2),
                            child: Text(
                              subtitle,
                              style: theme.textTheme.bodySmall?.copyWith(
                                color: colorScheme.onSurface.withValues(alpha: 0.7),
                              ),
                            ),
                          ),
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              const Divider(height: 1),
              const SizedBox(height: 12),
              Text(
                content,
                style: theme.textTheme.bodyMedium?.copyWith(
                  height: 1.7,
                ),
                textAlign: TextAlign.justify,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
