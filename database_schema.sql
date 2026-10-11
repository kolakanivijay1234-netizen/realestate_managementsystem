-- ============================================================
-- Real Estate Agent Mandate Management System Database Schema
-- Database Name: real_estate_mandate_db
-- ============================================================

CREATE DATABASE IF NOT EXISTS real_estate_mandate_db;
USE real_estate_mandate_db;

-- 1. Agencies Table
CREATE TABLE IF NOT EXISTS agencies (
    agency_id INT AUTO_INCREMENT PRIMARY KEY,
    agency_name VARCHAR(150) NOT NULL,
    agency_type VARCHAR(50) NOT NULL, -- 'Independent' or 'Franchise'
    address VARCHAR(255),
    phone VARCHAR(20)
);

-- 2. Geographical Areas Table
CREATE TABLE IF NOT EXISTS geographical_areas (
    area_id INT AUTO_INCREMENT PRIMARY KEY,
    area_name VARCHAR(150) NOT NULL,
    population INT NOT NULL
);

-- 3. Agents Table
CREATE TABLE IF NOT EXISTS agents (
    agent_id INT AUTO_INCREMENT PRIMARY KEY,
    agent_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(150) NOT NULL,
    agency_id INT NOT NULL,
    FOREIGN KEY (agency_id) REFERENCES agencies(agency_id) ON DELETE CASCADE
);

-- 4. Mandates Table
CREATE TABLE IF NOT EXISTS mandates (
    mandate_id INT AUTO_INCREMENT PRIMARY KEY,
    agent_id INT NOT NULL,
    agency_id INT NOT NULL,
    area_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    mandate_type VARCHAR(50) NOT NULL, -- 'SALE', 'RENT', 'PURCHASE', 'LEASE'
    FOREIGN KEY (agent_id) REFERENCES agents(agent_id) ON DELETE CASCADE,
    FOREIGN KEY (agency_id) REFERENCES agencies(agency_id) ON DELETE CASCADE,
    FOREIGN KEY (area_id) REFERENCES geographical_areas(area_id) ON DELETE CASCADE
);

-- Sample Initial Data (Optional for testing)
INSERT INTO agencies (agency_name, agency_type, address, phone) VALUES
('Prime Realty Network', 'Franchise', '101 Corporate Blvd, Metro City', '9876543210'),
('Heritage Real Estate', 'Independent', '45 Market Street, Old Town', '9876543211'),
('Global Apex Estates', 'Franchise', '500 Skyline Tower, Downtown', '9876543212');

INSERT INTO geographical_areas (area_name, population) VALUES
('Downtown Metro', 250000),
('North Hills Suburb', 85000),
('West Coast Bay', 140000);

INSERT INTO agents (agent_name, phone, email, agency_id) VALUES
('Vijay Kumar', '9876543220', 'vijay.k@realty.com', 1),
('Ananya Sharma', '9876543221', 'ananya.s@heritage.com', 2),
('Rahul Verma', '9876543222', 'rahul.v@apex.com', 3);

INSERT INTO mandates (agent_id, agency_id, area_id, start_date, end_date, mandate_type) VALUES
(1, 1, 1, '2026-01-01', '2026-12-31', 'SALE'),
(1, 1, 2, '2026-02-15', '2026-11-30', 'RENT'),
(2, 2, 2, '2026-03-01', '2026-09-30', 'PURCHASE'),
(3, 3, 3, '2025-01-01', '2025-12-31', 'LEASE');
