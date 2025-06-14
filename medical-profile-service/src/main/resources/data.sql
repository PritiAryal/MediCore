-- -- Create the medical_profile table
-- CREATE TABLE IF NOT EXISTS medical_profile (
--     id UUID PRIMARY KEY,
--     name VARCHAR(255) NOT NULL,
--     email VARCHAR(255) NOT NULL UNIQUE,
--     address VARCHAR(255) NOT NULL,
--     date_of_birth DATE NOT NULL,
--     registered_date DATE NOT NULL
--     );
--
-- -- Insert sample data
-- INSERT INTO medical_profile (id, name, email, address, date_of_birth, registered_date) VALUES
--                                                                                            ('1e7f74fa-3db9-4c30-b612-1d1234567890', 'Alice Johnson', 'alice@example.com', '123 Elm Street', '1990-05-20', '2024-06-01'),
--                                                                                            ('2b9f64ab-7cda-49ab-a111-2e9876543210', 'Bob Smith', 'bob@example.com', '456 Maple Avenue', '1985-12-15', '2024-06-02'),
--                                                                                            ('3c3e7f11-13ab-4781-8e22-3f6789012345', 'Carol Lee', 'carol@example.com', '789 Oak Lane', '1992-08-30', '2024-06-03');

-- Create the medical_profile table
CREATE TABLE IF NOT EXISTS medical_profile (
                                               id UUID PRIMARY KEY,
                                               name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    address VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    registered_date DATE NOT NULL
    );

-- Insert Alice Johnson if not already present
INSERT INTO medical_profile (id, name, email, address, date_of_birth, registered_date)
SELECT '1e7f74fa-3db9-4c30-b612-1d1234567890', 'Alice Johnson', 'alice@example.com', '123 Elm Street', '1990-05-20', '2024-06-01'
    WHERE NOT EXISTS (
    SELECT 1 FROM medical_profile WHERE id = '1e7f74fa-3db9-4c30-b612-1d1234567890'
);

-- Insert Bob Smith
INSERT INTO medical_profile (id, name, email, address, date_of_birth, registered_date)
SELECT '2b9f64ab-7cda-49ab-a111-2e9876543210', 'Bob Smith', 'bob@example.com', '456 Maple Avenue', '1985-12-15', '2024-06-02'
    WHERE NOT EXISTS (
    SELECT 1 FROM medical_profile WHERE id = '2b9f64ab-7cda-49ab-a111-2e9876543210'
);

-- Insert Carol Lee
INSERT INTO medical_profile (id, name, email, address, date_of_birth, registered_date)
SELECT '3c3e7f11-13ab-4781-8e22-3f6789012345', 'Carol Lee', 'carol@example.com', '789 Oak Lane', '1992-08-30', '2024-06-03'
    WHERE NOT EXISTS (
    SELECT 1 FROM medical_profile WHERE id = '3c3e7f11-13ab-4781-8e22-3f6789012345'
);
