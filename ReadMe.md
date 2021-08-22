#Family 👪
Приложение для хранения и обработки информации о родственниках. Для разделения наименования сущностей пользователей приложения и людей о которых собирается информация введены сущности User и Human.
##1. Пользователь. 👤
###1.1. Регистрация.
Request:

    POST /api/v1/register
    Headers:
        Content-Type: application/json
    Body:
    {
        "username": string, min 2 char, required // Логин
        "phone": int, 11 dight, start with 79, required // Телефон
        "email": string, as email // Адрес электронной почты
        "password": string, min 3 char // Пароль
    }

Response:

    Body:
    {
        "id": int, // Идентификатор пользователя
        "username": string, min 2 char, required // Логин
        "phone": int, 11 dight, start with 79, required // Телефон
        "email": string, as email // Адрес электронной почты
    }

###1.2. Авторизация.
Request:

    POST /api/v1/auth/login
    Headers:
        Content-Type: application/json
    Body:
    {
        "username": string, // Логин
        "password": string // Пароль
    }

Response:

    Body:
    {
        "username": string,
        "token": string
    }

###1.3. Получение информации о своем профиле пользователя.
Request:

    GET /api/v1/users/userinfo
    Headers:
        Authorization: Bearer_{token}

Response:

    Body:
    {
        "id": int, // Идентификатор пользователя
        "username": string, min 2 char, required // Логин
        "phone": int, 11 dight, start with 79, required // Телефон
        "email": string, as email // Адрес электронной почты
    }

###1.4. Изменение данных пользователя. ✏
Request:

    PUT /api/v1/users/change_info
    Headers:
        Content-Type: application/json
        Authorization: Bearer_{token}
    Body:
    {
        "username": string, min 2 char, required // Логин
        "phone": int, 11 dight, start with 79, required // Телефон
        "email": string, as email // Адрес электронной почты
        "password": string, min 3 char // Пароль
    }
Request:

    {
        "id": int, // Идентификатор пользователя
        "username": string, min 2 char, required // Логин
        "phone": int, 11 dight, start with 79, required // Телефон
        "email": string, as email // Адрес электронной почты
    }

###1.5. Поиск пользователя по части username, имени, фамилии, e-mail 🔎
Поиск зарегистрированных пользователей по части имени, за исключением себя.

Request:

    GET /api/v1/users/search_people/{часть имени} // от 3 до 15 символов
    Headers:
    Authorization: Bearer_{token}

Response:

    Body:
    [
        {
            "username": string, min 2 char, required // Логин
            "phone": int, 11 dight, start with 79, required // Телефон
            "email": string, as email // Адрес электронной почты
            "password": string, min 3 char // Пароль
        },
        {
            ...
        }
    ]

###1.6. Удаление самого себя ☠
Request:

    DELETE /api/v1/users
    Headers:
        Authorization: Bearer_{token}
Response:

    STATUS: 200
    Body: 
    {
        "message": "User successfully deleted"
    }

##2.Human 🧕 🙎 🤵
###2.1 Получить полную информацию о человеке
Request:

    GET /api/v1/humans/full_info/{id} //id - идентификатор человека
    Headers
    Authorization:  Bearer_{token}
Response:

    STATUS: 200
    Body:
    {
        "id": int,
        "surname": string,
        "name": string,
        "patronim": string,
        "birthdate": date, as dd.MM.YYYY
        "deathdate":  date, as dd.MM.YYYY
        "birthplace": string,
        "gender": char, 'M' or 'W'
        "parents": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
        ],
        "children": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
        ]
    }

###2.2 Получить полный список людей
Request:

    GET /api/v1/humans
    Headers
        Authorization:  Bearer_{token}
    Body:
    [
        {
            "id": int,
            "surname": string,
            "name": string,
            "patronim": string,
            "birthdate": date, as dd.MM.YYYY
            "deathdate":  date, as dd.MM.YYYY
            "birthplace": string,
            "gender": string, "M" or "W"
        }, 
        {
            ...
        }
    ]

###2.3 Получить краткую информацию о человеке
Request:

    GET /api/v1/humans/{id}
    Headers:
        Authorization:  Bearer_{token}
Response:

    Body:
    {
        "id": int,
        "surname": string,
        "name": string,
        "patronim": string,
        "birthdate": date, as dd.MM.YYYY
        "deathdate":  date, as dd.MM.YYYY
        "birthplace": string,
        "gender": string, "M" or "W"
    }

###2.4 Добавить человека
Request:

    POST /api/v1/humans/{id}
    Headers:
        Authorization:  Bearer_{token}
    Body:
    {
        "id": int, required
        "name": string, not required
        "patronim": string, not required
        "surname_id": int, not required
        "birthdate": date, as "YYYY-MM-DD", not required
        "deathdate": date, as "YYYY-MM-DD", not required
        "birthplace_id": int, not required
        "gender": string, "M" or "W", required
        "parents_id": [int], not required
        "children_id": [int], not required
        "previous_surnames_id": [int], not required
    }
Response:

    Body:
    {
        "id": int,
        "surname": string,
        "name": string,
        "patronim": string,
        "birthdate": date, as dd.MM.YYYY
        "deathdate":  date, as dd.MM.YYYY
        "birthplace": string,
        "gender": char, 'M' or 'W'
        "parents": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
        ],
        "children": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
        ]
    }

###2.5 Изменить данные о человеке ✏
Данные в записи о человеке может изменить только автор записи.
Request:

    PUT /api/v1/admin/humans
    Headers:
        Authorization: Bearer_{token}
        Conent-type: application/json
    Body:
    {
        "id": int, required
        "name": string, not required
        "patronim": string, not required
        "surname_id": int, not required
        "birthdate": date, as "YYYY-MM-DD", not required
        "deathdate": date, as "YYYY-MM-DD", not required
        "birthplace_id": int, not required
        "gender": string, "M" or "W", required
        "parents_id": [int], not required
        "children_id": [int], not required
        "previous_surnames_id": [int], not required
    }

Response:

    Body:
    {
        "id": int,
        "surname": string,
        "name": string,
        "patronim": string,
        "birthdate": date, as dd.MM.YYYY
        "deathdate":  date, as dd.MM.YYYY
        "birthplace": string,
        "gender": char, 'M' or 'W'
        "parents": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
        ],
        "children": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
        ]
    }

###2.6 Поиск человека по части имени или фамилии 🔎
Request:

    POST /api/v1/humans/search_human/{partOfName}
    Headers:
        Authorization:  Bearer_{token}

Response:

    Body:
    [
        {
            "id": int,
            "name": string,
            "patronim": string,
            "surname": string,
            "birthdate": date, as dd.MM.YYYY
            "deathdate": null,
            "birthplace": string,
            "gender": char, 'M' or 'W'
        },
        {
            "id": ...
        }
    ]

##3. Сообщения ✉
##4. Фото 📷
###4.1 Добавление фото
Request:

    POST /api/v1/ photos
    Headers:
        Authorization: Bearer_{token}
        Content-Type: multipart/form-data
    Multipart body:
        file: JPEG-файл
        date: yyyy-MM-dd // Дата съемки
Response:

    Body:
    {
        "id": int, // идентификатор фото
        "name": string, // имя файла
        "size": int, // размер файла в байтах
        "imageResolution": string // разрешение файла
    }

##4.2 Получить фото
Request:

    GET /api/v1/photos/{id}/info // id – идентификатор фото
    Headers:
        Authorization:  Bearer_{token}
Response:

    Multipart body: 
        JPEG-файл
##4.3 Получить инфо о фото
Request:

    GET /api/v1/photos/{id}/info // id – идентификатор фото
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "id": int, // идентификатор фото
        "name": string, // имя файла
        "size": int, // размер файла в байтах
        "imageResolution": string // разрешение файла
    }

##5. Администратор 😎
###5.1 Удалить пользователя ☠
Request:

    DELETE /api/v1/admin/users/{id}
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "message": "User successfully deleted"
    }

###5.2 Удалить человека ☠
Request:

    DELETE /api/v1/admin/humans/{id} // id - идентификатор человека
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "message": "Human successfully deleted"
    }

###5.3 Изменить данные профиля пользователя ✏
Администратор может изменить данные профиля пользователя, включая пароль, роль и статус. Таким образом администратор может добавлять новых администраторов.

Request:

    PUT /api/v1/admin/users
    Headers:
        Authorization: Bearer_{token}
        Conent-type: application/json
    Body:
    {
        "id": int, required
        "username": string, min 2 char, not required
        "phone": int, 11 dight, start with 79, not required
        "email": string, email pattern, not required
        "roles": [string], from in role-table on last page, not required
        "password" string, min 3 char, not required
        "status": string, from in status-table on last page, not required
    }

Response:

    Body:
    {
        "id": int,
        "username": string, 
        "phone": int,
        "email": string,
        "roles": [string], from in role-table on last page
        "password" string, in code xBase64
        "status": string, from in status-table on last page 
        "created": date_time as YYYY-MM-DD'T'HH:mm:ss.sss+HH:mm
        "updated": date_time as YYYY-MM-DD'T'HH:mm:ss.sss+HH:mm
    }

## Изменить данные о человеке ✏
Администратор имеет расширенные права на изменение данных о человеке. 
Дополнительно он может изменить статус и автора записи. 

Request:

    PUT /api/v1/admin/humans
    Headers:
        Authorization: Bearer_{token}
        Conent-type: application/json
    Body:
    {
        "id": int, required
        "name": string, not required
        "patronim": string, not required
        "surname_id": int, not required
        "birthdate": date, as "YYYY-MM-DD", not required
        "deathdate": date, as "YYYY-MM-DD", not required
        "birthplace_id": int, not required
        "gender": string, "M" or "W", required
        "parents_id": [int], not required
        "author_id": int, not required
        "children_id": [int], not required
        "previous_surnames_id": [int], not required
        "status": string, from in status-table on last page, not required
    }
    
Response:

    Body:
    {
        "id": int,
        "surname": string,
        "name": string,
        "patronim": string,
        "birthdate": date, as dd.MM.YYYY
        "deathdate":  date, as dd.MM.YYYY
        "birthplace": string,
        "gender": char, 'M' or 'W'
        "parents": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
        ],
        "children": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
        ],
        "author": {
            "id": int,
            "username": string,
            "phone": int, 11 dight, start with 79
            "email": string
        },
        "status": string, from in status-table on last page 
        "created": date_time as YYYY-MM-DD'T'HH:mm:ss.sss+HH:mm
        "updated": date_time as YYYY-MM-DD'T'HH:mm:ss.sss+HH:mm
    }

###Roles:
| ROLE | DESCRIPTION |
|----- | ------------|
| ROLE_ADMIN | admin |
| ROLE_USER | user |

###Status:
|STATUS|DESCRIPTION|
|----- | ----------|
| ACTIVE |
| NOT_ACTIVE |
| DELETED | 
| READ | 
| NOT_READ |