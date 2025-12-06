import 'day_info.dart';

class MonthDayItem {
  MonthDayItem({
    required this.solarDate,
    required this.dayOfMonth,
    required this.lunar,
    required this.goodDayType,
    required this.special,
  });

  final DateTime solarDate;
  final int dayOfMonth;
  final LunarDate lunar;
  final GoodDayType goodDayType;
  final bool special;

  factory MonthDayItem.fromJson(Map<String, dynamic> json) => MonthDayItem(
        solarDate: DateTime.parse(json['solarDate'] as String),
        dayOfMonth: json['dayOfMonth'] as int,
        lunar: LunarDate.fromJson(json['lunar'] as Map<String, dynamic>),
        goodDayType: GoodDayTypeX.fromString(
            json['goodDayType'] as String? ?? 'NORMAL'),
        special: json['special'] as bool,
      );
}

class MonthCalendar {
  MonthCalendar({
    required this.year,
    required this.month,
    required this.days,
  });

  final int year;
  final int month;
  final List<MonthDayItem> days;

  factory MonthCalendar.fromJson(Map<String, dynamic> json) => MonthCalendar(
        year: json['year'] as int,
        month: json['month'] as int,
        days: (json['days'] as List<dynamic>)
            .map((e) => MonthDayItem.fromJson(e as Map<String, dynamic>))
            .toList(),
      );
}
