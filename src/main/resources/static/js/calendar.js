document.addEventListener('DOMContentLoaded', function () {
    const monthElement = document.getElementById('month');
    const calendarGrid = document.getElementById('calendar-grid');
    const prevBtn = document.querySelector('.prev-month');
    const nextBtn = document.querySelector('.next-month');

    let currentDate = new Date();
    let selectedDayElement = null;

    // Сохраняем информацию о сегодняшней дате глобально
    const today = new Date();
    const todayDate = {
        day: today.getDate(),
        month: today.getMonth(),
        year: today.getFullYear()
    };

    // Находим родительский элемент, где будут отображаться слоты
    const mainElement = document.querySelector('main');

    // НОВОЕ: Объект для кэширования данных по датам
    const slotsCache = {};

    // Функция для определения времени суток
    function getTimeCategory(time) {
        const hour = parseInt(time.split(':')[0]);
        if (hour < 12) return 'morning';
        if (hour < 18) return 'day';
        return 'evening';
    }

    // Функция для форматирования времени
    function formatTime(time) {
        return time.substring(0, 5); // "09:00" вместо "09:00:00"
    }

    // НОВОЕ: Функция для проверки, есть ли слоты в кэше для даты
    function hasSlotsForDate(year, month, day) {
        const dateKey = `${year}-${month + 1}-${day}`;
        return slotsCache[dateKey] && slotsCache[dateKey].length > 0;
    }

    // Функция для отображения слотов
    function displayTimeslots(timeslots) {
        console.log('Получены слоты:', timeslots);

        // Находим все существующие заголовки и слоты и удаляем их
        const existingSlots = document.querySelectorAll('.time-slot, .time-category');
        existingSlots.forEach(el => el.remove());

        // Также удаляем старые заголовки h2, которые были в разметке
        const oldHeaders = document.querySelectorAll('main h2');
        oldHeaders.forEach(el => el.remove());

        if (!timeslots || timeslots.length === 0) {
            // Если слотов нет вообще, показываем сообщение
            const noSlotsMessage = document.createElement('div');
            noSlotsMessage.className = 'no-slots-message';
            noSlotsMessage.textContent = 'На выбранную дату нет свободных слотов';
            mainElement.appendChild(noSlotsMessage);
            return;
        }

        // Группируем слоты по времени суток
        const slotsByCategory = {
            morning: [],
            day: [],
            evening: []
        };

        timeslots.forEach(slot => {
            const category = getTimeCategory(slot.startTime);
            slotsByCategory[category].push(slot);
        });

        // Функция для создания заголовка категории
        function createCategoryHeader(title) {
            const header = document.createElement('h2');
            header.className = 'time-category';
            header.textContent = title;
            return header;
        }

        // Функция для создания контейнера со слотами
        function createSlotsContainer(slots) {
            const container = document.createElement('div');
            container.className = 'slots-container';

            slots.forEach(slot => {
                const slotSpan = document.createElement('span');
                slotSpan.className = 'time-slot';
                slotSpan.dataset.slotId = slot.id;
                slotSpan.textContent = formatTime(slot.startTime);

                slotSpan.addEventListener('click', function() {
                    document.querySelectorAll('.time-slot').forEach(s => {
                        s.classList.remove('selected');
                    });
                    this.classList.add('selected');
                    console.log('Выбран слот ID:', this.dataset.slotId);
                });

                container.appendChild(slotSpan);
            });

            return container;
        }

        // Отображаем утренние слоты (только если они есть)
        if (slotsByCategory.morning.length > 0) {
            mainElement.appendChild(createCategoryHeader('Утро'));
            mainElement.appendChild(createSlotsContainer(slotsByCategory.morning));
        }

        // Отображаем дневные слоты (только если они есть)
        if (slotsByCategory.day.length > 0) {
            mainElement.appendChild(createCategoryHeader('День'));
            mainElement.appendChild(createSlotsContainer(slotsByCategory.day));
        }

        // Отображаем вечерние слоты (только если они есть)
        if (slotsByCategory.evening.length > 0) {
            mainElement.appendChild(createCategoryHeader('Вечер'));
            mainElement.appendChild(createSlotsContainer(slotsByCategory.evening));
        }
    }

    // Функция для отправки данных на бэкенд
    async function sendDateToBackend(year, month, day) {
        const dateKey = `${year}-${month + 1}-${day}`;

        // Формируем данные для отправки
        const dateData = {
            year: year,
            month: month + 1, // +1 потому что в JS месяцы с 0
            day: day,
            dateString: `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
        };

        // ОЧИЩАЕМ main ПЕРЕД КАЖДЫМ ЗАПРОСОМ
        const oldContent = document.querySelectorAll('.time-slot, .time-category, .no-slots-message, .error-message, main h2, .slots-container');
        oldContent.forEach(el => el.remove());

        try {
            // Показываем индикатор загрузки (опционально)
            showLoadingIndicator();

            // Отправляем POST запрос
            const response = await fetch('/booking/select-date-time', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(dateData)
            });

            // Проверяем ответ
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();
            console.log('Ответ от сервера:', result);

            // НОВОЕ: Сохраняем в кэш
            slotsCache[dateKey] = result;

            // Вызываем displayTimeslots с полученными данными
            displayTimeslots(result);

        } catch (error) {
            console.error('Ошибка при отправке даты:', error);

            // Показываем сообщение об ошибке
            const errorMessage = document.createElement('div');
            errorMessage.className = 'error-message';
            errorMessage.textContent = 'Не удалось загрузить расписание';
            mainElement.appendChild(errorMessage);
        } finally {
            hideLoadingIndicator();
        }
    }

    // Вспомогательные функции для UI (опционально)
    function showLoadingIndicator() {
        // Показать индикатор загрузки
        const loader = document.getElementById('loading-indicator');
        if (loader) loader.style.display = 'block';
    }

    function hideLoadingIndicator() {
        // Скрыть индикатор загрузки
        const loader = document.getElementById('loading-indicator');
        if (loader) loader.style.display = 'none';
    }

    function showErrorMessage(message) {
        // Показать сообщение об ошибке
        const errorDiv = document.getElementById('error-message');
        if (errorDiv) {
            errorDiv.textContent = message;
            errorDiv.style.display = 'block';
            setTimeout(() => {
                errorDiv.style.display = 'none';
            }, 3000);
        } else {
            alert(message);
        }
    }

    // Функция для отображения календаря
    function renderCalendar() {
        calendarGrid.innerHTML = '';

        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();

        // Устанавливаем название месяца
        const monthNames = [
            'Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
            'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'
        ];
        monthElement.textContent = `${monthNames[month]} ${year}`;

        // Получаем первый день месяца
        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const daysInMonth = lastDay.getDate();

        // Определяем день недели первого дня
        let firstDayOfWeek = firstDay.getDay();
        if (firstDayOfWeek === 0) firstDayOfWeek = 7;

        // Добавляем пустые ячейки
        for (let i = 1; i < firstDayOfWeek; i++) {
            const emptyCell = document.createElement('div');
            emptyCell.className = 'calendar-day empty';
            calendarGrid.appendChild(emptyCell);
        }

        // Добавляем дни текущего месяца
        for (let day = 1; day <= daysInMonth; day++) {
            const dayElement = document.createElement('div');
            dayElement.className = 'calendar-day';
            dayElement.textContent = day;

            // Проверяем, является ли этот день сегодняшним
            const isToday = (year === todayDate.year &&
                month === todayDate.month &&
                day === todayDate.day);

            if (isToday) {
                dayElement.classList.add('today');
            }

            // НОВОЕ: Создаем дату для проверки (прошлое или будущее)
            const cellDate = new Date(year, month, day);
            const isPastDate = cellDate < new Date(today.setHours(0, 0, 0, 0));

            // НОВОЕ: Если дата в прошлом - делаем её некликабельной
            if (isPastDate) {
                dayElement.classList.add('past');
                dayElement.style.opacity = '0.5';
                dayElement.style.pointerEvents = 'none';
                dayElement.style.cursor = 'not-allowed';
            } else {
                // Добавляем обработчик клика ТОЛЬКО для НЕ-прошлых дат
                dayElement.addEventListener('click', function (e) {
                    e.preventDefault();

                    // Получаем все элементы с классом today и today-black-underline
                    const allTodayElements = document.querySelectorAll('.calendar-day.today, .calendar-day.today-black-underline');

                    // Убираем выделение у всех дней
                    document.querySelectorAll('.calendar-day').forEach(d => {
                        d.classList.remove('selected');
                    });

                    // Обрабатываем сегодняшний день
                    allTodayElements.forEach(el => {
                        const dayNumber = parseInt(el.textContent);
                        const elMonth = parseInt(el.dataset.month);
                        const elYear = parseInt(el.dataset.year);

                        // Проверяем, является ли этот элемент сегодняшним днем
                        const isTodayElement = (elYear === todayDate.year &&
                            elMonth === todayDate.month &&
                            dayNumber === todayDate.day);

                        if (isTodayElement) {
                            if (this === el) {
                                // Если кликнули на сегодняшний день
                                el.classList.remove('today-black-underline');
                                el.classList.add('today'); // Возвращаем черный фон
                            } else {
                                // Если кликнули на другой день
                                el.classList.remove('today');
                                el.classList.add('today-black-underline'); // Черный underline
                            }
                        }
                    });

                    // Выделяем выбранный день
                    this.classList.add('selected');
                    selectedDayElement = this;

                    console.log(`Выбрана дата: ${day}.${month + 1}.${year}`);

                    // ОТПРАВЛЯЕМ ДАННЫЕ НА БЭКЕНД
                    sendDateToBackend(year, month, day);
                });
            }

            // Сохраняем месяц и год
            dayElement.dataset.month = month;
            dayElement.dataset.year = year;

            calendarGrid.appendChild(dayElement);
        }

        // Восстанавливаем выделение, если было в этом месяце
        if (selectedDayElement) {
            const selectedDay = parseInt(selectedDayElement.textContent);
            const selectedMonth = parseInt(selectedDayElement.dataset.month);
            const selectedYear = parseInt(selectedDayElement.dataset.year);

            if (selectedMonth === month && selectedYear === year) {
                const dayElements = document.querySelectorAll('.calendar-day:not(.empty)');
                dayElements.forEach(el => {
                    if (parseInt(el.textContent) === selectedDay) {
                        el.classList.add('selected');
                    }
                });
            }
        }
    }

    // Обработчики для кнопок навигации
    prevBtn.addEventListener('click', function () {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar();
    });

    nextBtn.addEventListener('click', function () {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar();
    });

    // Инициализация календаря
    renderCalendar();

    // Для клика по заголовку
    const headerLink = document.querySelector('.header-link');
    if (headerLink) {
        headerLink.addEventListener('click', function (e) {
            e.preventDefault();
            window.location.href = '/booking';
        });
    }
});