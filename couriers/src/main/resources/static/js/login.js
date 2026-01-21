document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('password').addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            handleLogin();
        }
    });
});

function handleLogin() {
    const u = document.getElementById('username').value;
    const p = document.getElementById('password').value;

    if (!u || !p) {
        alert("Введите логин и пароль");
        return;
    }
    login(u, p);
}

async function calculatePrice() {
    const from = document.getElementById('calcFrom').value;
    const to = document.getElementById('calcTo').value;
    const weight = document.getElementById('calcWeight').value;
    const resultBox = document.getElementById('calcResult');

    if (!from || !to || !weight) {
        alert("Заполните все поля!");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8089/api/orders/calculate?from=${from}&to=${to}&weight=${weight}`, {
            method: 'GET'
        });

        if (response.ok) {
            const price = await response.json();
            resultBox.classList.remove('d-none', 'alert-danger');
            resultBox.classList.add('alert-info');
            resultBox.innerHTML = `💵 Стоимость: <strong>${price} RUB</strong>`;
        } else {
            resultBox.classList.remove('d-none', 'alert-info');
            resultBox.classList.add('alert-danger');
            resultBox.innerText = "Ошибка расчета (возможно, неверный адрес)";
        }
    } catch (e) {
        console.error(e);
        resultBox.classList.remove('d-none', 'alert-info');
        resultBox.classList.add('alert-danger');
        resultBox.innerText = "Ошибка соединения с сервером";
    }
}