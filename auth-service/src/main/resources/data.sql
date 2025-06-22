CREATE TABLE IF NOT EXISTS "users" (
                                       id UUID PRIMARY KEY,
                                       email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
    );

-- Insert user if no existing user with the same id/email exists
INSERT INTO "users" (id, email, password, role)
SELECT 'b16d3e64-25ec-4f60-93be-951531ce169a', 'testpriti@test.com',
       '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu', 'ADMIN'
    WHERE NOT EXISTS (
    SELECT 1
    FROM "users"
    WHERE id = 'b16d3e64-25ec-4f60-93be-951531ce169a'
       OR email = 'testpriti@test.com'
);


