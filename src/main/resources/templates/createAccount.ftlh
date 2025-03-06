<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Создание нового счета</title>
</head>
<body>
<h1>Создание нового счета</h1>
<form th:action="@{/accounts/create}" th:object="${accountDto}" method="post">
    <label for="accountNumber">Номер счета:</label>
    <input type="text" id="accountNumber" th:field="*{accountNumber}" required><br><br>

    <label for="balance">Баланс:</label>
    <input type="number" id="balance" th:field="*{balance}" required><br><br>

    <button type="submit">Создать счет</button>
</form>
</body>
</html>
