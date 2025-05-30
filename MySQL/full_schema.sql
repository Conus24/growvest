 DROP DATABASE growvest;
CREATE DATABASE growvest DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE growvest;

CREATE TABLE user (
    us_num INT AUTO_INCREMENT PRIMARY KEY,
    us_id VARCHAR(50),
    us_pw VARCHAR(255),
    us_nickname VARCHAR(50),
    us_authority VARCHAR(10),
    us_created DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_asset (
    as_num INT PRIMARY KEY AUTO_INCREMENT,
    as_us_num INT,
    as_type INT,
    as_asset_type VARCHAR(50),
    as_currency ENUM('KRW', 'USD'),
    as_amount BIGINT,
    as_created DATETIME,
    FOREIGN KEY (as_us_num) REFERENCES user(us_num)
);

CREATE TABLE user_asset_target (
    ta_num INT PRIMARY KEY AUTO_INCREMENT,
    ta_us_num INT,
    ta_as_num INT,
    ta_target_percent FLOAT,
    ta_end_date DATE,
    UNIQUE KEY uniq_user_asset (ta_us_num, ta_as_num),
    FOREIGN KEY (ta_us_num) REFERENCES user(us_num)
);


CREATE TABLE present_asset (
    pr_num INT PRIMARY KEY AUTO_INCREMENT,
    pr_us_num INT,
    pr_cash_won INT,
    pr_cash_dollar INT,
    pr_deposit INT,
    pr_bond INT,
    pr_gold INT,
    pr_etf INT,
    pr_datetime DATETIME,
    FOREIGN KEY (pr_us_num) REFERENCES user(us_num)
);

CREATE TABLE recommendation_input (
    re_num INT PRIMARY KEY AUTO_INCREMENT,
    re_result_num INT,
    re_monthly_invest_amount BIGINT,
    re_lump_initial_amount BIGINT,
    re_max_mdd FLOAT,
    re_include_stock BOOLEAN,
    re_stock_max_percent FLOAT
);

CREATE TABLE etf (
    etf_num INT PRIMARY KEY AUTO_INCREMENT,
    etf_name VARCHAR(50),
    etf_expected_return FLOAT,
    etf_risk_score INT,
    etf_type ENUM('국내', '해외')
);

CREATE TABLE recommendation_result (
    result_num INT PRIMARY KEY AUTO_INCREMENT,
    us_num INT,
    result_etf_num INT,
    result_recommended_percent FLOAT,
    FOREIGN KEY (us_num) REFERENCES user(us_num),
    FOREIGN KEY (result_etf_num) REFERENCES etf(etf_num)
);

CREATE TABLE portfolio_risk_profile (
    ri_num INT PRIMARY KEY AUTO_INCREMENT,
    ri_us_num INT,
    ri_score INT,
    FOREIGN KEY (ri_us_num) REFERENCES user(us_num)
);

CREATE TABLE asset_type_score (
    at_num INT PRIMARY KEY AUTO_INCREMENT,
    at_as_num INT NOT NULL,
    at_name VARCHAR(20) NOT NULL,
    at_mdd FLOAT,
    at_score INT,
    at_grade VARCHAR(20),
    FOREIGN KEY (at_as_num) REFERENCES user_asset(as_num)
);

CREATE TABLE goal_tracker (
    go_num INT PRIMARY KEY AUTO_INCREMENT,
    go_us_num INT,
    go_target_amount BIGINT,
    go_current_amount BIGINT,
    go_start_date DATE,
    go_end_date DATE,
    go_tax_type ENUM('비과세', '이자소득세', '양도소득세'),
    go_state VARCHAR(20),
    FOREIGN KEY (go_us_num) REFERENCES user(us_num)
);

CREATE TABLE cpi_data (
    cpi_year INT PRIMARY KEY,
    cpi_change_rate FLOAT,
    cpi_interest_rate FLOAT,
    cpi_real_interest_rate FLOAT
);

CREATE TABLE historical_asset_data (
    hi_year INT PRIMARY KEY,
    hi_asset_type VARCHAR(50),
    hi_price BIGINT,
    hi_growth_rate FLOAT
);

CREATE TABLE api (
    api_num INT PRIMARY KEY AUTO_INCREMENT,
    api_name VARCHAR(50),
    api_value DECIMAL(10,2),
    api_date DATE,
    api_datetime DATETIME,
    UNIQUE KEY uq_api_name_date (api_name, api_date)
);
