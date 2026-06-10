<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <!DOCTYPE html>
        <html lang="en" class="dark">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>Join LuxeBite - Premium Food Delivery</title>
            <script src="https://cdn.tailwindcss.com"></script>
            <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet" />
            <style>
                .bg-food {
                    background: linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.8)), url('https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?q=80&w=2000') center/cover no-repeat;
                }
            </style>
        </head>

        <body class="bg-gray-900 min-h-screen flex items-center justify-center py-12 px-4">
            <div class="max-w-md w-full">
                <div class="text-center mb-8">
                    <h1 class="text-5xl font-bold text-amber-400 tracking-wider">LuxeBite</h1>
                    <p class="text-gray-400 mt-2">Premium Dining Delivered</p>
                </div>

                <div class="bg-gray-800/90 backdrop-blur-lg p-10 rounded-2xl shadow-2xl border border-amber-900/30">
                    <h2 class="text-2xl font-semibold text-white text-center mb-6">Create Your Account</h2>

                    <form action="${pageContext.request.contextPath}/register" method="POST" class="space-y-6">
                        <div>
                            <label for="name" class="block text-amber-200 text-sm font-medium mb-2">Username</label>
                            <input id="name" type="text" name="username" required placeholder="John Doe"
                                class="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:ring-2 focus:ring-amber-500 focus:border-transparent transition">
                        </div>

                        <div>
                            <label for="email" class="block text-amber-200 text-sm font-medium mb-2">Email</label>
                            <input type="email" name="email" required placeholder="john@example.com"
                                class="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:ring-2 focus:ring-amber-500 focus:border-transparent transition">
                        </div>

                        <div>
                            <label for="phone-no" class="block text-amber-200 text-sm font-medium mb-2">Phone
                                Number</label>
                            <input type="tel" name="phone" required placeholder="+1 234 567 8900"
                                class="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:ring-2 focus:ring-amber-500 focus:border-transparent transition">
                        </div>

                        <div>
                            <label for="password" class="block text-amber-200 text-sm font-medium mb-2">Password</label>
                            <input type="password" name="password" required placeholder="Enter your password"
                                class="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:ring-2 focus:ring-amber-500 focus:border-transparent transition">
                            <p class="mt-2 text-xs text-gray-500">Min. 8 characters, include a number and symbol</p>
                        </div>

                        <button type="submit"
                            class="w-full bg-gradient-to-r from-amber-600 to-orange-600 hover:from-amber-500 hover:to-orange-500 text-white font-bold py-4 rounded-lg transition transform hover:scale-105">
                            Join LuxeBite
                        </button>
                    </form>

                    <p class="text-center text-gray-400 mt-6 text-sm">
                        Already have an account? <a href="${pageContext.request.contextPath}/login"
                            class="text-amber-400 hover:text-amber-300 font-medium">Sign
                            in</a>
                    </p>
                </div>
            </div>

            <!-- TOAST CONTAINER -->
            <div id="toastContainer" class="fixed top-4 right-4 z-50 space-y-3"></div>

            <!-- TOAST JAVASCRIPT -->
            <script>
                function showToast(type, message) {
                    const container = document.getElementById("toastContainer");

                    const toast = document.createElement("div");
                    toast.className = `max-w-sm w-full shadow-2xl rounded-xl pointer-events-auto overflow-hidden transform transition-all duration-500 translate-x-full opacity-0`;

                    const bg = type === "success"
                        ? "from-emerald-500 to-green-600"
                        : "from-red-500 to-rose-600";

                    const icon = type === "success"
                        ? '<i class="fas fa-check-circle text-2xl"></i>'
                        : '<i class="fas fa-exclamation-circle text-2xl"></i>';

                    const toastHTML = '<div class="bg-gradient-to-r ' + bg + ' text-white p-4 flex items-center justify-between gap-3">' +
                        '<div class="flex items-center gap-3 flex-1">' +
                        icon +
                        '<p class="font-semibold text-base">' + message + '</p>' +
                        '</div>' +
                        '<button onclick="removeToast(this)" class="text-white hover:bg-white/20 rounded-full p-2 transition flex-shrink-0">' +
                        '<i class="fas fa-times"></i>' +
                        '</button>' +
                        '</div>';

                    toast.innerHTML = toastHTML;
                    container.appendChild(toast);

                    // Animate in
                    setTimeout(() => {
                        toast.classList.remove("translate-x-full", "opacity-0");
                        toast.classList.add("translate-x-0", "opacity-100");
                    }, 100);

                    // Auto remove after 4 seconds
                    setTimeout(() => {
                        removeToast(toast);
                    }, 4000);
                }

                function removeToast(element) {
                    const toast = element.tagName === 'BUTTON' ? element.closest('.max-w-sm') : element;
                    toast.classList.add("translate-x-full", "opacity-0");
                    setTimeout(() => toast.remove(), 500);
                }

        // Trigger toasts on page load
        <c:if test="${not empty successMessage && fn:trim(successMessage) != ''}">
            showToast("success", "${fn:escapeXml(successMessage)}");
        </c:if>

        <c:if test="${not empty errorMessage && fn:trim(errorMessage) != ''}">
            showToast("error", "${fn:escapeXml(errorMessage)}");
        </c:if>
            </script>
        </body>

        </html>