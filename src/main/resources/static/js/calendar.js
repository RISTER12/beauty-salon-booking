/**
 * Календарь для выбора даты и времени записи.
 * Поддерживает фильтрацию по сотруднику (employeeId).
 */
document.addEventListener('DOMContentLoaded', () => {
    // ==================== ЭЛЕМЕНТЫ DOM ====================
    const monthElement = document.getElementById('month');
    const calendarGrid = document.getElementById('calendar-grid');
    const prevBtn = document.querySelector('.prev-month');
    const nextBtn = document.querySelector('.next-month');
    const confirmBtn = document.querySelector('#go button');
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
    const PRELOAD_MONTHS = 11;

    // ==================== ПАРАМЕТРЫ ИЗ URL ====================
    const selectedEmployeeId = window.selectedEmployeeId ?? null;

    // ==================== СОСТОЯНИЕ ====================
    let currentDate = new Date();
    let selectedDayElement = null;
    let selectedSlotId = null;
    let globalSelectedDate = null;
    let currentlyDisplayedDateKey = null;
    let isAnimating = false;

    // ==================== КЭШИ ====================
    const getCacheKey = (dateKey) => selectedEmployeeId ? `${dateKey}-emp${selectedEmployeeId}` : dateKey;
    const slotsCache = {};
    const renderedSlotsCache = {};

    // Сегодняшняя дата
    const today = new Date();
    const todayDate = {
        day: today.getDate(),
        month: today.getMonth(),
        year: today.getFullYear()
    };

    // ==================== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ====================
    const getDateKey = (year, month, day) => `${year}-${month + 1}-${day}`;

    const getTimeCategory = (time) => {
        const hour = parseInt(time.split(':')[0]);
        if (hour < 12) return 'morning';
        if (hour < 18) return 'day';
        return 'evening';
    };

    const formatTime = (time) => time.substring(0, 5);

    // ==================== КНОПКА ПОДТВЕРЖДЕНИЯ ====================
    const updateConfirmButtonVisibility = () => {
        console.log('updateConfirmButtonVisibility, selectedSlotId =', selectedSlotId);
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

    // ==================== РАБОТА С ДАННЫМИ ====================
    const hasAvailableSlotsForDate = (dateKey, date) => {
        const cacheKey = getCacheKey(dateKey);
        const slots = slotsCache[cacheKey];
        if (!slots || slots.length === 0) return false;

        const now = new Date();
        const currentHour = now.getHours();
        const currentMinute = now.getMinutes();

        const selectedDate = new Date(date.year, date.month, date.day);
        const isToday = selectedDate.getTime() === new Date(todayDate.year, todayDate.month, todayDate.day).getTime();

        if (selectedDate > new Date(todayDate.year, todayDate.month, todayDate.day)) return true;
        if (isToday) {
            return slots.some(slot => {
                const [slotHour, slotMinute] = slot.startTime.split(':').map(Number);
                return (slotHour > currentHour) || (slotHour === currentHour && slotMinute > currentMinute);
            });
        }
        return false;
    };

    const loadMonthData = async (year, month) => {
        currentlyDisplayedDateKey = null;

        const daysInMonth = new Date(year, month + 1, 0).getDate();
        let hasMonthData = false;
        for (let d = 1; d <= daysInMonth; d++) {
            const cacheKey = getCacheKey(getDateKey(year, month, d));
            if (slotsCache[cacheKey]) {
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
            const body = { year, month: month + 1 };
            if (selectedEmployeeId) body.employeeId = selectedEmployeeId;

            const response = await fetch('/booking/select-date-time/month-slots', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                console.error('Ошибка загрузки данных месяца:', response.status);
                return;
            }

            const allMonthSlots = await response.json();
            console.log('Получены данные за месяц:', allMonthSlots);

            for (let d = 1; d <= daysInMonth; d++) {
                const cacheKey = getCacheKey(getDateKey(year, month, d));
                delete slotsCache[cacheKey];
            }

            if (allMonthSlots && allMonthSlots.length > 0) {
                allMonthSlots.forEach(slot => {
                    if (!slot.slotDate) return;
                    const parts = slot.slotDate.split('-');
                    if (parts.length !== 3) return;
                    const day = parseInt(parts[2]);
                    const cacheKey = getCacheKey(getDateKey(year, month, day));
                    if (!slotsCache[cacheKey]) slotsCache[cacheKey] = [];
                    slotsCache[cacheKey].push(slot);
                });
            }
            console.log('Кэш обновлён:', slotsCache);
        } catch (error) {
            console.error('Ошибка при загрузке данных месяца:', error);
        }

        renderCalendar();
    };

    const sendDateToBackend = async (year, month, day) => {
        const dateKey = getDateKey(year, month, day);
        const cacheKey = getCacheKey(dateKey);

        if (slotsCache[cacheKey]) {
            console.log('Данные для даты уже есть в кэше', dateKey);
            displayTimeslots(slotsCache[cacheKey]);
            return;
        }

        const dateData = {
            year,
            month: month + 1,
            day,
            dateString: `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
        };
        if (selectedEmployeeId) dateData.employeeId = selectedEmployeeId;

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
            console.log('Ответ от сервера (слоты дня):', result);

            slotsCache[cacheKey] = result;
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
    const createCollapsibleSection = (title, slots, delay) => {
        const now = new Date();
        const currentHour = now.getHours();
        const currentMinute = now.getMinutes();

        const filteredSlots = slots.filter(slot => {
            const slotDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), parseInt(slot.slotDate.split('-')[2]));
            const isToday = slotDate.getTime() === new Date(todayDate.year, todayDate.month, todayDate.day).getTime();
            if (slotDate > new Date(todayDate.year, todayDate.month, todayDate.day)) return true;
            if (isToday) {
                const [slotHour, slotMinute] = slot.startTime.split(':').map(Number);
                return (slotHour > currentHour) || (slotHour === currentHour && slotMinute > currentMinute);
            }
            return true;
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
            // Важно: проверяем наличие id
            if (!slot.id) {
                console.warn('Слот без id!', slot);
            }
            slotSpan.dataset.slotId = slot.id;
            slotSpan.textContent = formatTime(slot.startTime);
            if (selectedSlotId && selectedSlotId === slot.id.toString()) {
                slotSpan.classList.add('selected');
            }

            slotSpan.addEventListener('click', (e) => {
                e.stopPropagation();
                console.log('Клик по слоту, dataset.slotId =', slotSpan.dataset.slotId);
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

    const displayTimeslots = (timeslots) => {
        let dateKey = null;
        if (timeslots && timeslots.length > 0 && timeslots[0].slotDate) {
            const parts = timeslots[0].slotDate.split('-');
            if (parts.length === 3) {
                dateKey = `${parts[0]}-${parseInt(parts[1])}-${parseInt(parts[2])}`;
            }
        }

        if (dateKey && currentlyDisplayedDateKey === dateKey) {
            console.log('Слоты для этой даты уже отображаются, пропускаем');
            return;
        }

        if (dateKey) {
            const cacheKey = getCacheKey(dateKey);
            if (renderedSlotsCache[cacheKey]) {
                console.log('Используем кэшированный рендер для', dateKey);
                document.querySelectorAll('.collapsible-section').forEach(el => el.remove());
                renderedSlotsCache[cacheKey].forEach(section => mainElement.appendChild(section));
                document.querySelectorAll('.time-slot').forEach(slot => slot.classList.remove('selected'));
                if (selectedSlotId) {
                    const selectedSlot = document.querySelector(`.time-slot[data-slot-id="${selectedSlotId}"]`);
                    if (selectedSlot) selectedSlot.classList.add('selected');
                }
                currentlyDisplayedDateKey = dateKey;
                return;
            }
        }

        console.log('Получены слоты:', timeslots);
        resetSlotSelection();

        document.querySelectorAll('.collapsible-section').forEach(el => el.remove());

        if (!timeslots || timeslots.length === 0) {
            showNoSlotsMessage();
            currentlyDisplayedDateKey = dateKey;
            return;
        }

        const grouped = { morning: [], day: [], evening: [] };
        timeslots.forEach(slot => {
            const category = getTimeCategory(slot.startTime);
            grouped[category].push(slot);
        });

        let delay = 0.1;
        const createdSections = [];

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

        if (createdSections.length === 0) {
            showNoSlotsMessage(true);
        }

        setTimeout(() => {
            document.querySelectorAll('.collapsible-section .section-header').forEach(header => {
                const content = header.nextElementSibling;
                if (content?.children[0]?.children.length > 0) {
                    content.style.maxHeight = content.scrollHeight + 'px';
                    header.querySelector('.toggle-icon').textContent = '▼';
                }
            });
        }, 500);

        if (dateKey) {
            const cacheKey = getCacheKey(dateKey);
            renderedSlotsCache[cacheKey] = createdSections;
        }
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

    const findNextAvailableDate = () => {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();
        const daysInMonth = new Date(year, month + 1, 0).getDate();

        for (let d = todayDate.day; d <= daysInMonth; d++) {
            const dateKey = getDateKey(year, month, d);
            if (hasAvailableSlotsForDate(dateKey, { year, month, day: d })) {
                return { year, month, day: d };
            }
        }

        let nextMonth = month + 1;
        let nextYear = year;
        if (nextMonth > 11) {
            nextYear++;
            nextMonth = 0;
        }
        const nextDays = new Date(nextYear, nextMonth + 1, 0).getDate();
        for (let d = 1; d <= nextDays; d++) {
            const dateKey = getDateKey(nextYear, nextMonth, d);
            if (hasAvailableSlotsForDate(dateKey, { year: nextYear, month: nextMonth, day: d })) {
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

        monthElement.textContent = `${MONTH_NAMES[month]} ${year}`;

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const daysInMonth = lastDay.getDate();

        let firstDayOfWeek = firstDay.getDay();
        if (firstDayOfWeek === 0) firstDayOfWeek = 7;

        for (let i = 1; i < firstDayOfWeek; i++) {
            calendarGrid.appendChild(createEmptyDay());
        }

        let selectedInThisMonth = false;

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

            if (isGlobalSelected && isClickable && !selectedInThisMonth) {
                dayElement.classList.add('selected');
                selectedDayElement = dayElement;
                selectedInThisMonth = true;
                const cacheKey = getCacheKey(dateKey);
                if (slotsCache[cacheKey]) {
                    displayTimeslots(slotsCache[cacheKey]);
                } else {
                    setTimeout(() => sendDateToBackend(year, month, day), 100);
                }
            }
        }

        if (!selectedInThisMonth) {
            if (!globalSelectedDate) {
                const todayEl = Array.from(document.querySelectorAll('.calendar-day:not(.empty)')).find(el => {
                    const d = parseInt(el.textContent);
                    const m = parseInt(el.dataset.month);
                    const y = parseInt(el.dataset.year);
                    return y === todayDate.year && m === todayDate.month && d === todayDate.day;
                });

                if (todayEl) {
                    if (todayEl.classList.contains('unavailable')) {
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
        }
    };

    // Вспомогательные функции календаря
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

        document.querySelectorAll('.calendar-day').forEach(d => d.classList.remove('selected'));

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

            const days = new Date(year, month + 1, 0).getDate();
            let hasData = false;
            for (let d = 1; d <= days; d++) {
                const cacheKey = getCacheKey(getDateKey(year, month, d));
                if (slotsCache[cacheKey]) {
                    hasData = true;
                    break;
                }
            }

            if (!hasData) {
                console.log(`Предзагружаем месяц ${year}-${month+1}`);
                try {
                    const body = { year, month: month + 1 };
                    if (selectedEmployeeId) body.employeeId = selectedEmployeeId;

                    const response = await fetch('/booking/select-date-time/month-slots', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(body)
                    });
                    if (response.ok) {
                        const slots = await response.json();
                        for (let d = 1; d <= days; d++) {
                            const cacheKey = getCacheKey(getDateKey(year, month, d));
                            delete slotsCache[cacheKey];
                        }
                        if (slots && slots.length > 0) {
                            slots.forEach(slot => {
                                if (slot.slotDate) {
                                    const parts = slot.slotDate.split('-');
                                    if (parts.length === 3) {
                                        const day = parseInt(parts[2]);
                                        const cacheKey = getCacheKey(getDateKey(year, month, day));
                                        if (!slotsCache[cacheKey]) slotsCache[cacheKey] = [];
                                        slotsCache[cacheKey].push(slot);
                                    }
                                }
                            });
                        }
                    }
                } catch (e) {
                    console.error('Ошибка предзагрузки месяца', e);
                }
                await new Promise(resolve => setTimeout(resolve, 300));
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
            console.log('Кнопка подтверждения, selectedSlotId =', selectedSlotId);
            if (!selectedSlotId) {
                console.warn('Слот не выбран');
                return;
            }
            const params = new URLSearchParams();
            params.append('slotId', selectedSlotId);
            if (selectedEmployeeId !== null && selectedEmployeeId !== undefined) {
                params.append('employeeId', selectedEmployeeId);
            }
            // Можно добавить дату, если нужно
            window.location.href = `/booking?${params.toString()}`;
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