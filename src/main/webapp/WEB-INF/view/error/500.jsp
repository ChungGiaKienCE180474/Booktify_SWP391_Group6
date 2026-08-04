<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Something went wrong — Booktify</title>
    <link href="/css/styles.css" rel="stylesheet" />
    <script src="https://use.fontawesome.com/releases/v6.3.0/js/all.js" crossorigin="anonymous"></script>
    <style>
        .err-wrap { max-width: 560px; margin: 6rem auto; text-align: center; font-family: system-ui, sans-serif; padding: 0 1rem; }
        .err-icon { font-size: 3rem; color: #E53935; margin-bottom: 1rem; }
        .err-title { font-size: 1.5rem; font-weight: 700; color: #111827; margin-bottom: .5rem; }
        .err-msg { color: #6B7280; line-height: 1.6; margin-bottom: 1.5rem; }
        .err-ref { font-size: .8rem; color: #9CA3AF; margin-bottom: 1.5rem; }
        .err-home { display: inline-flex; align-items: center; gap: .5rem; padding: .7rem 1.5rem;
            background: #006B5E; color: #fff; border-radius: 8px; text-decoration: none; font-weight: 600; }
    </style>
</head>
<body>
    <div class="err-wrap">
        <div class="err-icon"><i class="fa-solid fa-triangle-exclamation"></i></div>
        <div class="err-title">Something went wrong</div>
        <p class="err-msg">
            We ran into an unexpected problem while processing your request.
            Please try again in a moment. If the issue keeps happening, contact support.
        </p>
        <c:if test="${not empty errorRef}">
            <p class="err-ref">Reference: <c:out value="${errorRef}"/></p>
        </c:if>
        <a href="/" class="err-home"><i class="fa-solid fa-house"></i> Back to home</a>
    </div>
</body>
</html>
