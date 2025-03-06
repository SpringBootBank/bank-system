<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Регистрация</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<header class="header">
    <div class="container">
        <h1 class="logo">Банк</h1>
        <nav>
            <ul class="nav-list">
                <li><a href="#">Главная</a></li>
                <li><a href="#">Услуги</a></li>
                <li><a href="#">Контакты</a></li>
                <li><a href="login.ftlh">Войти</a></li>
            </ul>
        </nav>
    </div>
</header>

<section class="form-container">
    <div class="container">
        <h2>Регистрация</h2>
        <form action="#" method="POST" class="form">
            <label for="username">Имя пользователя:</label>
            <input type="text" id="username" name="username" required>

            <label for="email">Электронная почта:</label>
            <input type="email" id="email" name="email" required>

            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>

            <button type="submit" class="btn-primary">Зарегистрироваться</button>
        </form>
    </div>
</section>

<footer class="footer">
    <div class="container">
        <p>&copy; 2025 Банк. Все права защищены.</p>
    </div>
</footer>
</body>
</html>
