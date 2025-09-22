CREATE DATABASE FLIXMATE_2_0;
GO

USE FLIXMATE_2_0;
GO

IF OBJECT_ID('[dbo].[booking_seats]', 'U') IS NOT NULL DROP TABLE [dbo].[booking_seats];
IF OBJECT_ID('[dbo].[payments]', 'U') IS NOT NULL DROP TABLE [dbo].[payments];
IF OBJECT_ID('[dbo].[bookings]', 'U') IS NOT NULL DROP TABLE [dbo].[bookings];
IF OBJECT_ID('[dbo].[showtimes]', 'U') IS NOT NULL DROP TABLE [dbo].[showtimes];
IF OBJECT_ID('[dbo].[seats]', 'U') IS NOT NULL DROP TABLE [dbo].[seats];
IF OBJECT_ID('[dbo].[cinema_halls]', 'U') IS NOT NULL DROP TABLE [dbo].[cinema_halls];
IF OBJECT_ID('[dbo].[reviews]', 'U') IS NOT NULL DROP TABLE [dbo].[reviews];
IF OBJECT_ID('[dbo].[promotional_banners]', 'U') IS NOT NULL DROP TABLE [dbo].[promotional_banners];
IF OBJECT_ID('[dbo].[reports]', 'U') IS NOT NULL DROP TABLE [dbo].[reports];
IF OBJECT_ID('[dbo].[discount_codes]', 'U') IS NOT NULL DROP TABLE [dbo].[discount_codes];
IF OBJECT_ID('[dbo].[loyalty_points]', 'U') IS NOT NULL DROP TABLE [dbo].[loyalty_points];
IF OBJECT_ID('[dbo].[movies]', 'U') IS NOT NULL DROP TABLE [dbo].[movies];
IF OBJECT_ID('[dbo].[staff_schedules]', 'U') IS NOT NULL DROP TABLE [dbo].[staff_schedules];
IF OBJECT_ID('[dbo].[users]', 'U') IS NOT NULL DROP TABLE [dbo].[users];
IF OBJECT_ID('[dbo].[user_status]', 'U') IS NOT NULL DROP TABLE [dbo].[user_status];
GO

CREATE TABLE [dbo].[user_status]
(
    [status_id]       INT IDENTITY(1,1) CONSTRAINT PK_user_status PRIMARY KEY,
    [status_name]     NVARCHAR(50)  NOT NULL UNIQUE,
    [role]            NVARCHAR(50)  NOT NULL DEFAULT('ROLE_USER')
);
GO

CREATE TABLE [dbo].[users]
(
    [user_id]          INT IDENTITY(1,1) CONSTRAINT PK_users PRIMARY KEY,
    [user_name]        NVARCHAR(100) NULL,
    [password_hash]    NVARCHAR(255) NOT NULL,
    [email]            NVARCHAR(150) NOT NULL UNIQUE,
    [phone]            NVARCHAR(30)  NULL,
    [registration_date] DATETIME2     NOT NULL DEFAULT(SYSDATETIME()),
    [last_login]       DATETIME2      NULL,
    [status_id]        INT            NOT NULL CONSTRAINT FK_users_status REFERENCES [dbo].[user_status]([status_id])
);
GO

CREATE TABLE [dbo].[movies]
(
    [movie_id]     INT IDENTITY(1,1) CONSTRAINT PK_movies PRIMARY KEY,
    [title]        NVARCHAR(200)  NOT NULL,
    [description]  NVARCHAR(MAX) NULL,
    [release_year] INT            NULL,
    [genre]        NVARCHAR(50)  NULL,
    [duration]     INT            NULL,
    [language]     NVARCHAR(50)  NULL,
    [director]     NVARCHAR(100) NULL,
    [movie_cast]   NVARCHAR(500) NULL,
    [trailer_url]  NVARCHAR(255) NULL,
    [poster_url]   NVARCHAR(255) NULL,
    [rating]       NVARCHAR(10)  NULL,
    [is_active]    BIT            NOT NULL DEFAULT(1),
    [created_date] DATETIME2       NOT NULL DEFAULT(SYSDATETIME()),
    [updated_date] DATETIME2       NOT NULL DEFAULT(SYSDATETIME())
);
GO

CREATE TABLE [dbo].[cinema_halls]
(
    [hall_id]  INT IDENTITY(1,1) CONSTRAINT PK_cinema_halls PRIMARY KEY,
    [name]     NVARCHAR(100) NOT NULL,
    [location] NVARCHAR(150) NULL,
    [capacity] INT           NOT NULL CHECK ([capacity] > 0)
);
GO

CREATE TABLE [dbo].[showtimes]
(
    [showtime_id] INT IDENTITY(1,1) CONSTRAINT PK_showtimes PRIMARY KEY,
    [start_time]  DATETIME2     NOT NULL,
    [end_time]    DATETIME2     NULL,
    [price]       DECIMAL(10,2) NOT NULL CHECK ([price] >= 0),
    [hall_id]     INT           NOT NULL,
    [movie_id]    INT           NOT NULL,
    CONSTRAINT FK_showtimes_hall  FOREIGN KEY ([hall_id])  REFERENCES [dbo].[cinema_halls]([hall_id]),
    CONSTRAINT FK_showtimes_movie FOREIGN KEY ([movie_id]) REFERENCES [dbo].[movies]([movie_id])
);
GO

CREATE TABLE [dbo].[seats]
(
    [seat_id]  INT IDENTITY(1,1) CONSTRAINT PK_seats PRIMARY KEY,
    [hall_id]  INT           NOT NULL,
    [row]      NVARCHAR(10)  NOT NULL,
    [number]   INT           NOT NULL,
    [status]   NVARCHAR(20)  NOT NULL DEFAULT('AVAILABLE'),
    CONSTRAINT FK_seats_hall FOREIGN KEY ([hall_id]) REFERENCES [dbo].[cinema_halls]([hall_id]),
    CONSTRAINT UQ_seats_hall_row_number UNIQUE ([hall_id], [row], [number])
);
GO

CREATE TABLE [dbo].[bookings]
(
    [booking_id]   INT IDENTITY(1,1) CONSTRAINT PK_bookings PRIMARY KEY,
    [booking_date] DATETIME2     NOT NULL DEFAULT(SYSDATETIME()),
    [total_seats]  INT           NOT NULL CHECK ([total_seats] > 0),
    [total_amount] DECIMAL(10,2) NOT NULL CHECK ([total_amount] >= 0),
    [status]       NVARCHAR(30)  NOT NULL DEFAULT('PENDING'),
    [user_id]      INT           NOT NULL,
    [showtime_id]  INT           NOT NULL,
    CONSTRAINT FK_bookings_user     FOREIGN KEY ([user_id])     REFERENCES [dbo].[users]([user_id]),
    CONSTRAINT FK_bookings_showtime FOREIGN KEY ([showtime_id]) REFERENCES [dbo].[showtimes]([showtime_id])
);
GO

CREATE TABLE [dbo].[booking_seats]
(
    [booking_id] INT NOT NULL,
    [seat_id]    INT NOT NULL,
    CONSTRAINT PK_booking_seats PRIMARY KEY ([booking_id], [seat_id]),
    CONSTRAINT FK_booking_seats_booking FOREIGN KEY ([booking_id]) REFERENCES [dbo].[bookings]([booking_id]) ON DELETE CASCADE,
    CONSTRAINT FK_booking_seats_seat    FOREIGN KEY ([seat_id])    REFERENCES [dbo].[seats]([seat_id])
);
GO

CREATE TABLE [dbo].[payments]
(
    [payment_id]      INT IDENTITY(1,1) CONSTRAINT PK_payments PRIMARY KEY,
    [transaction_id]  NVARCHAR(100) NOT NULL,
    [payment_date]    DATETIME2     NOT NULL DEFAULT(SYSDATETIME()),
    [payment_method]  NVARCHAR(30)  NOT NULL,
    [amount]          DECIMAL(10,2) NOT NULL CHECK ([amount] >= 0),
    [status]          NVARCHAR(20)  NOT NULL,
    [gateway_response] NVARCHAR(255) NULL,
    [failure_reason]  NVARCHAR(255) NULL,
    [refund_amount]   DECIMAL(10,2) NULL,
    [refund_date]     DATETIME2     NULL,
    [receipt_url]     NVARCHAR(255) NULL,
    [booking_id]      INT           NOT NULL,
    CONSTRAINT FK_payments_booking FOREIGN KEY ([booking_id]) REFERENCES [dbo].[bookings]([booking_id])
);
GO

CREATE TABLE [dbo].[reviews]
(
    [review_id]          INT IDENTITY(1,1) CONSTRAINT PK_reviews PRIMARY KEY,
    [rating]             INT           NOT NULL CHECK ([rating] BETWEEN 1 AND 5),
    [comment]            NVARCHAR(MAX) NULL,
    [title]              NVARCHAR(150) NULL,
    [is_verified_booking] BIT          NOT NULL DEFAULT(0),
    [is_reported]         BIT          NOT NULL DEFAULT(0),
    [report_reason]      NVARCHAR(255) NULL,
    [review_date]        DATETIME2     NOT NULL DEFAULT(SYSDATETIME()),
    [user_id]            INT           NOT NULL,
    [movie_id]           INT           NOT NULL,
    CONSTRAINT FK_reviews_user  FOREIGN KEY ([user_id])  REFERENCES [dbo].[users]([user_id]),
    CONSTRAINT FK_reviews_movie FOREIGN KEY ([movie_id]) REFERENCES [dbo].[movies]([movie_id])
);
GO

CREATE TABLE [dbo].[discount_codes]
(
    [code_id]            INT IDENTITY(1,1) CONSTRAINT PK_discount_codes PRIMARY KEY,
    [code]               NVARCHAR(50)  NOT NULL UNIQUE,
    [description]        NVARCHAR(255) NULL,
    [discount_type]      NVARCHAR(30)  NOT NULL,
    [discount_value]     DECIMAL(10,2) NOT NULL,
    [min_purchase_amount] DECIMAL(10,2) NOT NULL DEFAULT(0),
    [max_discount_amount] DECIMAL(10,2) NULL,
    [usage_limit]        INT           NULL,
    [used_count]         INT           NOT NULL DEFAULT(0),
    [valid_from]         DATETIME2     NOT NULL,
    [valid_until]        DATETIME2     NOT NULL,
    [is_active]          BIT           NOT NULL DEFAULT(1)
);
GO

CREATE TABLE [dbo].[loyalty_points]
(
    [points_id]      INT IDENTITY(1,1) CONSTRAINT PK_loyalty_points PRIMARY KEY,
    [user_id]        INT           NOT NULL,
    [points_balance] INT           NOT NULL DEFAULT(0),
    [total_earned]   INT           NOT NULL DEFAULT(0),
    [total_redeemed] INT           NOT NULL DEFAULT(0),
    [last_updated]   DATETIME2     NOT NULL DEFAULT(SYSDATETIME()),
    CONSTRAINT FK_loyalty_points_user FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([user_id])
);
GO

CREATE TABLE [dbo].[promotional_banners]
(
    [banner_id]          INT IDENTITY(1,1) CONSTRAINT PK_promotional_banners PRIMARY KEY,
    [title]              NVARCHAR(150) NOT NULL,
    [description]        NVARCHAR(255) NULL,
    [image_url]          NVARCHAR(255) NOT NULL,
    [target_url]         NVARCHAR(255) NULL,
    [discount_code]      NVARCHAR(50)  NULL,
    [discount_percentage] DECIMAL(5,2) NULL,
    [start_date]         DATETIME2     NOT NULL,
    [end_date]           DATETIME2     NOT NULL,
    [is_active]          BIT           NOT NULL DEFAULT(1),
    [display_order]      INT           NOT NULL DEFAULT(0),
    [click_count]        INT           NOT NULL DEFAULT(0)
);
GO

CREATE TABLE [dbo].[reports]
(
    [report_id]      INT IDENTITY(1,1) CONSTRAINT PK_reports PRIMARY KEY,
    [type]           NVARCHAR(50)  NOT NULL,
    [data]           NVARCHAR(MAX) NOT NULL,
    [generated_date] DATETIME2     NOT NULL DEFAULT(SYSDATETIME()),
    [user_id]        INT           NOT NULL,
    CONSTRAINT FK_reports_user FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([user_id])
);
GO

CREATE TABLE [dbo].[staff_schedules]
(
    [schedule_id] INT IDENTITY(1,1) CONSTRAINT PK_staff_schedules PRIMARY KEY,
    [staff_name]  NVARCHAR(150) NOT NULL,
    [start_time]  DATETIME2     NOT NULL,
    [end_time]    DATETIME2     NOT NULL,
    [hall_id]     INT           NOT NULL,
    CONSTRAINT FK_staff_schedules_hall FOREIGN KEY ([hall_id]) REFERENCES [dbo].[cinema_halls]([hall_id])
);
GO

CREATE INDEX IX_users_email ON [dbo].[users]([email]);
CREATE INDEX IX_bookings_user ON [dbo].[bookings]([user_id], [status]);
CREATE INDEX IX_showtimes_movie_time ON [dbo].[showtimes]([movie_id], [start_time]);
CREATE INDEX IX_payments_booking ON [dbo].[payments]([booking_id], [status]);
GO
