# Family 👪

Приложение для хранения и обработки информации о родственниках. Для разделения наименования сущностей пользователей приложения и людей о которых собирается информация введены сущности User и Human.

## 1. Пользователь. 👤

### 1.1. Регистрация.

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

### 1.2. Авторизация.

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

### 1.3. Получение информации о своем профиле пользователя.

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

### 1.4. Изменение данных своего пользователя. ✏

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

Response:

    {
        "id": int, // Идентификатор пользователя
        "username": string, min 2 char, required // Логин
        "phone": int, 11 dight, start with 79, required // Телефон
        "email": string, as email // Адрес электронной почты
    }

### 1.5. Поиск пользователя по части username, имени, фамилии, e-mail 🔎

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

### 1.6. Удаление самого себя ☠

Фактически запись в БД получает статус DELETED. Запись в этом статусе не будет отображаться для роли ROLE_USER.

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

## 2.Human 🧕 🙎 🤵

### 2.1 Получить полную информацию о человеке

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

### 2.2 Получить полный список людей

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

### 2.3 Получить краткую информацию о человеке

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

### 2.4 Добавить человека

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

### 2.5 Изменить данные о человеке ✏

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

### 2.6 Поиск человека по части имени или фамилии 🔎

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

## 3. Сообщения ✉

## 4. Фото 📷

### 4.1 Добавление фото

Request:

    POST /api/v1/ photos
    Headers:
        Authorization: Bearer_{token}
        Content-Type: multipart/form-data
    Multipart body:
        file: JPEG-файл
        date: yyyy-MM-dd // Дата съемки
        humans_id: string, example 1,2,3,4 // идентификаторы людей на фото через запятую 
Response:

    Body:
    {
        "id": int, // идентификатор фото
        "name": string, // имя файла
        "photoDate": date
        "size": int, // размер файла в байтах
        "height": int
        "width": int
        "humansOnPhoto": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
    }

## 4.2 Получить фото

Request:

    GET /api/v1/photos/{id}/info // id – идентификатор фото
    Headers:
        Authorization:  Bearer_{token}
Response:

    Multipart body: 
        JPEG-файл

## 4.3 Получить инфо о фото

Request:

    GET /api/v1/photos/{id}/info // id – идентификатор фото
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "id": int, // идентификатор фото
        "name": string, // имя файла
        "photoDate": date
        "size": int, // размер файла в байтах
        "height": int // высота в пикселях
        "width": int // ширина в пикселях
        "humansOnPhoto": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
    }

## 4.4 Получить инфо о фото, на которых отмечен человек:

Request:

    GET /api/v1/photos/humans/{id} // id – идентификатор человека
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    [
        {
            "id": int,
            "name": string",
            "photoDate": date
        }
        {
            ...
        }
    ]

### 4.5 Изменить инфо о фото:

Request:

    PUT /api/v1/photos
    Headers:
        Authorization: Bearer_{token}
    Body:
    {
        "id": int, required
        "name": string, not required
        "photoDate": date, not required
        "size": int, not required // размер файла в байтах
        "height": int, not required // высота в пикселях
        "width": int, not required // ширина в пикселях
        "humans_id": [int] // Идентификаторы людей на фото
    }

Response:

    Body:
    {
        "id": int, // идентификатор фото
        "name": string, // имя файла
        "photoDate": date
        "size": int, // размер файла в байтах
        "height": int // высота в пикселях
        "width": int // ширина в пикселях
        "humansOnPhoto": [
            {
                "id": int,
                "surname": string,
                "name": string,
                "patronim": string
            },
            {
                ...
            }
    }

### 4.6 Удалить фото: ☠

Удалить запись может только пользователь, который является автором записи.
Фактически удаления из БД не происходит, а запись получает статус DELETED.
Файл не удаляется. Запись в этом статусе не будет отображаться для роли пользователь.

Request:

    DELETE /api/v1/photos/{id} // id - Идентификатор места рождения
    Headers:
        Authorization: Bearer_{token}

Response:

    {
        "message": "photo with id {id} successfully deleted"
    }

Response, not found:

    Body:
    {
          "info": "Not found photo with id: {id}"
    }

## 5 Место рождения
### 5.1 Получить место рождения:

Request:

    GET /api/v1/birthplaces/{id} // id - Идентификатор места рождения
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "id": int,
        "birthplace": string,
        "guid": string UUID format
    }

### 5.2 Добавить место рождения:

Request:

    POST /api/v1/birthplaces
    Headers:
        Authorization: Bearer_{token}
    Body:
    {
        "birthplace": string, required
        "guid": string UUID format, not required
    }

Response:
    
    Body:
    {
        "id": int,
        "birthplace": string,
        "guid": string UUID format
    }

### 5.3 Изменить место рождения:

Request:

    PUT /api/v1/birthplaces
    Headers:
        Authorization: Bearer_{token}
    Body:
    {
        "id": int, required
        "birthplace": string, required
        "guid": string UUID format, not required
    }

Response:

    Body:
    {
        "id": int,
        "birthplace": string,
        "guid": string UUID format
    }

### 5.4 Найти место рождения:

Request:

    GET /api/v1/birthplaces/{часть названия} // от 3-х до 30 символов
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    [
        {
            "id": int,
            "birthplace": string
        },
        {
            ...
        }
    ]
### 5.5 Удалить место рождения: ☠

Удалить запись может только пользователь, который является автором записи.
Фактически удаления из БД не происходит, а запись получает статус DELETED. 
Запись в этом статусе не будет отображаться для роли пользователь.

Request:

    DELETE /api/v1/birthplaces/{id} // id - Идентификатор места рождения
    Headers:
        Authorization: Bearer_{token}

Response:

    {
        "message": "birthplace with id {id} successfully deleted"
    }

Response, not found:

    Body:
    {
          "info": "Not found birthplace with id: {id}"
    }

## 6 Фамилия
### 6.1 Получить фамилию:

Request:

    GET /api/v1/surnames/{id} // id - Идентификатор фамилии
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "id": int,
        "surname": string, // фамилия
        "surnameAlias1": string, // другой вариант фамилии
        "surnameAlias2": string, // другой вариант фамилии
        "declension": string, Y or N // Флаг, говорящий о том, склоняется фамилия или нет
    }

### 6.2 Добавить фамилию:

Request:

    POST /api/v1/surnames
    Headers:
        Authorization: Bearer_{token}
    Body:
    {
        "surname": string, only char and "-", required // фамилия
        "surnameAlias1": string, only char and "-", not required // другой вариант фамилии
        "surnameAlias2": string, only char and "-", not required // другой вариант фамилии
        "declension": string, only Y or N, required // Флаг, говорящий о том, склоняется фамилия или нет
    }

Response:
    
    Body:
    {
        "id": int,
        "surname": string, // фамилия
        "surnameAlias1": string, // другой вариант фамилии
        "surnameAlias2": string, // другой вариант фамилии
        "declension": string, Y or N // Флаг, говорящий о том, склоняется фамилия или нет
    }

### 6.3 Изменить фамилию:

Request:

    PUT /api/v1/surnames
    Headers:
        Authorization: Bearer_{token}
    Body:
    {
        "id": int, required
        "surname": string, only char and "-" , not required // фамилия
        "surnameAlias1": string, only char and "-", not required // другой вариант фамилии
        "surnameAlias2": string, only char and "-", not required // другой вариант фамилии
        "declension": string, only Y or N, not required // Флаг, говорящий о том, склоняется фамилия или нет
    }

Response:

    Body:
    {
        "id": int,
        "surname": string, // фамилия
        "surnameAlias1": string, // другой вариант фамилии
        "surnameAlias2": string, // другой вариант фамилии
        "declension": string, Y or N // Флаг, говорящий о том, склоняется фамилия или нет
    }

### 6.4 Найти фамилию:
Позволяет пользователю найти фамилию в базе по части фамилии независимо от регистра, транскрипции, смягчающих или твердых знаков.
Request:

    GET /api/v1/surnames/{часть фамилии} // от 3-х до 30 символов
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    [
        {
            "id": int,
            "surname": string, // фамилия
            "surnameAlias1": string, // другой вариант фамилии
            "surnameAlias2": string, // другой вариант фамилии
            "declension": string, Y or N // Флаг, говорящий о том, склоняется фамилия или нет
        },
        {
            ...
        }
    ]
### 6.5 Удалить фамилию: ☠

Удалить запись может только пользователь, который является автором записи.
Фактически удаления из БД не происходит, а запись получает статус DELETED. 
Запись в этом статусе не будет отображаться для роли пользователь.

Request:

    DELETE /api/v1/surnames/{id} // id - Идентификатор фамилии
    Headers:
        Authorization: Bearer_{token}

Response:

    {
        "message": "surname with id {id} successfully deleted"
    }

Response, not found:

    Body:
    {
          "info": "Not found surnames with id: {id}"
    }

# 7 История 📖
### 7.1 Получить историю:

Request:

    GET /api/v1/stories/{id} // id - Идентификатор истории
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "id": int,
        "story": string, // история
		"heroes": [
			{
				"id": int, // Идентификатор георя истории
                "surname": string, // Фамилия
                "name": string, // Имя
                "patronim": // Отчетство
			},
			{
				...
			}
		] 
    }

### 7.2 Получить все истории героя:

Request:

    GET /api/v1/stories/humans/{id} // id - Идентификатор героя
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
	[
		{
			"id": int,
			"story": string, // история
		},
		{
			...
		}
	]

### 7.3 Добавить историю:

Request:

    POST /api/v1/stories
    Headers:
        Authorization: Bearer_{token}
    Body:
    {
        "story": string, required // история
		"heroes_id": int[], required // идентификаторы героев истории

    }

Response:

    Body:
    {
        "id": int,
        "story": string, // история
		"heroes": [
			{
				"id": int, // Идентификатор георя истории
                "surname": string, // Фамилия
                "name": string, // Имя
                "patronim": // Отчетство
			},
			{
				...
			}
		] 
    }

### 7.4 Изменить историю:

Request:

    PUT /api/v1/stories
    Headers:
        Authorization: Bearer_{token}
    Body:
    {
        "story": string, not required // история
		"heroes_id": int[], not required, cannot be empty // идентификаторы героев истории
    }


Response:

    Body:
    {
        "id": int,
        "story": string, // история
		"heroes": [
			{
				"id": int, // Идентификатор георя истории
                "surname": string, // Фамилия
                "name": string, // Имя
                "patronim": // Отчетство
			},
			{
				...
			}
		] 
    }


### 7.5 Удалить историю: ☠

Удалить запись может только пользователь, который является автором записи.
Фактически удаления из БД не происходит, а запись получает статус DELETED.
Запись в этом статусе не будет отображаться для роли пользователь.

Request:

    DELETE /api/v1/stories/{id} // id - Идентификатор истории
    Headers:
        Authorization: Bearer_{token}

Response:

    {
        "message": "story with id {id} successfully deleted"
    }

Response, not found:

    Body:
    {
          "info": "Not found stories with id: {id}"
    }

## 8. Администратор 😎

### 8.1 Получить полную информацию о пользователе:

Request:

    GET /api/v1/admin/users/{id} // id - идентификатор пользователя
    Headers:
        Authorization: Bearer_{token}

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

### 8.2 Изменить данные профиля пользователя ✏

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
### 8.3 Удалить пользователя: ☠

Администратор может удалить запись из БД.

Request:

    DELETE /api/v1/admin/users/{id}
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "message": "User successfully deleted"
    }

## 8.4 Получить полную информацию о человеке:

Request:

    GET /api/v1/admin/humans/{id}
    Headers:
        Authorization: Bearer_{token}
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


## 8.5 Изменить данные о человеке ✏

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

### 8.6 Удалить человека ☠

Администратор может удалить запись из БД.

Request:

    DELETE /api/v1/admin/humans/{id} // id - идентификатор человека
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "message": "Human successfully deleted"
    }

## 8.7 Получить полную информацию о месте рождения:

Request:

    GET /api/v1/admin/birthplaces/{id} // id - идентификатор места рождения
    Headers:
    Authorization: Bearer_{token}

Response:

    Body:
    {
        "id": int,
        "birthplace": string,
        "guid": string UUID format
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

## 8.8 Изменить информацию о месте рождения:

Администратор имеет расширенные права на изменение данных о месте рождения.
Дополнительно он может изменить статус и автора записи.

Request:

    PUT /api/v1/admin/birthplaces/{id} // id - идентификатор места рождения
    Headers:
        Authorization: Bearer_{token}
    Body:
    {
        "id": int, required
        "birthplace": string, not required
        "guid": string UUID format, not required
        "author_id": int,, not required
        "status": string, from in status-table on last page, not required
    }
Response:

    Body:
    {
        "id": int,
        "birthplace": string,
        "guid": string UUID format
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

## 8.9 Удалить информацию о месте рождения из БД:

Администратор может удалить запись из БД.

Request:

    DELETE /api/v1/admin/birthplaces/{id} // id - идентификатор места рождения
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "message": "Birthplace with id {id} successfully deleted from database"
    }

## 8.10 Получить полную информацию о фамилии:

Request:

    GET /api/v1/admin/surnames/{id} // id - идентификатор фамилии
    Headers:
    Authorization: Bearer_{token}

Response:

    Body:
    {
        "id": int,
        "surname": string, // фамилия
        "surnameAlias1": string, // другой вариант фамилии
        "surnameAlias2": string, // другой вариант фамилии
        "declension": string, Y or N // Флаг, говорящий о том, склоняется фамилия или нет
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

## 8.11 Изменить информацию о фамилии:

Администратор имеет расширенные права на изменение данных о месте рождения.
Дополнительно он может изменить статус и автора записи.

Request:

    PUT /api/v1/admin/surnames/{id} // id - идентификатор фамилии
    Headers:
        Authorization: Bearer_{token}
    Body:
    {
        "id": int, required
        "surname": string, only char and "-" , not required // фамилия
        "surnameAlias1": string, only char and "-", not required // другой вариант фамилии
        "surnameAlias2": string, only char and "-", not required // другой вариант фамилии
        "declension": string, only Y or N, not required // Флаг, говорящий о том, склоняется фамилия или нет
        "author_id": int,, not required
        "status": string, from in status-table on last page, not required
    }
Response:

    Body:
    {
        "id": int,
        "surname": string, // фамилия
        "surnameAlias1": string, // другой вариант фамилии
        "surnameAlias2": string, // другой вариант фамилии
        "declension": string, Y or N // Флаг, говорящий о том, склоняется фамилия или нет
        
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

## 8.12 Удалить информацию о фамилии из БД:

Администратор может удалить запись из БД.

Request:

    DELETE /api/v1/admin/surnames/{id} // id - идентификатор фамилии
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "message": "Surname with id {id} successfully deleted from database"
    }

## 8.13 Получить полную информацию о фото:

Request:

    GET /api/v1/admin/photos/{id} // id - идентификатор фамилии
    Headers:
    Authorization: Bearer_{token}

Response:

    Body:
    {
        "id": int, // идентификатор фото
        "name": string, // имя файла
        "photoDate": date
        "size": int, // размер файла в байтах
        "height": int // высота в пикселях
        "width": int // ширина в пикселях
        "humansOnPhoto": [
        {
        "id": int,
        "surname": string,
        "name": string,
        "patronim": string
        },
        {
        ...
        }
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


## 8.14 Изменить информацию о фото:

Администратор имеет расширенные права на изменение данных о месте рождения.
Дополнительно он может изменить статус и автора записи.

Request:

    PUT /api/v1/admin/photos/{id} // id - идентификатор фамилии
    Headers:
        Authorization: Bearer_{token}
    Body:

Response:

    Body:
    {
        "id": int, // идентификатор фото
        "name": string, // имя файла
        "photoDate": date
        "size": int, // размер файла в байтах
        "height": int // высота в пикселях
        "width": int // ширина в пикселях
        "humansOnPhoto": [
        {
        "id": int,
        "surname": string,
        "name": string,
        "patronim": string
        },
        {
        ...
        }
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

## 8.15 Удалить фото из хранилища и запись из БД:

Администратор может удалить запись из БД.

Request:

    DELETE /api/v1/admin/photos/{id} // id - идентификатор фамилии
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "message": "Photo with id {id} successfully deleted from database"
    }
## 8.16 Получить полную информацию об истории:

Request:

    GET /api/v1/admin/stories/{id} // id - идентификатор истории
    Headers:
    Authorization: Bearer_{token}

Response:

    Body:
    {
        "id": int,
        "story": string, // история
		"heroes": [
			{
				"id": int, // Идентификатор георя истории
                "surname": string, // Фамилия
                "name": string, // Имя
                "patronim": // Отчетство
			},
			{
				...
			}
		] 
        "declension": string, Y or N // Флаг, говорящий о том, склоняется история или нет
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

## 8.17 Изменить историю:

Администратор имеет расширенные права на изменение данных о месте рождения.
Дополнительно он может изменить статус и автора записи.

Request:

    PUT /api/v1/admin/stories/{id} // id - идентификатор истории
    Headers:
        Authorization: Bearer_{token}
    Body:
    {
        "id": int, required
        "story": string, only char and "-" , not required // история
		"heroes_id": int[] // Идентификаторы героев истории
        "author_id": int,, not required
        "status": string, from in status-table on last page, not required
    }
Response:

    Body:
    {
        "id": int,
        "story": string, // история
		"heroes": [
					{
						"id": int, // Идентификатор георя истории
						"surname": string, // Фамилия
						"name": string, // Имя
						"patronim": // Отчетство
					},
					{
						...
					}
				] 
        
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

## 8.18 Удалить историю из БД:

Администратор может удалить запись из БД.

Request:

    DELETE /api/v1/admin/stories/{id} // id - идентификатор истории
    Headers:
        Authorization: Bearer_{token}
Response:

    Body:
    {
        "message": "Story with id {id} successfully deleted from database"
    }

### Roles:

| ROLE | DESCRIPTION |
|----- | ------------|
| ROLE_ADMIN | admin |
| ROLE_USER | user |

### Status:

|STATUS|DESCRIPTION|
|----- | ----------|
| ACTIVE |
| NOT_ACTIVE |
| DELETED | 
| READ | 
| NOT_READ |
