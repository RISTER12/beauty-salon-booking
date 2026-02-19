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

    const today = new Date();
    const todayDate = {
        day: today.getDate(),
        month: today.getMonth(),
        year: today.getFullYear()
    };

    const mainElement = document.querySelector('main');
    const slotsCache = {};

    // ========== ВЕСЬ ЭТОТ БЛОК УДАЛЕН (строки 26-55) ==========
    // Теперь скролл работает всегда естественно

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

        slots.forEach(slot => {
            const slotSpan = document.createElement('span');
            slotSpan.className = 'time-slot';
            slotSpan.dataset.slotId = slot.id;
            slotSpan.textContent = formatTime(slot.startTime);

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
                content.style.maxHeight = content.scrollHeight + 'px';
                icon.textContent = '▼';
                icon.style.transform = 'rotate(0deg)';

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

        return section;
    }

    function displayTimeslots(timeslots) {
        console.log('Получены слоты:', timeslots);

        resetSlotSelection();

        const existingSections = document.querySelectorAll('.collapsible-section');
        existingSections.forEach(el => el.remove());

        if (!timeslots || timeslots.length === 0) {
            const noSlotsMessage = document.createElement('div');
            noSlotsMessage.className = 'no-slots-message';
            noSlotsMessage.textContent = 'На выбранную дату нет свободных слотов';
            mainElement.appendChild(noSlotsMessage);
            return;
        }

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

        if (slotsByCategory.morning.length > 0) {
            const morningSection = createCollapsibleSection('Утро', slotsByCategory.morning, delay);
            mainElement.appendChild(morningSection);
            delay += 0.1;
        }

        if (slotsByCategory.day.length > 0) {
            const daySection = createCollapsibleSection('День', slotsByCategory.day, delay);
            mainElement.appendChild(daySection);
            delay += 0.1;
        }

        if (slotsByCategory.evening.length > 0) {
            const eveningSection = createCollapsibleSection('Вечер', slotsByCategory.evening, delay);
            mainElement.appendChild(eveningSection);
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

    async function sendDateToBackend(year, month, day) {
        const dateKey = `${year}-${month + 1}-${day}`;

        const dateData = {
            year: year,
            month: month + 1,
            day: day,
            dateString: `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
        };

        const oldContent = document.querySelectorAll('.collapsible-section, .no-slots-message, .error-message');
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

        let foundTodayInThisMonth = false;

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

            if (isPastDate) {
                dayElement.classList.add('past');
                dayElement.style.opacity = '0.5';
                dayElement.style.pointerEvents = 'none';
                dayElement.style.cursor = 'not-allowed';
            } else {
                dayElement.addEventListener('click', function (e) {
                    e.preventDefault();

                    resetSlotSelection();

                    const allTodayElements = document.querySelectorAll('.calendar-day.today, .calendar-day.today-black-underline');

                    document.querySelectorAll('.calendar-day').forEach(d => {
                        d.classList.remove('selected');
                    });

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

                    console.log(`Выбрана дата: ${day}.${month + 1}.${year}`);
                    sendDateToBackend(year, month, day);
                });
            }

            dayElement.dataset.month = month;
            dayElement.dataset.year = year;
            calendarGrid.appendChild(dayElement);

            if (isToday && !isPastDate && !foundTodayInThisMonth) {
                foundTodayInThisMonth = true;

                setTimeout(() => {
                    document.querySelectorAll('.calendar-day').forEach(d => {
                        d.classList.remove('selected');
                    });

                    dayElement.classList.add('selected');
                    selectedDayElement = dayElement;

                    console.log('Автоматически выбрана сегодняшняя дата:',
                        todayDate.day, todayDate.month + 1, todayDate.year);
                    sendDateToBackend(todayDate.year, todayDate.month, todayDate.day);
                }, 100);
            }
        }

        if (!foundTodayInThisMonth && !selectedDayElement) {
            const firstAvailableDay = Array.from(document.querySelectorAll('.calendar-day:not(.empty):not(.past)'))[0];
            if (firstAvailableDay) {
                setTimeout(() => {
                    firstAvailableDay.classList.add('selected');
                    selectedDayElement = firstAvailableDay;

                    const day = parseInt(firstAvailableDay.textContent);
                    console.log(`Автоматически выбран первый доступный день: ${day}.${month + 1}.${year}`);
                    sendDateToBackend(year, month, day);
                }, 100);
            }
        }
    }

    prevBtn.addEventListener('click', function () {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar();
    });

    nextBtn.addEventListener('click', function () {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar();
    });

    renderCalendar();

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