<!DOCTYPE html>
<html lang="ru" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Банк</title>
    <link rel="stylesheet" href="/css/styles.css">
</head>
<body>

<header class="header">
    <div class="container">
        <h1 class="logo">Банк</h1>
        <nav>
            <ul class="nav-list">
                <li><a href="/accounts">Счета</a></li>
                <li><a href="/accounts/create">Создать счет</a></li>
                <li><a href="/accounts/login">Войти</a></li>
                <li><a href="/accounts/register">Зарегистрироваться</a></li>
            </ul>
        </nav>
    </div>
</header>

<section class="hero">
    <div class="container">
        <h2 th:text="${message}">Добро пожаловать в наш банк!</h2>
        <p>Мы предлагаем лучшие условия для вашего финансового благополучия.</p>
        <a href="/accounts/create" class="btn-primary">Открыть счет</a>
    </div>
</section>

<footer class="footer">
    <div class="container">
        <p>&copy; 2025 Банк. Все права защищены.</p>
    </div>
</footer>

</body>
</html>
