class LunarDate {
  LunarDate({
    required this.day,
    required this.month,
    required this.year,
    required this.leapMonth,
  });

  final int day;
  final int month;
  final int year;
  final bool leapMonth;

  factory LunarDate.fromJson(Map<String, dynamic> json) => LunarDate(
        day: json['day'] as int,
        month: json['month'] as int,
        year: json['year'] as int,
        leapMonth: json['leapMonth'] as bool,
      );
}

class CanChi {
  CanChi({
    required this.day,
    required this.month,
    required this.year,
  });

  final String day;
  final String month;
  final String year;

  factory CanChi.fromJson(Map<String, dynamic> json) => CanChi(
        day: json['day'] as String,
        month: json['month'] as String,
        year: json['year'] as String,
      );
}

class GoldenHour {
  GoldenHour({
    required this.branch,
    required this.startHour,
    required this.endHour,
    required this.label,
  });

  final String branch;
  final int startHour;
  final int endHour;
  final String label;

  factory GoldenHour.fromJson(Map<String, dynamic> json) => GoldenHour(
        branch: json['branch'] as String,
        startHour: json['startHour'] as int,
        endHour: json['endHour'] as int,
        label: json['label'] as String,
      );
}

class DayInfo {
  DayInfo({
    required this.solarDate,
    required this.weekday,
    required this.lunar,
    required this.canChi,
    required this.goodDayType,
    this.currentTime,
    this.note,
    this.goldenHours = const [],
  });

  final DateTime solarDate;
  final String weekday;
  final LunarDate lunar;
  final CanChi canChi;
  final GoodDayType goodDayType;
  final CurrentTime? currentTime;
  final String? note;
  final List<GoldenHour> goldenHours;

  factory DayInfo.fromJson(Map<String, dynamic> json) => DayInfo(
        solarDate: DateTime.parse(json['solarDate'] as String),
        weekday: json['weekday'] as String,
        lunar: LunarDate.fromJson(json['lunar'] as Map<String, dynamic>),
        canChi: CanChi.fromJson(json['canChi'] as Map<String, dynamic>),
        goodDayType:
            GoodDayTypeX.fromString(json['goodDayType'] as String? ?? 'NORMAL'),
        currentTime: json['currentTime'] != null
            ? CurrentTime.fromJson(json['currentTime'] as Map<String, dynamic>)
            : null,
        note: json['note'] as String?,
        goldenHours: (json['goldenHours'] as List<dynamic>?)
                ?.map((e) =>
                    GoldenHour.fromJson(e as Map<String, dynamic>))
                .toList() ??
            const [],
      );
}

class CurrentTime {
  CurrentTime({
    required this.time,
    required this.timeLabel,
    required this.canChiHour,
  });

  final String time;
  final String timeLabel;
  final String canChiHour;

  factory CurrentTime.fromJson(Map<String, dynamic> json) => CurrentTime(
        time: json['time'] as String,
        timeLabel: json['timeLabel'] as String,
        canChiHour: json['canChiHour'] as String,
      );
}

enum GoodDayType { normal, hoangDao, hacDao }

extension GoodDayTypeX on GoodDayType {
  static GoodDayType fromString(String raw) {
    switch (raw.toUpperCase()) {
      case 'HOANG_DAO':
        return GoodDayType.hoangDao;
      case 'HAC_DAO':
        return GoodDayType.hacDao;
      default:
        return GoodDayType.normal;
    }
  }

  String get label {
    switch (this) {
      case GoodDayType.hoangDao:
        return 'HOÀNG ĐẠO';
      case GoodDayType.hacDao:
        return 'HẮC ĐẠO';
      default:
        return '';
    }
  }
}
