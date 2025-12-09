import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/di/providers.dart';
import '../../../../core/utils/date_time_utils.dart';
import '../../domain/horoscope_models.dart';

// ==================== Input State Providers ====================

class LifetimeByBirthInput {
  final String? date;
  final int? hour;
  final int? minute;
  final bool? isLunar;
  final bool? isLeapMonth;
  final String? gender;

  LifetimeByBirthInput({
    this.date,
    this.hour,
    this.minute,
    this.isLunar,
    this.isLeapMonth,
    this.gender,
  });
}

class LifetimeByBirthInputNotifier extends Notifier<LifetimeByBirthInput> {
  @override
  LifetimeByBirthInput build() => LifetimeByBirthInput();

  void setInput({
    required String date,
    required int hour,
    int minute = 0,
    bool isLunar = false,
    bool isLeapMonth = false,
    required String gender,
  }) {
    state = LifetimeByBirthInput(
      date: date,
      hour: hour,
      minute: minute,
      isLunar: isLunar,
      isLeapMonth: isLeapMonth,
      gender: gender,
    );
  }

  void clear() {
    state = LifetimeByBirthInput();
  }
}

final lifetimeByBirthInputProvider =
    NotifierProvider<LifetimeByBirthInputNotifier, LifetimeByBirthInput>(
        LifetimeByBirthInputNotifier.new);

// Yearly input
class YearlyHoroscopeInput {
  final int? zodiacId;
  final String? zodiacCode;
  final int? year;

  YearlyHoroscopeInput({
    this.zodiacId,
    this.zodiacCode,
    this.year,
  });
}

class YearlyHoroscopeInputNotifier extends Notifier<YearlyHoroscopeInput> {
  @override
  YearlyHoroscopeInput build() => YearlyHoroscopeInput();

  void setInput({
    int? zodiacId,
    String? zodiacCode,
    required int year,
  }) {
    state = YearlyHoroscopeInput(
      zodiacId: zodiacId,
      zodiacCode: zodiacCode,
      year: year,
    );
  }

  void clear() {
    state = YearlyHoroscopeInput();
  }
}

final yearlyHoroscopeInputProvider =
    NotifierProvider<YearlyHoroscopeInputNotifier, YearlyHoroscopeInput>(
        YearlyHoroscopeInputNotifier.new);

// Monthly input
class MonthlyHoroscopeInput {
  final int? zodiacId;
  final String? zodiacCode;
  final int? year;
  final int? month;

  MonthlyHoroscopeInput({
    this.zodiacId,
    this.zodiacCode,
    this.year,
    this.month,
  });
}

class MonthlyHoroscopeInputNotifier extends Notifier<MonthlyHoroscopeInput> {
  @override
  MonthlyHoroscopeInput build() => MonthlyHoroscopeInput();

  void setInput({
    int? zodiacId,
    String? zodiacCode,
    required int year,
    required int month,
  }) {
    state = MonthlyHoroscopeInput(
      zodiacId: zodiacId,
      zodiacCode: zodiacCode,
      year: year,
      month: month,
    );
  }

  void clear() {
    state = MonthlyHoroscopeInput();
  }
}

final monthlyHoroscopeInputProvider =
    NotifierProvider<MonthlyHoroscopeInputNotifier, MonthlyHoroscopeInput>(
        MonthlyHoroscopeInputNotifier.new);

// Daily input
class DailyHoroscopeInput {
  final int? zodiacId;
  final String? zodiacCode;
  final DateTime? date;

  DailyHoroscopeInput({
    this.zodiacId,
    this.zodiacCode,
    this.date,
  });
}

class DailyHoroscopeInputNotifier extends Notifier<DailyHoroscopeInput> {
  @override
  DailyHoroscopeInput build() => DailyHoroscopeInput();

  void setInput({
    int? zodiacId,
    String? zodiacCode,
    required DateTime date,
  }) {
    state = DailyHoroscopeInput(
      zodiacId: zodiacId,
      zodiacCode: zodiacCode,
      date: date,
    );
  }

  void clear() {
    state = DailyHoroscopeInput();
  }
}

final dailyHoroscopeInputProvider =
    NotifierProvider<DailyHoroscopeInputNotifier, DailyHoroscopeInput>(
        DailyHoroscopeInputNotifier.new);

// ==================== Result Providers ====================

final lifetimeByBirthResultProvider =
    FutureProvider.autoDispose<LifetimeByBirthResponse?>((ref) async {
  final input = ref.watch(lifetimeByBirthInputProvider);
  if (input.date == null || input.hour == null || input.gender == null) {
    return null;
  }

  final params = LifetimeByBirthParams(
    date: input.date!,
    hour: input.hour!,
    minute: input.minute ?? 0,
    isLunar: input.isLunar ?? false,
    isLeapMonth: input.isLeapMonth ?? false,
    gender: input.gender!,
  );

  return ref.read(lifetimeByBirthProvider(params).future);
});

final yearlyHoroscopeResultProvider =
    FutureProvider.autoDispose<HoroscopeYearly?>((ref) async {
  final input = ref.watch(yearlyHoroscopeInputProvider);
  if (input.year == null ||
      (input.zodiacId == null && input.zodiacCode == null)) {
    return null;
  }

  final params = YearlyHoroscopeParams(
    zodiacId: input.zodiacId,
    zodiacCode: input.zodiacCode,
    year: input.year!,
  );

  return ref.read(yearlyHoroscopeProvider(params).future);
});

final monthlyHoroscopeResultProvider =
    FutureProvider.autoDispose<HoroscopeMonthly?>((ref) async {
  final input = ref.watch(monthlyHoroscopeInputProvider);
  if (input.year == null ||
      input.month == null ||
      (input.zodiacId == null && input.zodiacCode == null)) {
    return null;
  }

  final params = MonthlyHoroscopeParams(
    zodiacId: input.zodiacId,
    zodiacCode: input.zodiacCode,
    year: input.year!,
    month: input.month!,
  );

  return ref.read(monthlyHoroscopeProvider(params).future);
});

final dailyHoroscopeResultProvider =
    FutureProvider.autoDispose<HoroscopeDaily?>((ref) async {
  final input = ref.watch(dailyHoroscopeInputProvider);
  if (input.date == null ||
      (input.zodiacId == null && input.zodiacCode == null)) {
    return null;
  }

  final params = DailyHoroscopeParams(
    zodiacId: input.zodiacId,
    zodiacCode: input.zodiacCode,
    date: input.date!,
  );

  return ref.read(dailyHoroscopeProvider(params).future);
});

