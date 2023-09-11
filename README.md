# restful
RESTfulAPI приложение на Spring Boot

**GET** -> ``/accounts`` -> получение всех учётных записей<br>
**POST** -> ``/accounts`` -> создание учётной записи<br>
**GET** -> ``/accounts/{id}`` -> получение пользователя по id<br>
**POST** -> ``/accounts/{fromId}/transfer/{toId}`` -> перевод денег между учётными записями<br>
**POST** -> ``/accounts/{id}/deposit`` -> пополнение счёта<br>
**GET** -> ``/transactions/{id}`` -> получение транзакции по id<br>
**GET** -> ``/accounts/{id}/transactions`` -> получение всех транзакций учётной записи<br>
**POST** -> ``/accounts/{id}/withdraw`` -> снятие денег<br>
