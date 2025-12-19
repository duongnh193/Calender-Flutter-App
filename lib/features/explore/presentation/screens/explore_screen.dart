import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/responsive_utils.dart';
import '../widgets/explore_item.dart';
import '../widgets/explore_section.dart';

class ExploreScreen extends ConsumerWidget {
  const ExploreScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final sizeClass = getSizeClass(constraints.maxWidth);

        return Scaffold(
          body: SafeArea(
            child: Column(
              children: [
                Padding(
                  padding: EdgeInsets.symmetric(
                    horizontal: horizontalPaddingFor(sizeClass),
                    vertical: AppSpacing.m,
                  ),
                  child: Row(
                    children: [
                      Expanded(
                        child: Center(
                          child: Text(
                            'Khám phá',
                            style: AppTypography.headline1(sizeClass),
                          ),
                        ),
                      ),
                      IconButton(
                        onPressed: () {},
                        icon: const Icon(Icons.settings_outlined),
                      ),
                    ],
                  ),
                ),
                Expanded(
                  child: SingleChildScrollView(
                    padding: EdgeInsets.symmetric(
                      horizontal: horizontalPaddingFor(sizeClass),
                      vertical: AppSpacing.m,
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        ExploreSection(
                          title: 'Tiện ích',
                          items: _utilitiesItems,
                          sizeClass: sizeClass,
                          onItemTap: (item) => _handleItemTap(context, item),
                        ),
                        const SizedBox(height: AppSpacing.xl),
                        ExploreSection(
                          title: 'Nội dung khác',
                          items: _contentItems,
                          sizeClass: sizeClass,
                          onItemTap: (item) => _handleItemTap(context, item),
                        ),
                        const SizedBox(height: AppSpacing.xl),
                        // Center(
                        //   child: ExploreItem(
                        //     item: ExploreItemModel(
                        //       icon: Icons.menu_book,
                        //       label: 'Cổ tích Việt Nam',
                        //       color: AppColors.accentBlue,
                        //     ),
                        //     sizeClass: sizeClass,
                        //   ),
                        // ),
                        const SizedBox(height: AppSpacing.l),
                        _AdPlaceholder(sizeClass: sizeClass),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  void _handleItemTap(BuildContext context, ExploreItemModel item) {
    if (item.label == 'Tử vi bói toán') {
      context.push('/horoscope');
    } else if (item.label == 'Lá số tử vi') {
      context.push('/tuvi-chart');
    }
    // Handle other items here
  }
}

final _utilitiesItems = <ExploreItemModel>[
  ExploreItemModel(
    icon: Icons.stars,
    label: 'Tử vi bói toán',
    color: AppColors.primaryRed,
  ),
  ExploreItemModel(
    icon: Icons.auto_awesome,
    label: 'Lá số tử vi',
    color: AppColors.accentPurple,
  ),
  ExploreItemModel(
    icon: Icons.sync_alt,
    label: 'Đổi ngày âm dương',
    color: AppColors.accentBlue,
  ),
  ExploreItemModel(
    icon: Icons.explore,
    label: 'La bàn phong thủy',
    color: AppColors.primaryGreen,
  ),
  ExploreItemModel(
    icon: Icons.emoji_people,
    label: 'Xông đất',
    color: AppColors.accentOrange,
  ),
  ExploreItemModel(
    icon: Icons.event_available,
    label: 'Xem ngày tốt',
    color: AppColors.accentYellow,
  ),
  ExploreItemModel(
    icon: Icons.nightlight_round,
    label: 'Giải mộng',
    color: AppColors.accentLightBlue,
  ),
];

final _contentItems = <ExploreItemModel>[
  ExploreItemModel(
    icon: Icons.person,
    label: 'Danh nhân thế giới',
    color: AppColors.accentOrange,
  ),
  ExploreItemModel(
    icon: Icons.format_quote,
    label: 'Danh ngôn cuộc sống',
    color: AppColors.primaryGreen,
  ),
  ExploreItemModel(
    icon: Icons.lightbulb_outline,
    label: 'Mẹo vặt hằng ngày',
    color: AppColors.primaryRed,
  ),
  ExploreItemModel(
    icon: Icons.emoji_emotions,
    label: 'Truyện cười',
    color: AppColors.accentBlue,
  ),
  ExploreItemModel(
    icon: Icons.menu_book,
    label: 'Cổ tích Việt Nam',
    color: AppColors.accentBlue,
  ),
];

class _AdPlaceholder extends StatelessWidget {
  const _AdPlaceholder({required this.sizeClass});

  final ScreenSizeClass sizeClass;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      height: 64,
      decoration: BoxDecoration(
        color: AppColors.cardBackground,
        borderRadius: BorderRadius.circular(AppRadius.medium),
        border: Border.all(color: AppColors.dividerColor),
      ),
      alignment: Alignment.center,
      child: Text(
        'Ad banner placeholder',
        style: AppTypography.body2(sizeClass),
      ),
    );
  }
}
