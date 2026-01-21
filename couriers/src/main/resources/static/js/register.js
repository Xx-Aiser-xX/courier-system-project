document.addEventListener('DOMContentLoaded', () => {
    const tabUser = document.getElementById('tab-user');
    const tabCourier = document.getElementById('tab-courier');
    const courierField = document.getElementById('courierField');
    const form = document.getElementById('regForm');

    let isCourier = false;

    tabUser.addEventListener('click', () => {
        isCourier = false;
        tabUser.classList.add('active');
        tabCourier.classList.remove('active');
        courierField.classList.add('d-none');
    });

    tabCourier.addEventListener('click', () => {
        isCourier = true;
        tabCourier.classList.add('active');
        tabUser.classList.remove('active');
        courierField.classList.remove('d-none');
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const data = {
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            name: document.getElementById('name').value,
            phone: document.getElementById('phone').value
        };

        let endpoint = '/api/auth/register/user';

        if (isCourier) {
            data.deliveryMethod = document.getElementById('deliveryMethod').value;
            endpoint = '/api/auth/register/courier';
        }

        try {
            const response = await fetch(`http://localhost:8089${endpoint}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert('Регистрация успешна! Теперь войдите.');
                await login(data.email, data.password);
            } else {
                const err = await response.text();
                alert('Ошибка: ' + err);
            }
        } catch (error) {
            console.error(error);
            alert('Ошибка соединения с сервером');
        }
    });
});