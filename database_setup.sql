-- ============================================
-- FURNITURE STORE SALES SYSTEM - DATABASE SETUP
-- ============================================

DROP DATABASE IF EXISTS furniture_store;
CREATE DATABASE furniture_store;
USE furniture_store;

-- ===================== USERS =====================
CREATE TABLE Users (
    UserID INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(100) NOT NULL,
    FullName VARCHAR(100) NOT NULL,
    Role VARCHAR(20) DEFAULT 'Cashier'
);

INSERT INTO Users (Username, Password, FullName, Role) VALUES
('admin', 'admin123', 'System Administrator', 'Admin'),
('cashier', 'cash123', 'Sales Cashier', 'Cashier');

-- ===================== CUSTOMER =====================
CREATE TABLE Customer (
    CustomerID INT PRIMARY KEY AUTO_INCREMENT,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(100),
    Phone VARCHAR(20) NOT NULL,
    Address VARCHAR(255),
    City VARCHAR(50),
    CreatedDate DATE DEFAULT (CURRENT_DATE)
);

-- ===================== CATEGORY =====================
CREATE TABLE Category (
    CategoryID INT PRIMARY KEY AUTO_INCREMENT,
    CategoryName VARCHAR(50) NOT NULL UNIQUE,
    Description VARCHAR(255)
);

-- ===================== PRODUCT =====================
CREATE TABLE Product (
    ProductID INT PRIMARY KEY AUTO_INCREMENT,
    ProductName VARCHAR(100) NOT NULL,
    CategoryID INT NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    StockQuantity INT NOT NULL DEFAULT 0,
    SupplierName VARCHAR(100),
    FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID)
);

-- ===================== SALES ORDER =====================
CREATE TABLE SalesOrder (
    OrderID INT PRIMARY KEY AUTO_INCREMENT,
    CustomerID INT NOT NULL,
    OrderDate DATE NOT NULL,
    TotalAmount DECIMAL(12,2) NOT NULL DEFAULT 0,
    Status VARCHAR(20) DEFAULT 'Completed',
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

-- ===================== ORDER DETAIL =====================
CREATE TABLE OrderDetail (
    OrderDetailID INT PRIMARY KEY AUTO_INCREMENT,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES SalesOrder(OrderID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

-- ===================== SAMPLE DATA =====================
INSERT INTO Category (CategoryName, Description) VALUES
('Living Room', 'Sofas, coffee tables, TV stands'),
('Bedroom', 'Beds, wardrobes, nightstands'),
('Dining Room', 'Dining tables, chairs, cabinets'),
('Office', 'Desks, office chairs, bookshelves');

INSERT INTO Product (ProductName, CategoryID, Price, StockQuantity, SupplierName) VALUES
('3-Seater Leather Sofa', 1, 45000.00, 15, 'FurniturePlus Ltd'),
('Queen Size Bed Frame', 2, 32000.00, 20, 'WoodWorks Inc'),
('Dining Table 6-Seater', 3, 38000.00, 10, 'FurniturePlus Ltd'),
('Office Desk 120cm', 4, 18500.00, 25, 'ModernOffice Co'),
('Glass Coffee Table', 1, 12500.00, 30, 'FurniturePlus Ltd'),
('Single Bed Frame', 2, 18000.00, 8, 'WoodWorks Inc'),
('Office Chair Ergonomic', 4, 22000.00, 12, 'ModernOffice Co'),
('Wardrobe 3-Door', 2, 55000.00, 6, 'WoodWorks Inc');

INSERT INTO Customer (FirstName, LastName, Email, Phone, Address, City) VALUES
('John', 'Silva', 'john.silva@email.com', '0771234567', '12 Main St', 'Colombo'),
('Sarah', 'Perera', 'sarah.p@email.com', '0719876543', '45 Hill Rd', 'Kandy'),
('Mike', 'Fernando', 'mike.f@email.com', '0765551234', '78 Beach Rd', 'Galle'),
('Nimal', 'Jayawardena', 'nimal.j@email.com', '0777778888', '3 Lake Rd', 'Colombo'),
('Kumari', 'Wickrama', 'kumari.w@email.com', '0712223333', '9 Temple Rd', 'Negombo');

INSERT INTO SalesOrder (CustomerID, OrderDate, TotalAmount, Status) VALUES
(1, '2026-01-15', 57500.00, 'Completed'),
(2, '2026-02-20', 32000.00, 'Completed'),
(3, '2026-03-05', 56500.00, 'Completed'),
(4, '2026-03-18', 45000.00, 'Completed'),
(5, '2026-04-02', 22000.00, 'Completed');

INSERT INTO OrderDetail (OrderID, ProductID, Quantity, UnitPrice, Subtotal) VALUES
(1, 1, 1, 45000.00, 45000.00),
(1, 5, 1, 12500.00, 12500.00),
(2, 2, 1, 32000.00, 32000.00),
(3, 3, 1, 38000.00, 38000.00),
(3, 4, 1, 18500.00, 18500.00),
(4, 1, 1, 45000.00, 45000.00),
(5, 7, 1, 22000.00, 22000.00);