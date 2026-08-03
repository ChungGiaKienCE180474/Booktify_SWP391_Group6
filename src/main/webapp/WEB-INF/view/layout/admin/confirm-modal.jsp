<div id="confirmModal" class="modal-overlay" style="display:none;" onclick="closeConfirmModal()">
    <div class="modal-box" style="max-width:420px;" onclick="event.stopPropagation()">
        <div class="modal-header">
            <h3 id="confirmModalTitle">
                <i class="fa-solid fa-circle-exclamation" style="color:#EF4444;"></i>
                Xác nhận
            </h3>
        </div>
        <div class="modal-body" style="display:block;">
            <p id="confirmModalMsg" style="margin:0;font-size:.9rem;color:#374151;line-height:1.65;"></p>
        </div>
        <div class="modal-footer">
            <button onclick="closeConfirmModal()" class="admin-button admin-button--ghost">
                <i class="fa-solid fa-xmark"></i> Hủy
            </button>
            <form id="confirmForm" method="post" style="display:inline;">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <button type="submit" id="confirmSubmitBtn" class="admin-button admin-button--danger">
                    <i class="fa-solid fa-check"></i> Xác nhận
                </button>
            </form>
        </div>
    </div>
</div>