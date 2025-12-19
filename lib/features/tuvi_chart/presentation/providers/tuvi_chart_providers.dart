// ignore_for_file: depend_on_referenced_packages
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import '../../../../core/di/providers.dart';
import '../../data/tuvi_chart_repository.dart';
import '../../domain/tuvi_chart_models.dart';

/// Provider for the Tu Vi Chart repository
final tuViChartRepositoryProvider = Provider<TuViChartRepository>((ref) {
  final client = ref.watch(apiClientProvider);
  return TuViChartRepository(client);
});

/// State provider for the input form data
class TuViInputState {
  final String? date;
  final int? hour;
  final int? minute;
  final String? gender;
  final bool isLunar;
  final bool isLeapMonth;
  final String? name;
  final String? birthPlace;

  const TuViInputState({
    this.date,
    this.hour,
    this.minute,
    this.gender,
    this.isLunar = false,
    this.isLeapMonth = false,
    this.name,
    this.birthPlace,
  });

  TuViInputState copyWith({
    String? date,
    int? hour,
    int? minute,
    String? gender,
    bool? isLunar,
    bool? isLeapMonth,
    String? name,
    String? birthPlace,
  }) {
    return TuViInputState(
      date: date ?? this.date,
      hour: hour ?? this.hour,
      minute: minute ?? this.minute,
      gender: gender ?? this.gender,
      isLunar: isLunar ?? this.isLunar,
      isLeapMonth: isLeapMonth ?? this.isLeapMonth,
      name: name ?? this.name,
      birthPlace: birthPlace ?? this.birthPlace,
    );
  }

  bool get isValid =>
      date != null && date!.isNotEmpty && hour != null && gender != null;

  TuViChartRequest toRequest() {
    return TuViChartRequest(
      date: date!,
      hour: hour!,
      minute: minute ?? 0,
      gender: gender!,
      isLunar: isLunar,
      isLeapMonth: isLeapMonth,
      name: name,
      birthPlace: birthPlace,
    );
  }
}

final tuViInputProvider = StateProvider<TuViInputState>((ref) {
  return const TuViInputState();
});

/// Provider for the chart result
final tuViChartResultProvider =
    FutureProvider.autoDispose<TuViChartResponse?>((ref) async {
  final input = ref.watch(tuViInputProvider);
  if (!input.isValid) {
    return null;
  }

  final repository = ref.read(tuViChartRepositoryProvider);
  return repository.generateChart(input.toRequest());
});

/// Provider to trigger chart generation manually
final tuViChartProvider = FutureProvider.autoDispose
    .family<TuViChartResponse, TuViChartRequest>((ref, request) async {
  final repository = ref.read(tuViChartRepositoryProvider);
  return repository.generateChart(request);
});

/// Debug mode toggle
final tuViDebugModeProvider = StateProvider<bool>((ref) => false);
