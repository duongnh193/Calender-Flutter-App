# generate_day_info_mt.py
# Tool tạo file day_info.sql chứa dữ liệu âm/dương 1900–2100
# - Dùng thuật toán Ho Ngọc Đức (lịch âm VN)
# - Dùng multi-thread (ThreadPoolExecutor) để tính toán nhanh hơn
# - Dùng multi-row INSERT để import MySQL nhanh hơn
#
# RUN:
#   python3 generate_day_info_mt.py
#
# Sau khi chạy xong:
#   mysql -u root -p <your_db_name> < day_info.sql

import math
from datetime import date, timedelta
from concurrent.futures import ThreadPoolExecutor

OUTPUT_FILE = "day_info.sql"

YEAR_START = 1900
YEAR_END = 2100
BATCH_SIZE = 1000      # số record / 1 INSERT
MAX_WORKERS = 8       # số thread tính toán


# ==========================
#  Thuật toán Lịch Âm VN (Ho Ngọc Đức)
# ==========================

def jd_from_date(dd, mm, yy):
    a = int((14 - mm) / 12)
    y = yy + 4800 - a
    m = mm + 12 * a - 3
    jd = dd + int((153 * m + 2) / 5) + 365 * y + int(y / 4) - int(y / 100) + int(y / 400) - 32045
    if jd < 2299161:
        jd = dd + int((153 * m + 2) / 5) + 365 * y + int(y / 4) - 32083
    return jd


def jd_to_date(jd):
    if jd > 2299160:
        a = jd + 32044
        b = int((4 * a + 3) / 146097)
        c = a - int((b * 146097) / 4)
    else:
        b = 0
        c = jd + 32082

    d = int((4 * c + 3) / 1461)
    e = c - int((1461 * d) / 4)
    m = int((5 * e + 2) / 153)

    day = e - int((153 * m + 2) / 5) + 1
    month = m + 3 - 12 * int(m / 10)
    year = b * 100 + d - 4800 + int(m / 10)
    return (day, month, year)


def new_moon(k):
    T = k / 1236.85
    T2 = T * T
    T3 = T2 * T
    dr = math.pi / 180

    Jd1 = 2415020.75933 + 29.53058868 * k \
          + 0.0001178 * T2 \
          - 0.000000155 * T3
    Jd1 += 0.00033 * math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr)

    M = (359.2242 + 29.10535608 * k
         - 0.0000333 * T2
         - 0.00000347 * T3) * dr

    Mpr = (306.0253 + 385.81691806 * k
           + 0.0107306 * T2
           + 0.00001236 * T3) * dr

    F = (21.2964 + 390.67050646 * k
         - 0.0016528 * T2
         - 0.00000239 * T3) * dr

    C1 = (0.1734 - 0.000393 * T) * math.sin(M) \
         + 0.0021 * math.sin(2 * M) \
         - 0.4068 * math.sin(Mpr) \
         + 0.0161 * math.sin(2 * Mpr) \
         - 0.0004 * math.sin(3 * Mpr) \
         + 0.0104 * math.sin(2 * F) \
         - 0.0051 * math.sin(M + Mpr) \
         - 0.0074 * math.sin(M - Mpr) \
         + 0.0004 * math.sin(2 * F + M) \
         - 0.0004 * math.sin(2 * F - M) \
         - 0.0006 * math.sin(2 * F + Mpr) \
         + 0.0010 * math.sin(2 * F - Mpr) \
         + 0.0005 * math.sin(M + 2 * Mpr)

    if T < -11:
        deltat = 0.001 + 0.000839 * T \
                 + 0.0002261 * T2 \
                 - 0.00000845 * T3 \
                 - 0.000000081 * T * T3
    else:
        deltat = -0.000278 + 0.000265 * T + 0.000262 * T2

    JdNew = Jd1 + C1 - deltat
    return JdNew


def sun_longitude(jdn):
    T = (jdn - 2451545.0) / 36525
    T2 = T * T
    dr = math.pi / 180

    M = 357.52910 + 35999.05030 * T \
        - 0.0001559 * T2 \
        - 0.00000048 * T * T2

    L0 = 280.46645 + 36000.76983 * T \
         + 0.0003032 * T2

    DL = (1.914600 - 0.004817 * T - 0.000014 * T2) * math.sin(dr * M)
    DL = DL + (0.019993 - 0.000101 * T) * math.sin(dr * 2 * M)
    DL = DL + 0.000290 * math.sin(dr * 3 * M)

    L = L0 + DL
    L = L % 360
    return L


def lunar_month11(yy):
    off = jd_from_date(31, 12, yy) - 2415021.076998695
    k = int(off / 29.530588853)
    nm = new_moon(k)
    sun_long = sun_longitude(nm)

    if sun_long >= 3.0:
        nm = new_moon(k - 1)

    return int(nm + 0.5)


def leap_month_offset(a11):
    k = int((a11 - 2415021.076998695) / 29.530588853)
    last = 0
    i = 1

    arc = sun_longitude(new_moon(k + i))
    while True:
        last = arc
        i = i + 1
        arc = sun_longitude(new_moon(k + i))
        if not (arc != last and i < 14):
            break
    return i - 1


def solar_to_lunar(dd, mm, yy):
    dayNumber = jd_from_date(dd, mm, yy)
    k = int((dayNumber - 2415021.076998695) / 29.530588853)

    monthStart = int(new_moon(k) + 0.5)
    if monthStart > dayNumber:
        k -= 1
        monthStart = int(new_moon(k) + 0.5)

    a11 = lunar_month11(yy)
    b11 = lunar_month11(yy + 1)
    if a11 >= monthStart:
        lunarYear = yy
        a11 = lunar_month11(yy - 1)
    else:
        lunarYear = yy + 1

    lunarDay = dayNumber - monthStart + 1
    diff = int((monthStart - a11) / 29)
    lunarMonth = diff + 11

    if b11 - a11 > 365:
        leapMonth = leap_month_offset(a11)
        isLeap = (diff == leapMonth)
        if diff >= leapMonth:
            lunarMonth -= 1
    else:
        isLeap = False

    if lunarMonth > 12:
        lunarMonth -= 12
    if lunarMonth >= 11 and diff < 4:
        lunarYear -= 1

    return lunarDay, lunarMonth, lunarYear, isLeap


CAN = ["Giáp","Ất","Bính","Đinh","Mậu","Kỷ","Canh","Tân","Nhâm","Quý"]
CHI = ["Tý","Sửu","Dần","Mão","Thìn","Tỵ","Ngọ","Mùi","Thân","Dậu","Tuất","Hợi"]


def get_can_chi(day, month, year):
    jd = jd_from_date(day, month, year)
    # Can chi ngày
    can_day = CAN[(jd + 9) % 10]
    chi_day = CHI[(jd + 1) % 12]
    # Can chi tháng
    can_month = CAN[(year * 12 + month + 3) % 10]
    chi_month = CHI[(month + 1) % 12]
    # Can chi năm
    can_year = CAN[(year + 6) % 10]
    chi_year = CHI[(year + 8) % 12]

    return f"{can_day} {chi_day}", f"{can_month} {chi_month}", f"{can_year} {chi_year}"


# ==========================
#  Helpers
# ==========================

def daterange(start_date, end_date):
    cur = start_date
    while cur <= end_date:
        yield cur
        cur += timedelta(days=1)


def compute_row(cur_date: date):
    dd = cur_date.day
    mm = cur_date.month
    yy = cur_date.year

    lunar_day, lunar_month, lunar_year, leap = solar_to_lunar(dd, mm, yy)
    can_day, can_month, can_year = get_can_chi(dd, mm, yy)
    weekday = cur_date.isoweekday()  # 1-7, Monday=1

    # Trả về tuple đủ info, sẽ format SQL sau
    return (
        cur_date.isoformat(),   # solar_date string
        weekday,
        lunar_day,
        lunar_month,
        lunar_year,
        1 if leap else 0,
        can_day,
        can_month,
        can_year
    )


# ==========================
#  MAIN GENERATOR
# ==========================

def main():
    print(f"Generating day_info.sql for {YEAR_START}–{YEAR_END} ...")

    start = date(YEAR_START, 1, 1)
    end = date(YEAR_END, 12, 31)

    all_dates = list(daterange(start, end))
    total = len(all_dates)
    print("Total days:", total)

    print(f"Computing lunar & can-chi with {MAX_WORKERS} threads ...")
    with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        rows = list(executor.map(compute_row, all_dates, chunksize=500))

    print("Writing SQL file:", OUTPUT_FILE)

    with open(OUTPUT_FILE, "w", encoding="utf8") as f:
        f.write("TRUNCATE TABLE day_info;\n\n")

        for i in range(0, total, BATCH_SIZE):
            batch = rows[i:i + BATCH_SIZE]
            if not batch:
                continue

            f.write(
                "INSERT INTO day_info "
                "(solar_date, weekday, lunar_day, lunar_month, lunar_year, "
                "lunar_leap_month, can_chi_day, can_chi_month, can_chi_year, "
                "is_good_day, note)\nVALUES\n"
            )

            values_lines = []
            for row in batch:
                (solar_date, weekday, lunar_day, lunar_month,
                 lunar_year, leap_flag, can_day, can_month, can_year) = row

                # note = NULL, is_good_day = 0 (m có thể update logic sau)
                line = (
                    f"('{solar_date}', {weekday}, {lunar_day}, {lunar_month}, {lunar_year}, "
                    f"{leap_flag}, '{can_day}', '{can_month}', '{can_year}', 0, NULL)"
                )
                values_lines.append(line)

            f.write(",\n".join(values_lines))
            f.write(";\n\n")

    print("DONE. File saved:", OUTPUT_FILE)


if __name__ == "__main__":
    main()
