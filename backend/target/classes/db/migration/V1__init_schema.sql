CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL,
    lat DECIMAL(10,8),
    lng DECIMAL(11,8),
    trust_score DECIMAL(3,2) DEFAULT 5.0,
    total_ratings INT DEFAULT 0,
    points INT DEFAULT 0,
    badge_level VARCHAR(50),
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS food_listings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donor_id BIGINT NOT NULL,
    food_name VARCHAR(255) NOT NULL,
    food_type VARCHAR(50),
    quantity INT NOT NULL,
    unit VARCHAR(50),
    description TEXT,
    pickup_address VARCHAR(255),
    lat DECIMAL(10,8),
    lng DECIMAL(11,8),
    expiry_time TIMESTAMP,
    priority_score DECIMAL(5,2),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    listing_id BIGINT NOT NULL,
    ngo_id BIGINT NOT NULL,
    volunteer_id BIGINT,
    claimed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP,
    delivery_status VARCHAR(50),
    otp_code VARCHAR(10),
    otp_verified BOOLEAN DEFAULT false,
    donor_rating INT,
    ngo_rating INT,
    FOREIGN KEY (listing_id) REFERENCES food_listings(id),
    FOREIGN KEY (ngo_id) REFERENCES users(id),
    FOREIGN KEY (volunteer_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    listing_id BIGINT,
    transaction_id BIGINT,
    type VARCHAR(50),
    channel VARCHAR(50),
    message TEXT,
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (listing_id) REFERENCES food_listings(id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE TABLE IF NOT EXISTS impact_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    meals_count INT NOT NULL,
    co2_saved_kg DECIMAL(10,2),
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE TABLE IF NOT EXISTS volunteer_deliveries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id BIGINT NOT NULL,
    transaction_id BIGINT NOT NULL,
    accepted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    status VARCHAR(50),
    distance_km DECIMAL(6,2),
    FOREIGN KEY (volunteer_id) REFERENCES users(id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);
