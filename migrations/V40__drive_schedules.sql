CREATE TYPE VehicleType AS ENUM ('TRUCK', 'VAN', 'CAR');

CREATE TABLE drive_schedule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    grid_connection_id UUID NOT NULL REFERENCES grid_connection(id) ON DELETE CASCADE,
    vehicle_type VehicleType NOT NULL,
    n_vehicles INTEGER NOT NULL
);

CREATE TABLE drive_schedule_trip (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    drive_schedule_id UUID NOT NULL REFERENCES drive_schedule(id) ON DELETE CASCADE,
    day_of_week VARCHAR(9) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);