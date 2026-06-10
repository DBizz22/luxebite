<!DOCTYPE html>
<html lang="en" class="dark">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>Checkout Error – LuxeBite</title>
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
                        <a href="${pageContext.request.contextPath}/cart"
                            class="relative hover:text-amber-400 transition" title="View Cart">
                            <i class="fas fa-shopping-bag text-2xl"></i>
                        </a>
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
                    </div>
                </div>
            </nav>

            <!-- Toast Container -->
            <div id="toastContainer" class="fixed top-20 right-6 z-[60] space-y-3"></div>

            <div class="max-w-4xl mx-auto p-6 lg:p-10 flex items-center justify-center min-h-[calc(100vh-200px)]">

                <!-- Error Card -->
                <div class="bg-gray-900 rounded-3xl p-12 border-2 border-red-900/50 text-center shadow-2xl w-full">
                    <div class="mb-6">
                        <i class="fas fa-exclamation-circle text-8xl text-red-500 mb-4"></i>
                    </div>

                    <h1 class="text-5xl font-extrabold text-red-400 mb-6">Checkout Failed</h1>

                    <div class="bg-red-900/20 rounded-xl p-6 mb-8 border border-red-800/50">
                        <p class="text-2xl text-red-300 font-medium">
                            <c:choose>
                                <c:when test="${not empty errorMessage}">
                                    ${errorMessage}
                                </c:when>
                                <c:otherwise>
                                    An error occurred while processing your order. Please try again.
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>

                    <div class="space-y-4">
                        <p class="text-gray-400 text-lg mb-6">
                            Don't worry! Your items are still in your cart. You can try checking out again or contact
                            support if the issue persists.
                        </p>

                        <div class="flex flex-col sm:flex-row gap-4 justify-center">
                            <a href="${pageContext.request.contextPath}/cart"
                                class="bg-gradient-to-r from-amber-600 to-orange-600 hover:from-amber-500 hover:to-orange-500 text-black font-bold text-xl px-8 py-4 rounded-xl transition transform hover:scale-105 inline-flex items-center justify-center">
                                <i class="fas fa-shopping-cart mr-3"></i>Back to Cart
                            </a>

                            <a href="${pageContext.request.contextPath}/products"
                                class="bg-gray-800 hover:bg-gray-700 text-white font-bold text-xl px-8 py-4 rounded-xl transition transform hover:scale-105 inline-flex items-center justify-center border border-amber-600/50">
                                <i class="fas fa-utensils mr-3"></i>Continue Shopping
                            </a>
                        </div>
                    </div>

                    <div class="mt-8 pt-6 border-t border-gray-800">
                        <p class="text-gray-500 text-sm">
                            Need help? <a href="${pageContext.request.contextPath}/contact"
                                class="text-amber-400 hover:text-amber-300 underline">Contact Support</a>
                        </p>
                    </div>
                </div>

            </div>

            <script>
                const ctx = "${pageContext.request.contextPath}";
                const csrfToken = "${sessionScope.CSRF_TOKEN}";

                // Toast notification function
                function showToast(message, type = 'success') {
                    const container = document.getElementById('toastContainer');
                    const toast = document.createElement('div');

                    const bgColor = type === 'success' ? 'bg-green-600' : 'bg-red-600';
                    const icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';

                    toast.className = `${bgColor} text-white px-6 py-4 rounded-lg shadow-2xl flex items-center space-x-3 min-w-[300px] transform transition-all duration-300 translate-x-[400px] opacity-0`;
                    toast.innerHTML = `
                <i class="fas ${icon} text-2xl"></i>
                <span class="flex-1 font-medium">${message}</span>
                <button onclick="this.parentElement.remove()" class="text-white hover:text-gray-200">
                    <i class="fas fa-times"></i>
                </button>
            `;

                    container.appendChild(toast);

                    setTimeout(() => {
                        toast.classList.remove('translate-x-[400px]', 'opacity-0');
                    }, 10);

                    setTimeout(() => {
                        toast.classList.add('translate-x-[400px]', 'opacity-0');
                        setTimeout(() => toast.remove(), 300);
                    }, 4000);
                }

                // Check for messages from session on page load
                document.addEventListener('DOMContentLoaded', function () {
                    const successMessage = "${sessionScope.successMessage}";
                    const errorMessage = "${sessionScope.errorMessage}";

                    if (successMessage && successMessage !== '') {
                        showToast(successMessage, 'success');
                    }

                    if (errorMessage && errorMessage !== '') {
                        showToast(errorMessage, 'error');
                    }
                });

                // Logout function
                function logout() {
                    if (confirm('Are you sure you want to logout?')) {
                        const form = document.createElement('form');
                        form.method = 'POST';
                        form.action = ctx + '/logout';
                        form.innerHTML = `
                    <input type="hidden" name="CSRF_TOKEN" value="${csrfToken}">
                `;
                        document.body.appendChild(form);
                        form.submit();
                    }
                }
            </script>
        </body>

</html>