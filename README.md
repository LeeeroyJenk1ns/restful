# restful
RESTfulAPI приложение на Spring Boot

GET -> /accounts -> получение всех учётных записей
POST -> /accounts -> создание учётной записи
GET -> /accounts/{id} -> получение пользователя по id
POST -> /accounts/{fromId}/transfer/{toId} -> перевод денег между учётными записями
POST -> /accounts/{id}/deposit -> пополнение счёта
GET -> /transactions/{id} -> получение транзакции по id
GET -> /accounts/{id}/transactions -> получение всех транзакций учётной записи
POST -> /accounts/{id}/withdraw -> снятие денег
