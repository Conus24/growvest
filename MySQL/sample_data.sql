INSERT INTO user (
    us_id, us_pw, us_nickname, us_authority
) VALUES (
    'a',
    '$2a$10$bDSTHew9PH.nHBCWSr4jsOPkPzaVX1ycgScYXCSSD9Q0krcB9kd2q',
    '테스트유저',
    'USER');
    
INSERT INTO user_asset (as_us_num, as_type, as_asset_type, as_currency, as_amount, as_created) VALUES
   (1, '1', '현금 (원)', 'KRW', 5000000, NOW()),
   (1, '2', '현금 (달러)', 'USD', 4000, NOW()),
   (1, '3', '예적금', 'KRW', 10000000, NOW()),
   (1, '4', '채권', 'KRW', 7000000, NOW()),
   (1, '5', '금', 'KRW', 3000000, NOW()),
   (1, '6', 'ETF', 'USD', 8000, NOW());

INSERT INTO asset_type_score (at_as_num, at_name, at_mdd, at_score) VALUES
   (1, '현금 (원)', 0, 0),
   (2, '현금 (달러)', 0, 0),
   (3, '예적금', 0, 0),
   (4, '채권', 0, 0),
   (5, '금', 0, 0),
   (4, 'ETF', 0, 0);