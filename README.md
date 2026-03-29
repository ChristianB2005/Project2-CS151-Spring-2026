# Restaurant Management System
**CS151 Spring 2026**

## Overview

A Java-based restaurant management system simulating real-world restaurant operations. The system manages customers, reservations, tables, orders, menu items, kitchen staff, and servers through a text-based UI.

📹 **[Video Presentation](https://drive.google.com/file/d/19ilrzzFi8RCYjIv92Qf4SQh-xhAqMMW4/view?usp=sharing)**

---

## Design

**Class structure:**

| Class | Description |
|---|---|
| `Customer` | Manages customer state, reservations, bill payment, and loyalty points |
| `Reservation` | Tracks reservation status (Pending → Confirmed → Cancelled) |
| `Table` | Handles seating, occupancy, and server assignment |
| `Order` | Manages per-customer item selections and pricing |
| `MenuItem` | Tracks price, availability, and stock count |
| `Chef` | Extends `Employee`; manages an order queue |
| `Server` | Extends `Employee`; manages table assignments and order-taking |
| `Kitchen` | Tracks active orders, staff, and capacity |

**OOP structure:**
- **Abstract class**: `Employee` — shared fields (`employeeId`, `name`, `isOnDuty`) and abstract method `updateStatus()`, extended by `Chef` and `Server`
- **Interface**: `Discountable` — implemented by both `Order` and `MenuItem`, providing `applyDiscount()`, `applyFlatDiscount()`, and `getPrice()`
- **Enum**: `OrderStatus` — `TAKING_ORDER`, `IN_KITCHEN`, `READY`
- **Custom exceptions**: `TooManyInstancesException`, `KitchenAtCapacityException`, `ReservationException`, `InvalidOrderState`, `InvalidDiscountException`
- All classes enforce a 100-instance cap via `Constants.MAXIMUM_INSTANCES`

---

## Installation

1. Clone the repository
2. Open in IntelliJ IDEA or VS Code with the Java extension
3. Add JUnit 5 (`junit-jupiter`) to the project dependencies for running tests
4. Compile all files under `src/`

---

## Usage

Run `src/ui/MainUI.java` to launch the text-based menu. Navigate using numbered options at each prompt. Type `EXIT` at any point to quit gracefully.

**Main menu options:**
1. **Manage Menu Items** — create items, update prices, restock inventory, mark items unavailable, apply percentage or flat discounts
2. **Manage Tables** — create tables, assign servers, view table status and occupancy
3. **Manage Orders** — create orders, add or remove items per customer, submit orders to the kitchen, apply discounts
4. **Manage Customers** — create customers, make or cancel reservations, seat customers, pay bills
5. **Manage Kitchen** — add chefs, clock staff in/out, view active orders, assign orders to chefs, mark orders complete
6. **Exit**

**Recommended first-time flow:**
1. Create menu items with stock and pricing
2. Create tables and assign servers
3. Add chefs and clock them in
4. Create customers and seat them at tables
5. Take and submit their orders
6. Complete orders in the kitchen
7. Pay the bill to earn loyalty points

---

## Contributions

| Name            | Ownership                                                                       |
|-----------------|---------------------------------------------------------------------------------|
| Abhishek Roy    | `Customer`, `Reservation`, UML diagram, `Constants`, `TooManyInstacesException` |
| JD Bowman       | `Table`, `Order`, `Discountable` (interface), `InvalidOrderState`               |
| Christian Barber | `Chef`, `Kitchen`, `Table` (updates), `Customer` (updates)                      |
| Jesse Olufade   | `MenuItem`, `Server`, `Employee` (abstract), `OrderStatus`, `MainUI`            |

---

## Project Structure

```
src/
├── model/          Customer, Reservation, Table, Order, MenuItem, Chef, Server, Kitchen
├── core/           Employee (abstract), Discountable (interface)
├── exceptions/     All custom exceptions
├── util/           Constants, OrderStatus
├── ui/             MainUI
├── test/           JUnit 5 test files (one per model class)
```
