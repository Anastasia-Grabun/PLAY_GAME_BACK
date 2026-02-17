# Отчёт аудита безопасности (Spring Boot + Spring Security)

**Дата:** 2025-02-15  
**Роль:** Senior Java Security Engineer  
**Область:** IDOR, SecurityContext, @PreAuthorize, валидация, инкапсуляция данных.

---

## 1. Сводная таблица уязвимостей

| Файл и метод | Тип проблемы | Уровень риска | Рекомендация по исправлению |
|--------------|--------------|---------------|-----------------------------|
| **BucketController.getBucketById(Long id)** | IDOR | **High** | Эндпоинт возвращает любой bucket по `id`. В сервисе нет проверки, что `bucket.account.id` совпадает с текущим пользователем. **Исправление:** использовать `@AuthenticationPrincipal` и в сервисе возвращать bucket только для `accountId` из токена, либо принимать только свой bucket (например, через `/my-bucket`). |
| **BucketController.addGameToBucket(bucketId, gameId)** | IDOR | **High** | Любой USER может добавить игру в **чужой** bucket: проверки владения bucket в `BucketServiceImpl.addGameToBucket` нет. **Исправление:** передавать в сервис `accountId` из `@AuthenticationPrincipal`/AuthService и проверять `bucket.getAccount().getId().equals(accountId)`. |
| **BucketController.removeGameFromBucket(bucketId, gameId)** | IDOR | **High** | Аналогично — удаление из чужого bucket. **Исправление:** та же проверка владения bucket по текущему пользователю. |
| **BucketController.moveGamesToBuyList(accountId, List)** | IDOR | **High** | `accountId` приходит с клиента. USER может перенести игры в buylist **другого** аккаунта. В `BucketServiceImpl.moveGamesToBuyList` проверки владения нет. **Исправление:** не принимать `accountId` из пути; брать из `@AuthenticationPrincipal`/AuthService и использовать только его. |
| **AccountsController.getAccountById(Long id)** | IDOR / избыточный доступ | **Medium** | Любой USER/DEVELOPER может запросить любой аккаунт по id (включая email, баланс и т.д.). **Исправление:** для роли USER разрешать доступ только к своему id (сравнивать id с accountId из SecurityContext). Для ADMIN/DEVELOPER — оставить как есть или разделить эндпоинты. |
| **AccountsController.deleteAccount(Long id)** | IDOR | **High** | USER может удалить **чужой** аккаунт: в `AccountServiceImpl.deleteById` проверки владения нет. **Исправление:** для USER разрешать удаление только своего аккаунта (id == accountId из токена); для ADMIN — без ограничений. |
| **AccountsController.getBalance(Long accountId)** | IDOR | **High** | USER может запросить баланс любого аккаунта по `accountId`. **Исправление:** для USER возвращать баланс только своего аккаунта — брать accountId из `@AuthenticationPrincipal`. |
| **AccountsController.updateAccount(AccountUpdateDto)** | IDOR | **High** | В DTO передаётся `id`. Любой USER может вызвать update с `id` другого пользователя и изменить username/email. В `AccountServiceImpl.update` проверки «текущий пользователь == accountDto.getId()» нет. **Исправление:** не принимать id из DTO с клиента; брать id текущего пользователя из SecurityContext и подставлять в логику обновления. Добавить в сервис проверку владения. |
| **AccountsController.updateAccountBalance(accountId, newBalance)** | Контекст | **Low** | Доступ только для ADMIN — IDOR по сути нет. Рекомендация: при необходимости логировать кто и кому менял баланс. |
| **GameController.addGameToBucket(bucketId, gameId)** | IDOR | **High** | Дублирует логику BucketController — добавление в чужой bucket. **Исправление:** проверять владение bucket текущим пользователем (как в BucketController). |
| **GameController.removeGameFromBucket(bucketId, gameId)** | IDOR | **High** | Аналогично — удаление из чужого bucket. **Исправление:** та же проверка владения. |
| **GameController.addRating(gameId, accountId, rating)** | IDOR | **High** | `accountId` приходит с клиента. USER может добавить рейтинг **от имени другого** пользователя. **Исправление:** не принимать accountId из запроса; брать из `@AuthenticationPrincipal` и передавать в сервис только его. |
| **TopUpController.getTopUpById(Long id)** | IDOR | **High** | Любой USER может запросить любой TopUp по id (видны суммы и привязка к аккаунту). В сервисе проверки «topUp принадлежит текущему пользователю» нет. **Исправление:** в сервисе проверять `topUp.getAccount().getId().equals(currentAccountId)`; иначе 403. |
| **TopUpController.getTopUpsByAccountId(accountId)** | IDOR | **High** | USER может запросить историю пополнений **любого** аккаунта. **Исправление:** для USER разрешать только свой accountId (из SecurityContext). |
| **TopUpController.deposit(accountId, amount)** | IDOR | **High** | USER может сделать deposit на **чужой** счёт (указать любой accountId). **Исправление:** принимать только текущего пользователя — accountId из токена/`@AuthenticationPrincipal`. |
| **TopUpController.transferFunds(senderAccountId, receiverAccountId, amount)** | IDOR | **High** | USER может указать произвольного отправителя. Можно инициировать перевод **с чужого счёта** на свой. **Исправление:** senderAccountId должен быть только текущий пользователь; проверять в сервисе и возвращать 403 при несовпадении. |
| **TransactionController.getTransactionById(Long id)** | IDOR | **High** | Любой USER может запросить любую транзакцию по id. **Исправление:** в сервисе проверять `transaction.getAccount().getId().equals(currentAccountId)`; иначе 403. |
| **TransactionController.getAllTransactionsByAccountId(Long id)** | IDOR | **High** | USER может запросить список транзакций любого аккаунта. **Исправление:** для USER разрешать только свой id (из SecurityContext). |
| **PurchaseController.getPurchaseById(Long id)** | IDOR | **High** | Любой USER может запросить любую покупку по id. **Исправление:** проверять `purchase.getOwner().getId().equals(currentAccountId)`; иначе 403. |
| **PurchaseController.getPurchasesByOwnerId(Long ownerId)** | IDOR | **High** | USER может запросить покупки любого владельца. **Исправление:** для USER разрешать только свой ownerId (из SecurityContext). |
| **CredentialController.getCredentialByLogin(String login)** | IDOR / избыточный доступ | **Medium** | USER может запросить credential любого пользователя по login (в т.ч. хэш пароля/данные для сброса, если они в DTO). **Исправление:** для USER возвращать только свой credential (login из SecurityContext). |
| **CredentialController.updateCredential(CredentialUpdateDto)** | IDOR | **Critical** | В DTO передаётся `login`. Сервис обновляет credential по этому login без проверки «текущий пользователь == этот login». Любой USER может **сменить пароль любому** пользователю, передав его login. **Исправление:** не принимать login из DTO с клиента; брать login текущего пользователя из SecurityContext и обновлять только свой credential. |
| **CredentialController.updateCredential(CredentialUpdateDto)** | Missing Validation | **Medium** | Метод с `@RequestBody` без `@Valid`. **Исправление:** добавить `@Valid @RequestBody CredentialUpdateDto`. |
| **GameController.createGame(GameRequestDto)** | Missing Validation | **Medium** | `@RequestBody` без `@Valid`. **Исправление:** добавить `@Valid @RequestBody GameRequestDto`. |
| **GameController.updateGame(GameUpdateDto)** | Missing Validation | **Medium** | `@RequestBody` без `@Valid`. **Исправление:** добавить `@Valid @RequestBody GameUpdateDto`. |
| **GenreController.createGenre(GenreRequestDto)** | Missing Validation | **Medium** | `@RequestBody` без `@Valid`. **Исправление:** добавить `@Valid @RequestBody GenreRequestDto`. |
| **GenreController.updateGenre(GenreUpdateDto)** | Missing Validation | **Medium** | `@RequestBody` без `@Valid`. **Исправление:** добавить `@Valid @RequestBody GenreUpdateDto`. |
| **AccountsController.updateAccountBalance(accountId, newBalance)** | Missing Validation | **Low** | `@RequestBody BigDecimal` — при необходимости ограничить диапазон (например, @DecimalMin/@DecimalMax) и добавить @Valid если обернуть в DTO. |
| **CredentialController.addRoleToCredential(CredentialToAddRoleRequestDto)** | Missing Validation | **Low** | `@RequestBody` без `@Valid`. **Исправление:** добавить `@Valid` для консистентности и валидации полей. |
| **AccountsController.getAccountById / getAccountByUsername** | Use SecurityContext | **Low** | ID можно не передавать с клиента для «своего» профиля. **Рекомендация:** эндпоинт вида `GET /me` с `@AuthenticationPrincipal`, возвращающий данные текущего пользователя. |
| **BucketController.getMyBucket** | Use SecurityContext | **Low** | Уже использует authHeader. **Рекомендация:** заменить на `@AuthenticationPrincipal CustomUserDetails` и передавать в сервис accountId из него — единообразие и меньше зависимость от заголовка. |
| **Recommendations / Favourites (AccountsController)** | Use SecurityContext | **Low** | Уже используют authHeader. **Рекомендация:** перейти на `@AuthenticationPrincipal` для единообразия. |

**Инкапсуляция данных (Entity vs DTO):** по коду контроллеров везде возвращаются типы `*ResponseDto` / `*ShortcutResponseDto`. Прямого возврата Entity в контроллерах не обнаружено — **замечаний нет**.

---

## 2. Эндпоинты без проверки владения (кратко)

- Все методы, где в URL или в теле передаётся **id пользователя/владельца/аккаунта** (accountId, ownerId, bucketId без проверки владения, senderAccountId, login для update), должны либо не принимать этот id с клиента (брать из SecurityContext), либо в сервисе сравнивать с текущим пользователем и при несовпадении возвращать 403.

---

## 3. План рефакторинга: @AuthenticationPrincipal и проверка владения

### 3.1. Расширение контракта текущего пользователя

- **CustomUserDetails** уже хранит `Credential` и через него можно получить `Account` (и id). Убедиться, что в JWT/фильтре в SecurityContext кладётся именно `CustomUserDetails` с подгруженным `credential.account`.
- Добавить в **CustomUserDetails** метод вида `getAccountId()` (или использовать `getCredential().getAccount().getId()`), чтобы не таскать accountId по всему коду из заголовка.

Пример (если ещё нет):

```java
// CustomUserDetails
public Long getAccountId() {
    return credential.getAccount() != null ? credential.getAccount().getId() : null;
}
```

Убедиться, что при загрузке UserDetails account подгружается (например, через `CredentialRepository` с join fetch по account), чтобы не было LazyInitializationException.

### 3.2. Контроллеры: замена id с клиента на @AuthenticationPrincipal

- **Единая точка истины:** для операций «текущий пользователь» не принимать accountId/ownerId/bucketId (или login) с клиента там, где это возможно. Использовать параметр метода `@AuthenticationPrincipal CustomUserDetails user` и вызывать `user.getAccountId()` / `user.getUsername()` (login).

Примеры целей:

| Текущий эндпоинт | Целевой вид (идея) |
|------------------|---------------------|
| `GET /accounts/{id}` | Для «своего» профиля: `GET /accounts/me` с `@AuthenticationPrincipal`, возвращать account по user.getAccountId(). |
| `GET /accounts/{id}/balance` | `GET /accounts/me/balance` с user.getAccountId(). |
| `PUT /accounts` (updateAccount) | В сервис не передавать id из DTO; в контроллере/сервисе подставлять user.getAccountId(). |
| `DELETE /accounts/{id}` | В сервисе проверять: если роль USER — разрешать только id == user.getAccountId(). |
| `PUT /buckets/accounts/{accountId}/games/move-to-buylist` | Заменить на `PUT /buckets/me/games/move-to-buylist`, accountId = user.getAccountId(). |
| `POST /games/add-rating?gameId=&accountId=&rating=` | Убрать accountId из запроса; в контроллере передавать user.getAccountId() в сервис. |
| `POST /topups/deposit?accountId=&amount=` | accountId только из user.getAccountId(). |
| `PUT /topups/{sender}/transfer/{receiver}` | В сервисе требовать sender == user.getAccountId(); иначе 403. |
| `GET /topups/account/{accountId}` | Для USER разрешать только accountId == user.getAccountId(). |
| `GET /transactions/account/{id}` | Для USER разрешать только id == user.getAccountId(). |
| `GET /purchases/owner/{ownerId}` | Для USER разрешать только ownerId == user.getAccountId(). |
| `PUT /credentials` (updateCredential) | Не принимать login из DTO; обновлять только credential с login == user.getUsername(). |

### 3.3. Сервисы: явная проверка владения

- Для операций с ресурсом (bucket, transaction, purchase, topup, account) добавить в сервис параметр `Long currentAccountId` (или получать его внутри через сервис-хелпер из SecurityContext, если решите централизовать).
- Перед выполнением действия проверять:
  - **Bucket:** `bucket.getAccount().getId().equals(currentAccountId)`.
  - **Transaction:** `transaction.getAccount().getId().equals(currentAccountId)`.
  - **Purchase:** `purchase.getOwner().getId().equals(currentAccountId)`.
  - **TopUp:** `topUp.getAccount().getId().equals(currentAccountId)`.
  - **Account (update/delete/balance):** `accountId.equals(currentAccountId)` (для USER).
- При несовпадении — выбрасывать исключение (например, `AccessDeniedException`), которое глобальный обработчик преобразует в 403.

### 3.4. Валидация входящих данных

- Для всех методов с `@RequestBody` добавить `@Valid` и при необходимости группы валидации.
- Проверить DTO на наличие нужных ограничений (@NotNull, @Size, @Email, @Min/@Max для числовых полей и т.д.), особенно для CredentialUpdateDto, GameRequestDto, GameUpdateDto, GenreRequestDto, GenreUpdateDto, CredentialToAddRoleRequestDto.

### 3.5. Порядок внедрения (по приоритету)

1. **Критично:** CredentialController.updateCredential — убрать возможность менять чужой login/пароль; использовать только текущий пользователь из SecurityContext; добавить @Valid.
2. **Высокий приоритет:** TopUp (deposit, transfer), Accounts (update, delete, balance), Bucket (getById, add/remove game, moveGamesToBuyList), Game (addRating, add/remove from bucket), Transaction и Purchase (getById и по accountId/ownerId) — везде ввести проверку владения и/или подстановку currentAccountId из @AuthenticationPrincipal.
3. **Средний приоритет:** Остальные IDOR (getAccountById, getCredentialByLogin и т.д.) и добавление @Valid для всех @RequestBody.
4. **Низкий приоритет:** Замена передачи authHeader на @AuthenticationPrincipal там, где ещё используется (recommendations, favourites, getMyBucket).

После внедрения стоит пройтись по списку эндпоинтов с PathVariable/RequestParam, где фигурирует id пользователя/владельца, и убедиться, что каждый либо не принимает этот id с клиента (берётся из SecurityContext), либо в сервисе есть явная проверка владения с 403 при нарушении.
