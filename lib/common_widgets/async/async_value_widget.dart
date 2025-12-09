import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/network/api_exception.dart';
import '../../core/theme/app_colors.dart';
import '../../core/theme/app_spacing.dart';

/// Generic widget to handle AsyncValue states (loading, error, data)
class AsyncValueWidget<T> extends StatelessWidget {
  const AsyncValueWidget({
    super.key,
    required this.value,
    required this.data,
    this.loading,
    this.error,
    this.skipLoadingOnReload = false,
    this.skipLoadingOnRefresh = true,
  });

  final AsyncValue<T> value;
  final Widget Function(T data) data;
  final Widget Function()? loading;
  final Widget Function(Object error, StackTrace? stackTrace)? error;
  final bool skipLoadingOnReload;
  final bool skipLoadingOnRefresh;

  @override
  Widget build(BuildContext context) {
    return value.when(
      skipLoadingOnReload: skipLoadingOnReload,
      skipLoadingOnRefresh: skipLoadingOnRefresh,
      data: data,
      loading: loading ?? () => const DefaultLoadingWidget(),
      error: error ?? (e, st) => DefaultErrorWidget(error: e, stackTrace: st),
    );
  }
}

/// Default loading widget with spinner
class DefaultLoadingWidget extends StatelessWidget {
  const DefaultLoadingWidget({super.key, this.message});

  final String? message;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const CircularProgressIndicator(
            valueColor: AlwaysStoppedAnimation<Color>(AppColors.primaryRed),
          ),
          if (message != null) ...[
            const SizedBox(height: AppSpacing.m),
            Text(
              message!,
              style: const TextStyle(
                color: AppColors.textSecondary,
                fontSize: 14,
              ),
            ),
          ],
        ],
      ),
    );
  }
}

/// Default error widget with retry option
class DefaultErrorWidget extends StatelessWidget {
  const DefaultErrorWidget({
    super.key,
    required this.error,
    this.stackTrace,
    this.onRetry,
    this.message,
  });

  final Object error;
  final StackTrace? stackTrace;
  final VoidCallback? onRetry;
  final String? message;

  @override
  Widget build(BuildContext context) {
    String errorMessage = message ?? 'Đã xảy ra lỗi';

    // Extract user-friendly message from ApiException
    if (error is ApiException) {
      errorMessage = (error as ApiException).message;
    }

    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AppSpacing.l),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(
              Icons.error_outline,
              size: 48,
              color: AppColors.primaryRed,
            ),
            const SizedBox(height: AppSpacing.m),
            Text(
              errorMessage,
              textAlign: TextAlign.center,
              style: const TextStyle(
                color: AppColors.textPrimary,
                fontSize: 16,
              ),
            ),
            if (onRetry != null) ...[
              const SizedBox(height: AppSpacing.l),
              ElevatedButton.icon(
                onPressed: onRetry,
                icon: const Icon(Icons.refresh),
                label: const Text('Thử lại'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primaryRed,
                  foregroundColor: Colors.white,
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

/// Skeleton loading placeholder
class SkeletonWidget extends StatelessWidget {
  const SkeletonWidget({
    super.key,
    this.width,
    this.height,
    this.borderRadius,
  });

  final double? width;
  final double? height;
  final double? borderRadius;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width,
      height: height ?? 16,
      decoration: BoxDecoration(
        color: AppColors.dividerColor,
        borderRadius: BorderRadius.circular(borderRadius ?? 4),
      ),
    );
  }
}

/// Empty state widget
class EmptyStateWidget extends StatelessWidget {
  const EmptyStateWidget({
    super.key,
    required this.message,
    this.icon,
    this.action,
    this.actionLabel,
  });

  final String message;
  final IconData? icon;
  final VoidCallback? action;
  final String? actionLabel;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AppSpacing.l),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              icon ?? Icons.inbox_outlined,
              size: 64,
              color: AppColors.textSecondary.withValues(alpha: 0.5),
            ),
            const SizedBox(height: AppSpacing.m),
            Text(
              message,
              textAlign: TextAlign.center,
              style: const TextStyle(
                color: AppColors.textSecondary,
                fontSize: 16,
              ),
            ),
            if (action != null && actionLabel != null) ...[
              const SizedBox(height: AppSpacing.l),
              TextButton(
                onPressed: action,
                child: Text(actionLabel!),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

