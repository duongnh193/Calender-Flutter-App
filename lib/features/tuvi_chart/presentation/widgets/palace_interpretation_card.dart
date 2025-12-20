import 'package:flutter/material.dart';

import '../../domain/tuvi_interpretation_models.dart';

/// Card widget for displaying a single palace interpretation.
class PalaceInterpretationCard extends StatelessWidget {
  const PalaceInterpretationCard({
    super.key,
    required this.interpretation,
    this.isExpanded = false,
  });

  final PalaceInterpretation interpretation;
  final bool isExpanded;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: ExpansionTile(
        initiallyExpanded: isExpanded,
        tilePadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        childrenPadding:
            const EdgeInsets.only(left: 16, right: 16, bottom: 16),
        leading: CircleAvatar(
          backgroundColor: colorScheme.primaryContainer,
          child: Text(
            interpretation.palaceName?.substring(0, 1) ?? '?',
            style: TextStyle(
              color: colorScheme.onPrimaryContainer,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        title: Row(
          children: [
            Expanded(
              child: Text(
                'Cung ${interpretation.palaceName ?? 'N/A'}',
                style: theme.textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            if (interpretation.hasTuan)
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                margin: const EdgeInsets.only(right: 4),
                decoration: BoxDecoration(
                  color: Colors.orange.shade100,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: const Text(
                  'Tuần',
                  style: TextStyle(fontSize: 10, color: Colors.orange),
                ),
              ),
            if (interpretation.hasTriet)
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: Colors.red.shade100,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: const Text(
                  'Triệt',
                  style: TextStyle(fontSize: 10, color: Colors.red),
                ),
              ),
          ],
        ),
        subtitle: Padding(
          padding: const EdgeInsets.only(top: 4),
          child: Text(
            '${interpretation.palaceChi ?? ''} ${interpretation.canChiPrefix != null ? '(${interpretation.canChiPrefix})' : ''}',
            style: theme.textTheme.bodySmall?.copyWith(
              color: colorScheme.onSurfaceVariant,
            ),
          ),
        ),
        children: [
          _buildContentSection(
            context,
            title: 'Tóm tắt',
            content: interpretation.summary,
            icon: Icons.summarize_outlined,
          ),
          const Divider(height: 24),
          _buildContentSection(
            context,
            title: 'Giới thiệu',
            content: interpretation.introduction,
            icon: Icons.info_outline,
          ),
          if (interpretation.detailedAnalysis != null &&
              interpretation.detailedAnalysis!.isNotEmpty) ...[
            const Divider(height: 24),
            _buildContentSection(
              context,
              title: 'Phân tích chi tiết',
              content: interpretation.detailedAnalysis,
              icon: Icons.analytics_outlined,
            ),
          ],
          if (interpretation.genderAnalysis != null &&
              interpretation.genderAnalysis!.isNotEmpty) ...[
            const Divider(height: 24),
            _buildContentSection(
              context,
              title: 'Phân tích theo giới tính',
              content: interpretation.genderAnalysis,
              icon: Icons.person_outline,
            ),
          ],
          if (interpretation.starAnalyses != null &&
              interpretation.starAnalyses!.isNotEmpty) ...[
            const Divider(height: 24),
            _buildStarsSection(context),
          ],
          if (interpretation.tuanTrietEffect != null &&
              interpretation.tuanTrietEffect!.isNotEmpty) ...[
            const Divider(height: 24),
            _buildContentSection(
              context,
              title: 'Ảnh hưởng Tuần/Triệt',
              content: interpretation.tuanTrietEffect,
              icon: Icons.warning_amber_outlined,
              isWarning: true,
            ),
          ],
          if (interpretation.adviceSection != null &&
              interpretation.adviceSection!.isNotEmpty) ...[
            const Divider(height: 24),
            _buildContentSection(
              context,
              title: 'Lời khuyên',
              content: interpretation.adviceSection,
              icon: Icons.lightbulb_outline,
              isAdvice: true,
            ),
          ],
          if (interpretation.conclusion != null &&
              interpretation.conclusion!.isNotEmpty) ...[
            const Divider(height: 24),
            _buildContentSection(
              context,
              title: 'Kết luận',
              content: interpretation.conclusion,
              icon: Icons.check_circle_outline,
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildContentSection(
    BuildContext context, {
    required String title,
    required String? content,
    required IconData icon,
    bool isWarning = false,
    bool isAdvice = false,
  }) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    Color iconColor = colorScheme.primary;
    if (isWarning) iconColor = Colors.orange;
    if (isAdvice) iconColor = Colors.green;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Icon(icon, size: 18, color: iconColor),
            const SizedBox(width: 8),
            Text(
              title,
              style: theme.textTheme.titleSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: iconColor,
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Text(
          content ?? 'Đang cập nhật nội dung...',
          style: theme.textTheme.bodyMedium?.copyWith(
            height: 1.6,
          ),
          textAlign: TextAlign.justify,
        ),
      ],
    );
  }

  Widget _buildStarsSection(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Icon(Icons.star_outline, size: 18, color: colorScheme.secondary),
            const SizedBox(width: 8),
            Text(
              'Luận các sao trong cung',
              style: theme.textTheme.titleSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: colorScheme.secondary,
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        ...interpretation.starAnalyses!.map((star) => _buildStarItem(
              context,
              star,
            )),
      ],
    );
  }

  Widget _buildStarItem(BuildContext context, StarInterpretation star) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.3),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(
          color: colorScheme.outline.withValues(alpha: 0.2),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                Icons.stars,
                size: 16,
                color: _getStarColor(star.starType),
              ),
              const SizedBox(width: 6),
              Text(
                star.starName ?? 'Sao',
                style: theme.textTheme.bodyMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              if (star.brightness != null) ...[
                const SizedBox(width: 8),
                Container(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(
                    color: _getBrightnessColor(star.brightness!)
                        .withValues(alpha: 0.2),
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    star.brightness!,
                    style: TextStyle(
                      fontSize: 10,
                      color: _getBrightnessColor(star.brightness!),
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ],
          ),
          if (star.interpretation != null &&
              star.interpretation!.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(
              star.interpretation!,
              style: theme.textTheme.bodySmall?.copyWith(
                height: 1.5,
              ),
              textAlign: TextAlign.justify,
            ),
          ],
        ],
      ),
    );
  }

  Color _getStarColor(String? type) {
    switch (type) {
      case 'CHINH_TINH':
        return Colors.red.shade700;
      case 'PHU_TINH':
        return Colors.blue.shade700;
      default:
        return Colors.grey.shade600;
    }
  }

  Color _getBrightnessColor(String brightness) {
    if (brightness.contains('Miếu') || brightness.contains('Vượng')) {
      return Colors.green.shade700;
    } else if (brightness.contains('Đắc')) {
      return Colors.blue.shade700;
    } else if (brightness.contains('Bình')) {
      return Colors.orange.shade700;
    } else {
      return Colors.red.shade700;
    }
  }
}
