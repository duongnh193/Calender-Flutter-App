import '../constants/size_breakpoints.dart';
import '../theme/app_spacing.dart';

double horizontalPaddingFor(ScreenSizeClass sizeClass) {
  return sizeClass == ScreenSizeClass.small ? AppSpacing.m : AppSpacing.l;
}
