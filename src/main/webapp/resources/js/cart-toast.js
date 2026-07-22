(function () {
    function ensureContainer() {
        var container = document.getElementById('cartToastContainer');
        if (container) {
            return container;
        }
        container = document.createElement('div');
        container.id = 'cartToastContainer';
        container.className = 'cart-toast-container';
        document.body.appendChild(container);
        return container;
    }

    function updateCartBadge(count) {
        if (typeof count !== 'number') {
            return;
        }
        var badge = document.getElementById('hdrCartBadge');
        if (!badge) {
            return;
        }
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : String(count);
            badge.hidden = false;
        } else {
            badge.hidden = true;
        }
    }

    window.showCartToast = function (message, type) {
        if (!message) {
            return;
        }
        var container = ensureContainer();
        var toast = document.createElement('div');
        var toastType = type === 'error' ? 'error' : 'success';
        var icon = toastType === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation';

        toast.className = 'cart-toast cart-toast--' + toastType;
        toast.innerHTML = '<i class="fa-solid ' + icon + '"></i><span></span>';
        toast.querySelector('span').textContent = message;

        container.appendChild(toast);
        requestAnimationFrame(function () {
            toast.classList.add('cart-toast--show');
        });

        setTimeout(function () {
            toast.classList.remove('cart-toast--show');
            setTimeout(function () {
                toast.remove();
            }, 320);
        }, 3500);
    };

    function submitCartForm(form) {
        var submitBtn = form.querySelector('[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = true;
        }

        fetch(form.action, {
            method: 'POST',
            body: new FormData(form),
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            credentials: 'same-origin'
        })
            .then(function (response) {
                return response.json().then(function (data) {
                    if (!response.ok || !data.success) {
                        throw new Error(data.message || 'Unable to add item to cart.');
                    }
                    return data;
                });
            })
            .then(function (data) {
                showCartToast(data.message, 'success');
                updateCartBadge(data.cartItemCount);
            })
            .catch(function (error) {
                showCartToast(error.message || 'Unable to add item to cart.', 'error');
            })
            .finally(function () {
                if (submitBtn) {
                    submitBtn.disabled = false;
                }
            });
    }

    document.addEventListener('submit', function (event) {
        var form = event.target;
        if (!(form instanceof HTMLFormElement) || !form.classList.contains('js-cart-add-form')) {
            return;
        }
        event.preventDefault();
        submitCartForm(form);
    });

    document.addEventListener('DOMContentLoaded', function () {
        var flashSuccess = document.getElementById('cartFlashSuccess');
        if (flashSuccess && flashSuccess.textContent.trim()) {
            showCartToast(flashSuccess.textContent.trim(), 'success');
        }

        var flashError = document.getElementById('cartFlashError');
        if (flashError && flashError.textContent.trim()) {
            showCartToast(flashError.textContent.trim(), 'error');
        }
    });
})();
