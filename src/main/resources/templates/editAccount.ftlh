<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Редактировать счет</title>
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
            </ul>
        </nav>
    </div>
</header>

<section class="form-container">
    <div class="container">
        <h2>Редактировать счет</h2>
        <form action="/accounts/{id}/edit" method="post" class="form" th:object="${accountDto}">
            <label for="accountNumber">Номер счета:</label>
            <input type="text" id="accountNumber" th:field="*{accountNumber}" required>

            <label for="balance">Баланс:</label>
            <input type="number" id="balance" th:field="*{balance}" required>

            <button type="submit" class="btn-primary">Сохранить изменения</button>
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
