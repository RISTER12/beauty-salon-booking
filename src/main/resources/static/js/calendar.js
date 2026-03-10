document.addEventListener('DOMContentLoaded', function () {
    const monthElement = document.getElementById('month');
    const calendarGrid = document.getElementById('calendar-grid');
    const prevBtn = document.querySelector('.prev-month');
    const nextBtn = document.querySelector('.next-month');
    const confirmBtn = document.querySelector('#go button[type="submit"]');
    const confirmWrapper = document.getElementById('go');

    let currentDate = new Date();
    let selectedDayElement = null;
    let selectedSlotId = null;

    // Хранилище выбранных дат для каждого месяца (ключ: "год-месяц")
    const selectedDatesByMonth = {};

    const today = new Date();
    const todayDate = {
        day: today.getDate(),
        month: today.getMonth(),
        year: today.getFullYear()
    };

    const mainElement = document.querySelector('main');
    const slotsCache = {};

    // Флаг для отслеживания загрузки данных месяца
    let monthDataLoaded = false;

    // Флаг для анимации
    let isAnimating = false;

    // Функция для определения времени суток
    function getTimeCategory(time) {
        const hour = parseInt(time.split(':')[0]);
        if (hour < 12) return 'morning';
        if (hour < 18) return 'day';
        return 'evening';
    }

    function formatTime(time) {
        return time.substring(0, 5);
    }

    function updateConfirmButtonVisibility() {
        if (selectedSlotId) {
            confirmWrapper.style.display = 'block';
            setTimeout(() => {
                confirmWrapper.style.opacity = '1';
                confirmWrapper.style.transform = 'translateY(0)';
            }, 50);
        } else {
            confirmWrapper.style.opacity = '0';
            confirmWrapper.style.transform = 'translateY(20px)';
            setTimeout(() => {
                if (!selectedSlotId) {
                    confirmWrapper.style.display = 'none';
                }
            }, 300);
        }
    }

    function resetSlotSelection() {
        selectedSlotId = null;
        updateConfirmButtonVisibility();
    }

    // ========== ФУНКЦИИ ДЛЯ РАБОТЫ С ВЫБРАННЫМИ ДАТАМИ ПО МЕСЯЦАМ ==========
    function getMonthKey(year, month) {
        return `${year}-${month + 1}`;
    }

    function saveSelectedDateForCurrentMonth() {
        const key = getMonthKey(currentDate.getFullYear(), currentDate.getMonth());
        if (selectedDayElement) {
            const day = parseInt(selectedDayElement.textContent);
            selectedDatesByMonth[key] = {
                year: currentDate.getFullYear(),
                month: currentDate.getMonth(),
                day
            };
        }
    }

    function loadSelectedDateForCurrentMonth() {
        const key = getMonthKey(currentDate.getFullYear(), currentDate.getMonth());
        return selectedDatesByMonth[key] || null;
    }

    // ========== ФУНКЦИЯ: поиск ближайшей доступной даты (только для сообщения) ==========
    function findNextAvailableDate() {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();
        const daysInMonth = new Date(year, month + 1, 0).getDate();

        // Сначала ищем в текущем месяце
        for (let day = todayDate.day; day <= daysInMonth; day++) {
            const dateKey = `${year}-${month + 1}-${day}`;
            const hasAvailableSlots = hasAvailableSlotsForDate(dateKey, { year, month, day });
            if (hasAvailableSlots) {
                return { year, month, day };
            }
        }

        // Если в текущем месяце нет, ищем в следующем
        const nextMonth = month + 1;
        const nextMonthYear = nextMonth > 11 ? year + 1 : year;
        const nextMonthIndex = nextMonth > 11 ? 0 : nextMonth;
        const nextMonthDays = new Date(nextMonthYear, nextMonthIndex + 1, 0).getDate();

        for (let day = 1; day <= nextMonthDays; day++) {
            const dateKey = `${nextMonthYear}-${nextMonthIndex + 1}-${day}`;
            const hasAvailableSlots = hasAvailableSlotsForDate(dateKey, {
                year: nextMonthYear,
                month: nextMonthIndex,
                day
            });
            if (hasAvailableSlots) {
                return { year: nextMonthYear, month: nextMonthIndex, day };
            }
        }

        return null;
    }

    // ========== ФУНКЦИЯ: проверка наличия актуальных слотов для даты ==========
    function hasAvailableSlotsForDate(dateKey, date) {
        if (!slotsCache[dateKey] || slotsCache[dateKey].length === 0) {
            return false;
        }

        const now = new Date();
        const currentHour = now.getHours();
        const currentMinute = now.getMinutes();

        const selectedDate = new Date(date.year, date.month, date.day);
        const isToday = selectedDate.getTime() === new Date(todayDate.year, todayDate.month, todayDate.day).getTime();

        // Если дата в будущем - достаточно наличия слотов
        if (selectedDate > new Date(todayDate.year, todayDate.month, todayDate.day)) {
            return true;
        }

        // Если дата сегодняшняя - проверяем каждый слот
        if (isToday) {
            return slotsCache[dateKey].some(slot => {
                const [slotHour, slotMinute] = slot.startTime.split(':').map(Number);
                return (slotHour > currentHour) ||
                    (slotHour === currentHour && slotMinute > currentMinute);
            });
        }

        return false;
    }

    function createCollapsibleSection(title, slots, delay) {
        const section = document.createElement('div');
        section.className = 'collapsible-section';
        section.style.opacity = '0';
        section.style.transform = 'translateY(20px)';
        section.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        section.style.transitionDelay = `${delay}s`;

        const header = document.createElement('div');
        header.className = 'section-header';
        header.innerHTML = `
            <h2 class="time-category">${title}</h2>
            <span class="toggle-icon">▼</span>
        `;

        const content = document.createElement('div');
        content.className = 'section-content';
        content.style.maxHeight = '0';
        content.style.overflow = 'hidden';
        content.style.transition = 'max-height 0.4s ease-out';

        const slotsContainer = document.createElement('div');
        slotsContainer.className = 'slots-container';

        // ========== ФИЛЬТРУЕМ СЛОТЫ ПО ТЕКУЩЕМУ ВРЕМЕНИ ==========
        const now = new Date();
        const currentHour = now.getHours();
        const currentMinute = now.getMinutes();

        const filteredSlots = slots.filter(slot => {
            // Если дата в будущем - показываем все слоты
            const selectedDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), parseInt(slot.slotDate.split('-')[2]));

            if (selectedDate > new Date(todayDate.year, todayDate.month, todayDate.day)) {
                return true; // Будущая дата - показываем все слоты
            }

            // Если дата сегодняшняя - фильтруем по времени
            if (selectedDate.getTime() === new Date(todayDate.year, todayDate.month, todayDate.day).getTime()) {
                const [slotHour, slotMinute] = slot.startTime.split(':').map(Number);
                return (slotHour > currentHour) ||
                    (slotHour === currentHour && slotMinute > currentMinute);
            }

            return true; // Прошлые даты уже отфильтрованы в календаре
        });

        filteredSlots.forEach(slot => {
            const slotSpan = document.createElement('span');
            slotSpan.className = 'time-slot';
            slotSpan.dataset.slotId = slot.id;
            slotSpan.textContent = formatTime(slot.startTime);

            // ========== ВОССТАНАВЛИВАЕМ ВЫБРАННЫЙ СЛОТ ==========
            if (selectedSlotId && selectedSlotId === slot.id.toString()) {
                slotSpan.classList.add('selected');
            }

            slotSpan.addEventListener('click', function(e) {
                e.stopPropagation();

                document.querySelectorAll('.time-slot').forEach(s => {
                    s.classList.remove('selected');
                });

                this.classList.add('selected');
                selectedSlotId = this.dataset.slotId;
                console.log('Выбран слот ID:', selectedSlotId);

                updateConfirmButtonVisibility();
            });

            slotsContainer.appendChild(slotSpan);
        });

        content.appendChild(slotsContainer);

        header.addEventListener('click', function() {
            const isOpen = content.style.maxHeight !== '0px';
            const icon = this.querySelector('.toggle-icon');

            if (isOpen) {
                content.style.maxHeight = '0';
                icon.textContent = '▶';
                icon.style.transform = 'rotate(0deg)';
            } else {
                // Если есть слоты после фильтрации, открываем секцию
                if (filteredSlots.length > 0) {
                    content.style.maxHeight = content.scrollHeight + 'px';
                    icon.textContent = '▼';
                    icon.style.transform = 'rotate(0deg)';
                }

                setTimeout(() => {
                    icon.style.opacity = '1';
                }, 50);
            }
        });

        section.appendChild(header);
        section.appendChild(content);

        setTimeout(() => {
            section.style.opacity = '1';
            section.style.transform = 'translateY(0)';
        }, 50);

        // Возвращаем секцию только если есть слоты после фильтрации
        return filteredSlots.length > 0 ? section : null;
    }

    function displayTimeslots(timeslots) {
        console.log('Получены слоты:', timeslots);

        resetSlotSelection();

        const existingSections = document.querySelectorAll('.collapsible-section');
        existingSections.forEach(el => el.remove());

        if (!timeslots || timeslots.length === 0) {
            const nextDate = findNextAvailableDate();

            const messageContainer = document.createElement('div');
            messageContainer.className = 'no-slots-container';

            const messageElement = document.createElement('div');
            messageElement.className = 'no-slots-message';
            messageElement.textContent = 'На выбранную дату нет свободных слотов';
            messageContainer.appendChild(messageElement);

            if (nextDate) {
                const monthNames = [
                    'января', 'февраля', 'марта', 'апреля', 'мая', 'июня',
                    'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря'
                ];

                const nextDateInfo = document.createElement('div');
                nextDateInfo.className = 'next-date-info';
                nextDateInfo.textContent = `Ближайшая доступная запись: ${nextDate.day} ${monthNames[nextDate.month]}`;
                messageContainer.appendChild(nextDateInfo);
            }

            mainElement.appendChild(messageContainer);
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

        let delay = 0.1;
        let hasAnySlots = false;

        // Отображаем утренние слоты (только если есть после фильтрации)
        if (slotsByCategory.morning.length > 0) {
            const morningSection = createCollapsibleSection('Утро', slotsByCategory.morning, delay);
            if (morningSection) {
                mainElement.appendChild(morningSection);
                delay += 0.1;
                hasAnySlots = true;
            }
        }

        // Отображаем дневные слоты (только если есть после фильтрации)
        if (slotsByCategory.day.length > 0) {
            const daySection = createCollapsibleSection('День', slotsByCategory.day, delay);
            if (daySection) {
                mainElement.appendChild(daySection);
                delay += 0.1;
                hasAnySlots = true;
            }
        }

        // Отображаем вечерние слоты (только если есть после фильтрации)
        if (slotsByCategory.evening.length > 0) {
            const eveningSection = createCollapsibleSection('Вечер', slotsByCategory.evening, delay);
            if (eveningSection) {
                mainElement.appendChild(eveningSection);
                hasAnySlots = true;
            }
        }

        // Если после фильтрации не осталось слотов
        if (!hasAnySlots) {
            const nextDate = findNextAvailableDate();

            const messageContainer = document.createElement('div');
            messageContainer.className = 'no-slots-container';

            const messageElement = document.createElement('div');
            messageElement.className = 'no-slots-message';
            messageElement.textContent = 'На выбранную дату нет доступных слотов (все слоты уже прошли)';
            messageContainer.appendChild(messageElement);

            if (nextDate) {
                const monthNames = [
                    'января', 'февраля', 'марта', 'апреля', 'мая', 'июня',
                    'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря'
                ];

                const nextDateInfo = document.createElement('div');
                nextDateInfo.className = 'next-date-info';
                nextDateInfo.textContent = `Ближайшая доступная запись: ${nextDate.day} ${monthNames[nextDate.month]}`;
                messageContainer.appendChild(nextDateInfo);
            }

            mainElement.appendChild(messageContainer);
        }

        setTimeout(() => {
            document.querySelectorAll('.collapsible-section .section-header').forEach(header => {
                const content = header.nextElementSibling;
                const icon = header.querySelector('.toggle-icon');

                if (content && content.classList.contains('section-content') && content.children[0]?.children.length > 0) {
                    content.style.maxHeight = content.scrollHeight + 'px';
                    if (icon) icon.textContent = '▼';
                }
            });
        }, 500);
    }

    // ========== ОТПРАВКА ДАТЫ С ИСПОЛЬЗОВАНИЕМ КЭША ==========
    async function sendDateToBackend(year, month, day) {
        const dateKey = `${year}-${month + 1}-${day}`;

        // Если данные для этой даты уже есть в кэше, отображаем их без запроса
        if (slotsCache[dateKey]) {
            console.log('Данные для даты уже есть в кэше', dateKey);
            displayTimeslots(slotsCache[dateKey]);
            return;
        }

        const dateData = {
            year: year,
            month: month + 1,
            day: day,
            dateString: `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
        };

        const oldContent = document.querySelectorAll('.collapsible-section, .no-slots-message, .error-message, .no-slots-container');
        oldContent.forEach(el => el.remove());

        try {
            showLoadingIndicator();

            const response = await fetch('/booking/select-date-time', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(dateData)
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();
            console.log('Ответ от сервера:', result);

            slotsCache[dateKey] = result;
            displayTimeslots(result);

        } catch (error) {
            console.error('Ошибка при отправке даты:', error);

            const errorMessage = document.createElement('div');
            errorMessage.className = 'error-message';
            errorMessage.textContent = 'Не удалось загрузить расписание';
            mainElement.appendChild(errorMessage);
        } finally {
            hideLoadingIndicator();
        }
    }

    function showLoadingIndicator() {
        const loader = document.getElementById('loading-indicator');
        if (loader) loader.style.display = 'block';
    }

    function hideLoadingIndicator() {
        const loader = document.getElementById('loading-indicator');
        if (loader) loader.style.display = 'none';
    }

    // ========== ЗАГРУЗКА ДАННЫХ ЗА МЕСЯЦ С КЭШИРОВАНИЕМ ==========
    async function loadMonthData(year, month) {
        // Проверяем, есть ли уже данные для этого месяца в кэше
        const monthKey = getMonthKey(year, month);
        let hasMonthData = false;
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        for (let d = 1; d <= daysInMonth; d++) {
            const dateKey = `${year}-${month + 1}-${d}`;
            if (slotsCache[dateKey]) {
                hasMonthData = true;
                break;
            }
        }

        if (hasMonthData) {
            console.log(`Данные за месяц ${monthKey} уже есть в кэше, пропускаем запрос`);
            monthDataLoaded = true;
            renderCalendar();
            return;
        }

        try {
            console.log(`Загружаем данные за месяц: ${year}-${month + 1}`);

            const response = await fetch('/booking/select-date-time/month-slots', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    year: year,
                    month: month + 1
                })
            });

            if (response.ok) {
                const allMonthSlots = await response.json();
                console.log('Получены данные за месяц:', allMonthSlots);

                // Очищаем старые данные из кэша для этого месяца
                for (let d = 1; d <= daysInMonth; d++) {
                    const dateKey = `${year}-${month + 1}-${d}`;
                    delete slotsCache[dateKey];
                }

                // Если есть слоты, сохраняем их в кэш
                if (allMonthSlots && allMonthSlots.length > 0) {
                    allMonthSlots.forEach(slot => {
                        if (slot.slotDate) {
                            const dateParts = slot.slotDate.split('-');
                            if (dateParts.length === 3) {
                                const slotDay = parseInt(dateParts[2]);
                                const dateKey = `${year}-${month + 1}-${slotDay}`;

                                if (!slotsCache[dateKey]) {
                                    slotsCache[dateKey] = [];
                                }
                                slotsCache[dateKey].push(slot);
                            }
                        }
                    });
                }

                monthDataLoaded = true;
                console.log('Кэш обновлен:', slotsCache);
            } else {
                console.error('Ошибка загрузки данных месяца:', response.status);
            }
        } catch (error) {
            console.error('Ошибка при загрузке данных месяца:', error);
        }

        renderCalendar();
    }

    // ========== ОТРИСОВКА КАЛЕНДАРЯ ==========
    function renderCalendar() {
        calendarGrid.innerHTML = '';

        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();

        const monthNames = [
            'Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
            'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'
        ];
        monthElement.textContent = `${monthNames[month]} ${year}`;

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const daysInMonth = lastDay.getDate();

        let firstDayOfWeek = firstDay.getDay();
        if (firstDayOfWeek === 0) firstDayOfWeek = 7;

        for (let i = 1; i < firstDayOfWeek; i++) {
            const emptyCell = document.createElement('div');
            emptyCell.className = 'calendar-day empty';
            calendarGrid.appendChild(emptyCell);
        }

        // Флаг, указывающий, был ли уже выбран день в этом месяце
        let selectedInThisMonth = false;

        // Загружаем сохранённый день для этого месяца
        let savedDate = loadSelectedDateForCurrentMonth();

        for (let day = 1; day <= daysInMonth; day++) {
            const dayElement = document.createElement('div');
            dayElement.className = 'calendar-day';
            dayElement.textContent = day;

            const isToday = (year === todayDate.year &&
                month === todayDate.month &&
                day === todayDate.day);

            if (isToday) {
                dayElement.classList.add('today');
            }

            const cellDate = new Date(year, month, day);
            const startOfToday = new Date(todayDate.year, todayDate.month, todayDate.day);
            const isPastDate = cellDate < startOfToday;

            // Проверка наличия актуальных слотов
            const dateKey = `${year}-${month + 1}-${day}`;
            const hasAvailableSlots = hasAvailableSlotsForDate(dateKey, { year, month, day });

            // День кликабелен, если не в прошлом и есть слоты
            const isClickable = !isPastDate && hasAvailableSlots;

            const isSavedDate = savedDate && savedDate.year === year && savedDate.month === month && savedDate.day === day;

            if (!isClickable) {
                dayElement.classList.add('unavailable');
                dayElement.style.opacity = '0.5';
                dayElement.style.pointerEvents = 'none';
                dayElement.style.cursor = 'not-allowed';

                if (isPastDate) {
                    dayElement.title = 'Дата в прошлом';
                } else {
                    dayElement.title = 'Нет доступных слотов';
                }
            } else {
                dayElement.addEventListener('click', function (e) {
                    e.preventDefault();
                    if (this.classList.contains('selected')) {
                        console.log('Этот день уже выбран');
                        return;
                    }

                    resetSlotSelection();

                    // Убираем выделение у всех дней
                    document.querySelectorAll('.calendar-day').forEach(d => {
                        d.classList.remove('selected');
                    });

                    // Обрабатываем сегодняшний день (подчёркивание)
                    const allTodayElements = document.querySelectorAll('.calendar-day.today, .calendar-day.today-black-underline');
                    allTodayElements.forEach(el => {
                        const dayNumber = parseInt(el.textContent);
                        const elMonth = parseInt(el.dataset.month);
                        const elYear = parseInt(el.dataset.year);

                        const isTodayElement = (elYear === todayDate.year &&
                            elMonth === todayDate.month &&
                            dayNumber === todayDate.day);

                        if (isTodayElement) {
                            if (this === el) {
                                el.classList.remove('today-black-underline');
                                el.classList.add('today');
                            } else {
                                el.classList.remove('today');
                                el.classList.add('today-black-underline');
                            }
                        }
                    });

                    this.classList.add('selected');
                    selectedDayElement = this;

                    // Сохраняем выбранную дату для этого месяца
                    saveSelectedDateForCurrentMonth();

                    console.log(`Выбрана дата: ${day}.${month + 1}.${year}`);
                    sendDateToBackend(year, month, day);
                });
            }

            dayElement.dataset.month = month;
            dayElement.dataset.year = year;
            calendarGrid.appendChild(dayElement);

            // Если это сохранённый день и он доступен, выделяем его
            if (isSavedDate && isClickable && !selectedInThisMonth) {
                dayElement.classList.add('selected');
                selectedDayElement = dayElement;
                selectedInThisMonth = true;
                // Загружаем слоты для этого дня (с небольшой задержкой, чтобы календарь успел отрисоваться)
                setTimeout(() => {
                    sendDateToBackend(year, month, day);
                }, 100);
            }
        }

        // Если сохранённый день не был выделен (т.е. он был, но недоступен), удаляем его из хранилища
        if (!selectedInThisMonth && savedDate) {
            delete selectedDatesByMonth[getMonthKey(currentDate.getFullYear(), currentDate.getMonth())];
        }

        // Если в этом месяце ещё не выбран день, выбираем сегодняшний
        if (!selectedInThisMonth) {
            const todayElement = Array.from(document.querySelectorAll('.calendar-day:not(.empty)')).find(el => {
                const elDay = parseInt(el.textContent);
                const elMonth = parseInt(el.dataset.month);
                const elYear = parseInt(el.dataset.year);
                return elYear === todayDate.year && elMonth === todayDate.month && elDay === todayDate.day;
            });

            if (todayElement) {
                if (todayElement.classList.contains('unavailable')) {
                    todayElement.classList.add('selected');
                    selectedDayElement = todayElement;
                    selectedInThisMonth = true;
                    console.log('Визуально выбран сегодняшний день (без слотов)');
                } else {
                    todayElement.classList.add('selected');
                    selectedDayElement = todayElement;
                    selectedInThisMonth = true;
                    sendDateToBackend(todayDate.year, todayDate.month, todayDate.day);
                }
            }
        }
    }

    // ========== АНИМИРОВАННОЕ ПЕРЕКЛЮЧЕНИЕ МЕСЯЦЕВ ==========
    async function changeMonth(direction) {
        if (isAnimating) return;
        isAnimating = true;

        // Сохраняем выбранный день для текущего месяца перед уходом
        saveSelectedDateForCurrentMonth();

        // Анимация исчезновения
        calendarGrid.style.opacity = '0';
        calendarGrid.style.transform = 'scale(0.95)';
        calendarGrid.style.transition = 'opacity 0.2s ease, transform 0.2s ease';

        // Меняем месяц
        if (direction === 'prev') {
            currentDate.setMonth(currentDate.getMonth() - 1);
        } else {
            currentDate.setMonth(currentDate.getMonth() + 1);
        }

        monthDataLoaded = false;

        // Ждем окончания анимации
        await new Promise(resolve => setTimeout(resolve, 200));

        // Загружаем данные для нового месяца (с кэшированием)
        await loadMonthData(currentDate.getFullYear(), currentDate.getMonth());

        // Анимация появления
        calendarGrid.style.opacity = '1';
        calendarGrid.style.transform = 'scale(1)';

        // Сбрасываем флаг
        setTimeout(() => {
            isAnimating = false;
        }, 200);
    }

    // ========== ОБРАБОТЧИКИ НАВИГАЦИИ ==========
    prevBtn.addEventListener('click', function () {
        changeMonth('prev');
    });

    nextBtn.addEventListener('click', function () {
        changeMonth('next');
    });

    // ========== ИНИЦИАЛИЗАЦИЯ ==========
    async function initialize() {
        // Загружаем данные для текущего месяца
        await loadMonthData(currentDate.getFullYear(), currentDate.getMonth());
    }

    initialize();

    if (confirmWrapper) {
        confirmWrapper.style.display = 'none';
        confirmWrapper.style.opacity = '0';
        confirmWrapper.style.transform = 'translateY(20px)';
        confirmWrapper.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
    }

    if (confirmBtn) {
        confirmBtn.addEventListener('click', function(e) {
            e.preventDefault();
            if (selectedSlotId) {
                console.log('Подтверждение записи для слота:', selectedSlotId);
                fetch('/booking/confirm', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ slotId: selectedSlotId })
                }).then(response => {
                    if (response.ok) {
                        window.location.href = '/booking/success';
                    }
                });
            }
        });
    }

    const headerLink = document.querySelector('.header-link');
    if (headerLink) {
        headerLink.addEventListener('click', function (e) {
            e.preventDefault();
            window.location.href = '/booking';
        });
    }
});