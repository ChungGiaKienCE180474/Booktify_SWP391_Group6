document.addEventListener(
    "DOMContentLoaded",
    function () {

        const checkoutForm =
            document.getElementById(
                "checkoutForm"
            );

        if (!checkoutForm) {
            console.error(
                "Checkout form was not found."
            );

            return;
        }

        const promotionControls =
            checkoutForm.querySelectorAll(
                ".promotion-selection"
            );

        promotionControls.forEach(
            function (control) {

                control.addEventListener(
                    "change",
                    function () {

                        updatePromotionPreview(
                            checkoutForm
                        );
                    }
                );
            }
        );
    }
);

function updatePromotionPreview(
        checkoutForm) {

    if (!checkoutForm) {
        return;
    }

    /*
     * Lấy toàn bộ dữ liệu hiện tại của form.
     *
     * Bao gồm:
     * - bookPromotionSelections[bookId]
     * - voucherCode
     * - recipientName
     * - recipientPhone
     * - shippingAddress
     * - _csrf
     */
    const formData =
        new FormData(checkoutForm);

    const requestBody =
        new URLSearchParams();

    formData.forEach(
        function (value, key) {

            requestBody.append(
                key,
                value
            );
        }
    );

    setPromotionControlsDisabled(
        checkoutForm,
        true
    );

    fetch(
        "/orders/preview-promotion",
        {
            method: "POST",

            headers: {
                "Content-Type":
                    "application/x-www-form-urlencoded;charset=UTF-8",

                "X-Requested-With":
                    "XMLHttpRequest"
            },

            credentials: "same-origin",

            body: requestBody.toString()
        }
    )
    .then(
        async function (response) {

            const contentType =
                response.headers.get(
                    "content-type"
                ) || "";

            if (!contentType.includes(
                    "application/json")) {

                const responseText =
                    await response.text();

                console.error(
                    "Server returned non-JSON response:",
                    responseText
                );

                throw new Error(
                    "The server did not return promotion data."
                );
            }

            const data =
                await response.json();

            if (!response.ok) {
                throw new Error(
                    data.message ||
                    "Unable to calculate promotion."
                );
            }

            return data;
        }
    )
    .then(
        function (data) {

            updateText(
                "originalSubtotalDisplay",
                formatMoney(
                    data.originalSubtotalFormatted
                )
            );

            updateText(
                "promotionDiscountDisplay",
                "-" + formatMoney(
                    data.promotionDiscountFormatted
                )
            );

            updateText(
                "voucherDiscountDisplay",
                "-" + formatMoney(
                    data.voucherDiscountFormatted
                )
            );

            updateText(
                "checkoutTotalDisplay",
                formatMoney(
                    data.finalTotalFormatted
                )
            );

            showPreviewMessage(
                data.message ||
                "Promotion prices updated.",
                false
            );
        }
    )
    .catch(
        function (error) {

            console.error(
                "Promotion preview error:",
                error
            );

            showPreviewMessage(
                error.message ||
                "Unable to calculate promotion.",
                true
            );
        }
    )
    .finally(
        function () {

            setPromotionControlsDisabled(
                checkoutForm,
                false
            );
        }
    );
}

function updateText(
        elementId,
        value) {

    const element =
        document.getElementById(
            elementId
        );

    if (element) {
        element.textContent = value;
    }
}

function formatMoney(value) {

    if (value === null
            || value === undefined
            || value === "") {

        return "0 ₫";
    }

    return value + " ₫";
}

function setPromotionControlsDisabled(
        checkoutForm,
        disabled) {

    const controls =
        checkoutForm.querySelectorAll(
            ".promotion-selection"
        );

    controls.forEach(
        function (control) {

            control.disabled =
                disabled;
        }
    );
}

function showPreviewMessage(
        message,
        isError) {

    const messageElement =
        document.getElementById(
            "promotionPreviewMessage"
        );

    if (!messageElement) {
        return;
    }

    messageElement.hidden = false;

    messageElement.textContent =
        message;

    messageElement.className =
        "cart-alert checkout-preview-message " +
        (
            isError
                ? "cart-alert--error is-error"
                : "cart-alert--success is-success"
        );

    if (window.promotionMessageTimer) {
        window.clearTimeout(
            window.promotionMessageTimer
        );
    }

    window.promotionMessageTimer =
        window.setTimeout(
            function () {

                messageElement.hidden =
                    true;
            },
            2500
        );
}