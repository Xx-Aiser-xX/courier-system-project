let socket;
let currentOfferId = null;
let currentOrderId = null;
let timerInterval;
let offerModal;

document.addEventListener('DOMContentLoaded', () => {
    offerModal = new bootstrap.Modal(document.getElementById('offerModal'));

    const userId = localStorage.getItem('user_id');
    if (!userId) {
        window.location.href = 'index.html';
        return;
    }
    connectWebSocket(userId);
});

function connectWebSocket(userId) {
    socket = new WebSocket(`ws://localhost:8083/ws/orders?userId=${userId}`);

    socket.onopen = () => console.log("WS Connected");

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log("WS Message:", data);

        if (data.type === 'OFFER') {
            showOffer(data.orderId);
        } else if (data.type === 'ASSIGNED') {
            startActiveOrder(data.orderId);
        }
    };

    socket.onclose = () => {
        console.warn("WS Connection closed. Reconnecting...");
        setTimeout(() => connectWebSocket(userId), 3000);
    };
}

function toggleWork() {
    const checkbox = document.getElementById('workSwitch');
    const isActive = checkbox.checked;
    const label = document.getElementById('workStatusLabel');
    const userId = localStorage.getItem('user_id');

    const status = isActive ? 'FREE' : 'INACTIVE';
    apiRequest(`/couriers/status`, 'PUT', { status: status })
        .then(res => {
            if (res && res.ok) {
                label.innerText = isActive ? 'На линии' : 'Оффлайн';
            } else {
                alert('Ошибка смены статуса');
                checkbox.checked = !isActive;
            }
        });
}

function showOffer(orderId) {
    currentOfferId = orderId;
    document.getElementById('offerOrderIdShort').innerText = orderId.substring(0, 8);
    let timeLeft = 30;
    const timerElement = document.getElementById('timer');
    timerElement.innerText = timeLeft;

    offerModal.show();

    clearInterval(timerInterval);
    timerInterval = setInterval(() => {
        timeLeft--;
        timerElement.innerText = timeLeft;
        if (timeLeft <= 0) {
            clearInterval(timerInterval);
            offerModal.hide();
        }
    }, 1000);
}

function acceptOrder() {
    clearInterval(timerInterval);
    offerModal.hide();
    apiRequest(`/couriers/orders/${currentOfferId}/accept`, 'POST');
}

function declineOrder() {
    clearInterval(timerInterval);
    offerModal.hide();
    apiRequest(`/couriers/orders/${currentOfferId}/decline`, 'POST');
}

function startActiveOrder(orderId) {
    currentOrderId = orderId;
    document.getElementById('activeOrderPanel').classList.remove('d-none');

    document.getElementById('orderFrom').innerText = "Загрузка...";
    document.getElementById('orderTo').innerText = "Загрузка...";

    apiRequest(`/orders/${orderId}`, 'GET').then(async res => {
        if(res.ok) {
            const orderData = await res.json();
            document.getElementById('orderFrom').innerText = orderData.senderAddress;
            document.getElementById('orderTo').innerText = orderData.recipientAddress;
        }
    });
}

function changeStatus(newStatus) {
    apiRequest(`/couriers/orders/${currentOrderId}/change-status?newStatus=${newStatus}`, 'POST')
        .then(() => {
            if (newStatus === 'IN_TRANSIT') {
                document.getElementById('btnPickup').classList.add('d-none');
                document.getElementById('btnDeliver').classList.remove('d-none');
            } else if (newStatus === 'DELIVERED') {
                alert('Заказ завершен!');
                document.getElementById('activeOrderPanel').classList.add('d-none');
                document.getElementById('btnPickup').classList.remove('d-none');
                document.getElementById('btnDeliver').classList.add('d-none');
            }
        });
}