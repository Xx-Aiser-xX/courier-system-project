let isGuest = true;

document.addEventListener('DOMContentLoaded', () => {
    const userId = localStorage.getItem('user_id');
    const token = localStorage.getItem('access_token');
    if (userId && token) {
        isGuest = false;
        enableUserMode(userId);
    }
});

function enableUserMode(userId) {
    document.getElementById('notificationArea').classList.remove('d-none');

    const socket = new WebSocket(`ws://localhost:8083/ws/orders?userId=${userId}`);
    socket.onmessage = (event) => addNotification(JSON.parse(event.data));
    socket.onclose = () => console.warn("WS Disconnected");
}

async function tryCreateOrder() {
    if (isGuest) {
        if(confirm("Для оформления заказа нужно войти. Переейти на страницу входа?")) {
            window.location.href = 'index.html';
        }
        return;
    }
    // Если авторизован - создаем заказ
    await createOrder();
}

async function calculatePrice() {
    const from = document.getElementById('from').value;
    const to = document.getElementById('to').value;
    const weight = document.getElementById('weight').value;
    const resultBox = document.getElementById('priceResult');
    const priceValue = document.getElementById('priceValue');

    if (!from || !to || !weight) {
        alert("Заполните все поля!");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8089/api/orders/calculate?from=${from}&to=${to}&weight=${weight}`);
        if (response.ok) {
            const price = await response.json();
            resultBox.classList.remove('d-none', 'alert-danger');
            resultBox.classList.add('alert-info');
            priceValue.innerText = price;
        } else {
            alert("Ошибка расчета");
        }
    } catch (e) {
        console.error(e);
        alert("Ошибка соединения");
    }
}

async function createOrder() {
    const fromInput = document.getElementById('from');
    const toInput = document.getElementById('to');
    const weightInput = document.getElementById('weight');

    const body = {
        senderAddress: fromInput.value,
        recipientAddress: toInput.value,
        weight: parseFloat(weightInput.value)
    };

    const res = await apiRequest('/orders', 'POST', body);
    if (res && res.ok) {
        addNotification({ text: "Заказ создан! Ищем курьера...", type: "INFO" });
    } else {
        alert('Ошибка создания заказа');
    }
}

function addNotification(data) {
    const list = document.getElementById('notifications');
    const item = document.createElement('div');
    let color = 'list-group-item-info';

    if (data.type === 'ORDER_CREATED') color = 'list-group-item-secondary';
    if (data.text && (data.text.includes('найден') || data.text.includes('назначен'))) color = 'list-group-item-success';

    item.className = `list-group-item ${color}`;
    const time = new Date().toLocaleTimeString();
    item.innerHTML = `<small class="text-muted">[${time}]</small> ${data.text || JSON.stringify(data)}`;
    list.prepend(item);
}