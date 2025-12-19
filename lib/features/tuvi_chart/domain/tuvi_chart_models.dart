/// Tu Vi Chart domain models matching backend DTOs.

class TuViChartRequest {
  final String date;
  final int hour;
  final int minute;
  final String gender;
  final bool isLunar;
  final bool isLeapMonth;
  final String? name;
  final String? birthPlace;

  TuViChartRequest({
    required this.date,
    required this.hour,
    this.minute = 0,
    required this.gender,
    this.isLunar = false,
    this.isLeapMonth = false,
    this.name,
    this.birthPlace,
  });

  Map<String, dynamic> toJson() => {
        'date': date,
        'hour': hour,
        'minute': minute,
        'gender': gender,
        'isLunar': isLunar,
        'isLeapMonth': isLeapMonth,
        if (name != null) 'name': name,
        if (birthPlace != null) 'birthPlace': birthPlace,
      };
}

class TuViChartResponse {
  final CenterInfo center;
  final List<PalaceInfo> palaces;
  final MarkerInfo markers;
  final CycleInfo cycles;
  final String calculatedAt;
  final Map<String, dynamic>? debug;

  TuViChartResponse({
    required this.center,
    required this.palaces,
    required this.markers,
    required this.cycles,
    required this.calculatedAt,
    this.debug,
  });

  factory TuViChartResponse.fromJson(Map<String, dynamic> json) {
    return TuViChartResponse(
      center: CenterInfo.fromJson(json['center'] as Map<String, dynamic>),
      palaces: (json['palaces'] as List<dynamic>)
          .map((e) => PalaceInfo.fromJson(e as Map<String, dynamic>))
          .toList(),
      markers: MarkerInfo.fromJson(json['markers'] as Map<String, dynamic>),
      cycles: CycleInfo.fromJson(json['cycles'] as Map<String, dynamic>),
      calculatedAt: json['calculatedAt'] as String? ?? '',
      debug: json['debug'] as Map<String, dynamic>?,
    );
  }
}

class CenterInfo {
  final String? name;
  final String? birthPlace;
  final String solarDate;
  final String lunarYearCanChi;
  final int lunarYear;
  final int lunarMonth;
  final String lunarMonthCanChi;
  final bool isLeapMonth;
  final int lunarDay;
  final String lunarDayCanChi;
  final int birthHour;
  final int birthMinute;
  final String birthHourCanChi;
  final int hourBranchIndex;
  final String gender;
  final String amDuong;
  final String thuanNghich;
  final String banMenh;
  final String banMenhNguHanh;
  final String? banMenhDescription;
  final String cuc;
  final int cucValue;
  final String cucNguHanh;
  final String? menhCucRelation;
  final String? chuMenh;
  final String? chuThan;
  final String? canLuong;
  final String? laiNhan;
  final String? thanCu;
  final String? note;

  CenterInfo({
    this.name,
    this.birthPlace,
    required this.solarDate,
    required this.lunarYearCanChi,
    required this.lunarYear,
    required this.lunarMonth,
    required this.lunarMonthCanChi,
    required this.isLeapMonth,
    required this.lunarDay,
    required this.lunarDayCanChi,
    required this.birthHour,
    required this.birthMinute,
    required this.birthHourCanChi,
    required this.hourBranchIndex,
    required this.gender,
    required this.amDuong,
    required this.thuanNghich,
    required this.banMenh,
    required this.banMenhNguHanh,
    this.banMenhDescription,
    required this.cuc,
    required this.cucValue,
    required this.cucNguHanh,
    this.menhCucRelation,
    this.chuMenh,
    this.chuThan,
    this.canLuong,
    this.laiNhan,
    this.thanCu,
    this.note,
  });

  factory CenterInfo.fromJson(Map<String, dynamic> json) {
    return CenterInfo(
      name: json['name'] as String?,
      birthPlace: json['birthPlace'] as String?,
      solarDate: json['solarDate'] as String? ?? '',
      lunarYearCanChi: json['lunarYearCanChi'] as String? ?? '',
      lunarYear: json['lunarYear'] as int? ?? 0,
      lunarMonth: json['lunarMonth'] as int? ?? 0,
      lunarMonthCanChi: json['lunarMonthCanChi'] as String? ?? '',
      isLeapMonth: json['isLeapMonth'] as bool? ?? false,
      lunarDay: json['lunarDay'] as int? ?? 0,
      lunarDayCanChi: json['lunarDayCanChi'] as String? ?? '',
      birthHour: json['birthHour'] as int? ?? 0,
      birthMinute: json['birthMinute'] as int? ?? 0,
      birthHourCanChi: json['birthHourCanChi'] as String? ?? '',
      hourBranchIndex: json['hourBranchIndex'] as int? ?? 0,
      gender: json['gender'] as String? ?? '',
      amDuong: json['amDuong'] as String? ?? '',
      thuanNghich: json['thuanNghich'] as String? ?? '',
      banMenh: json['banMenh'] as String? ?? '',
      banMenhNguHanh: json['banMenhNguHanh'] as String? ?? '',
      banMenhDescription: json['banMenhDescription'] as String?,
      cuc: json['cuc'] as String? ?? '',
      cucValue: json['cucValue'] as int? ?? 0,
      cucNguHanh: json['cucNguHanh'] as String? ?? '',
      menhCucRelation: json['menhCucRelation'] as String?,
      chuMenh: json['chuMenh'] as String?,
      chuThan: json['chuThan'] as String?,
      canLuong: json['canLuong'] as String?,
      laiNhan: json['laiNhan'] as String?,
      thanCu: json['thanCu'] as String?,
      note: json['note'] as String?,
    );
  }
}

class PalaceInfo {
  final int index;
  final String nameCode;
  final String name;
  final String diaChiCode;
  final String diaChi;
  final String? canChiPrefix;
  final List<StarInfo> stars;
  final int? daiVanStartAge;
  final String? daiVanLabel;
  final bool hasTuan;
  final bool hasTriet;
  final String? truongSinhStage;
  final bool isThanCu;

  PalaceInfo({
    required this.index,
    required this.nameCode,
    required this.name,
    required this.diaChiCode,
    required this.diaChi,
    this.canChiPrefix,
    required this.stars,
    this.daiVanStartAge,
    this.daiVanLabel,
    this.hasTuan = false,
    this.hasTriet = false,
    this.truongSinhStage,
    this.isThanCu = false,
  });

  factory PalaceInfo.fromJson(Map<String, dynamic> json) {
    return PalaceInfo(
      index: json['index'] as int? ?? 0,
      nameCode: json['nameCode'] as String? ?? '',
      name: json['name'] as String? ?? '',
      diaChiCode: json['diaChiCode'] as String? ?? '',
      diaChi: json['diaChi'] as String? ?? '',
      canChiPrefix: json['canChiPrefix'] as String?,
      stars: (json['stars'] as List<dynamic>?)
              ?.map((e) => StarInfo.fromJson(e as Map<String, dynamic>))
              .toList() ??
          [],
      daiVanStartAge: json['daiVanStartAge'] as int?,
      daiVanLabel: json['daiVanLabel'] as String?,
      hasTuan: json['hasTuan'] as bool? ?? false,
      hasTriet: json['hasTriet'] as bool? ?? false,
      truongSinhStage: json['truongSinhStage'] as String?,
      isThanCu: json['isThanCu'] as bool? ?? false,
    );
  }
}

class StarInfo {
  final String code;
  final String name;
  final String type;
  final String nguHanh;
  final String? brightness;
  final String? brightnessCode;
  final bool isMainStar;
  final bool? isPositive;

  StarInfo({
    required this.code,
    required this.name,
    required this.type,
    required this.nguHanh,
    this.brightness,
    this.brightnessCode,
    required this.isMainStar,
    this.isPositive,
  });

  factory StarInfo.fromJson(Map<String, dynamic> json) {
    return StarInfo(
      code: json['code'] as String? ?? '',
      name: json['name'] as String? ?? '',
      type: json['type'] as String? ?? '',
      nguHanh: json['nguHanh'] as String? ?? '',
      brightness: json['brightness'] as String?,
      brightnessCode: json['brightnessCode'] as String?,
      isMainStar: json['isMainStar'] as bool? ?? false,
      isPositive: json['isPositive'] as bool?,
    );
  }
}

class MarkerInfo {
  final String tuanStart;
  final String tuanEnd;
  final String tuanText;
  final String trietStart;
  final String trietEnd;
  final String trietText;

  MarkerInfo({
    required this.tuanStart,
    required this.tuanEnd,
    required this.tuanText,
    required this.trietStart,
    required this.trietEnd,
    required this.trietText,
  });

  factory MarkerInfo.fromJson(Map<String, dynamic> json) {
    return MarkerInfo(
      tuanStart: json['tuanStart'] as String? ?? '',
      tuanEnd: json['tuanEnd'] as String? ?? '',
      tuanText: json['tuanText'] as String? ?? '',
      trietStart: json['trietStart'] as String? ?? '',
      trietEnd: json['trietEnd'] as String? ?? '',
      trietText: json['trietText'] as String? ?? '',
    );
  }
}

class CycleInfo {
  final String direction;
  final String directionText;
  final int daiVanStartAge;
  final int cyclePeriod;
  final List<DaiVanEntry> daiVanList;

  CycleInfo({
    required this.direction,
    required this.directionText,
    required this.daiVanStartAge,
    required this.cyclePeriod,
    required this.daiVanList,
  });

  factory CycleInfo.fromJson(Map<String, dynamic> json) {
    return CycleInfo(
      direction: json['direction'] as String? ?? '',
      directionText: json['directionText'] as String? ?? '',
      daiVanStartAge: json['daiVanStartAge'] as int? ?? 0,
      cyclePeriod: json['cyclePeriod'] as int? ?? 10,
      daiVanList: (json['daiVanList'] as List<dynamic>?)
              ?.map((e) => DaiVanEntry.fromJson(e as Map<String, dynamic>))
              .toList() ??
          [],
    );
  }
}

class DaiVanEntry {
  final int palaceIndex;
  final String palaceName;
  final int startAge;
  final int endAge;
  final String label;

  DaiVanEntry({
    required this.palaceIndex,
    required this.palaceName,
    required this.startAge,
    required this.endAge,
    required this.label,
  });

  factory DaiVanEntry.fromJson(Map<String, dynamic> json) {
    return DaiVanEntry(
      palaceIndex: json['palaceIndex'] as int? ?? 0,
      palaceName: json['palaceName'] as String? ?? '',
      startAge: json['startAge'] as int? ?? 0,
      endAge: json['endAge'] as int? ?? 0,
      label: json['label'] as String? ?? '',
    );
  }
}
