<!DOCTYPE html>
<html lang="en" class="dark">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Error – LuxeBite</title>
        <script src="https://cdn.tailwindcss.com"></script>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet" />
    </head>

    <body class="bg-gray-950 text-white min-h-screen">

        <!-- Navbar -->
        <nav class="bg-black/90 backdrop-blur-lg border-b border-amber-900/40 sticky top-0 z-50">
            <div class="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
                <div class="flex items-center space-x-4">
                    <a href="${pageContext.request.contextPath}/products">
                        <h1
                            class="text-4xl font-bold text-amber-400 tracking-wider cursor-pointer hover:text-amber-300 transition">
                            LuxeBite</h1>
                    </a>
                </div>
                <div class="flex items-center space-x-6">
                    <a href="${pageContext.request.contextPath}/products" class="hover:text-amber-400 transition"
                        title="Browse Menu">
                        <i class="fas fa-home text-2xl"></i>
                    </a>
                    <a href="${pageContext.request.contextPath}/cart" class="relative hover:text-amber-400 transition"
                        title="View Cart">
                        <i class="fas fa-shopping-bag text-2xl"></i>
                    </a>
                    <c:if test="${not empty LOGGED_IN_USER}">
                        <a href="${pageContext.request.contextPath}/profile"
                            class="flex items-center space-x-2 hover:text-amber-400" title="Profile">
                            <i class="fas fa-user-circle text-2xl"></i>
                            <span class="hidden md:inline">${LOGGED_IN_USER.username}</span>
                        </a>
                        <button onclick="logout()" class="flex items-center space-x-2 hover:text-red-400 transition"
                            title="Logout">
                            <i class="fas fa-sign-out-alt text-2xl"></i>
                            <span class="hidden md:inline">Logout</span>
                        </button>
                    </c:if>
                </div>
            </div>
        </nav>

        <!-- Error Content -->
        <div class="max-w-4xl mx-auto p-6 lg:p-10">
            <div class="bg-gray-900 rounded-2xl p-12 text-center border border-red-900/30 shadow-2xl">
                <div class="mb-8">
                    <i class="fas fa-exclamation-triangle text-8xl text-red-500 animate-pulse"></i>
                </div>

                <h1 class="text-5xl font-extrabold text-red-400 mb-4">Oops! Something Went Wrong</h1>

                <c:choose>
                    <c:when test="${not empty errorMessage}">
                        <p class="text-xl text-gray-300 mb-8">${errorMessage}</p>
                    </c:when>
                    <c:otherwise>
                        <p class="text-xl text-gray-300 mb-8">
                            We encountered an unexpected error while processing your request.
                        </p>
                    </c:otherwise>
                </c:choose>

                <div class="bg-gray-800/50 rounded-xl p-6 mb-8 text-left">
                    <h3 class="text-lg font-bold text-amber-400 mb-3">
                        <i class="fas fa-info-circle mr-2"></i>What can you do?
                    </h3>
                    <ul class="space-y-2 text-gray-300">
                        <li><i class="fas fa-chevron-right text-amber-400 mr-2"></i>Check your cart and try again
                        </li>
                        <li><i class="fas fa-chevron-right text-amber-400 mr-2"></i>Make sure you have items in your
                            cart</li>
                        <li><i class="fas fa-chevron-right text-amber-400 mr-2"></i>Verify your payment information
                        </li>
                        <li><i class="fas fa-chevron-right text-amber-400 mr-2"></i>Contact support if the problem
                            persists</li>
                    </ul>
                </div>

                <div class="flex flex-col sm:flex-row gap-4 justify-center">
                    <a href="${pageContext.request.contextPath}/cart"
                        class="inline-flex items-center justify-center bg-amber-600 hover:bg-amber-500 text-black font-bold px-8 py-4 rounded-xl transition transform hover:scale-105">
                        <i class="fas fa-shopping-cart mr-2"></i>Back to Cart
                    </a>
                    <a href="${pageContext.request.contextPath}/products"
                        class="inline-flex items-center justify-center bg-gray-700 hover:bg-gray-600 text-white font-bold px-8 py-4 rounded-xl transition transform hover:scale-105">
                        <i class="fas fa-home mr-2"></i>Browse Menu
                    </a>
                </div>

                <c:if test="${not empty exception}">
                    <div class="mt-8 text-left">
                        <details class="bg-gray-800/30 rounded-lg p-4">
                            <summary class="cursor-pointer text-gray-400 hover:text-amber-400 font-medium transition">
                                <i class="fas fa-code mr-2"></i>Technical Details (for developers)
                            </summary>
                            <pre class="mt-4 text-xs text-gray-500 overflow-auto max-h-64">${exception}</pre>
                        </details>
                    </div>
                </c:if>
            </div>
        </div>

        <script>
            const ctx = "${pageContext.request.contextPath}";
            const csrfToken = "${sessionScope.CSRF_TOKEN}";

            // Logout function
            function logout() {
                if (confirm('Are you sure you want to logout?')) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = ctx + '/logout';
                    form.innerHTML = '<input type="hidden" name="CSRF_TOKEN" value="' + csrfToken + '">';
                    document.body.appendChild(form);
                    form.submit();
                }
            }
        </script>
    </body>

</html>