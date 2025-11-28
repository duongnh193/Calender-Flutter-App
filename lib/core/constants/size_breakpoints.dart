enum ScreenSizeClass { small, medium, large }

ScreenSizeClass getSizeClass(double width) {
  if (width < 360) return ScreenSizeClass.small;
  if (width < 480) return ScreenSizeClass.medium;
  return ScreenSizeClass.large;
}
