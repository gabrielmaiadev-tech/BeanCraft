CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    origin VARCHAR(120) NOT NULL,
    roast_level VARCHAR(16) NOT NULL CHECK (roast_level IN ('LIGHT', 'MEDIUM', 'DARK')),
    weight_grams INTEGER NOT NULL CHECK (weight_grams > 0),
    price NUMERIC(10, 2) NOT NULL CHECK (price > 0),
    stock INTEGER NOT NULL CHECK (stock >= 0),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_products_name UNIQUE (name)
);

CREATE INDEX idx_products_origin ON products (origin);
CREATE INDEX idx_products_roast_level ON products (roast_level);