
-- Common
CREATE TABLE version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    version VARCHAR(255) NOT NULL
) DEFAULT CHARSET=utf8mb4;

-- User
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    state VARCHAR(255) NOT NULL,
    service_id VARCHAR(255) NOT NULL,
    service_user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    created_date_time DATETIME NOT NULL,
    updated_date_time DATETIME NOT NULL
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_deactivation_reason (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reason VARCHAR(255) NOT NULL,
    content VARCHAR(3000) NOT NULL
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE terms_agreement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    age_over_fourteen BOOLEAN NOT NULL,
    service_agreement BOOLEAN NOT NULL,
    personal_info_agreement BOOLEAN NOT NULL,
    marketing_agreement BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE forbidden_word (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(255) UNIQUE NOT NULL,
    INDEX idx_word (word)
) DEFAULT CHARSET=utf8mb4;

-- Profile
CREATE TABLE room_count_range (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    min_count INT NOT NULL,
    max_count INT NOT NULL
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE color (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    mode VARCHAR(255) NOT NULL,
    shape VARCHAR(255) NOT NULL,
    direction VARCHAR(255) NOT NULL,
    start_color VARCHAR(255) NOT NULL,
    end_color VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL,
    priority INT NOT NULL
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE element (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    sub_title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    emoji VARCHAR(255) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL,
    priority INT NOT NULL
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    state VARCHAR(255) NOT NULL,
    count INT NOT NULL,
    min_count INT NOT NULL,
    max_count INT NOT NULL,
    is_count_range BOOLEAN NOT NULL,
    mbti VARCHAR(255) NOT NULL,
    color_id BIGINT,
    created_date_time DATETIME NOT NULL,
    updated_date_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (color_id) REFERENCES color(id)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE profile_element (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    element_id BIGINT NULL,
    type VARCHAR(255) NOT NULL,
    FOREIGN KEY (profile_id) REFERENCES profile(id),
    FOREIGN KEY (element_id) REFERENCES element(id)
) DEFAULT CHARSET=utf8mb4;

-- Review
CREATE TABLE theme (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    area VARCHAR(255) NOT NULL,
    store_name VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    score DOUBLE NOT NULL,
    difficulty VARCHAR(255) NOT NULL,
    review_count INT NOT NULL,
    is_deleted BOOLEAN NOT NULL
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    state VARCHAR(255) NOT NULL,
    score DOUBLE NOT NULL,
    store_name VARCHAR(255) NOT NULL,
    theme_name VARCHAR(255) NOT NULL,
    spoiler BOOLEAN NOT NULL,
    is_public BOOLEAN NOT NULL,
    success BOOLEAN NULL,
    play_date DATE NULL,
    total_time INT NULL,
    remaining_time INT NULL,
    hint_count INT NULL,
    participants INT NULL,
    difficulty_level VARCHAR(255) NOT NULL,
    fear_level VARCHAR(255) NOT NULL,
    activity_level VARCHAR(255) NOT NULL,
    interior_rating DOUBLE NULL,
    direction_rating DOUBLE NULL,
    story_rating DOUBLE NULL,
    content VARCHAR(3000) NOT NULL,
    created_date_time DATETIME NOT NULL,
    updated_date_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE review_genre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    element_id BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    FOREIGN KEY (element_id) REFERENCES element(id),
    FOREIGN KEY (review_id) REFERENCES review(id)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE review_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL,
    FOREIGN KEY (review_id) REFERENCES review(id)
) DEFAULT CHARSET=utf8mb4;
