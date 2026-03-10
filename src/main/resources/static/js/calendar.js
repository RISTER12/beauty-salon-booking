/**
 * Календарь для выбора даты и времени записи.
 * Основные возможности:
 * - Отображение календаря на текущий месяц с возможностью переключения.
 * - Загрузка слотов за месяц и за конкретный день.
 * - Кэширование данных и отрисованных секций для быстрого возврата.
 * - Единая глобальная выбранная дата (только один выделенный день).
 * - Автоматический выбор сегодняшнего дня при первом открытии.
 * - Анимация переключения месяцев.
 */

document.addEventListener('DOMContentLoaded', () => {
    // ==================== ЭЛЕМЕНТЫ DOM ====================
    const monthElement = document.getElementById('month');
    const calendarGrid = document.getElementById('calendar-grid');
    const prevBtn = document.querySelector('.prev-month');
    const nextBtn = document.querySelector('.next-month');
    const confirmBtn = document.querySelector('#go button[type="submit"]');
    const confirmWrapper = document.getElementById('go');
    const mainElement = document.querySelector('main');

    // ==================== КОНСТАНТЫ ====================
    const MONTH_NAMES = [
        'Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
        'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'
    ];
    const MONTH_NAMES_GENITIVE = [
        'января', 'февраля', 'марта', 'апреля', 'мая', 'июня',
        'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря'
    ];
    const PRELOAD_MONTHS = 11; // сколько месяцев предзагружать вперёд

    // ==================== СОСТОЯНИЕ ====================
    let currentDate = new Date();               // текущий отображаемый месяц
    let selectedDayElement = null;               // DOM-элемент выбранного дня
    let selectedSlotId = null;                   // ID выбранного временного слота
    let globalSelectedDate = null;                // глобальная выбранная дата { year, month, day }
    let currentlyDisplayedDateKey = null;         // ключ даты, слоты которой сейчас показаны (год-месяц-день)
    let isAnimating = false;                      // флаг анимации переключения месяцев

    // ==================== КЭШИ ====================
    const slotsCache = {};                        // данные слотов по ключу даты (год-месяц-день)
    const renderedSlotsCache = {};                 // DOM-секции для каждой даты (год-месяц-день)

    // Сегодняшняя дата (фиксируется при загрузке)
    const today = new Date();
    const todayDate = {
        day: today.getDate(),
        month: today.getMonth(),
        year: today.getFullYear()
    };

    // ==================== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ====================
    const getDateKey = (year, month, day) => `${year}-${month + 1}-${day}`;
    const getMonthKey = (year, month) => `${year}-${month + 1}`;

    // Определение времени суток по часу
    const getTimeCategory = (time) => {
        const hour = parseInt(time.split(':')[0]);
        if (hour < 12) return 'morning';
        if (hour < 18) return 'day';
        return 'evening';
    };

    // Форматирование времени (убираем секунды)
    const formatTime = (time) => time.substring(0, 5);

    // ==================== УПРАВЛЕНИЕ КНОПКОЙ ПОДТВЕРЖДЕНИЯ ====================
    const updateConfirmButtonVisibility = () => {
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
                if (!selectedSlotId) confirmWrapper.style.display = 'none';
            }, 300);
        }
    };

    const resetSlotSelection = () => {
        selectedSlotId = null;
        updateConfirmButtonVisibility();
    };

    // ==================== РАБОТА С ДАННЫМИ СЛОТОВ ====================
    /**
     * Проверяет, есть ли актуальные (не прошедшие) слоты для указанной даты.
     * @param {string} dateKey - ключ даты
     * @param {Object} date - { year, month, day }
     * @returns {boolean}
     */
    const hasAvailableSlotsForDate = (dateKey, date) => {
        const slots = slotsCache[dateKey];
        if (!slots || slots.length === 0) return false;

        const now = new Date();
        const currentHour = now.getHours();
        const currentMinute = now.getMinutes();

        const selectedDate = new Date(date.year, date.month, date.day);
        const isToday = selectedDate.getTime() === new Date(todayDate.year, todayDate.month, todayDate.day).getTime();

        // Будущая дата — достаточно наличия любых слотов
        if (selectedDate > new Date(todayDate.year, todayDate.month, todayDate.day)) return true;

        // Сегодня — проверяем каждый слот
        if (isToday) {
            return slots.some(slot => {
                const [slotHour, slotMinute] = slot.startTime.split(':').map(Number);
                return (slotHour > currentHour) || (slotHour === currentHour && slotMinute > currentMinute);
            });
        }

        return false; // прошлая дата (уже отсечена в календаре)
    };

    /**
     * Загружает данные за месяц (если их нет в кэше) и рендерит календарь.
     */
    const loadMonthData = async (year, month) => {
        currentlyDisplayedDateKey = null; // при смене месяца сбрасываем отображаемую дату

        // Проверяем наличие данных за месяц в кэше
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        let hasMonthData = false;
        for (let d = 1; d <= daysInMonth; d++) {
            if (slotsCache[getDateKey(year, month, d)]) {
                hasMonthData = true;
                break;
            }
        }

        if (hasMonthData) {
            console.log(`Данные за месяц ${year}-${month+1} уже есть в кэше`);
            renderCalendar();
            return;
        }

        try {
            console.log(`Загружаем данные за месяц: ${year}-${month + 1}`);
            const response = await fetch('/booking/select-date-time/month-slots', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ year, month: month + 1 })
            });

            if (!response.ok) {
                console.error('Ошибка загрузки данных месяца:', response.status);
                return;
            }

            const allMonthSlots = await response.json();
            console.log('Получены данные за месяц:', allMonthSlots);

            // Очищаем старые данные этого месяца
            for (let d = 1; d <= daysInMonth; d++) {
                delete slotsCache[getDateKey(year, month, d)];
            }

            // Сохраняем новые данные
            if (allMonthSlots && allMonthSlots.length > 0) {
                allMonthSlots.forEach(slot => {
                    if (!slot.slotDate) return;
                    const parts = slot.slotDate.split('-');
                    if (parts.length !== 3) return;
                    const day = parseInt(parts[2]);
                    const key = getDateKey(year, month, day);
                    if (!slotsCache[key]) slotsCache[key] = [];
                    slotsCache[key].push(slot);
                });
            }
            console.log('Кэш обновлён:', slotsCache);
        } catch (error) {
            console.error('Ошибка при загрузке данных месяца:', error);
        }

        renderCalendar();
    };

    /**
     * Запрашивает слоты для конкретного дня (если нет в кэше) и отображает их.
     */
    const sendDateToBackend = async (year, month, day) => {
        const dateKey = getDateKey(year, month, day);

        // Если данные уже есть — просто отображаем
        if (slotsCache[dateKey]) {
            console.log('Данные для даты уже есть в кэше', dateKey);
            displayTimeslots(slotsCache[dateKey]);
            return;
        }

        const dateData = {
            year,
            month: month + 1,
            day,
            dateString: `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
        };

        // Очищаем старые секции
        document.querySelectorAll('.collapsible-section, .no-slots-message, .error-message, .no-slots-container')
            .forEach(el => el.remove());

        try {
            showLoadingIndicator();

            const response = await fetch('/booking/select-date-time', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(dateData)
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

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
    };

    // ==================== РЕНДЕРИНГ СЛОТОВ ====================
    /**
     * Создаёт свёрнутую секцию с заголовком и слотами.
     * @param {string} title - заголовок (Утро/День/Вечер)
     * @param {Array} slots - массив слотов
     * @param {number} delay - задержка анимации появления
     * @returns {HTMLElement|null} секция или null, если нет слотов после фильтрации
     */
    const createCollapsibleSection = (title, slots, delay) => {
        const now = new Date();
        const currentHour = now.getHours();
        const currentMinute = now.getMinutes();

        // Фильтруем слоты по текущему времени
        const filteredSlots = slots.filter(slot => {
            const slotDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), parseInt(slot.slotDate.split('-')[2]));
            const isToday = slotDate.getTime() === new Date(todayDate.year, todayDate.month, todayDate.day).getTime();
            if (slotDate > new Date(todayDate.year, todayDate.month, todayDate.day)) return true; // будущее
            if (isToday) {
                const [slotHour, slotMinute] = slot.startTime.split(':').map(Number);
                return (slotHour > currentHour) || (slotHour === currentHour && slotMinute > currentMinute);
            }
            return true; // прошлые даты уже отфильтрованы в календаре
        });

        if (filteredSlots.length === 0) return null;

        const section = document.createElement('div');
        section.className = 'collapsible-section';
        section.style.opacity = '0';
        section.style.transform = 'translateY(20px)';
        section.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        section.style.transitionDelay = `${delay}s`;

        const header = document.createElement('div');
        header.className = 'section-header';
        header.innerHTML = `<h2 class="time-category">${title}</h2><span class="toggle-icon">▼</span>`;

        const content = document.createElement('div');
        content.className = 'section-content';
        content.style.maxHeight = '0';
        content.style.overflow = 'hidden';
        content.style.transition = 'max-height 0.4s ease-out';

        const slotsContainer = document.createElement('div');
        slotsContainer.className = 'slots-container';

        filteredSlots.forEach(slot => {
            const slotSpan = document.createElement('span');
            slotSpan.className = 'time-slot';
            slotSpan.dataset.slotId = slot.id;
            slotSpan.textContent = formatTime(slot.startTime);
            if (selectedSlotId && selectedSlotId === slot.id.toString()) {
                slotSpan.classList.add('selected');
            }

            slotSpan.addEventListener('click', (e) => {
                e.stopPropagation();
                document.querySelectorAll('.time-slot').forEach(s => s.classList.remove('selected'));
                slotSpan.classList.add('selected');
                selectedSlotId = slotSpan.dataset.slotId;
                console.log('Выбран слот ID:', selectedSlotId);
                updateConfirmButtonVisibility();
            });

            slotsContainer.appendChild(slotSpan);
        });

        content.appendChild(slotsContainer);

        header.addEventListener('click', () => {
            const isOpen = content.style.maxHeight !== '0px';
            const icon = header.querySelector('.toggle-icon');
            if (isOpen) {
                content.style.maxHeight = '0';
                icon.textContent = '▶';
            } else {
                content.style.maxHeight = content.scrollHeight + 'px';
                icon.textContent = '▼';
                setTimeout(() => icon.style.opacity = '1', 50);
            }
        });

        section.appendChild(header);
        section.appendChild(content);

        setTimeout(() => {
            section.style.opacity = '1';
            section.style.transform = 'translateY(0)';
        }, 50);

        return section;
    };

    /**
     * Отображает слоты в main.
     * Если для этой даты уже есть кэшированные DOM-секции — использует их.
     * Иначе создаёт новые и сохраняет в кэш.
     */
    const displayTimeslots = (timeslots) => {
        // Определяем ключ даты из первого слота
        let dateKey = null;
        if (timeslots && timeslots.length > 0 && timeslots[0].slotDate) {
            const parts = timeslots[0].slotDate.split('-');
            if (parts.length === 3) {
                dateKey = `${parts[0]}-${parseInt(parts[1])}-${parseInt(parts[2])}`;
            }
        }

        // Если уже отображается та же дата — ничего не делаем
        if (dateKey && currentlyDisplayedDateKey === dateKey) {
            console.log('Слоты для этой даты уже отображаются, пропускаем');
            return;
        }

        // Если есть кэшированный рендер — используем его
        if (dateKey && renderedSlotsCache[dateKey]) {
            console.log('Используем кэшированный рендер для', dateKey);
            document.querySelectorAll('.collapsible-section').forEach(el => el.remove());
            renderedSlotsCache[dateKey].forEach(section => mainElement.appendChild(section));

            // Сбрасываем выделение слотов: убираем класс selected у всех слотов
            document.querySelectorAll('.time-slot').forEach(slot => slot.classList.remove('selected'));
            // Если есть выбранный слот для этой даты, выделяем его (но при возврате после переключения selectedSlotId = null)
            if (selectedSlotId) {
                const selectedSlot = document.querySelector(`.time-slot[data-slot-id="${selectedSlotId}"]`);
                if (selectedSlot) selectedSlot.classList.add('selected');
            }

            currentlyDisplayedDateKey = dateKey;
            return;
        }

        console.log('Получены слоты:', timeslots);
        resetSlotSelection();

        // Удаляем старые секции
        document.querySelectorAll('.collapsible-section').forEach(el => el.remove());

        if (!timeslots || timeslots.length === 0) {
            showNoSlotsMessage();
            currentlyDisplayedDateKey = dateKey;
            return;
        }

        // Группировка по времени суток
        const grouped = { morning: [], day: [], evening: [] };
        timeslots.forEach(slot => {
            const category = getTimeCategory(slot.startTime);
            grouped[category].push(slot);
        });

        let delay = 0.1;
        const createdSections = [];

        // Создаём секции для каждой категории, где есть слоты
        if (grouped.morning.length > 0) {
            const section = createCollapsibleSection('Утро', grouped.morning, delay);
            if (section) {
                mainElement.appendChild(section);
                createdSections.push(section);
                delay += 0.1;
            }
        }
        if (grouped.day.length > 0) {
            const section = createCollapsibleSection('День', grouped.day, delay);
            if (section) {
                mainElement.appendChild(section);
                createdSections.push(section);
                delay += 0.1;
            }
        }
        if (grouped.evening.length > 0) {
            const section = createCollapsibleSection('Вечер', grouped.evening, delay);
            if (section) {
                mainElement.appendChild(section);
                createdSections.push(section);
            }
        }

        // Если ни одной секции не создано (например, все слоты прошедшие)
        if (createdSections.length === 0) {
            showNoSlotsMessage(true);
        }

        // Автоматическое открытие всех секций
        setTimeout(() => {
            document.querySelectorAll('.collapsible-section .section-header').forEach(header => {
                const content = header.nextElementSibling;
                if (content?.children[0]?.children.length > 0) {
                    content.style.maxHeight = content.scrollHeight + 'px';
                    header.querySelector('.toggle-icon').textContent = '▼';
                }
            });
        }, 500);

        // Кэшируем созданные секции
        if (dateKey) renderedSlotsCache[dateKey] = createdSections;
        currentlyDisplayedDateKey = dateKey;
    };

    const showNoSlotsMessage = (allPast = false) => {
        const nextDate = findNextAvailableDate();

        const container = document.createElement('div');
        container.className = 'no-slots-container';

        const message = document.createElement('div');
        message.className = 'no-slots-message';
        message.textContent = allPast
            ? 'На выбранную дату нет доступных слотов (все слоты уже прошли)'
            : 'На выбранную дату нет свободных слотов';
        container.appendChild(message);

        if (nextDate) {
            const info = document.createElement('div');
            info.className = 'next-date-info';
            info.textContent = `Ближайшая доступная запись: ${nextDate.day} ${MONTH_NAMES_GENITIVE[nextDate.month]}`;
            container.appendChild(info);
        }

        mainElement.appendChild(container);
    };

    /**
     * Поиск ближайшей доступной даты (для сообщения).
     */
    const findNextAvailableDate = () => {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();
        const daysInMonth = new Date(year, month + 1, 0).getDate();

        // Сначала в текущем месяце
        for (let d = todayDate.day; d <= daysInMonth; d++) {
            if (hasAvailableSlotsForDate(getDateKey(year, month, d), { year, month, day: d })) {
                return { year, month, day: d };
            }
        }

        // Затем в следующем
        let nextMonth = month + 1;
        let nextYear = year;
        if (nextMonth > 11) {
            nextYear++;
            nextMonth = 0;
        }
        const nextDays = new Date(nextYear, nextMonth + 1, 0).getDate();
        for (let d = 1; d <= nextDays; d++) {
            if (hasAvailableSlotsForDate(getDateKey(nextYear, nextMonth, d), { year: nextYear, month: nextMonth, day: d })) {
                return { year: nextYear, month: nextMonth, day: d };
            }
        }
        return null;
    };

    // ==================== РЕНДЕРИНГ КАЛЕНДАРЯ ====================
    const renderCalendar = () => {
        calendarGrid.innerHTML = '';

        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();

        // Заголовок месяца
        monthElement.textContent = `${MONTH_NAMES[month]} ${year}`;

        // Определяем первый день месяца и количество дней
        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const daysInMonth = lastDay.getDate();

        let firstDayOfWeek = firstDay.getDay();
        if (firstDayOfWeek === 0) firstDayOfWeek = 7; // воскресенье -> 7

        // Пустые ячейки перед первым днём
        for (let i = 1; i < firstDayOfWeek; i++) {
            calendarGrid.appendChild(createEmptyDay());
        }

        let selectedInThisMonth = false; // был ли уже выбран день в этом месяце

        for (let day = 1; day <= daysInMonth; day++) {
            const dayElement = createDayElement(year, month, day);
            const isToday = (year === todayDate.year && month === todayDate.month && day === todayDate.day);
            if (isToday) dayElement.classList.add('today');

            const dateKey = getDateKey(year, month, day);
            const hasAvailable = hasAvailableSlotsForDate(dateKey, { year, month, day });
            const isClickable = !isPastDate(year, month, day) && hasAvailable;

            const isGlobalSelected = globalSelectedDate &&
                globalSelectedDate.year === year &&
                globalSelectedDate.month === month &&
                globalSelectedDate.day === day;

            if (!isClickable) {
                dayElement.classList.add('unavailable');
                dayElement.style.opacity = '0.5';
                dayElement.style.pointerEvents = 'none';
                dayElement.style.cursor = 'not-allowed';
                dayElement.title = isPastDate(year, month, day) ? 'Дата в прошлом' : 'Нет доступных слотов';
            } else {
                dayElement.addEventListener('click', createDayClickHandler(year, month, day));
            }

            calendarGrid.appendChild(dayElement);

            // Если это глобально выбранный день и он доступен, выделяем и показываем слоты
            if (isGlobalSelected && isClickable && !selectedInThisMonth) {
                dayElement.classList.add('selected');
                selectedDayElement = dayElement;
                selectedInThisMonth = true;
                if (slotsCache[dateKey]) {
                    displayTimeslots(slotsCache[dateKey]);
                } else {
                    setTimeout(() => sendDateToBackend(year, month, day), 100);
                }
            }
        }

        // Если в этом месяце ещё нет выбранного дня
        if (!selectedInThisMonth) {
            if (!globalSelectedDate) {
                // Нет глобальной даты — выбираем сегодня, если он кликабелен (даже если нет слотов)
                const todayEl = Array.from(document.querySelectorAll('.calendar-day:not(.empty)')).find(el => {
                    const d = parseInt(el.textContent);
                    const m = parseInt(el.dataset.month);
                    const y = parseInt(el.dataset.year);
                    return y === todayDate.year && m === todayDate.month && d === todayDate.day;
                });

                if (todayEl) {
                    if (todayEl.classList.contains('unavailable')) {
                        // Визуально выделяем, но не загружаем слоты (их нет)
                        todayEl.classList.add('selected');
                        selectedDayElement = todayEl;
                        globalSelectedDate = { year: todayDate.year, month: todayDate.month, day: todayDate.day };
                        console.log('Визуально выбран сегодняшний день (без слотов)');
                    } else {
                        todayEl.classList.add('selected');
                        selectedDayElement = todayEl;
                        globalSelectedDate = { year: todayDate.year, month: todayDate.month, day: todayDate.day };
                        sendDateToBackend(todayDate.year, todayDate.month, todayDate.day);
                        console.log('Автоматически выбран сегодняшний день (доступен)');
                    }
                }
            }
            // Если глобальная дата есть, но не в этом месяце — ничего не делаем
        }
    };

    // Вспомогательные функции для рендеринга календаря
    const createEmptyDay = () => {
        const empty = document.createElement('div');
        empty.className = 'calendar-day empty';
        return empty;
    };

    const createDayElement = (year, month, day) => {
        const el = document.createElement('div');
        el.className = 'calendar-day';
        el.textContent = day;
        el.dataset.month = month;
        el.dataset.year = year;
        return el;
    };

    const isPastDate = (year, month, day) => {
        const cellDate = new Date(year, month, day);
        const startOfToday = new Date(todayDate.year, todayDate.month, todayDate.day);
        return cellDate < startOfToday;
    };

    const createDayClickHandler = (year, month, day) => (e) => {
        e.preventDefault();
        const dayEl = e.currentTarget;
        if (dayEl.classList.contains('selected')) {
            console.log('Этот день уже выбран');
            return;
        }

        resetSlotSelection();

        // Снимаем выделение со всех дней
        document.querySelectorAll('.calendar-day').forEach(d => d.classList.remove('selected'));

        // Обрабатываем сегодняшний день (подчёркивание)
        const allToday = document.querySelectorAll('.calendar-day.today, .calendar-day.today-black-underline');
        allToday.forEach(el => {
            const d = parseInt(el.textContent);
            const m = parseInt(el.dataset.month);
            const y = parseInt(el.dataset.year);
            const isTodayEl = (y === todayDate.year && m === todayDate.month && d === todayDate.day);
            if (isTodayEl) {
                if (el === dayEl) {
                    el.classList.remove('today-black-underline');
                    el.classList.add('today');
                } else {
                    el.classList.remove('today');
                    el.classList.add('today-black-underline');
                }
            }
        });

        dayEl.classList.add('selected');
        selectedDayElement = dayEl;
        globalSelectedDate = { year, month, day };
        console.log(`Выбрана дата: ${day}.${month + 1}.${year}`);
        sendDateToBackend(year, month, day);
    };

    // ==================== ПРЕДЗАГРУЗКА МЕСЯЦЕВ ====================
    const preloadMonths = async (startYear, startMonth, count) => {
        console.log(`Начинаем предзагрузку ${count} месяцев, начиная с ${startYear}-${startMonth+1}`);
        for (let i = 1; i <= count; i++) {
            let year = startYear;
            let month = startMonth + i;
            if (month > 11) {
                year += Math.floor(month / 12);
                month %= 12;
            }

            // Проверяем наличие данных для этого месяца
            const days = new Date(year, month + 1, 0).getDate();
            let hasData = false;
            for (let d = 1; d <= days; d++) {
                if (slotsCache[getDateKey(year, month, d)]) {
                    hasData = true;
                    break;
                }
            }

            if (!hasData) {
                console.log(`Предзагружаем месяц ${year}-${month+1}`);
                try {
                    const response = await fetch('/booking/select-date-time/month-slots', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ year, month: month + 1 })
                    });
                    if (response.ok) {
                        const slots = await response.json();
                        // Очищаем и заполняем кэш
                        for (let d = 1; d <= days; d++) delete slotsCache[getDateKey(year, month, d)];
                        if (slots && slots.length > 0) {
                            slots.forEach(slot => {
                                if (slot.slotDate) {
                                    const parts = slot.slotDate.split('-');
                                    if (parts.length === 3) {
                                        const day = parseInt(parts[2]);
                                        const key = getDateKey(year, month, day);
                                        if (!slotsCache[key]) slotsCache[key] = [];
                                        slotsCache[key].push(slot);
                                    }
                                }
                            });
                        }
                    }
                } catch (e) {
                    console.error('Ошибка предзагрузки месяца', e);
                }
                await new Promise(resolve => setTimeout(resolve, 300)); // небольшая пауза
            } else {
                console.log(`Месяц ${year}-${month+1} уже загружен`);
            }
        }
        console.log('Предзагрузка завершена');
    };

    // ==================== АНИМАЦИЯ ПЕРЕКЛЮЧЕНИЯ МЕСЯЦЕВ ====================
    const changeMonth = async (direction) => {
        if (isAnimating) return;
        isAnimating = true;

        calendarGrid.style.opacity = '0';
        calendarGrid.style.transform = 'scale(0.95)';
        calendarGrid.style.transition = 'opacity 0.2s ease, transform 0.2s ease';

        if (direction === 'prev') {
            currentDate.setMonth(currentDate.getMonth() - 1);
        } else {
            currentDate.setMonth(currentDate.getMonth() + 1);
        }

        await new Promise(resolve => setTimeout(resolve, 200));
        await loadMonthData(currentDate.getFullYear(), currentDate.getMonth());

        calendarGrid.style.opacity = '1';
        calendarGrid.style.transform = 'scale(1)';

        setTimeout(() => { isAnimating = false; }, 200);
    };

    // ==================== ИНДИКАТОР ЗАГРУЗКИ ====================
    const showLoadingIndicator = () => {
        const loader = document.getElementById('loading-indicator');
        if (loader) loader.style.display = 'block';
    };

    const hideLoadingIndicator = () => {
        const loader = document.getElementById('loading-indicator');
        if (loader) loader.style.display = 'none';
    };

    // ==================== ИНИЦИАЛИЗАЦИЯ ====================
    const initialize = async () => {
        await loadMonthData(currentDate.getFullYear(), currentDate.getMonth());
        preloadMonths(currentDate.getFullYear(), currentDate.getMonth(), PRELOAD_MONTHS)
            .then(() => console.log('Предзагрузка завершена в фоне'));
    };

    initialize();

    // ==================== ОБРАБОТЧИКИ ====================
    prevBtn.addEventListener('click', () => changeMonth('prev'));
    nextBtn.addEventListener('click', () => changeMonth('next'));

    if (confirmWrapper) {
        confirmWrapper.style.display = 'none';
        confirmWrapper.style.opacity = '0';
        confirmWrapper.style.transform = 'translateY(20px)';
        confirmWrapper.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
    }

    if (confirmBtn) {
        confirmBtn.addEventListener('click', (e) => {
            e.preventDefault();
            if (selectedSlotId) {
                console.log('Подтверждение записи для слота:', selectedSlotId);
                fetch('/booking/confirm', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ slotId: selectedSlotId })
                }).then(response => {
                    if (response.ok) window.location.href = '/booking/success';
                });
            }
        });
    }

    const headerLink = document.querySelector('.header-link');
    if (headerLink) {
        headerLink.addEventListener('click', (e) => {
            e.preventDefault();
            window.location.href = '/booking';
        });
    }
});