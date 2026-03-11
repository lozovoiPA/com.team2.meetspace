# Meetspace
Мобильное приложение для создания, планирования и ведения видео-конференций. 

Разработано командой 2 - студентами гр. 932201: 
- Николаев А.Н.
- Николаева М.К.
- Лозовой П.А.
- Гомбоев Ч.С.

## Стек разработки
Язык разработки: Kotlin, среда разработки: Android Studio.

Для хранения данных на устройстве используется Room.

## Документация и описание приложения

### Реализованный сценарий
В приложении реализован сценарий планирования встречи. Экземпляр сценария инициируется нажатием кнопки "Создать встречу" на главном экране. 

После этого отображается нижняя панель, в которой пользователю необходимо ввести информацию о встрече. 

Для успешного создания встречи требуется наличие интернет-подключения, поскольку происходит взаимодействие с сервером - комната планируется на определенное время, и в качестве ответа сервер возвращает ее идентификатор.

Примечание: для повторного отображения нижней панели по нажатию на кнопку "Создать встречу" необходимо переключить вкладку внизу на "Встречи" и вернуться на вкладку "Главная".

Примечание: при первом заходе в приложение отображается лендинг.

### Использование заглушек и обработка состояний
Для имитации ответа сервера используется метод sendRoomCreateRequest в классе RoomRemoteDataSource. "Сервер" возвращает случайный 32-байтный строку-идентификатор встречи.

На главном экране отображается обработка состояний: отсутсвие подключения к интернету, отсутствие созданных встреч, наличие созданных встреч. 

### Ссылки на проектные артефакты
- [Описание реализованного сценария "Запланировать видео-встречу"](https://docs.google.com/document/d/12eR_NIYRfvVgZ4m3DMHeS_tjmWGjqPJ-N2t2Rxi7veE/edit?usp=sharing)
- [Макеты приложения в Figma](https://www.figma.com/design/GciSFn7k8ODzlOorRfG7DD/meetspace?node-id=0-1&t=LWdLtjeJBf7XSqMI-1)
- [Диаграмма классов (сущности)](https://viewer.diagrams.net/?tags=%7B%7D&lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1&title=Meetspace_Entities.drawio&dark=auto#Uhttps%3A%2F%2Fdrive.google.com%2Fuc%3Fid%3D1ddVUidacdKYg59yONEZ1YsWlI1smYBUY%26export%3Ddownload)
- [Диаграмма классов (архитектура)](https://viewer.diagrams.net/?tags=%7B%7D&lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1&title=Meetspace_classesPlanMeeting.drawio&dark=auto#Uhttps%3A%2F%2Fdrive.google.com%2Fuc%3Fid%3D1fc83Wy2ARza3xolh7zlBn40Pg56ZJTmx%26export%3Ddownload)
- [Диаграмма пакетов](https://viewer.diagrams.net/?tags=%7B%7D&lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1&title=Meetspace_packages.drawio&dark=auto#Uhttps%3A%2F%2Fdrive.google.com%2Fuc%3Fid%3D1JnSs48razmFNkOdvl7klH_kRWskMr8KT%26export%3Ddownload)
- [Диаграмма последовательности (действия по сценарию)](https://viewer.diagrams.net/?tags=%7B%7D&lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1&title=%D0%94%D0%9F_%D0%BC%D0%BE%D0%B1%D0%B8%D0%BB_meetspace.drawio&dark=auto#Uhttps%3A%2F%2Fdrive.google.com%2Fuc%3Fid%3D1B2VACWEuiqNQMlN1dOt1wzmTks-h2DVO%26export%3Ddownload)

## Запуск приложения
Приложение необходимо создать как проект из данного репозитория в версии Android Studio с поддержкой AGP 9.0.0 (2025.2.3 и выше)

Для запуска приложения возможно понадобится установить и синхронизовать зависимости Gradle, для этого необходимо выбрать в меню File команду Sync Project With Gradle Files.

Приложение возможно запустить на эмуляторе Medium Phone API 36.1 или на устройстве с Android 9 и выше (SDK компиляции: версия 36).
