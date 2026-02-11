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

            // Добавляем обработчик клика
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
            });

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