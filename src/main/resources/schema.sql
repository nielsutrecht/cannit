CREATE TABLE IF NOT EXISTS templates (
    sub VARCHAR(255) NOT NULL,
    trigger VARCHAR(255) NOT NULL,
    params VARCHAR(255),
    response TEXT NOT NULL,
    lastUpdated TIMESTAMP NOT NULL,
    PRIMARY KEY (sub, trigger)
);
