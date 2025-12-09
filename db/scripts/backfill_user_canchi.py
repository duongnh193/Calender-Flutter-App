#!/usr/bin/env python3
"""
backfill_user_canchi.py

Script to compute and backfill can_chi and zodiac_id for existing users
based on their birth_date and birth_time.

Usage:
  python3 backfill_user_canchi.py --db-host localhost --db-user root --db-name lich_van_nien
  python3 backfill_user_canchi.py --dry-run  # Preview without writing
  python3 backfill_user_canchi.py --batch-size 100

Requirements:
  pip install mysql-connector-python
"""

import argparse
import sys
from datetime import datetime, date
from typing import Optional, Tuple
import logging

try:
    import mysql.connector
    from mysql.connector import Error
except ImportError:
    print("Error: mysql-connector-python is required. Install with: pip install mysql-connector-python")
    sys.exit(1)

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S'
)
logger = logging.getLogger(__name__)

# Thiên Can (10)
THIEN_CAN = ["Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý"]

# Địa Chi (12) with zodiac code mapping
DIA_CHI = [
    ("Tý", "ti", 1),
    ("Sửu", "suu", 2),
    ("Dần", "dan", 3),
    ("Mão", "mao", 4),
    ("Thìn", "thin", 5),
    ("Tỵ", "ty", 6),
    ("Ngọ", "ngo", 7),
    ("Mùi", "mui", 8),
    ("Thân", "than", 9),
    ("Dậu", "dau", 10),
    ("Tuất", "tuat", 11),
    ("Hợi", "hoi", 12)
]

# Reference year for Can-Chi calculation (Giáp Tý)
REFERENCE_YEAR = 1984


def calculate_can_chi_year(year: int) -> Tuple[str, str, str, int]:
    """
    Calculate Can-Chi for a given year.
    
    Returns: (can, chi, can_chi, zodiac_id)
    """
    # Calculate index in 60-year cycle
    offset = year - REFERENCE_YEAR
    
    can_index = offset % 10
    chi_index = offset % 12
    
    can = THIEN_CAN[can_index]
    chi_name, chi_code, zodiac_id = DIA_CHI[chi_index]
    
    can_chi = f"{can} {chi_name}"
    
    return can, chi_name, can_chi, zodiac_id


def calculate_hour_branch(hour: int, minute: int = 0) -> Tuple[str, str]:
    """
    Calculate hour branch (canh giờ) from hour and minute.
    
    Returns: (branch_code, branch_name)
    """
    # Tý: 23:00 - 00:59
    if hour == 23 or hour == 0:
        return "ti", "Tý"
    
    # Calculate branch index for hours 1-22
    index = (hour + 1) // 2
    
    chi_name, chi_code, _ = DIA_CHI[index]
    return chi_code, chi_name


def get_lunar_year(solar_date: date) -> int:
    """
    Get lunar year from solar date.
    Note: This is a simplified version. For accurate conversion,
    use a proper lunar calendar library.
    
    For dates in January, the lunar year might be the previous year.
    """
    # Simple approximation: If before Feb 5, might be previous lunar year
    # This is not accurate but serves as a fallback
    if solar_date.month == 1 or (solar_date.month == 2 and solar_date.day < 5):
        return solar_date.year - 1
    return solar_date.year


def process_users(connection, batch_size: int, dry_run: bool) -> Tuple[int, int, int]:
    """
    Process users and update their can_chi and zodiac_id.
    
    Returns: (total_processed, total_updated, total_errors)
    """
    cursor = connection.cursor(dictionary=True)
    update_cursor = connection.cursor()
    
    total_processed = 0
    total_updated = 0
    total_errors = 0
    
    try:
        # Select users with birth_date but missing can_chi
        # Adjust this query based on your actual table structure
        select_query = """
            SELECT id, birth_date, birth_time
            FROM users
            WHERE birth_date IS NOT NULL
            AND (can_chi IS NULL OR can_chi = '' OR zodiac_id IS NULL)
            LIMIT %s
        """
        
        cursor.execute(select_query, (batch_size,))
        users = cursor.fetchall()
        
        if not users:
            logger.info("No users to process")
            return 0, 0, 0
        
        logger.info(f"Processing {len(users)} users...")
        
        updates = []
        
        for user in users:
            total_processed += 1
            
            try:
                user_id = user['id']
                birth_date = user['birth_date']
                birth_time = user.get('birth_time')
                
                # Get lunar year (simplified)
                lunar_year = get_lunar_year(birth_date)
                
                # Calculate Can-Chi
                can, chi, can_chi, zodiac_id = calculate_can_chi_year(lunar_year)
                
                # Calculate hour branch if time available
                hour_branch = None
                if birth_time:
                    if isinstance(birth_time, str):
                        parts = birth_time.split(':')
                        hour = int(parts[0])
                        minute = int(parts[1]) if len(parts) > 1 else 0
                    else:
                        hour = birth_time.hour
                        minute = birth_time.minute
                    hour_branch, _ = calculate_hour_branch(hour, minute)
                
                updates.append({
                    'id': user_id,
                    'can_chi': can_chi,
                    'zodiac_id': zodiac_id,
                    'hour_branch': hour_branch,
                    'birth_date': birth_date
                })
                
                if total_processed % 100 == 0:
                    logger.info(f"Processed {total_processed} users...")
                    
            except Exception as e:
                total_errors += 1
                logger.error(f"Error processing user {user.get('id')}: {e}")
        
        # Perform updates
        if not dry_run and updates:
            update_query = """
                UPDATE users
                SET can_chi = %s, zodiac_id = %s
                WHERE id = %s
            """
            
            for update in updates:
                try:
                    update_cursor.execute(update_query, (
                        update['can_chi'],
                        update['zodiac_id'],
                        update['id']
                    ))
                    total_updated += 1
                except Exception as e:
                    total_errors += 1
                    logger.error(f"Error updating user {update['id']}: {e}")
            
            connection.commit()
            logger.info(f"Committed {total_updated} updates")
        else:
            logger.info("Dry run - no updates committed")
            # Print sample results
            for update in updates[:5]:
                logger.info(f"  Would update user {update['id']}: can_chi={update['can_chi']}, zodiac_id={update['zodiac_id']}")
            if len(updates) > 5:
                logger.info(f"  ... and {len(updates) - 5} more")
            total_updated = len(updates)
        
    finally:
        cursor.close()
        update_cursor.close()
    
    return total_processed, total_updated, total_errors


def main():
    parser = argparse.ArgumentParser(description="Backfill user can_chi and zodiac_id")
    parser.add_argument("--db-host", default="localhost", help="MySQL host")
    parser.add_argument("--db-port", type=int, default=3306, help="MySQL port")
    parser.add_argument("--db-user", default="root", help="MySQL user")
    parser.add_argument("--db-password", default="", help="MySQL password")
    parser.add_argument("--db-name", default="lich_van_nien", help="Database name")
    parser.add_argument("--batch-size", type=int, default=1000, help="Batch size for processing")
    parser.add_argument("--dry-run", action="store_true", help="Preview without writing")
    parser.add_argument("--continuous", action="store_true", help="Continue until all users processed")
    args = parser.parse_args()
    
    logger.info("=" * 60)
    logger.info("User Can-Chi Backfill Script")
    logger.info("=" * 60)
    logger.info(f"Host: {args.db_host}:{args.db_port}")
    logger.info(f"Database: {args.db_name}")
    logger.info(f"Batch size: {args.batch_size}")
    logger.info(f"Dry run: {args.dry_run}")
    logger.info("=" * 60)
    
    try:
        connection = mysql.connector.connect(
            host=args.db_host,
            port=args.db_port,
            user=args.db_user,
            password=args.db_password,
            database=args.db_name
        )
        
        logger.info("Connected to database")
        
        total_processed = 0
        total_updated = 0
        total_errors = 0
        
        while True:
            processed, updated, errors = process_users(connection, args.batch_size, args.dry_run)
            total_processed += processed
            total_updated += updated
            total_errors += errors
            
            if processed == 0 or not args.continuous:
                break
        
        logger.info("=" * 60)
        logger.info("Summary")
        logger.info("=" * 60)
        logger.info(f"Total processed: {total_processed}")
        logger.info(f"Total updated: {total_updated}")
        logger.info(f"Total errors: {total_errors}")
        
    except Error as e:
        logger.error(f"Database error: {e}")
        sys.exit(1)
    finally:
        if 'connection' in locals() and connection.is_connected():
            connection.close()
            logger.info("Database connection closed")


if __name__ == "__main__":
    main()

