<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Contact Detail - Booktify</title>

            <link rel="stylesheet" href="/resources/client/css/bootstrap.min.css">
            <link rel="stylesheet" href="/resources/css/header.css">
            <link rel="stylesheet" href="/resources/css/footer.css">
            <link rel="stylesheet" href="/resources/css/contact.css">

            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        </head>

        <body>

            <jsp:include page="../layout/header.jsp" />

            <div class="contact-page">
                <div class="contact-container">

                    <div class="contact-breadcrumb">
                        <a href="/">Home</a>
                        <span>›</span>
                        <a href="/contact">Contact Us</a>
                        <span>›</span>
                        <span>Contact Detail</span>
                    </div>

                    <div class="contact-grid contact-grid-detail">

                        <div class="contact-card">
                            <h3>Contact Detail</h3>

                            <table class="detail-table">
                                <tr>
                                    <th>Subject:</th>
                                    <td>
                                        <c:out value="${request.subject}" />
                                    </td>
                                </tr>

                                <tr>
                                    <th>Created Date:</th>
                                    <td>
                                        <c:out value="${request.formattedCreatedAt}" />
                                    </td>
                                </tr>

                                <tr>
                                    <th>Status:</th>
                                    <td>
                                        <span class="status-badge status-${request.status}">
                                            <c:out value="${request.status}" />
                                        </span>
                                    </td>
                                </tr>
                            </table>

                            <hr>

                            <div class="detail-attachments-section">
                                <h4>Attached Images:</h4>

                                <c:set var="hasImage" value="false" />

                                <div class="detail-image-grid">
                                    <c:forEach var="att" items="${attachments}">
                                        <c:if test="${att.type == 'IMAGE'}">
                                            <c:set var="hasImage" value="true" />

                                            <a href="${att.filePath}" target="_blank" class="detail-image-item">
                                                <img src="${att.filePath}" alt="${att.fileName}"
                                                    class="detail-attached-image">
                                                <span>
                                                    <c:out value="${att.fileName}" />
                                                </span>
                                            </a>
                                        </c:if>
                                    </c:forEach>
                                </div>

                                <c:if test="${hasImage == false}">
                                    <c:choose>
                                        <c:when test="${not empty request.imagePath}">
                                            <a href="${request.imagePath}" target="_blank" class="detail-image-item">
                                                <img src="${request.imagePath}" alt="Attached image"
                                                    class="detail-attached-image">
                                            </a>
                                        </c:when>

                                        <c:otherwise>
                                            <p>No image.</p>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </div>

                            <div class="detail-attachments-section">
                                <h4>Attached Files:</h4>

                                <c:set var="hasFile" value="false" />

                                <div class="detail-file-list">
                                    <c:forEach var="att" items="${attachments}">
                                        <c:if test="${att.type == 'FILE'}">
                                            <c:set var="hasFile" value="true" />

                                            <a href="${att.filePath}" target="_blank" class="detail-file-item">
                                                <i class="fa-solid fa-file"></i>
                                                <span>
                                                    <c:out value="${att.fileName}" />
                                                </span>
                                            </a>
                                        </c:if>
                                    </c:forEach>
                                </div>

                                <c:if test="${hasFile == false}">
                                    <c:choose>
                                        <c:when test="${not empty request.filePath}">
                                            <a href="${request.filePath}" target="_blank" class="detail-file-item">
                                                <i class="fa-solid fa-file"></i>
                                                <span>
                                                    <c:out value="${request.fileName}" />
                                                </span>
                                            </a>
                                        </c:when>

                                        <c:otherwise>
                                            <p>No file.</p>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </div>

                            <hr>

                            <div class="detail-content-box">
                                <h4>Content:</h4>
                                <p>
                                    <c:out value="${request.content}" />
                                </p>
                            </div>

                            <a href="/contact" class="back-btn">
                                <i class="fa-solid fa-arrow-left"></i>
                                Back to Requests
                            </a>
                        </div>

                        <div class="contact-card">
                            <h3>Conversation</h3>

                            <div class="chat-box">
                                <c:choose>
                                    <c:when test="${not empty messages}">
                                        <c:forEach var="msg" items="${messages}">

                                            <c:set var="isMe"
                                                value="${msg.sender != null && msg.sender.email == currentPrincipalName}" />

                                            <div class="chat-message ${isMe ? 'chat-me' : 'chat-other'}">
                                                <div class="chat-meta">
                                                    <strong>
                                                        <c:choose>
                                                            <c:when test="${isMe}">
                                                                You
                                                            </c:when>

                                                            <c:when
                                                                test="${msg.sender != null && msg.sender.role.name == 'ADMIN'}">
                                                                Administrator
                                                            </c:when>

                                                            <c:when
                                                                test="${msg.sender != null && msg.sender.role.name == 'STAFF'}">
                                                                Staff
                                                            </c:when>

                                                            <c:otherwise>
                                                                Customer
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </strong>

                                                    <span>
                                                        <c:out value="${msg.formattedSentAt}" />
                                                    </span>
                                                </div>

                                                <div class="chat-bubble">
                                                    <c:out value="${msg.message}" />
                                                </div>
                                            </div>

                                        </c:forEach>
                                    </c:when>

                                    <c:otherwise>
                                        <div class="empty-chat">
                                            No messages yet.
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <form action="/contact/${request.id}/chat" method="post" class="chat-form">
                                <c:if test="${_csrf != null}">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                </c:if>

                                <input type="text" name="message" class="chat-input" placeholder="Type your message..."
                                    maxlength="1000" required>

                                <button type="submit" class="chat-send-btn">
                                    <i class="fa-solid fa-paper-plane"></i>
                                </button>
                            </form>
                        </div>

                    </div>
                </div>
            </div>

            <jsp:include page="../layout/footer.jsp" />

        </body>

        </html>