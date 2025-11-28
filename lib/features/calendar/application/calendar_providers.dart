import 'package:flutter_riverpod/flutter_riverpod.dart' as rp;
import 'package:flutter_riverpod/legacy.dart' as legacy;

final selectedDateProvider = legacy.StateProvider<DateTime>((ref) {
  final now = DateTime.now();
  return DateTime(now.year, now.month, now.day);
});

final focusedMonthProvider = legacy.StateProvider<DateTime>((ref) {
  final now = DateTime.now();
  return DateTime(now.year, now.month);
});

final specialDatesProvider = rp.Provider<Set<int>>((ref) {
  // Mocked list of special days in a month for highlighting.
  return {1, 8, 12, 15, 22, 28};
});
