import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../domain/tuvi_chart_models.dart';
import '../providers/tuvi_chart_providers.dart';
import '../widgets/center_card.dart';
import '../widgets/palace_card.dart';

/// Main screen for displaying Tu Vi Chart in a 4x4 grid layout.
class TuViChartScreen extends ConsumerStatefulWidget {
  const TuViChartScreen({super.key});

  @override
  ConsumerState<TuViChartScreen> createState() => _TuViChartScreenState();
}

class _TuViChartScreenState extends ConsumerState<TuViChartScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _dateController = TextEditingController();
  final _hourController = TextEditingController();
  final _minuteController = TextEditingController(text: '0');
  String _selectedGender = 'female';
  bool _isLunar = false;
  bool _isLoading = false;
  TuViChartResponse? _chartResult;
  String? _errorMessage;

  @override
  void dispose() {
    _nameController.dispose();
    _dateController.dispose();
    _hourController.dispose();
    _minuteController.dispose();
    super.dispose();
  }

  Future<void> _generateChart() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final repository = ref.read(tuViChartRepositoryProvider);
      final request = TuViChartRequest(
        date: _dateController.text,
        hour: int.parse(_hourController.text),
        minute: int.tryParse(_minuteController.text) ?? 0,
        gender: _selectedGender,
        isLunar: _isLunar,
        name: _nameController.text.trim(),
      );

      final result = await repository.generateChart(request);
      setState(() {
        _chartResult = result;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Lỗi: $e';
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final debugMode = ref.watch(tuViDebugModeProvider);

    return LayoutBuilder(
      builder: (context, constraints) {
        final sizeClass = getSizeClass(constraints.maxWidth);

        return Scaffold(
          appBar: AppBar(
            title: const Text('Lá Số Tử Vi'),
            actions: [
              // Debug toggle
              IconButton(
                icon: Icon(debugMode ? Icons.bug_report : Icons.bug_report_outlined),
                onPressed: () {
                  ref.read(tuViDebugModeProvider.notifier).state = !debugMode;
                },
                tooltip: 'Toggle Debug Mode',
              ),
            ],
          ),
          body: _chartResult == null
              ? _buildInputForm(sizeClass)
              : _buildChartView(_chartResult!, debugMode, sizeClass),
        );
      },
    );
  }

  Widget _buildInputForm(ScreenSizeClass sizeClass) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppSpacing.m),
      child: Form(
        key: _formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              'Nhập thông tin',
              style: AppTypography.headline2(sizeClass),
            ),
            const SizedBox(height: AppSpacing.m),

            // Name input (required)
            TextFormField(
              controller: _nameController,
              decoration: const InputDecoration(
                labelText: 'Họ và Tên *',
                hintText: 'Nhập họ và tên đầy đủ',
                prefixIcon: Icon(Icons.person_outline),
              ),
              validator: (value) {
                if (value == null || value.trim().isEmpty) {
                  return 'Vui lòng nhập họ và tên';
                }
                if (value.trim().length < 2) {
                  return 'Họ và tên phải có ít nhất 2 ký tự';
                }
                return null;
              },
            ),
            const SizedBox(height: AppSpacing.s),

            // Date input
            TextFormField(
              controller: _dateController,
              decoration: const InputDecoration(
                labelText: 'Ngày sinh (yyyy-MM-dd)',
                hintText: '1995-03-02',
                prefixIcon: Icon(Icons.calendar_today),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Vui lòng nhập ngày sinh';
                }
                if (!RegExp(r'^\d{4}-\d{2}-\d{2}$').hasMatch(value)) {
                  return 'Định dạng: yyyy-MM-dd';
                }
                return null;
              },
            ),
            const SizedBox(height: AppSpacing.s),

            // Hour and Minute row
            Row(
              children: [
                Expanded(
                  child: TextFormField(
                    controller: _hourController,
                    decoration: const InputDecoration(
                      labelText: 'Giờ (0-23)',
                      prefixIcon: Icon(Icons.access_time),
                    ),
                    keyboardType: TextInputType.number,
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Nhập giờ';
                      }
                      final hour = int.tryParse(value);
                      if (hour == null || hour < 0 || hour > 23) {
                        return '0-23';
                      }
                      return null;
                    },
                  ),
                ),
                const SizedBox(width: AppSpacing.s),
                Expanded(
                  child: TextFormField(
                    controller: _minuteController,
                    decoration: const InputDecoration(
                      labelText: 'Phút (0-59)',
                    ),
                    keyboardType: TextInputType.number,
                    validator: (value) {
                      if (value != null && value.isNotEmpty) {
                        final minute = int.tryParse(value);
                        if (minute == null || minute < 0 || minute > 59) {
                          return '0-59';
                        }
                      }
                      return null;
                    },
                  ),
                ),
              ],
            ),
            const SizedBox(height: AppSpacing.s),

            // Gender selection
            DropdownButtonFormField<String>(
              value: _selectedGender,
              decoration: const InputDecoration(
                labelText: 'Giới tính',
                prefixIcon: Icon(Icons.person),
              ),
              items: const [
                DropdownMenuItem(value: 'male', child: Text('Nam')),
                DropdownMenuItem(value: 'female', child: Text('Nữ')),
              ],
              onChanged: (value) {
                if (value != null) {
                  setState(() => _selectedGender = value);
                }
              },
            ),
            const SizedBox(height: AppSpacing.s),

            // Lunar calendar toggle
            SwitchListTile(
              title: const Text('Ngày âm lịch'),
              value: _isLunar,
              onChanged: (value) => setState(() => _isLunar = value),
            ),
            const SizedBox(height: AppSpacing.m),

            // Error message
            if (_errorMessage != null)
              Container(
                padding: const EdgeInsets.all(AppSpacing.s),
                decoration: BoxDecoration(
                  color: Colors.red.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(AppRadius.small),
                ),
                child: Text(
                  _errorMessage!,
                  style: const TextStyle(color: Colors.red),
                ),
              ),

            const SizedBox(height: AppSpacing.m),

            // Submit button
            ElevatedButton(
              onPressed: _isLoading ? null : _generateChart,
              child: _isLoading
                  ? const SizedBox(
                      height: 20,
                      width: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Text('Lập Lá Số'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildChartView(
      TuViChartResponse chart, bool debugMode, ScreenSizeClass sizeClass) {
    return Column(
      children: [
        // Back button and info bar
        Container(
          padding: const EdgeInsets.symmetric(
            horizontal: AppSpacing.m,
            vertical: AppSpacing.s,
          ),
          color: AppColors.primary.withOpacity(0.1),
          child: Row(
            children: [
              IconButton(
                icon: const Icon(Icons.arrow_back),
                onPressed: () => setState(() => _chartResult = null),
              ),
              Expanded(
                child: Text(
                  '${chart.center.lunarYearCanChi} - ${chart.center.cuc}',
                  style: AppTypography.headline2(sizeClass),
                  textAlign: TextAlign.center,
                ),
              ),
              Text(
                chart.cycles.directionText,
                style: AppTypography.body2(sizeClass).copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),

        // Chart grid
        Expanded(
          child: Padding(
            padding: const EdgeInsets.all(AppSpacing.xs),
            child: _buildChartGrid(chart, debugMode),
          ),
        ),

        // Debug overlay
        if (debugMode && chart.debug != null)
          Container(
            height: 150,
            padding: const EdgeInsets.all(AppSpacing.s),
            color: Colors.black87,
            child: SingleChildScrollView(
              child: Text(
                chart.debug.toString(),
                style: const TextStyle(
                  color: Colors.green,
                  fontFamily: 'monospace',
                  fontSize: 10,
                ),
              ),
            ),
          ),
      ],
    );
  }

  Widget _buildChartGrid(TuViChartResponse chart, bool debugMode) {
    // The 12 palaces are laid out in a 4x4 grid with the center 2x2 for CenterCard
    // Layout (positions correspond to DiaChi):
    //
    //  [Tỵ]   [Ngọ]   [Mùi]   [Thân]
    //  [Thìn] [CENTER][CENTER][Dậu]
    //  [Mão]  [CENTER][CENTER][Tuất]
    //  [Dần]  [Sửu]   [Tý]    [Hợi]
    //
    // DiaChi indices: Tý=0, Sửu=1, Dần=2, Mão=3, Thìn=4, Tỵ=5,
    //                 Ngọ=6, Mùi=7, Thân=8, Dậu=9, Tuất=10, Hợi=11

    // Map DiaChi to grid position
    final gridPositions = <String, List<int>>{
      'TI': [0, 0],    // Tỵ (index 5)
      'NGO': [0, 1],   // Ngọ
      'MUI': [0, 2],   // Mùi
      'THAN': [0, 3],  // Thân
      'THIN': [1, 0],  // Thìn
      'DAU': [1, 3],   // Dậu
      'MAO': [2, 0],   // Mão
      'TUAT': [2, 3],  // Tuất
      'DAN': [3, 0],   // Dần
      'SUU': [3, 1],   // Sửu
      'TY': [3, 2],    // Tý
      'HOI': [3, 3],   // Hợi
    };

    return LayoutBuilder(
      builder: (context, constraints) {
        final cellWidth = constraints.maxWidth / 4;
        final cellHeight = constraints.maxHeight / 4;

        return Stack(
          children: [
            // Background grid lines
            ...List.generate(5, (i) => Positioned(
                  left: 0,
                  right: 0,
                  top: i * cellHeight,
                  child: Divider(height: 1, color: Colors.grey.withOpacity(0.3)),
                )),
            ...List.generate(5, (i) => Positioned(
                  top: 0,
                  bottom: 0,
                  left: i * cellWidth,
                  child: VerticalDivider(
                      width: 1, color: Colors.grey.withOpacity(0.3)),
                )),

            // Palace cards
            ...chart.palaces.map((palace) {
              final pos = gridPositions[palace.diaChiCode];
              if (pos == null) return const SizedBox();

              return Positioned(
                top: pos[0] * cellHeight,
                left: pos[1] * cellWidth,
                width: cellWidth,
                height: cellHeight,
                child: PalaceCard(palace: palace, showDebug: debugMode),
              );
            }),

            // Center card (2x2 in the middle)
            Positioned(
              top: cellHeight,
              left: cellWidth,
              width: cellWidth * 2,
              height: cellHeight * 2,
              child: CenterCard(center: chart.center),
            ),
          ],
        );
      },
    );
  }
}
