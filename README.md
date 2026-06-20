# Personal Expense Tracker

A beginner-friendly Java 17 Spring Boot MVC project for BSc Computer Science students. It uses Thymeleaf pages, Bootstrap 5 styling, plain session-based login, and JSON file storage with Jackson `ObjectMapper`.

## Folder Structure

```text
Personal-Expense-Tracker/
├── pom.xml
├── data/
│   ├── users.json
│   └── expenses.json
└── src/
    └── main/
        ├── java/com/example/expensetracker/
        │   ├── PersonalExpenseTrackerApplication.java
        │   ├── controller/
        │   │   ├── AuthController.java
        │   │   ├── DashboardController.java
        │   │   └── ExpenseController.java
        │   ├── model/
        │   │   ├── Category.java
        │   │   ├── Expense.java
        │   │   └── User.java
        │   └── service/
        │       ├── ExpenseService.java
        │       └── UserService.java
        └── resources/
            ├── application.properties
            ├── static/
            │   ├── css/style.css
            │   └── js/app.js
            └── templates/
                ├── dashboard.html
                ├── expense-form.html
                ├── expenses.html
                ├── fragments.html
                └── login.html
```

## Features

- Login and logout
- Add, edit, delete, view, and search expenses
- Dashboard with total expenses, recent expenses, and category summary
- JSON file storage using `users.json` and `expenses.json`
- Categories: Food, Travel, Shopping, Education, Entertainment, Other

## Demo Login

```text
Username: student
Password: student123
```

## Run The Project

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

## JSON Structure

`data/users.json`

```json
[
  {
    "id": 1,
    "username": "student",
    "password": "student123",
    "fullName": "BSc Student"
  }
]
```

`data/expenses.json`

```json
[
  {
    "id": 1,
    "title": "Lunch",
    "amount": 180.00,
    "category": "FOOD",
    "date": "2026-06-10",
    "description": "College canteen",
    "username": "student"
  }
]
```

This project intentionally avoids MySQL, H2, JWT, Docker, REST APIs, microservices, and complex Spring Security.
