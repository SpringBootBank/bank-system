<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Счета</title>
</head>
<body>
<h1>Список всех счетов</h1>
<a href="/accounts/create">Создать новый счет</a>
<table>
    <thead>
    <tr>
        <th>Номер счета</th>
        <th>Баланс</th>
        <th>Действия</th>
    </tr>
    </thead>
    <tbody>
    <tr th:each="account : ${accounts}">
        <td th:text="${account.accountNumber}"></td>
        <td th:text="${account.balance}"></td>
        <td>
            <a th:href="@{/accounts/{id}(id=${account.id})}">Подробнее</a> |
            <a th:href="@{/accounts/{id}/edit(id=${account.id})}">Редактировать</a> |
            <a th:href="@{/accounts/{id}/delete(id=${account.id})}">Удалить</a>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>
