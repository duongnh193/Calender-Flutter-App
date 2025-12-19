import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/constants/size_breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_radius.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/utils/date_time_utils.dart';
import '../../domain/horoscope_models.dart';
import '../screens/horoscope_screen.dart';
import '../providers/horoscope_providers.dart';

class HoroscopeInputModal extends ConsumerStatefulWidget {
  const HoroscopeInputModal({
    super.key,
    required this.type,
    required this.onSubmitted,
  });

  final HoroscopeType type;
  final VoidCallback onSubmitted;

  @override
  ConsumerState<HoroscopeInputModal> createState() =>
      _HoroscopeInputModalState();
}

class _HoroscopeInputModalState extends ConsumerState<HoroscopeInputModal> {
  final _formKey = GlobalKey<FormState>();
  final _zodiacCodeController = TextEditingController();
  final _dailyZodiacCodeController = TextEditingController();
  
  // Valid zodiac codes
  static const List<String> _validZodiacCodes = [
    'ti', 'suu', 'dan', 'mao', 'thin', 'ty',
    'ngo', 'mui', 'than', 'dau', 'tuat', 'hoi',
  ];

  // Lifetime by birth
  DateTime? _selectedDate;
  int _hour = 0;
  int _minute = 0;
  bool _isLunar = false;
  bool _isLeapMonth = false;
  HoroscopeGender _gender = HoroscopeGender.male;

  // Yearly/Monthly/Daily
  int? _selectedZodiacId;
  String? _selectedZodiacCode;
  int _selectedYear = DateTime.now().year;
  int _selectedMonth = DateTime.now().month;
  DateTime? _selectedDay;
  
  // Loading state to prevent double-submit
  bool _isSubmitting = false;
  
  @override
  void dispose() {
    _zodiacCodeController.dispose();
    _dailyZodiacCodeController.dispose();
    super.dispose();
  }
  
  // Helper method to validate zodiac code
  // Note: This is used for validation when value is not empty
  // Empty validation is handled in the field's validator
  String? _validateZodiacCode(String? value) {
    if (value == null || value.isEmpty) {
      return null; // Empty is handled by field validator
    }
    
    // Check if it's a valid ID (1-12)
    final id = int.tryParse(value);
    if (id != null && id >= 1 && id <= 12) {
      return null; // Valid ID
    }
    
    // Check if it's a valid zodiac code
    final normalizedCode = value.toLowerCase().trim();
    if (_validZodiacCodes.contains(normalizedCode)) {
      return null; // Valid code
    }
    
    // Invalid code
    return 'Mã cung hoàng đạo không hợp lệ. Vui lòng nhập mã (ti, suu, dan, ...) hoặc ID (1-12)';
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      child: DraggableScrollableSheet(
        initialChildSize: 0.7,
        minChildSize: 0.5,
        maxChildSize: 0.95,
        builder: (context, scrollController) {
          return SingleChildScrollView(
            controller: scrollController,
            padding: const EdgeInsets.all(AppSpacing.l),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                mainAxisSize: MainAxisSize.min,
                children: [
                  // Drag handle
                  Center(
                    child: Container(
                      width: 40,
                      height: 4,
                      margin: const EdgeInsets.only(bottom: AppSpacing.l),
                      decoration: BoxDecoration(
                        color: AppColors.dividerColor,
                        borderRadius: BorderRadius.circular(2),
                      ),
                    ),
                  ),
                  // Title
                  Text(
                    _getTitle(),
                    style: AppTypography.headline2(ScreenSizeClass.medium)
                        .copyWith(fontWeight: FontWeight.bold),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: AppSpacing.xl),
                  // Input fields based on type
                  ..._buildInputFields(),
                  const SizedBox(height: AppSpacing.xl),
                  // Submit button
                  ElevatedButton(
                    onPressed: _isSubmitting ? null : _handleSubmit,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.primaryRed,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(
                        vertical: AppSpacing.m,
                      ),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(AppRadius.medium),
                      ),
                    ),
                    child: _isSubmitting
                        ? const SizedBox(
                            height: 20,
                            width: 20,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                            ),
                          )
                        : const Text('Xem tử vi'),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  String _getTitle() {
    switch (widget.type) {
      case HoroscopeType.lifetime:
        return 'Nhập thông tin tử vi trọn đời';
      case HoroscopeType.yearly:
        return 'Chọn năm và cung hoàng đạo';
      case HoroscopeType.monthly:
        return 'Chọn tháng và cung hoàng đạo';
      case HoroscopeType.daily:
        return 'Chọn ngày và cung hoàng đạo';
    }
  }

  List<Widget> _buildInputFields() {
    switch (widget.type) {
      case HoroscopeType.lifetime:
        return _buildLifetimeInputs();
      case HoroscopeType.yearly:
        return _buildYearlyInputs();
      case HoroscopeType.monthly:
        return _buildMonthlyInputs();
      case HoroscopeType.daily:
        return _buildDailyInputs();
    }
  }

  List<Widget> _buildLifetimeInputs() {
    return [
      // Birth date
      TextFormField(
        key: const ValueKey('birth_date'),
        readOnly: true,
        decoration: InputDecoration(
          labelText: 'Ngày sinh *',
          hintText: 'Chọn ngày sinh',
          suffixIcon: const Icon(Icons.calendar_today),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppRadius.medium),
          ),
          errorText: _selectedDate == null && _formKey.currentState?.validate() == false
              ? 'Vui lòng chọn ngày sinh'
              : null,
        ),
        controller: TextEditingController(
          text: _selectedDate != null
              ? DateTimeUtils.formatDateForDisplay(_selectedDate!)
              : '',
        ),
        validator: (value) {
          if (_selectedDate == null) {
            return 'Vui lòng chọn ngày sinh';
          }
          return null;
        },
        onTap: () async {
          // Ensure we use the root navigator context to get MaterialLocalizations
          final navigatorContext = Navigator.of(context, rootNavigator: true).context;
          final date = await showDatePicker(
            context: navigatorContext,
            initialDate: _selectedDate ?? DateTime(1990, 1, 1),
            firstDate: DateTime(1900),
            lastDate: DateTime.now(),
            helpText: 'Chọn ngày sinh',
          );
          if (date != null) {
            setState(() => _selectedDate = date);
            _formKey.currentState?.validate();
          }
        },
      ),
      const SizedBox(height: AppSpacing.m),
      // Hour
      Row(
        children: [
          Expanded(
            child: TextFormField(
              decoration: InputDecoration(
                labelText: 'Giờ sinh (0-23)',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(AppRadius.medium),
                ),
                errorMaxLines: 2,
              ),
              keyboardType: TextInputType.number,
              inputFormatters: [
                FilteringTextInputFormatter.digitsOnly,
                LengthLimitingTextInputFormatter(2),
              ],
              initialValue: _hour.toString(),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Vui lòng nhập giờ sinh';
                }
                final hour = int.tryParse(value);
                if (hour == null) {
                  return 'Giờ phải là số';
                }
                if (hour < 0 || hour > 23) {
                  return 'Giờ phải trong khoảng 0-23';
                }
                return null;
              },
              onChanged: (value) {
                final hour = int.tryParse(value);
                if (hour != null && hour >= 0 && hour <= 23) {
                  setState(() => _hour = hour);
                  // Clear validation error when valid value is entered
                  _formKey.currentState?.validate();
                } else if (value.isEmpty) {
                  // Clear error when field is empty (will be validated on submit)
                  _formKey.currentState?.validate();
                }
              },
              onSaved: (value) {
                final hour = int.tryParse(value ?? '0');
                if (hour != null && hour >= 0 && hour <= 23) {
                  _hour = hour;
                }
              },
            ),
          ),
          const SizedBox(width: AppSpacing.m),
          Expanded(
            child: TextFormField(
              decoration: InputDecoration(
                labelText: 'Phút (0-59)',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(AppRadius.medium),
                ),
                errorMaxLines: 2,
              ),
              keyboardType: TextInputType.number,
              inputFormatters: [
                FilteringTextInputFormatter.digitsOnly,
                LengthLimitingTextInputFormatter(2),
              ],
              initialValue: _minute.toString(),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Vui lòng nhập phút';
                }
                final minute = int.tryParse(value);
                if (minute == null) {
                  return 'Phút phải là số';
                }
                if (minute < 0 || minute > 59) {
                  return 'Phút phải trong khoảng 0-59';
                }
                return null;
              },
              onChanged: (value) {
                final minute = int.tryParse(value);
                if (minute != null && minute >= 0 && minute <= 59) {
                  setState(() => _minute = minute);
                  // Clear validation error when valid value is entered
                  _formKey.currentState?.validate();
                } else if (value.isEmpty) {
                  // Clear error when field is empty (will be validated on submit)
                  _formKey.currentState?.validate();
                }
              },
              onSaved: (value) {
                final minute = int.tryParse(value ?? '0');
                if (minute != null && minute >= 0 && minute <= 59) {
                  _minute = minute;
                }
              },
            ),
          ),
        ],
      ),
      const SizedBox(height: AppSpacing.m),
      // Lunar calendar toggle
      CheckboxListTile(
        title: const Text('Ngày âm lịch'),
        value: _isLunar,
        onChanged: (value) => setState(() => _isLunar = value ?? false),
        controlAffinity: ListTileControlAffinity.leading,
      ),
      if (_isLunar)
        CheckboxListTile(
          title: const Text('Tháng nhuận'),
          value: _isLeapMonth,
          onChanged: (value) =>
              setState(() => _isLeapMonth = value ?? false),
          controlAffinity: ListTileControlAffinity.leading,
        ),
      const SizedBox(height: AppSpacing.m),
      // Gender
      SegmentedButton<HoroscopeGender>(
        segments: const [
          ButtonSegment(
            value: HoroscopeGender.male,
            label: Text('Nam'),
          ),
          ButtonSegment(
            value: HoroscopeGender.female,
            label: Text('Nữ'),
          ),
        ],
        selected: {_gender},
        onSelectionChanged: (Set<HoroscopeGender> newSelection) {
          setState(() => _gender = newSelection.first);
        },
      ),
    ];
  }

  List<Widget> _buildYearlyInputs() {
    // Generate years from (currentYear - 5) to min(2026, currentYear + 4)
    final currentYear = DateTime.now().year;
    final maxYear = 2026;
    final minYear = 2024;
    final maxSelectableYear = currentYear + 4 > maxYear ? maxYear : currentYear + 4;
    final yearCount = maxSelectableYear - minYear + 1;
    
    return [
      // Year selector
      DropdownButtonFormField<int>(
        decoration: InputDecoration(
          labelText: 'Năm',
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppRadius.medium),
          ),
        ),
        value: _selectedYear > maxYear ? maxYear : _selectedYear,
        items: List.generate(yearCount, (index) {
          final year = minYear + index;
          return DropdownMenuItem(value: year, child: Text('$year'));
        }),
        onChanged: (value) {
          if (value != null) {
            setState(() => _selectedYear = value);
          }
        },
        validator: (value) {
          if (value == null) {
            return 'Vui lòng chọn năm';
          }
          if (value > 2026) {
            return 'Năm ngoài phạm vi hỗ trợ (tối đa 2026)';
          }
          return null;
        },
      ),
      const SizedBox(height: AppSpacing.m),
      // Zodiac selector - accepts either code (ti, suu, dan...) OR ID (1-12)
      TextFormField(
        key: const ValueKey('yearly_zodiac'),
        controller: _zodiacCodeController,
        decoration: InputDecoration(
          labelText: 'Cung hoàng đạo *',
          hintText: 'Nhập mã (ti, suu, dan...) hoặc ID (1-12)',
          helperText: 'Chỉ cần nhập MÃ hoặc ID, không cần cả hai',
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppRadius.medium),
          ),
        ),
        validator: (value) {
          if (value == null || value.isEmpty) {
            return 'Vui lòng nhập mã cung hoàng đạo (ti, suu, dan...) hoặc ID (1-12)';
          }
          return _validateZodiacCode(value);
        },
        onChanged: (value) {
          final trimmedValue = value.trim();
          
          // Try to parse as ID first (1-12)
          final id = int.tryParse(trimmedValue);
          if (id != null && id >= 1 && id <= 12) {
            // User entered a valid ID
            setState(() {
              _selectedZodiacId = id;
              _selectedZodiacCode = null; // Clear code when ID is set
            });
            _formKey.currentState?.validate();
          } else if (trimmedValue.isNotEmpty) {
            // User entered text - treat as zodiac code
            final normalizedCode = trimmedValue.toLowerCase();
            // Only set code if it's a valid zodiac code
            if (_validZodiacCodes.contains(normalizedCode)) {
              setState(() {
                _selectedZodiacCode = normalizedCode;
                _selectedZodiacId = null; // Clear ID when code is set
              });
              _formKey.currentState?.validate();
            } else {
              // Invalid code - clear both but don't validate yet (let validator show error)
              setState(() {
                _selectedZodiacCode = null;
                _selectedZodiacId = null;
              });
            }
          } else {
            // Empty input - clear both
            setState(() {
              _selectedZodiacCode = null;
              _selectedZodiacId = null;
            });
          }
        },
      ),
    ];
  }

  List<Widget> _buildMonthlyInputs() {
    return [
      ..._buildYearlyInputs(),
      const SizedBox(height: AppSpacing.m),
      // Month selector
      DropdownButtonFormField<int>(
        decoration: InputDecoration(
          labelText: 'Tháng',
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppRadius.medium),
          ),
        ),
        value: _selectedMonth,
        items: List.generate(12, (index) {
          final month = index + 1;
          return DropdownMenuItem(
            value: month,
            child: Text('Tháng $month'),
          );
        }),
        onChanged: (value) => setState(() => _selectedMonth = value!),
      ),
    ];
  }

  List<Widget> _buildDailyInputs() {
    return [
      // Date selector
      TextFormField(
        key: const ValueKey('daily_date'),
        readOnly: true,
        decoration: InputDecoration(
          labelText: 'Ngày *',
          hintText: 'Chọn ngày',
          suffixIcon: const Icon(Icons.calendar_today),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppRadius.medium),
          ),
        ),
        validator: (value) {
          if (_selectedDay == null) {
            return 'Vui lòng chọn ngày';
          }
          return null;
        },
        controller: TextEditingController(
          text: _selectedDay != null
              ? DateTimeUtils.formatDateForDisplay(_selectedDay!)
              : '',
        ),
        onTap: () async {
          // Ensure we use the root navigator context to get MaterialLocalizations
          final navigatorContext = Navigator.of(context, rootNavigator: true).context;
          final date = await showDatePicker(
            context: navigatorContext,
            initialDate: _selectedDay ?? DateTime.now(),
            firstDate: DateTime(2024),
            lastDate: DateTime(2026),
            helpText: 'Chọn ngày',
          );
          if (date != null) {
            setState(() => _selectedDay = date);
            _formKey.currentState?.validate();
          }
        },
      ),
      const SizedBox(height: AppSpacing.m),
      // Zodiac selector - accepts either code (ti, suu, dan...) OR ID (1-12)
      TextFormField(
        key: const ValueKey('daily_zodiac'),
        controller: _dailyZodiacCodeController,
        decoration: InputDecoration(
          labelText: 'Cung hoàng đạo *',
          hintText: 'Nhập mã (ti, suu, dan...) hoặc ID (1-12)',
          helperText: 'Chỉ cần nhập MÃ hoặc ID, không cần cả hai',
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppRadius.medium),
          ),
        ),
        validator: (value) {
          if (value == null || value.isEmpty) {
            return 'Vui lòng nhập mã cung hoàng đạo (ti, suu, dan...) hoặc ID (1-12)';
          }
          return _validateZodiacCode(value);
        },
        onChanged: (value) {
          final trimmedValue = value.trim();
          
          // Try to parse as ID first (1-12)
          final id = int.tryParse(trimmedValue);
          if (id != null && id >= 1 && id <= 12) {
            // User entered a valid ID
            setState(() {
              _selectedZodiacId = id;
              _selectedZodiacCode = null; // Clear code when ID is set
            });
            _formKey.currentState?.validate();
          } else if (trimmedValue.isNotEmpty) {
            // User entered text - treat as zodiac code
            final normalizedCode = trimmedValue.toLowerCase();
            // Only set code if it's a valid zodiac code
            if (_validZodiacCodes.contains(normalizedCode)) {
              setState(() {
                _selectedZodiacCode = normalizedCode;
                _selectedZodiacId = null; // Clear ID when code is set
              });
              _formKey.currentState?.validate();
            } else {
              // Invalid code - clear both but don't validate yet (let validator show error)
              setState(() {
                _selectedZodiacCode = null;
                _selectedZodiacId = null;
              });
            }
          } else {
            // Empty input - clear both
            setState(() {
              _selectedZodiacCode = null;
              _selectedZodiacId = null;
            });
          }
        },
      ),
    ];
  }

  void _handleSubmit() async {
    if (_isSubmitting) return; // Prevent double-submit
    
    // Validate form - this will show inline errors
    if (!_formKey.currentState!.validate()) {
      return; // Stop here, errors are shown inline
    }

    // Additional validation: Ensure at least one zodiac identifier is provided
    // (This should already be validated by the form field, but double-check)
    if (widget.type == HoroscopeType.yearly || 
        widget.type == HoroscopeType.monthly || 
        widget.type == HoroscopeType.daily) {
      if (_selectedZodiacId == null && _selectedZodiacCode == null) {
        // This should not happen if form validation passed, but add safety check
        return;
      }
    }

    // Additional validation for year range
    if (widget.type == HoroscopeType.yearly || widget.type == HoroscopeType.monthly) {
      if (_selectedYear > 2026) {
        // This should be caught by the dropdown validator, but double-check
        return;
      }
    }

    setState(() => _isSubmitting = true);

    try {
    switch (widget.type) {
      case HoroscopeType.lifetime:
          // Date is already validated by form validator
        ref.read(lifetimeByBirthInputProvider.notifier).setInput(
              date: DateTimeUtils.formatDateForApi(_selectedDate!),
              hour: _hour,
              minute: _minute,
              isLunar: _isLunar,
              isLeapMonth: _isLeapMonth,
              gender: _gender.value,
            );
        break;
      case HoroscopeType.yearly:
          // Only pass the zodiac identifier that was actually provided (either ID or code, not both)
        ref.read(yearlyHoroscopeInputProvider.notifier).setInput(
              zodiacId: _selectedZodiacId,
              zodiacCode: _selectedZodiacCode,
              year: _selectedYear,
            );
        break;
      case HoroscopeType.monthly:
          // Only pass the zodiac identifier that was actually provided (either ID or code, not both)
        ref.read(monthlyHoroscopeInputProvider.notifier).setInput(
              zodiacId: _selectedZodiacId,
              zodiacCode: _selectedZodiacCode,
              year: _selectedYear,
              month: _selectedMonth,
            );
        break;
      case HoroscopeType.daily:
          // Only pass the zodiac identifier that was actually provided (either ID or code, not both)
        ref.read(dailyHoroscopeInputProvider.notifier).setInput(
              zodiacId: _selectedZodiacId,
              zodiacCode: _selectedZodiacCode,
              date: _selectedDay!,
            );
        break;
    }

    widget.onSubmitted();
    } finally {
      // Reset submitting state after a short delay to allow UI to update
      Future.delayed(const Duration(milliseconds: 300), () {
        if (mounted) {
          setState(() => _isSubmitting = false);
        }
      });
    }
  }
}

