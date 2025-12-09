// ignore_for_file: depend_on_referenced_packages
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import '../../../core/utils/date_time_utils.dart';

/// Provider for the currently selected date
final selectedDateProvider = StateProvider<DateTime>((ref) {
  return DateTimeUtils.todayInVietnam();
});

/// Provider for the currently focused month in month view
final focusedMonthProvider = StateProvider<DateTime>((ref) {
  final today = DateTimeUtils.todayInVietnam();
  return DateTime(today.year, today.month);
});

/// Provider for special dates in a month (for highlighting)
/// This is a placeholder - should be replaced with actual data from API
final specialDatesProvider = Provider<Set<int>>((ref) {
  // TODO: Replace with actual special dates from backend
  return {1, 8, 15, 22};
});

/// Notifier for selected date with utility methods
class SelectedDateNotifier extends Notifier<DateTime> {
  @override
  DateTime build() => DateTimeUtils.todayInVietnam();

  void selectDate(DateTime date) {
    state = DateTime(date.year, date.month, date.day);
  }

  void goToToday() {
    state = DateTimeUtils.todayInVietnam();
  }

  void goToPreviousDay() {
    state = state.subtract(const Duration(days: 1));
  }

  void goToNextDay() {
    state = state.add(const Duration(days: 1));
  }
}

/// Enhanced selected date provider with notifier
final selectedDateNotifierProvider =
    NotifierProvider<SelectedDateNotifier, DateTime>(SelectedDateNotifier.new);

/// Notifier for focused month with utility methods
class FocusedMonthNotifier extends Notifier<DateTime> {
  @override
  DateTime build() {
    final today = DateTimeUtils.todayInVietnam();
    return DateTime(today.year, today.month);
  }

  void setMonth(DateTime date) {
    state = DateTime(date.year, date.month);
  }

  void goToPreviousMonth() {
    state = DateTime(state.year, state.month - 1);
  }

  void goToNextMonth() {
    state = DateTime(state.year, state.month + 1);
  }

  void goToCurrentMonth() {
    final today = DateTimeUtils.todayInVietnam();
    state = DateTime(today.year, today.month);
  }
}

/// Enhanced focused month provider with notifier
final focusedMonthNotifierProvider =
    NotifierProvider<FocusedMonthNotifier, DateTime>(FocusedMonthNotifier.new);
