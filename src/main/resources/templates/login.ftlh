<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Войти в систему</title>
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
                <li><a href="register.html">Зарегистрироваться</a></li>
            </ul>
        </nav>
    </div>
</header>

<section class="form-container">
    <div class="container">
        <h2>Войти в систему</h2>
        <form action="#" method="POST" class="form">
            <label for="email">Электронная почта:</label>
            <input type="email" id="email" name="email" required>

            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>

            <button type="submit" class="btn-primary">Войти</button>
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
