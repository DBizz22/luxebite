<!DOCTYPE html>
<html lang="en" class="dark">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Your Orders – LuxeBite</title>
                <script src="https://cdn.tailwindcss.com"></script>
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"
                    rel="stylesheet" />
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
                            <a href="${pageContext.request.contextPath}/products"
                                class="hover:text-amber-400 transition" title="Browse Menu">
                                <i class="fas fa-home text-2xl"></i>
                            </a>
                            <a href="${pageContext.request.contextPath}/cart" class="relative text-amber-400"
                                title="View Cart">
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

                <div class="max-w-7xl mx-auto p-6 lg:p-10">

                    <div class="flex justify-between items-center mb-10">
                        <h1 class="text-5xl font-extrabold text-amber-400">Your Orders</h1>
                        <div class="flex gap-4">
                            <button onclick="showTab('pending')" id="pendingTab"
                                class="px-6 py-3 rounded-lg font-bold transition bg-amber-600 text-black">
                                <i class="fas fa-clock mr-2"></i>Pending
                            </button>
                            <button onclick="showTab('completed')" id="completedTab"
                                class="px-6 py-3 rounded-lg font-bold transition bg-gray-800 text-gray-400 hover:bg-gray-700">
                                <i class="fas fa-check-circle mr-2"></i>Completed
                            </button>
                        </div>
                    </div>

                    <!-- Pending Orders Section -->
                    <div id="pendingSection">
                        <c:choose>
                            <c:when test="${empty pendingOrders}">
                                <div class="bg-gray-900 rounded-2xl p-12 text-center border border-amber-900/30">
                                    <i class="fas fa-shopping-cart text-6xl text-gray-600 mb-4"></i>
                                    <h3 class="text-2xl font-bold text-gray-400 mb-2">No Pending Orders</h3>
                                    <p class="text-gray-500 mb-6">Start shopping and add items to your cart!</p>
                                    <a href="${pageContext.request.contextPath}/products"
                                        class="inline-block bg-amber-600 hover:bg-amber-500 text-black font-bold px-8 py-3 rounded-lg transition">
                                        Browse Menu
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="order" items="${pendingOrders}">
                                    <div class="bg-gray-900 rounded-2xl p-6 mb-6 border border-amber-900/30">
                                        <!-- Order Header -->
                                        <div
                                            class="flex justify-between items-center mb-6 pb-4 border-b border-amber-900/20">
                                            <div>
                                                <h3 class="text-2xl font-bold text-amber-400">Order #${order.id()}</h3>
                                                <p class="text-gray-400 text-sm mt-1">
                                                    <i class="fas fa-calendar mr-2"></i>
                                                    ${fn:replace(fn:replace(order.createdAt(), 'T', ' at '), ':00.000',
                                                    '')}
                                                </p>
                                            </div>
                                            <div class="flex items-center gap-3">
                                                <span
                                                    class="px-4 py-2 rounded-lg bg-yellow-600/20 text-yellow-400 font-bold text-sm">
                                                    <i class="fas fa-clock mr-2"></i>PENDING
                                                </span>
                                            </div>
                                        </div>

                                        <!-- Order Items -->
                                        <div class="space-y-4 mb-6">
                                            <c:forEach var="item" items="${orderItemsMap[order.id()]}">
                                                <div
                                                    class="bg-gray-800/50 rounded-xl p-4 flex flex-col md:flex-row items-center justify-between gap-4">
                                                    <div class="flex items-center gap-4 flex-1">
                                                        <img src="${pageContext.request.contextPath}${item.product().imageUrl()}"
                                                            class="w-20 h-20 rounded-lg object-cover"
                                                            alt="${item.product().name()}">
                                                        <div>
                                                            <h4 class="text-lg font-bold text-amber-400">
                                                                ${item.product().name()}</h4>
                                                            <p class="text-gray-400 text-sm">
                                                                ${item.product().category()}
                                                            </p>
                                                            <p class="text-white font-medium mt-1">$${item.unitPrice()}
                                                            </p>
                                                        </div>
                                                    </div>

                                                    <div class="flex items-center gap-6">
                                                        <!-- Quantity Controls -->
                                                        <div class="flex items-center bg-gray-700 rounded-lg">
                                                            <button
                                                                onclick="updateQuantity(${item.id()}, ${item.quantity() - 1})"
                                                                class="px-4 py-2 hover:bg-gray-600 rounded-l-lg transition">
                                                                <i class="fas fa-minus"></i>
                                                            </button>
                                                            <input type="text" value="${item.quantity()}"
                                                                id="qty-${item.id()}"
                                                                class="w-16 text-center bg-transparent font-bold"
                                                                readonly>
                                                            <button
                                                                onclick="updateQuantity(${item.id()}, ${item.quantity() + 1})"
                                                                class="px-4 py-2 hover:bg-gray-600 rounded-r-lg transition">
                                                                <i class="fas fa-plus"></i>
                                                            </button>
                                                        </div>

                                                        <!-- Subtotal -->
                                                        <span class="w-28 text-right font-bold text-amber-400 text-lg"
                                                            id="subtotal-${item.id()}">
                                                            $${item.unitPrice() * item.quantity()}
                                                        </span>

                                                        <!-- Remove Button -->
                                                        <button onclick="removeItem(${item.id()})"
                                                            class="text-gray-500 hover:text-red-400 transition">
                                                            <i class="fas fa-trash text-xl"></i>
                                                        </button>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <!-- Order Summary -->
                                        <div class="bg-gray-800/30 rounded-xl p-6 mt-4">
                                            <div class="space-y-3 text-lg">
                                                <div class="flex justify-between">
                                                    <span class="text-gray-400">Subtotal</span>
                                                    <span class="font-bold" id="order-subtotal-${order.id()}">
                                                        $
                                                        <c:set var="orderTotal" value="0" />
                                                        <c:forEach var="item" items="${orderItemsMap[order.id()]}">
                                                            <c:set var="orderTotal"
                                                                value="${orderTotal + (item.unitPrice() * item.quantity())}" />
                                                        </c:forEach>
                                                        <fmt:formatNumber value="${orderTotal}" pattern="#,##0.00" />
                                                    </span>
                                                </div>
                                                <div class="flex justify-between">
                                                    <span class="text-gray-400">Delivery Fee</span>
                                                    <span class="text-green-400 font-bold">FREE</span>
                                                </div>
                                                <div class="flex justify-between">
                                                    <span class="text-gray-400">Tax (8.875%)</span>
                                                    <span class="font-bold" id="order-tax-${order.id()}">
                                                        $
                                                        <fmt:formatNumber value="${orderTotal * 0.08875}"
                                                            pattern="#,##0.00" />
                                                    </span>
                                                </div>
                                                <div class="border-t border-amber-900/40 pt-3 mt-3">
                                                    <div class="flex justify-between text-2xl font-extrabold">
                                                        <span>Total</span>
                                                        <span class="text-amber-400" id="order-total-${order.id()}">
                                                            $
                                                            <fmt:formatNumber
                                                                value="${orderTotal + (orderTotal * 0.08875)}"
                                                                pattern="#,##0.00" />
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>

                                            <button onclick="checkout(${order.id()})"
                                                class="w-full mt-6 bg-gradient-to-r from-amber-600 to-orange-600 hover:from-amber-500 hover:to-orange-500 text-black font-bold text-xl py-4 rounded-xl transition transform hover:scale-105">
                                                <i class="fas fa-credit-card mr-2"></i>Proceed to Checkout
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Completed Orders Section -->
                    <div id="completedSection" class="hidden">
                        <c:choose>
                            <c:when test="${empty completedOrders}">
                                <div class="bg-gray-900 rounded-2xl p-12 text-center border border-amber-900/30">
                                    <i class="fas fa-receipt text-6xl text-gray-600 mb-4"></i>
                                    <h3 class="text-2xl font-bold text-gray-400 mb-2">No Completed Orders</h3>
                                    <p class="text-gray-500">Your order history will appear here once you complete a
                                        purchase.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="order" items="${completedOrders}">
                                    <div class="bg-gray-900 rounded-2xl p-6 mb-6 border border-green-900/30">
                                        <!-- Order Header -->
                                        <div
                                            class="flex justify-between items-center mb-6 pb-4 border-b border-green-900/20">
                                            <div>
                                                <h3 class="text-2xl font-bold text-green-400">Order #${order.id()}</h3>
                                                <p class="text-gray-400 text-sm mt-1">
                                                    <i class="fas fa-calendar mr-2"></i>
                                                    ${fn:replace(fn:replace(order.createdAt(), 'T', ' at '), ':00.000',
                                                    '')}
                                                </p>
                                            </div>
                                            <div class="flex items-center gap-3">
                                                <span
                                                    class="px-4 py-2 rounded-lg bg-green-600/20 text-green-400 font-bold text-sm">
                                                    <i class="fas fa-check-circle mr-2"></i>COMPLETED
                                                </span>
                                            </div>
                                        </div>

                                        <!-- Order Items (Read-only) -->
                                        <div class="space-y-4 mb-6">
                                            <c:forEach var="item" items="${orderItemsMap[order.id()]}">
                                                <div
                                                    class="bg-gray-800/30 rounded-xl p-4 flex items-center justify-between">
                                                    <div class="flex items-center gap-4 flex-1">
                                                        <img src="${pageContext.request.contextPath}${item.product().imageUrl()}"
                                                            class="w-20 h-20 rounded-lg object-cover"
                                                            alt="${item.product().name()}">
                                                        <div>
                                                            <h4 class="text-lg font-bold text-green-400">
                                                                ${item.product().name()}</h4>
                                                            <p class="text-gray-400 text-sm">
                                                                ${item.product().category()}
                                                            </p>
                                                            <p class="text-white font-medium mt-1">$${item.unitPrice()}
                                                                &times; ${item.quantity()}</p>
                                                        </div>
                                                    </div>
                                                    <span class="font-bold text-green-400 text-lg">
                                                        $
                                                        <fmt:formatNumber value="${item.unitPrice() * item.quantity()}"
                                                            pattern="#,##0.00" />
                                                    </span>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <!-- Order Summary (Read-only) -->
                                        <div class="bg-gray-800/20 rounded-xl p-6">
                                            <div class="space-y-3 text-lg">
                                                <c:set var="completedTotal" value="0" />
                                                <c:forEach var="item" items="${orderItemsMap[order.id()]}">
                                                    <c:set var="completedTotal"
                                                        value="${completedTotal + (item.unitPrice() * item.quantity())}" />
                                                </c:forEach>
                                                <div class="flex justify-between">
                                                    <span class="text-gray-400">Subtotal</span>
                                                    <span class="font-bold">$
                                                        <fmt:formatNumber value="${completedTotal}"
                                                            pattern="#,##0.00" />
                                                    </span>
                                                </div>
                                                <div class="flex justify-between">
                                                    <span class="text-gray-400">Delivery Fee</span>
                                                    <span class="text-green-400 font-bold">FREE</span>
                                                </div>
                                                <div class="flex justify-between">
                                                    <span class="text-gray-400">Tax (8.875%)</span>
                                                    <span class="font-bold">$
                                                        <fmt:formatNumber value="${completedTotal * 0.08875}"
                                                            pattern="#,##0.00" />
                                                    </span>
                                                </div>
                                                <div class="border-t border-green-900/40 pt-3 mt-3">
                                                    <div class="flex justify-between text-2xl font-extrabold">
                                                        <span>Total Paid</span>
                                                        <span class="text-green-400">$
                                                            <fmt:formatNumber
                                                                value="${completedTotal + (completedTotal * 0.08875)}"
                                                                pattern="#,##0.00" />
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </div>

                <script>
                    const ctx = "${pageContext.request.contextPath}";
                    const csrfToken = "${sessionScope.CSRF_TOKEN}";

                    // Toast notification function
                    function showToast(message, type = 'success') {
                        const container = document.getElementById('toastContainer');
                        const toast = document.createElement('div');

                        const bgColor = type === 'success' ? '#16a34a' : '#dc2626';
                        const icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';

                        toast.style.cssText = 'padding: 1rem 1.5rem; border-radius: 0.5rem; box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25); display: flex; align-items: center; gap: 0.75rem; min-width: 300px; transform: translateX(400px); opacity: 0; transition: all 0.3s ease; position: relative; z-index: 9999; color: white; background-color: ' + bgColor;

                        toast.innerHTML = '<i class="fas ' + icon + '" style="font-size: 1.5rem;"></i>' +
                            '<span style="flex: 1; font-weight: 500;">' + message + '</span>' +
                            '<button onclick="this.parentElement.remove()" style="color: white; cursor: pointer;">' +
                            '<i class="fas fa-times"></i>' +
                            '</button>';

                        container.appendChild(toast);

                        setTimeout(() => {
                            toast.style.transform = 'translateX(0)';
                            toast.style.opacity = '1';
                        }, 10);

                        setTimeout(() => {
                            toast.style.transform = 'translateX(400px)';
                            toast.style.opacity = '0';
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

                    // Tab switching
                    function showTab(tab) {
                        const pendingSection = document.getElementById('pendingSection');
                        const completedSection = document.getElementById('completedSection');
                        const pendingTab = document.getElementById('pendingTab');
                        const completedTab = document.getElementById('completedTab');

                        if (tab === 'pending') {
                            pendingSection.classList.remove('hidden');
                            completedSection.classList.add('hidden');
                            pendingTab.className = 'px-6 py-3 rounded-lg font-bold transition bg-amber-600 text-black';
                            completedTab.className = 'px-6 py-3 rounded-lg font-bold transition bg-gray-800 text-gray-400 hover:bg-gray-700';
                        } else {
                            pendingSection.classList.add('hidden');
                            completedSection.classList.remove('hidden');
                            pendingTab.className = 'px-6 py-3 rounded-lg font-bold transition bg-gray-800 text-gray-400 hover:bg-gray-700';
                            completedTab.className = 'px-6 py-3 rounded-lg font-bold transition bg-amber-600 text-black';
                        }
                    }

                    // Update quantity
                    function updateQuantity(itemId, newQuantity) {
                        if (newQuantity < 1) {
                            if (!confirm('Remove this item from your order?')) {
                                return;
                            }
                            removeItem(itemId);
                            return;
                        }

                        const formData = new URLSearchParams();
                        formData.append('itemId', itemId);
                        formData.append('quantity', newQuantity);
                        formData.append('CSRF_TOKEN', csrfToken);

                        fetch(ctx + '/cart/update', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: formData
                        })
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error('Server error: ' + response.status);
                                }
                                return response.json();
                            })
                            .then(data => {
                                if (data.success) {
                                    showToast(data.message || 'Quantity updated successfully', 'success');
                                    // Update UI
                                    document.getElementById('qty-' + itemId).value = newQuantity;
                                    const unitPrice = parseFloat(data.unitPrice);
                                    const subtotal = unitPrice * newQuantity;
                                    document.getElementById('subtotal-' + itemId).textContent = '$' + subtotal.toFixed(2);

                                    // Reload page to update order totals
                                    setTimeout(() => location.reload(), 1000);
                                } else {
                                    showToast(data.message || 'Failed to update quantity', 'error');
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                showToast('An error occurred while updating quantity', 'error');
                            });
                    }

                    // Remove item
                    function removeItem(itemId) {
                        if (!confirm('Are you sure you want to remove this item?')) {
                            return;
                        }

                        const formData = new URLSearchParams();
                        formData.append('itemId', itemId);
                        formData.append('CSRF_TOKEN', csrfToken);

                        fetch(ctx + '/cart/remove', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: formData
                        })
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error('Server error: ' + response.status);
                                }
                                return response.json();
                            })
                            .then(data => {
                                if (data.success) {
                                    showToast(data.message || 'Item removed successfully', 'success');
                                    setTimeout(() => location.reload(), 1000);
                                } else {
                                    showToast(data.message || 'Failed to remove item', 'error');
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                showToast('An error occurred while removing item', 'error');
                            });
                    }

                    // Checkout
                    function checkout(orderId) {
                        const form = document.createElement('form');
                        form.method = 'POST';
                        form.action = ctx + '/checkout';
                        form.innerHTML = '<input type="hidden" name="orderId" value="' + orderId + '">' +
                            '<input type="hidden" name="CSRF_TOKEN" value="' + csrfToken + '">';
                        document.body.appendChild(form);
                        form.submit();
                    }

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