<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Shared admin toast notification renderer.
     Include this once, anywhere near the end of <body>, on any admin page
     that redirects back with successMessage / errorMessage / warningMessage
     flash attributes (delete, restore, hide, create, update, ban, unban...).
     Requires admin-dashboard.css (.toast / .toast-container classes) to
     already be linked on the page. --%>
<div id="toastContainer" class="toast-container"></div>
<c:if test="${not empty successMessage}">
    <div id="toastSuccessMessage" style="display:none;"><c:out value="${successMessage}"/></div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div id="toastErrorMessage" style="display:none;"><c:out value="${errorMessage}"/></div>
</c:if>
<c:if test="${not empty warningMessage}">
    <div id="toastWarningMessage" style="display:none;"><c:out value="${warningMessage}"/></div>
</c:if>
<script>
    (function () {
        function showToast(msg, type) {
            var tc = document.getElementById('toastContainer');
            if (!tc || !msg) return;
            var t = document.createElement('div');
            t.className = 'toast toast--' + type;
            var icon = type === 'success' ? 'fa-circle-check'
                : (type === 'warning' ? 'fa-triangle-exclamation' : 'fa-circle-exclamation');
            t.innerHTML = '<i class="fa-solid ' + icon + '"></i> ' + msg;
            tc.appendChild(t);
            setTimeout(function () { t.classList.add('toast--show'); }, 10);
            setTimeout(function () {
                t.classList.remove('toast--show');
                setTimeout(function () { t.remove(); }, 320);
            }, 3500);
        }
        // Exposed globally in case a page wants to fire its own toast later
        // (e.g. from an AJAX callback) instead of only on page load.
        window.showToast = showToast;

        var ts = document.getElementById('toastSuccessMessage');
        if (ts) showToast(ts.textContent.trim(), 'success');
        var te = document.getElementById('toastErrorMessage');
        if (te) showToast(te.textContent.trim(), 'error');
        var tw = document.getElementById('toastWarningMessage');
        if (tw) showToast(tw.textContent.trim(), 'warning');
    })();
</script>
