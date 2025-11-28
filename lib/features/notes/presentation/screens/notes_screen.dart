import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/responsive_utils.dart';

class NotesScreen extends ConsumerWidget {
  const NotesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final sizeClass = getSizeClass(constraints.maxWidth);

        return Scaffold(
          body: SafeArea(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Padding(
                  padding: EdgeInsets.symmetric(
                    horizontal: horizontalPaddingFor(sizeClass),
                    vertical: AppSpacing.l,
                  ),
                  child: _NotesMonthHeader(sizeClass: sizeClass),
                ),
                const Divider(height: 1),
                Expanded(
                  child: ListView.builder(
                    padding: EdgeInsets.symmetric(
                      horizontal: horizontalPaddingFor(sizeClass),
                      vertical: AppSpacing.l,
                    ),
                    itemCount: _mockNotes.length,
                    itemBuilder: (context, index) {
                      final section = _mockNotes[index];
                      return _NotesSection(
                        section: section,
                        sizeClass: sizeClass,
                      );
                    },
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}

class _NotesMonthHeader extends StatelessWidget {
  const _NotesMonthHeader({required this.sizeClass});

  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        IconButton(onPressed: () {}, icon: const Icon(Icons.chevron_left)),
        Expanded(
          child: Column(
            children: [
              Text('Tháng 11', style: AppTypography.headline1(sizeClass)),
              const SizedBox(height: AppSpacing.xs),
              Text(
                '2025',
                style: AppTypography.subtitle1(
                  sizeClass,
                ).copyWith(color: AppColors.textSecondary),
              ),
            ],
          ),
        ),
        IconButton(onPressed: () {}, icon: const Icon(Icons.chevron_right)),
      ],
    );
  }
}

class _NotesSection extends StatelessWidget {
  const _NotesSection({required this.section, required this.sizeClass});

  final NotesSection section;
  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: AppSpacing.l),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(section.title, style: AppTypography.headline2(sizeClass)),
          const SizedBox(height: AppSpacing.xs),
          Text(section.lunar, style: AppTypography.body2(sizeClass)),
          const SizedBox(height: AppSpacing.s),
          Container(
            decoration: BoxDecoration(
              color: AppColors.cardBackground,
              borderRadius: BorderRadius.circular(AppRadius.medium),
              border: Border.all(color: AppColors.dividerColor),
            ),
            child: Column(
              children: [
                for (var i = 0; i < section.items.length; i++) ...[
                  _NoteItem(item: section.items[i], sizeClass: sizeClass),
                  if (i != section.items.length - 1)
                    const Divider(height: 1, color: AppColors.dividerColor),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _NoteItem extends StatelessWidget {
  const _NoteItem({required this.item, required this.sizeClass});

  final NoteItem item;
  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    final labelStyle = AppTypography.body2(
      sizeClass,
    ).copyWith(color: AppColors.textSecondary);

    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.l,
        vertical: AppSpacing.m,
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(width: 70, child: Text(item.timeLabel, style: labelStyle)),
          const SizedBox(width: AppSpacing.m),
          Expanded(
            child: Text(
              item.title,
              style: AppTypography.body1(
                sizeClass,
              ).copyWith(fontWeight: FontWeight.w600),
            ),
          ),
        ],
      ),
    );
  }
}

class NoteItem {
  NoteItem({required this.timeLabel, required this.title});

  final String timeLabel;
  final String title;
}

class NotesSection {
  NotesSection({required this.title, required this.lunar, required this.items});

  final String title;
  final String lunar;
  final List<NoteItem> items;
}

final _mockNotes = <NotesSection>[
  NotesSection(
    title: 'Chủ Nhật, 2 Tháng 11, 2025',
    lunar: '13/9 Âm Lịch',
    items: [NoteItem(timeLabel: 'Cả ngày', title: 'Khai hội chùa Keo')],
  ),
  NotesSection(
    title: 'Thứ Ba, 4 Tháng 11, 2025',
    lunar: '15/9 Âm Lịch',
    items: [NoteItem(timeLabel: 'Cả ngày', title: 'Ngày rằm')],
  ),
  NotesSection(
    title: 'Thứ Năm, 20 Tháng 11, 2025',
    lunar: '1/10 Âm Lịch',
    items: [
      NoteItem(timeLabel: 'Cả ngày', title: 'Ngày nhà giáo Việt Nam'),
      NoteItem(timeLabel: 'Cả ngày', title: 'Ngày mùng một'),
    ],
  ),
];
