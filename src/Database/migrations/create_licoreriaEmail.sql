-- =========================================================================
-- MIGRATION V1: SETUP INICIAL DE TABLAS
-- =========================================================================
-- Migration: create_licoreriaEmail.sql
-- Esquema: licoreriaEmail
-- PostgreSQL compatible

-- =========================================================================
-- 1. TABLAS MAESTRAS (Catálogos)
-- =========================================================================

CREATE TABLE rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE metodoPago (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE cuotas (
    id SERIAL PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL,
    cantidadesCuotas INT NOT NULL
);

CREATE TABLE producto (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL
);

CREATE TABLE proveedor (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE promo (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descuento DECIMAL(10, 2) NOT NULL
);

CREATE TABLE tiposSalida (
    id SERIAL PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL
);

-- =========================================================================
-- 2. TABLAS DE NIVEL 1
-- =========================================================================

CREATE TABLE "user" (
    id SERIAL PRIMARY KEY,
    rol_id INT REFERENCES rol(id),
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE stock (
    id SERIAL PRIMARY KEY,
    producto_id INT UNIQUE REFERENCES producto(id),
    cantidad INT NOT NULL,
    min INT NOT NULL,
    max INT NOT NULL
);

CREATE TABLE lote (
    id SERIAL PRIMARY KEY,
    producto_id INT REFERENCES producto(id),
    cantidad INT NOT NULL,
    fechaExpiracion DATE NOT NULL
);

CREATE TABLE compra (
    id SERIAL PRIMARY KEY,
    proveedor_id INT REFERENCES proveedor(id),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL
);

CREATE TABLE detallePromo (
    id SERIAL PRIMARY KEY,
    promo_id INT REFERENCES promo(id),
    producto_id INT REFERENCES producto(id),
    cantidad INT NOT NULL
);

CREATE TABLE notaSalida (
    id SERIAL PRIMARY KEY,
    tiposSalida_id INT REFERENCES tiposSalida(id),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================================
-- 3. TABLAS DE NIVEL 2
-- =========================================================================

CREATE TABLE aperturaCaja (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES "user"(id),
    fechaInicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fechaCierre TIMESTAMP,
    totalEfectivo DECIMAL(10, 2) DEFAULT 0,
    totalQR DECIMAL(10, 2) DEFAULT 0,
    totalTarjeta DECIMAL(10, 2) DEFAULT 0,
    totalSistema DECIMAL(10, 2) DEFAULT 0,
    cajaChica DECIMAL(10, 2) DEFAULT 0,
    realEfectivo DECIMAL(10, 2),
    realQR DECIMAL(10, 2),
    realTarjeta DECIMAL(10, 2),
    realTotal DECIMAL(10, 2),
    diferencia DECIMAL(10, 2)
);

CREATE TABLE venta (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES "user"(id),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    descuento DECIMAL(10, 2) DEFAULT 0,
    totalOriginal DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL
);

CREATE TABLE detalleCompra (
    id SERIAL PRIMARY KEY,
    compra_id INT REFERENCES compra(id),
    producto_id INT REFERENCES producto(id),
    cantidad INT NOT NULL,
    precioCompra DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL
);

CREATE TABLE salidaDetalle (
    id SERIAL PRIMARY KEY,
    notaSalida_id INT REFERENCES notaSalida(id),
    producto_id INT REFERENCES producto(id),
    cantidad INT NOT NULL
);

-- =========================================================================
-- 4. TABLAS DE NIVEL 3
-- =========================================================================

CREATE TABLE detalleVenta (
    id SERIAL PRIMARY KEY,
    venta_id INT REFERENCES venta(id),
    producto_id INT REFERENCES producto(id),
    cantidad INT NOT NULL,
    precioVenta DECIMAL(10, 2) NOT NULL,
    subTotal DECIMAL(10, 2) NOT NULL,
    descuento DECIMAL(10, 2) DEFAULT 0
);

CREATE TABLE ventaPromo (
    id SERIAL PRIMARY KEY,
    venta_id INT REFERENCES venta(id),
    promo_id INT REFERENCES promo(id)
);

CREATE TABLE detalleCuotas (
    id SERIAL PRIMARY KEY,
    venta_id INT REFERENCES venta(id),
    cuotas_id INT REFERENCES cuotas(id),
    monto DECIMAL(10, 2) NOT NULL,
    fecha DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'Pendiente'
);

-- =========================================================================
-- 5. TABLA DE TRANSACCIONES (Nivel 4)
-- =========================================================================

CREATE TABLE transaccion (
    id SERIAL PRIMARY KEY,
    venta_id INT REFERENCES venta(id),
    aperturaCaja_id INT REFERENCES aperturaCaja(id),
    metodoPago_id INT REFERENCES metodoPago(id),
    detalleCuotas_id INT REFERENCES detalleCuotas(id), 
    monto DECIMAL(10, 2) NOT NULL
);

-- =========================================================================
-- 6. DATOS INICIALES (SEMILLAS / SEEDS)
-- =========================================================================

-- Insertar Roles
INSERT INTO rol (nombre) VALUES 
('Propietario'),
('Vendedor'),
('Cliente');

-- Insertar Métodos de Pago
INSERT INTO metodoPago (nombre) VALUES 
('Efectivo'),
('Tarjeta'),
('QR');

-- Insertar Tipos de Salida (Inventario)
INSERT INTO tiposSalida (descripcion) VALUES 
('Rotura'),
('Consumo interno'),
('Vencimiento');

-- Insertar Planes de Cuotas
INSERT INTO cuotas (descripcion, cantidadesCuotas) VALUES 
('Plan 2 Cuotas', 2),
('Plan 3 Cuotas', 3);