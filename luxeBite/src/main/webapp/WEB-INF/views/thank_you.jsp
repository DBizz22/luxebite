<!DOCTYPE html>
<html lang="en" class="dark">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>Order Complete – LuxeBite</title>
            <script src="https://cdn.tailwindcss.com"></script>
            <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet" />
            <style>
                @keyframes checkmark {
                    0% {
                        stroke-dashoffset: 100;
                        opacity: 0;
                    }

                    50% {
                        opacity: 1;
                    }

                    100% {
                        stroke-dashoffset: 0;
                        opacity: 1;
                    }
                }

                .checkmark-animation {
                    animation: checkmark 1s ease-in-out forwards;
                }

                @keyframes bounce-in {
                    0% {
                        transform: scale(0);
                        opacity: 0;
                    }

                    50% {
                        transform: scale(1.1);
                    }

                    100% {
                        transform: scale(1);
                        opacity: 1;
                    }
                }

                .bounce-in {
                    animation: bounce-in 0.6s ease-out forwards;
                }
            </style>
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
                            class="relative hover:text-amber-400 transition" title="View Orders">
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

                <!-- Success Card -->
                <div
                    class="bg-gray-900 rounded-3xl p-12 border-2 border-green-900/50 text-center shadow-2xl w-full bounce-in">

                    <!-- Animated Checkmark -->
                    <div class="mb-8 flex justify-center">
                        <div class="relative">
                            <div
                                class="w-32 h-32 rounded-full bg-green-600/20 flex items-center justify-center border-4 border-green-600">
                                <i class="fas fa-check text-6xl text-green-400"></i>
                            </div>
                            <div
                                class="absolute inset-0 rounded-full border-4 border-green-400 animate-ping opacity-75">
                            </div>
                        </div>
                    </div>

                    <h1 class="text-6xl font-extrabold text-green-400 mb-4">Order Complete!</h1>
                    <p class="text-2xl text-gray-300 mb-8">Thank you for your purchase</p>

                    <!-- Order Details -->
                    <c:if test="${not empty order}">
                        <div class="bg-green-900/10 rounded-xl p-8 mb-8 border border-green-800/30">
                            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 text-left">
                                <div>
                                    <p class="text-gray-400 text-sm mb-1">Order Number</p>
                                    <p class="text-2xl font-bold text-green-400">#${order.id()}</p>
                                </div>
                                <div>
                                    <p class="text-gray-400 text-sm mb-1">Order Date</p>
                                    <p class="text-xl font-medium text-white">
                                        <fmt:formatDate value="${order.createdAt()}" pattern="MMM dd, yyyy hh:mm a" />
                                    </p>
                                </div>
                                <div>
                                    <p class="text-gray-400 text-sm mb-1">Status</p>
                                    <p class="text-xl font-bold text-green-400">
                                        <i class="fas fa-check-circle mr-2"></i>COMPLETED
                                    </p>
                                </div>
                                <div>
                                    <p class="text-gray-400 text-sm mb-1">Total Amount</p>
                                    <p class="text-2xl font-bold text-amber-400">
                                        <c:if test="${not empty orderTotal}">
                                            $
                                            <fmt:formatNumber value="${orderTotal}" pattern="#,##0.00" />
                                        </c:if>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <!-- Success Message -->
                    <div class="bg-gray-800/50 rounded-xl p-6 mb-8">
                        <div class="flex items-start gap-4">
                            <i class="fas fa-info-circle text-3xl text-blue-400 mt-1"></i>
                            <div class="text-left">
                                <h3 class="text-xl font-bold text-white mb-2">What's Next?</h3>
                                <ul class="text-gray-300 space-y-2">
                                    <li class="flex items-center gap-2">
                                        <i class="fas fa-envelope text-green-400"></i>
                                        <span>A confirmation email has been sent to your registered email</span>
                                    </li>
                                    <li class="flex items-center gap-2">
                                        <i class="fas fa-truck text-amber-400"></i>
                                        <span>Your order will be prepared and delivered shortly</span>
                                    </li>
                                    <li class="flex items-center gap-2">
                                        <i class="fas fa-history text-blue-400"></i>
                                        <span>You can track your order history in your cart page</span>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="flex flex-col sm:flex-row gap-4 justify-center">
                        <a href="${pageContext.request.contextPath}/cart"
                            class="bg-gradient-to-r from-green-600 to-emerald-600 hover:from-green-500 hover:to-emerald-500 text-white font-bold text-xl px-8 py-4 rounded-xl transition transform hover:scale-105 inline-flex items-center justify-center">
                            <i class="fas fa-receipt mr-3"></i>View Order History
                        </a>

                        <a href="${pageContext.request.contextPath}/products"
                            class="bg-amber-600 hover:bg-amber-500 text-black font-bold text-xl px-8 py-4 rounded-xl transition transform hover:scale-105 inline-flex items-center justify-center">
                            <i class="fas fa-utensils mr-3"></i>Continue Shopping
                        </a>
                    </div>

                    <!-- Promotional Message -->
                    <div class="mt-10 pt-8 border-t border-gray-800">
                        <p class="text-gray-400 text-lg mb-4">
                            <i class="fas fa-star text-amber-400 mr-2"></i>
                            Enjoyed your experience? Share your feedback!
                        </p>
                        <div class="flex justify-center gap-3">
                            <button class="text-3xl hover:scale-125 transition transform" title="Rate 5 stars">
                                <i class="far fa-star hover:fas hover:text-amber-400"></i>
                            </button>
                            <button class="text-3xl hover:scale-125 transition transform" title="Rate 5 stars">
                                <i class="far fa-star hover:fas hover:text-amber-400"></i>
                            </button>
                            <button class="text-3xl hover:scale-125 transition transform" title="Rate 5 stars">
                                <i class="far fa-star hover:fas hover:text-amber-400"></i>
                            </button>
                            <button class="text-3xl hover:scale-125 transition transform" title="Rate 5 stars">
                                <i class="far fa-star hover:fas hover:text-amber-400"></i>
                            </button>
                            <button class="text-3xl hover:scale-125 transition transform" title="Rate 5 stars">
                                <i class="far fa-star hover:fas hover:text-amber-400"></i>
                            </button>
                        </div>
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

                    // Confetti effect on page load
                    celebrateOrder();
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

                // Simple celebration effect
                function celebrateOrder() {
                    showToast('🎉 Your order has been successfully placed!', 'success');
                }
            </script>
        </body>

</html>