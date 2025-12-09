import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/di/providers.dart';
import '../../features/calendar/application/calendar_providers.dart';

/// Widget that handles app lifecycle events and refetches data on resume
class AppLifecycleHandler extends ConsumerStatefulWidget {
  const AppLifecycleHandler({super.key, required this.child});

  final Widget child;

  @override
  ConsumerState<AppLifecycleHandler> createState() =>
      _AppLifecycleHandlerState();
}

class _AppLifecycleHandlerState extends ConsumerState<AppLifecycleHandler>
    with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);
    
    if (state == AppLifecycleState.resumed) {
      // Refetch data when app resumes from background
      _refetchActiveProviders();
    }
  }

  void _refetchActiveProviders() {
    // Invalidate day info provider to refetch current date data
    final selectedDate = ref.read(selectedDateProvider);
    ref.invalidate(dayInfoProvider(selectedDate));
    
    // Invalidate month calendar provider
    final focusedMonth = ref.read(focusedMonthProvider);
    ref.invalidate(monthCalendarProvider(focusedMonth));
    
    // Note: Horoscope providers are auto-dispose and will refetch automatically
    // when their input providers change, so we don't need to invalidate them here
  }

  @override
  Widget build(BuildContext context) {
    return widget.child;
  }
}
